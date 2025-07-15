package hr.ferit.ltoprek.aqsense.components.inteface.main

import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.DateTimeFilterComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.DateTimeFilterComponent.DateRangePreset
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.GraphComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.SensorMapComponent
import hr.ferit.ltoprek.aqsense.models.Sensor
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant

interface SensorDetailsComponent
{
    val sensor: StateFlow<Sensor?>

    val globalError: StateFlow<Exception?>

    val graphComponent: StateFlow<GraphComponent?>

    val mapComponent: StateFlow<SensorMapComponent?>

    val inProgress: StateFlow<Boolean>

    val selectedDateRange: StateFlow<DateRangePreset?>

    val dateTimeFilterComponent: DateTimeFilterComponent

    val startDate: StateFlow<Instant?>

    val endDate: StateFlow<Instant?>

    fun onResetGlobalError()

    fun onBackClicked()

    fun getSensor()

    fun filterMeasurementsByDateTime()
}