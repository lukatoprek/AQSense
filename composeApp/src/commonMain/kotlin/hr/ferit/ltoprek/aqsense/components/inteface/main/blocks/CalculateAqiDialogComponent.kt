package hr.ferit.ltoprek.aqsense.components.inteface.main.blocks

import hr.ferit.ltoprek.aqsense.models.Sensor
import kotlinx.coroutines.flow.StateFlow

interface CalculateAqiDialogComponent
{
    val selectedSensors: StateFlow<List<Sensor>>

    val globalError: StateFlow<Exception?>

    val calculatedAqi: StateFlow<Double?>

    val isDateRangeCalculationModeSet: StateFlow<Boolean>

    fun setDateRangeCalculationMode()

    fun resetDateRangeCalculationMode()

    fun onSelectedSensorsChanged(sensors: List<Sensor>)

    fun onCalculateAqiClicked()

    fun onDismiss()
}