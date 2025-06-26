package hr.ferit.ltoprek.aqsense.components.implementation.main.blocks

import com.arkivanov.decompose.ComponentContext
import dev.sargunv.maplibrecompose.compose.CameraState
import dev.sargunv.maplibrecompose.core.CameraPosition
import dev.sargunv.maplibrecompose.core.source.GeoJsonData
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.SensorMapComponent
import hr.ferit.ltoprek.aqsense.models.Sensor
import hr.ferit.ltoprek.aqsense.models.SensorType
import io.github.dellisd.spatialk.geojson.Feature
import io.github.dellisd.spatialk.geojson.FeatureCollection
import io.github.dellisd.spatialk.geojson.Point
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.JsonPrimitive

class RealSensorMapComponent(
    componentContext: ComponentContext,
    sensor: Sensor
) : ComponentContext by componentContext, SensorMapComponent {

    private val _sensor = sensor

    override val error = MutableStateFlow<Exception?>(null)

    companion object{
        private const val DEFAULT_CAMERA_ZOOM = 12.0
    }

    override fun getCameraState(): CameraState? {
        val coordinates = _sensor.coordinates

        return coordinates?.let { c ->
            CameraState(
                firstPosition = CameraPosition(
                    target = Position(c.longitude, c.latitude),
                    zoom = DEFAULT_CAMERA_ZOOM
                )
            )
        } ?: run {
            error.value = Exception("Sensor has no coordinates")
            null
        }
    }

    override fun getSourceData(): GeoJsonData.Features?
    {
        val coordinates = _sensor.coordinates
        coordinates?.let { c ->
            val markers = FeatureCollection(
                listOf(
                    Feature(
                        geometry = Point(Position(c.longitude, c.latitude)),
                        properties = mapOf(
                            "name" to JsonPrimitive(_sensor.name),
                            "type" to JsonPrimitive(SensorType.sensorColors[_sensor.type]?:"Black")
                        )
                    )
                )
            )
            return GeoJsonData.Features(markers)
        }?: run {
            error.value = Exception("Sensor has no coordinates")
            return null
        }
    }
}