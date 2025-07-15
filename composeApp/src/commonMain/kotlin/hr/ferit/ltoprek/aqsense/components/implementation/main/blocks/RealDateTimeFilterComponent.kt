package hr.ferit.ltoprek.aqsense.components.implementation.main.blocks

import com.arkivanov.decompose.ComponentContext
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.DateTimeFilterComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.DateTimeFilterComponent.DateRangePreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class RealDateTimeFilterComponent(
    componentContext: ComponentContext,
    val onDateTimeSelectedFeedback: (DateRangePreset?) -> Unit,
    val setGlobalError: (e: Exception) -> Unit,
) : ComponentContext by componentContext, DateTimeFilterComponent
{
    override val selectedDateTime = MutableStateFlow<DateRangePreset?>(null)
    override val startDate = MutableStateFlow(LocalDateTime.parse("2000-01-01T00:00:00").toInstant(TimeZone.currentSystemDefault()))
    override val endDate = MutableStateFlow(Clock.System.now())

    override fun onDateTimeSelected(preset: DateRangePreset?) {
        selectedDateTime.value = preset
        onDateTimeSelectedFeedback(preset)
    }

    override fun onStartDateChanged(date: Instant) { startDate.value = date }

    override fun onEndDateChanged(date: Instant) { endDate.value = date }

    override fun onErrorFeedback(exception: Exception) { setGlobalError(exception) }
}