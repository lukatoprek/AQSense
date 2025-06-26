package hr.ferit.ltoprek.aqsense.components.inteface.main

import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.GraphComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.SensorMapComponent
import hr.ferit.ltoprek.aqsense.models.Sensor
import kotlinx.coroutines.flow.StateFlow

interface SensorDetailsComponent
{
    val sensor: StateFlow<Sensor?>

    val error: StateFlow<Exception?>

    val graphComponent: StateFlow<GraphComponent?>

    val mapComponent: StateFlow<SensorMapComponent?>

    val inProgress: StateFlow<Boolean>

    fun onBackClicked()

    fun getSensor()
}