package hr.ferit.ltoprek.aqsense.components.inteface.main.blocks

import com.arkivanov.decompose.value.Value
import hr.ferit.ltoprek.aqsense.utilities.Model

interface SensorListComponent
{
    val model: Value<Model>

    fun onSensorClicked(sensorId: String)
}