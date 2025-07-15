package hr.ferit.ltoprek.aqsense.utilities

import dev.gitlive.firebase.firestore.Timestamp
import hr.ferit.ltoprek.aqsense.models.Sensor
import hr.ferit.ltoprek.aqsense.models.SensorType
import kotlinx.datetime.Instant

enum class NavItem { PROFILE, HOME, MAP }

object TimestampConverter
{
    fun Timestamp.toKotlinInstant(): Instant = this.let {
        Instant.fromEpochSeconds(it.seconds, it.nanoseconds.toLong())
    }
}

object AqiCalculator
{
    private const val CO_MIN = 0
    private const val CO_MAX = 50
    private const val CO2_MIN = 0
    private const val CO2_MAX = 2000
    private const val VOC_MIN = 0
    private const val VOC_MAX = 1000
    private const val PRIMARY_POLLUTANT_WEIGHT = 0.5

    private fun getAverageMeasurement(sensors: List<Sensor>, targetType: SensorType, isDateRangeCalculationModeSet: Boolean): Double?
    {
        val relevantSensors = sensors.filter { it.type == targetType }
        if(isDateRangeCalculationModeSet)
        {
            if (relevantSensors.isEmpty()) return null
            val allMeasurements = relevantSensors.flatMap { it.measurements.map { m -> m.value } }
            return if (allMeasurements.isNotEmpty()) allMeasurements.average() else null
        }
        else
        {
            if (relevantSensors.isEmpty()) return null
            return relevantSensors.map { it.measurements.lastOrNull()?.value ?: 0.0 }.average()
        }
    }

    fun calculateAqi(sensors: List<Sensor>, isDateRangeCalculationModeSet: Boolean): Double {
        if (sensors.isEmpty()) {
            throw IllegalArgumentException("Cannot calculate AQI with an empty list of sensors.")
        }

        val avgCO = getAverageMeasurement(sensors, SensorType.CO, isDateRangeCalculationModeSet)
        val avgVOC = getAverageMeasurement(sensors, SensorType.VOC, isDateRangeCalculationModeSet)
        val avgCO2 = getAverageMeasurement(sensors, SensorType.CO2, isDateRangeCalculationModeSet)

        val coIndex = avgCO?.let { calculateCOIndex(it) }
        val vocIndex = avgVOC?.let { calculateVOCIndex(it) }
        val co2Index = avgCO2?.let { calculateCO2Index(it) }

        return when {
            coIndex != null && vocIndex == null && co2Index == null -> coIndex

            coIndex == null && (vocIndex != null || co2Index != null) -> {
                val primary = vocIndex ?: 0.0
                val secondary = co2Index ?: 0.0

                val vocWeight = if (vocIndex != null && co2Index != null) PRIMARY_POLLUTANT_WEIGHT else if (vocIndex != null) 1.0 else 0.0
                val co2Weight = if (vocIndex != null && co2Index != null) (1 - PRIMARY_POLLUTANT_WEIGHT) else if (co2Index != null) 1.0 else 0.0

                vocWeight * primary + co2Weight * secondary
            }

            coIndex != null && (vocIndex != null || co2Index != null) -> {
                val nonCOIndex: Double = when {
                    vocIndex != null && co2Index != null -> (vocIndex + co2Index)
                    vocIndex != null -> vocIndex
                    co2Index != null -> co2Index
                    else -> 0.0
                }
                PRIMARY_POLLUTANT_WEIGHT * coIndex + (1 - PRIMARY_POLLUTANT_WEIGHT) * nonCOIndex
            }
            else -> throw IllegalStateException("Cannot calculate AQI with the provided sensor data. Insufficient sensor types (CO, VOC, CO2).")
        }
    }

    private fun calculateCOIndex(value: Double): Double {
        return ((value-CO_MIN)/(CO_MAX-CO_MIN))*300
    }
    private fun calculateCO2Index(value: Double): Double {
        return ((value-CO2_MIN)/(CO2_MAX-CO2_MIN))*300
    }
    private fun calculateVOCIndex(value: Double): Double {
        return ((value-VOC_MIN)/(VOC_MAX-VOC_MIN))*300
    }

    enum class AqiRisk{
        GOOD,
        MODERATE,
        UNHEALTHY,
    }

    object AqiRiskWarning{
        const val GOOD = "Air quality is satisfactory, and air pollution poses little or no risk."
        const val MODERATE = "Members of sensitive groups may experience health effects and should limit or avoid outdoor activities."
        const val UNHEALTHY = "Health alert! The risk of health effects is increased and everyone should avoid all outdoor activities."
    }

    fun checkAqiRisk(aqi: Double): AqiRisk{
        return when(aqi){
            in 0.0..50.0 -> AqiRisk.GOOD
            in 51.0..150.0 -> AqiRisk.MODERATE
            else -> AqiRisk.UNHEALTHY
        }
    }
}

data class MeasurementTimestamp(
    val value: Double = 0.0,
    val time: Instant = Instant.fromEpochMilliseconds(0)
)

class RegistrationData(
    val name: String? = null,
    val email: String,
    val password: String,
)
{
    fun isNameValid(): String
    {
        return if(name!!.isEmpty()) {
            "Name is required"
        } else if (name.length<4) {
            "Name must be at least 4 characters long"
        } else {
            ""
        }
    }

    fun isEmailValid(): String
    {
        return if(email.isEmpty()) {
            "Email is required"
        } else {
            ""
        }
    }

    fun isPasswordValid(): List<String>
    {
        val validationMessages = mutableListOf<String>()

        if(password.isEmpty())
        {
            validationMessages.add("Password is required")
        }
        else if (password.length < 8) {
            validationMessages.add("Password must be at least 8 characters")
        }
        if (!password.any { it.isDigit() }) {
            validationMessages.add("Password must contain at least 1 number")
        }
        if (!password.any { it.isUpperCase() }) {
            validationMessages.add("Password must contain at least 1 uppercase letter")
        }
        if (!password.any { it.isLowerCase() }) {
            validationMessages.add("Password must contain at least 1 lowercase letter")
        }
        if (!password.any { !it.isLetterOrDigit() }) {
            validationMessages.add("Password must contain at least 1 special character")
        }

        return validationMessages
    }

    companion object{

        fun validateRegistrationData(data: RegistrationData): (List<String>)
        {
            val errors = mutableListOf<String>()

            if(data.isNameValid()!= "")
            {
                errors.add(data.isNameValid())
            }
            if(data.isEmailValid()!= "")
            {
                errors.add(data.isEmailValid())
            }
            if(data.isPasswordValid().isNotEmpty())
            {
                errors.addAll(data.isPasswordValid())
            }
            return errors
        }

        fun validateLoginData(data: RegistrationData): (List<String>)
        {
            val errors = mutableListOf<String>()
            if(data.isEmailValid()!= "")
            {
                errors.add(data.isEmailValid())
            }
            if(data.isPasswordValid().isNotEmpty())
            {
                errors.addAll(data.isPasswordValid())
            }
            return errors
        }
    }
}


fun hourMinFormater(value: Int):String {
    return if (value < 10) {
        "0$value"
    } else {
        value.toString()
    }
}