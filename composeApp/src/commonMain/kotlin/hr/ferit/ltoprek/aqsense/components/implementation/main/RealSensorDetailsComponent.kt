package hr.ferit.ltoprek.aqsense.components.implementation.main

import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import hr.ferit.ltoprek.aqsense.components.implementation.main.blocks.RealDateTimeFilterComponent
import hr.ferit.ltoprek.aqsense.components.implementation.main.blocks.RealGraphComponent
import hr.ferit.ltoprek.aqsense.components.implementation.main.blocks.RealSensorMapComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.SensorDetailsComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.DateTimeFilterComponent.DateRangePreset
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
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.random.Random
import kotlin.time.Duration.Companion.days

class RealSensorDetailsComponent(
    componentContext: ComponentContext,
    private val sensorId: String,
    private val sensorRepository : SensorRepository,
    private val onExitFeedback: () -> Unit
) : ComponentContext by componentContext, SensorDetailsComponent {

    private val _sensor = MutableStateFlow<Sensor?>(null)
    override val sensor = MutableStateFlow<Sensor?>(null)

    override val globalError = MutableStateFlow<Exception?>(null)

    override val inProgress = MutableStateFlow(false)
    private val _componentContext = componentContext

    override val graphComponent = MutableStateFlow<GraphComponent?>(null)
    override val mapComponent = MutableStateFlow<SensorMapComponent?>(null)

    override val selectedDateRange = MutableStateFlow<DateRangePreset?>(null)
    override val startDate = MutableStateFlow<Instant?>(null)
    override val endDate = MutableStateFlow<Instant?>(null)

    override val dateTimeFilterComponent = RealDateTimeFilterComponent(
        componentContext = childContext(key = "dateTimeFilter"),
        setGlobalError = {
            globalError.value = it
            graphComponent.value = null
            sensor.value = null
        },
        onDateTimeSelectedFeedback =
        { dateRangePreset ->
            if(dateRangePreset is DateRangePreset.Custom)
            {
                startDate.value = dateRangePreset.startDate
                endDate.value = dateRangePreset.endDate
            }
            selectedDateRange.value = dateRangePreset
            filterMeasurementsByDateTime()
        }
    )

    private val componentScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    init{
        lifecycle.doOnDestroy{
            componentScope.cancel()
        }
    }

    override fun onResetGlobalError() {
        globalError.value = null
        sensor.value = _sensor.value
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
        onResetGlobalError()

        componentScope.launch {
            try {
                _sensor.value = sensorRepository.getSensorById(id = sensorId)
                sensor.value = _sensor.value
                setGraphComponent()
                setMapComponent()
            }
            catch (e: Exception)
            {
                _sensor.value = null
                globalError.value = e
            } finally {
                inProgress.value = false
                filterMeasurementsByDateTime()
            }
        }
    }

    override fun filterMeasurementsByDateTime()
    {
        onResetGlobalError()
        val filteredMeasurements = selectedDateRange.value?.let { selectedDateRange ->
            val now = Clock.System.now()

            val (currentFilterStartTime, currentFilterEndTime) = when (selectedDateRange) {
                is DateRangePreset.LastDay -> now.minus(1.days) to now
                is DateRangePreset.LastWeek -> now.minus(7.days) to now
                is DateRangePreset.LastThreeDays -> now.minus(3.days) to now
                is DateRangePreset.LastMonth -> now.minus(30.days) to now
                is DateRangePreset.Custom -> startDate.value to endDate.value
            }
            sensor.value = _sensor.value!!.copy()
            sensor.value!!.measurements.filter { measurement ->
                measurement.time >= currentFilterStartTime!! && measurement.time <= currentFilterEndTime!!
            }.sortedBy { it.time }.toMutableList()
        } ?: _sensor.value!!.measurements

        sensor.value = _sensor.value!!.copy(measurements = filteredMeasurements)
        setGraphComponent()
    }

    private fun getRandomColor(): Color {
        return Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
    }
}
