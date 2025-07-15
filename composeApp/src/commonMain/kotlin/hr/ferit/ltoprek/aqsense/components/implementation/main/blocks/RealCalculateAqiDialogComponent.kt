package hr.ferit.ltoprek.aqsense.components.implementation.main.blocks

import com.arkivanov.decompose.ComponentContext
import com.mmk.kmpnotifier.notification.NotifierManager
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.CalculateAqiDialogComponent
import hr.ferit.ltoprek.aqsense.models.Sensor
import hr.ferit.ltoprek.aqsense.utilities.AqiCalculator
import hr.ferit.ltoprek.aqsense.utilities.AqiCalculator.AqiRisk
import hr.ferit.ltoprek.aqsense.utilities.AqiCalculator.AqiRiskWarning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.roundToInt
import kotlin.random.Random

class RealCalculateAqiDialogComponent(
    componentContext: ComponentContext,
    private val onDismissFeedback: () -> Unit
) : ComponentContext by componentContext, CalculateAqiDialogComponent
{
    override val selectedSensors = MutableStateFlow<List<Sensor>>(emptyList())

    override val globalError = MutableStateFlow<Exception?>(null)

    override val calculatedAqi = MutableStateFlow<Double?>(null)

    override val isDateRangeCalculationModeSet = MutableStateFlow(false)

    override fun setDateRangeCalculationMode() { isDateRangeCalculationModeSet.value = true }

    override fun resetDateRangeCalculationMode() { isDateRangeCalculationModeSet.value = false }

    override fun onSelectedSensorsChanged(sensors: List<Sensor>) { selectedSensors.value = sensors }

    override fun onCalculateAqiClicked() {
        globalError.value = null
        if(selectedSensors.value.isEmpty())
        {
            globalError.value = Exception("No sensors selected")
            return
        }

        val aqi = AqiCalculator.calculateAqi(selectedSensors.value, isDateRangeCalculationModeSet.value)
        calculatedAqi.value = aqi

        val aqiRisk = AqiCalculator.checkAqiRisk(aqi)
        val riskWarning = when (aqiRisk) {
            AqiRisk.GOOD -> AqiRiskWarning.GOOD
            AqiRisk.MODERATE -> AqiRiskWarning.MODERATE
            AqiRisk.UNHEALTHY -> AqiRiskWarning.UNHEALTHY
        }


        var sensorNames = ""
        for(sensor in selectedSensors.value)
        {
            sensorNames += sensor.name + ", "
        }

        val notifier = NotifierManager.getLocalNotifier()
        notifier.notify {
            id = Random.nextInt(0,Int.MAX_VALUE)
            title = "Calculated AQI: ${aqi.roundToInt()}"
            body = "$riskWarning Based on AQI calculated for sensors: $sensorNames"
        }
    }

    override fun onDismiss() {
        calculatedAqi.value = null
        onDismissFeedback()
    }
}