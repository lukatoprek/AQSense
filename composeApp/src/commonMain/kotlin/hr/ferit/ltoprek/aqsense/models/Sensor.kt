package hr.ferit.ltoprek.aqsense.models

import dev.gitlive.firebase.firestore.GeoPoint
import hr.ferit.ltoprek.aqsense.utilities.MeasurementTimestamp
import io.github.koalaplot.core.xygraph.Point
import io.github.koalaplot.core.xygraph.autoScaleRange
import kotlinx.datetime.Instant
import net.sergeych.sprintf.format

enum class SensorType(rawValue:Long)
{
    UNKNOWN(0),
    TEMPERATURE(1),
    MOISTURE(2),
    CO(3),
    CO2(4),
    VOC(5),
    NOX(6),
    PM1(7),
    PM2_5(8),
    PM10(9);

    val dbValue: Long = if(rawValue in 0..9) rawValue else 0

    companion object
    {
        private val values = entries.toTypedArray()
        fun fromDbValue(dbValue: Long?, default: SensorType = UNKNOWN): SensorType{
           return values.firstOrNull{ it.dbValue == dbValue } ?: default
        }

        val sensorColors = mapOf(
            TEMPERATURE to "Red",
            MOISTURE to "Blue",
            CO2 to "Yellow",
            VOC to "Green",
            CO to "Orange",
            NOX to "Purple",
            PM1 to "Wheat",
            PM2_5 to "Tan",
            PM10 to "Brown"
        )
    }
}

data class Sensor(
    var id: String = "",
    var name: String = "",
    var unitOfMeasurement: String = "",
    var measurements: MutableList<MeasurementTimestamp> = mutableListOf(),
    val ownerId: String = "",
    var type: SensorType = SensorType.UNKNOWN,
    val coordinates: GeoPoint? = GeoPoint(0.0,0.0)
){
    fun getLatestMeasurement(): String{
        return "%.2f %s".format(measurements.lastOrNull()?.value ?: 0.0, unitOfMeasurement)
    }

    fun getSensorTimes(): List<Instant>
    {
        val times = measurements.map {it.time}
        return times
    }

    fun getSensorValues(): List<Double>
    {
        val values = measurements.map {  it.value }
        return values
    }

    fun getSensorValueRange(): ClosedFloatingPointRange<Double> {
        val values = getSensorValues()
        if (values.isEmpty()) return 0.0..1.0

        if (type == SensorType.NOX) {
            val max = values.max()
            val ceiling = when {
                max <= 50.0  -> 50.0
                max <= 150.0 -> 150.0
                max <= 300.0 -> 300.0
                else         -> 500.0
            }
            return 0.0..ceiling
        }

        val min = values.min()
        val max = values.max()
        val span = max - min

        if (span < 1e-9) {
            val half = if (min == 0.0) 0.5 else maxOf(kotlin.math.abs(min) * 0.5, 0.5)
            return (min - half)..(min + half)
        }

        val scaled = values.autoScaleRange()
        val scaledSpan = scaled.endInclusive - scaled.start
        if (scaledSpan < 1e-9) {
            val pad = maxOf(span * 0.1, 0.5)
            return (min - pad)..(max + pad)
        }
        return scaled
    }

    fun getMeasurementPointList(): List<Point<Instant, Double>>
    {
        val pointList = measurements.map {Point(it.time,it.value)}
        return pointList
    }
}