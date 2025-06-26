package hr.ferit.ltoprek.aqsense.components.implementation.main.blocks

import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ComponentContext
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.GraphComponent
import io.github.koalaplot.core.xygraph.Point
import kotlinx.datetime.Instant

class RealGraphComponent(
    private val sensorTimes: List<Instant>,
    private val sensorValueRange: ClosedFloatingPointRange<Double>,
    private val measurementPointList: List<Point<Instant, Double>>,
    private val unitOfMeasurements: String,
    private val color: Color,
    componentContext: ComponentContext,
) : ComponentContext by componentContext, GraphComponent
{
    override fun getSensorTimes() : List<Instant> {
        return sensorTimes
    }

    override fun getSensorValueRange() : ClosedFloatingPointRange<Double> {
        return sensorValueRange
    }

    override fun getMeasurementPointList(): List<Point<Instant, Double>> {
        return measurementPointList
    }

    override fun getColor(): Color {
        return color
    }

    override fun getUnitOfMeasurements(): String {
        return unitOfMeasurements
    }
}