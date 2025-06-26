package hr.ferit.ltoprek.aqsense.components.inteface.main.blocks

import androidx.compose.ui.graphics.Color
import io.github.koalaplot.core.xygraph.Point
import kotlinx.datetime.Instant

interface GraphComponent
{
    fun getSensorTimes() : List<Instant>
    fun getSensorValueRange() : ClosedFloatingPointRange<Double>
    fun getMeasurementPointList() : List<Point<Instant, Double>>
    fun getColor() : Color
    fun getUnitOfMeasurements() : String
}