package hr.ferit.ltoprek.aqsense.components.implementation.main.blocks

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.SensorListComponent
import hr.ferit.ltoprek.aqsense.models.Sensor
import hr.ferit.ltoprek.aqsense.utilities.Model

class RealSensorListComponent(
    componentContext: ComponentContext,
    sensors: List<Sensor>,
    private val onSensorClickedFeedback: (String) -> Unit
) : ComponentContext by componentContext, SensorListComponent
{
    override val model: Value<Model> = MutableValue(Model(sensors))

    override fun onSensorClicked(sensorId: String) {
        onSensorClickedFeedback(sensorId)
    }
}