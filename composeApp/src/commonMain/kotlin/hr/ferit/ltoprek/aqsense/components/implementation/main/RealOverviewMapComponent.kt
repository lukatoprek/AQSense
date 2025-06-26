package hr.ferit.ltoprek.aqsense.components.implementation.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.lifecycle.doOnDestroy
import dev.sargunv.maplibrecompose.compose.CameraState
import dev.sargunv.maplibrecompose.core.CameraPosition
import dev.sargunv.maplibrecompose.core.source.GeoJsonData
import hr.ferit.ltoprek.aqsense.components.implementation.main.blocks.RealBottomNavBarComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.OverviewMapComponent
import hr.ferit.ltoprek.aqsense.models.SensorRepository
import hr.ferit.ltoprek.aqsense.models.SensorType
import hr.ferit.ltoprek.aqsense.utilities.Model
import hr.ferit.ltoprek.aqsense.utilities.NavItem
import io.github.dellisd.spatialk.geojson.Feature
import io.github.dellisd.spatialk.geojson.FeatureCollection
import io.github.dellisd.spatialk.geojson.Point
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive

class RealOverviewMapComponent(
    componentContext: ComponentContext,
    private val sensorRepository: SensorRepository,
    onHomeClicked: () -> Unit,
    onProfileClicked: () -> Unit,
) : ComponentContext by componentContext, OverviewMapComponent
{
    private val _sensors = MutableValue(Model(emptyList()))

    override val sensors = _sensors

    override val inProgress = MutableStateFlow(false)

    override val globalError = MutableStateFlow<Exception?>(null)

    private val componentScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    init{
        lifecycle.doOnDestroy{
            componentScope.cancel()
        }
    }

    companion object{
        private const val DEFAULT_CAMERA_ZOOM = 9.0
    }

    override val bottomNavBarComponent = RealBottomNavBarComponent(
        componentContext = childContext(key = "bottomNavBar"),
        onHomeClicked = onHomeClicked,
        onProfileClicked = onProfileClicked,
        onOverviewMapClicked = {},
        currentScreen = NavItem.MAP
    )

    override fun onResetGlobalError() { globalError.value = null }

    override fun getSensors()
    {
        inProgress.value = true
        onResetGlobalError()

        componentScope.launch {
            try {
                val sensorsData = sensorRepository.getSensors()
                _sensors.value = Model(sensorsData)
            } catch(e: Exception){
                globalError.value = e
                _sensors.value = Model(emptyList())
            } finally {
                inProgress.value = false
            }
        }
    }

    override fun getCameraState(): CameraState {
        val coordinates = _sensors.value.sensors.map{ sensor ->
            sensor.coordinates
        }

        var sumLat = 0.0
        var sumLong = 0.0

        coordinates.filterNotNull().let { c ->
            sumLat += c.sumOf{it.latitude}
            sumLong += c.sumOf{it.longitude}
        }

        return CameraState(
            firstPosition = CameraPosition(
                target =
                if(coordinates.isNotEmpty()) {
                    Position(
                        sumLong / coordinates.size,
                        sumLat / coordinates.size.toDouble()
                    )
                } else { Position(0.0,0.0) },
                zoom = DEFAULT_CAMERA_ZOOM
            )
        )
    }

    override fun getSourceData(): GeoJsonData.Features
    {
        val sensors = _sensors.value.sensors.filter { it.coordinates != null }
        val markers = sensors.map{ sensor ->
            val coordinates = sensor.coordinates!!
            Feature(
                geometry = Point(Position(coordinates.longitude, coordinates.latitude)),
                properties = mapOf(
                    "name" to JsonPrimitive(sensor.name),
                    "type" to JsonPrimitive(SensorType.sensorColors[sensor.type]?:"Black")
                )
            )
        }
        return GeoJsonData.Features(FeatureCollection(markers))
    }
}