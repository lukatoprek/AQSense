package hr.ferit.ltoprek.aqsense.utilities

import dev.gitlive.firebase.firestore.Timestamp
import hr.ferit.ltoprek.aqsense.models.Sensor
import hr.ferit.ltoprek.aqsense.models.SensorType
import kotlinx.datetime.Instant

enum class NavItem { PROFILE, HOME, MAP }

object AqiCalculator {

    private data class Breakpoint(
        val cLow: Double,
        val cHigh: Double,
        val iLow: Double,
        val iHigh: Double
    )

    // Concentration -> AQI breakpoints, per pollutant.
    private val breakpoints: Map<SensorType, List<Breakpoint>> = mapOf(
        // CO (ppm, 8-hour) — EPA
        SensorType.CO to listOf(
            Breakpoint(0.0,   4.4,   0.0,  50.0),
            Breakpoint(4.5,   9.4,  51.0, 100.0),
            Breakpoint(9.5,  12.4, 101.0, 150.0),
            Breakpoint(12.5, 15.4, 151.0, 200.0),
            Breakpoint(15.5, 30.4, 201.0, 300.0),
            Breakpoint(30.5, 50.4, 301.0, 500.0)
        ),
        // CO2 (ppm) — indoor-air guidance, not EPA
        SensorType.CO2 to listOf(
            Breakpoint(   0.0,  600.0,   0.0,  50.0),
            Breakpoint( 601.0, 1000.0,  51.0, 100.0),
            Breakpoint(1001.0, 1500.0, 101.0, 150.0),
            Breakpoint(1501.0, 2000.0, 151.0, 200.0),
            Breakpoint(2001.0, 5000.0, 201.0, 300.0)
        ),
        // VOC — Sensirion VOC Index (1..500, 100 = typical)
        SensorType.VOC to listOf(
            Breakpoint(  0.0, 100.0,   0.0,  50.0),
            Breakpoint(101.0, 200.0,  51.0, 100.0),
            Breakpoint(201.0, 300.0, 101.0, 150.0),
            Breakpoint(301.0, 400.0, 151.0, 200.0),
            Breakpoint(401.0, 500.0, 201.0, 300.0)
        ),
        // NOx — Sensirion NOx Index (1..500, 100 = typical)
        SensorType.NOX to listOf(
            Breakpoint(  0.0, 100.0,   0.0,  50.0),
            Breakpoint(101.0, 200.0,  51.0, 100.0),
            Breakpoint(201.0, 300.0, 101.0, 150.0),
            Breakpoint(301.0, 400.0, 151.0, 200.0),
            Breakpoint(401.0, 500.0, 201.0, 300.0)
        ),
        // PM1 (µg/m³) — no official EPA table; mirrors PM2.5
        SensorType.PM1 to listOf(
            Breakpoint(  0.0,  12.0,   0.0,  50.0),
            Breakpoint( 12.1,  35.4,  51.0, 100.0),
            Breakpoint( 35.5,  55.4, 101.0, 150.0),
            Breakpoint( 55.5, 150.4, 151.0, 200.0),
            Breakpoint(150.5, 250.4, 201.0, 300.0),
            Breakpoint(250.5, 500.4, 301.0, 500.0)
        ),
        // PM2.5 (µg/m³, 24-hour) — EPA
        SensorType.PM2_5 to listOf(
            Breakpoint(  0.0,  12.0,   0.0,  50.0),
            Breakpoint( 12.1,  35.4,  51.0, 100.0),
            Breakpoint( 35.5,  55.4, 101.0, 150.0),
            Breakpoint( 55.5, 150.4, 151.0, 200.0),
            Breakpoint(150.5, 250.4, 201.0, 300.0),
            Breakpoint(250.5, 500.4, 301.0, 500.0)
        ),
        // PM10 (µg/m³, 24-hour) — EPA
        SensorType.PM10 to listOf(
            Breakpoint(  0.0,  54.0,   0.0,  50.0),
            Breakpoint( 55.0, 154.0,  51.0, 100.0),
            Breakpoint(155.0, 254.0, 101.0, 150.0),
            Breakpoint(255.0, 354.0, 151.0, 200.0),
            Breakpoint(355.0, 424.0, 201.0, 300.0),
            Breakpoint(425.0, 604.0, 301.0, 500.0)
        )
    )

    val aqiPollutants: Set<SensorType> = breakpoints.keys

    private fun getAverageMeasurement(
        sensors: List<Sensor>,
        targetType: SensorType,
        isDateRangeCalculationModeSet: Boolean
    ): Double? {
        val relevant = sensors.filter { it.type == targetType }
        if (relevant.isEmpty()) return null

        return if (isDateRangeCalculationModeSet) {
            val all = relevant.flatMap { s -> s.measurements.map { it.value } }
            if (all.isNotEmpty()) all.average() else null
        } else {
            val latest = relevant.mapNotNull { it.measurements.lastOrNull()?.value }
            if (latest.isNotEmpty()) latest.average() else null
        }
    }

    private fun calculateSubIndex(type: SensorType, value: Double): Double? {
        val bps = breakpoints[type] ?: return null
        if (value <= 0.0) return 0.0

        val top = bps.last()
        if (value > top.cHigh) return top.iHigh

        val bp = bps.firstOrNull { value in it.cLow..it.cHigh } ?: return null
        return ((bp.iHigh - bp.iLow) / (bp.cHigh - bp.cLow)) * (value - bp.cLow) + bp.iLow
    }

    fun calculatePollutantAqi(
        sensors: List<Sensor>,
        type: SensorType,
        isDateRangeCalculationModeSet: Boolean
    ): Double? {
        val avg = getAverageMeasurement(sensors, type, isDateRangeCalculationModeSet) ?: return null
        return calculateSubIndex(type, avg)
    }

    fun calculateAllSubIndices(
        sensors: List<Sensor>,
        isDateRangeCalculationModeSet: Boolean
    ): Map<SensorType, Double> =
        aqiPollutants.mapNotNull { type ->
            calculatePollutantAqi(sensors, type, isDateRangeCalculationModeSet)?.let { type to it }
        }.toMap()

    fun calculateAqi(sensors: List<Sensor>, isDateRangeCalculationModeSet: Boolean): Double {
        if (sensors.isEmpty()) {
            throw IllegalArgumentException("Cannot calculate AQI with an empty list of sensors.")
        }
        val subs = calculateAllSubIndices(sensors, isDateRangeCalculationModeSet)
        return subs.values.maxOrNull() ?: 0.0
    }

    enum class AqiRisk { GOOD, MODERATE, UNHEALTHY }

    object AqiRiskWarning {
        const val GOOD = "Air quality is satisfactory, and air pollution poses little or no risk."
        const val MODERATE = "Members of sensitive groups may experience health effects and should limit or avoid outdoor activities."
        const val UNHEALTHY = "Health alert! The risk of health effects is increased and everyone should avoid all outdoor activities."
    }

    fun checkAqiRisk(aqi: Double): AqiRisk = when {
        aqi <= 50.0  -> AqiRisk.GOOD
        aqi <= 150.0 -> AqiRisk.MODERATE
        else         -> AqiRisk.UNHEALTHY
    }
}

fun isPollutant(type: SensorType?): Boolean = AqiCalculator.aqiPollutants.contains(type)

data class MeasurementTimestamp(
    val value: Double = 0.0,
    val time: Instant = Instant.fromEpochMilliseconds(0)
)

object TimestampConverter
{
    fun Timestamp.toKotlinInstant(): Instant = this.let {
        Instant.fromEpochSeconds(it.seconds, it.nanoseconds.toLong())
    }
}

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