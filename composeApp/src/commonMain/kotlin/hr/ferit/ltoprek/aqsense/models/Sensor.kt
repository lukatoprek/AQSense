package hr.ferit.ltoprek.aqsense.models

import dev.gitlive.firebase.firestore.GeoPoint
import hr.ferit.ltoprek.aqsense.utilities.MeasurementTimestamp
import io.github.koalaplot.core.xygraph.Point
import kotlinx.datetime.Instant

enum class SensorType(rawValue:Long)
{
    UNKNOWN(0),
    TEMPERATURE(1),
    MOISTURE(2),
    CO(3),
    CO2(4),
    VOC(5);

    val dbValue: Long = if(rawValue in 0..5) rawValue else 0

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
            CO to "Orange"
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
        return "${measurements.last().value} $unitOfMeasurement"
    }

    fun getSensorTimes(): List<Instant>
    {
        val times = measurements.map {it.time}
        return times
    }

    fun getSensorValues(): List<Double>
    {
        val values = measurements.map {it.value}
        return values
    }

    fun getSensorValueRange(): ClosedFloatingPointRange<Double> {
        val values = getSensorValues()
        val min = values.minOrNull() ?: 0.0
        val max = values.maxOrNull() ?: 0.0
        return min..max
    }

    fun getMeasurementPointList(): List<Point<Instant, Double>>
    {
        val pointList = measurements.map {Point(it.time,it.value)}
        return pointList
    }
}