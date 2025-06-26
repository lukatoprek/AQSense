package hr.ferit.ltoprek.aqsense.components.implementation.main

import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import hr.ferit.ltoprek.aqsense.components.implementation.main.blocks.RealGraphComponent
import hr.ferit.ltoprek.aqsense.components.implementation.main.blocks.RealSensorMapComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.SensorDetailsComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.GraphComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.SensorMapComponent
import hr.ferit.ltoprek.aqsense.models.Sensor
import hr.ferit.ltoprek.aqsense.models.SensorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class RealSensorDetailsComponent(
    componentContext: ComponentContext,
    private val sensorId: String,
    private val sensorRepository : SensorRepository,
    private val onExitFeedback: () -> Unit
) : ComponentContext by componentContext, SensorDetailsComponent {

    override val sensor = MutableStateFlow<Sensor?>(null)
    override val error = MutableStateFlow<Exception?>(null)
    override val graphComponent = MutableStateFlow<GraphComponent?>(null)
    override val mapComponent = MutableStateFlow<SensorMapComponent?>(null)
    private val _componentContext = componentContext
    override val inProgress = MutableStateFlow(false)

    private val componentScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    init{
        lifecycle.doOnDestroy{
            componentScope.cancel()
        }
    }

    private fun setGraphComponent()
    {
        graphComponent.value = RealGraphComponent(
            componentContext = _componentContext,
            sensorTimes = sensor.value!!.getSensorTimes(),
            sensorValueRange = sensor.value!!.getSensorValueRange(),
            measurementPointList = sensor.value!!.getMeasurementPointList(),
            color = getRandomColor(),
            unitOfMeasurements = sensor.value!!.unitOfMeasurement
        )
    }

    private fun setMapComponent()
    {
        mapComponent.value = RealSensorMapComponent(
            componentContext = _componentContext,
            sensor = sensor.value!!
        )
    }

    override fun onBackClicked() {
        onExitFeedback()
    }

    override fun getSensor() {
        inProgress.value = true
        error.value = null

        componentScope.launch {
            try {
                sensor.value = sensorRepository.getSensorById(id = sensorId)
                setGraphComponent()
                setMapComponent()
            } catch (e: Exception)
            {
                sensor.value = null
                error.value = e
            } finally {
                inProgress.value = false
            }
        }
    }

    private fun getRandomColor(): Color {
        return Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
    }
}
