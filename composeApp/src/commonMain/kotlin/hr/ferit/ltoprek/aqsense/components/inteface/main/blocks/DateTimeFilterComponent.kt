package hr.ferit.ltoprek.aqsense.components.inteface.main.blocks

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant

interface DateTimeFilterComponent
{
    sealed interface DateRangePreset
    {
        data object LastDay : DateRangePreset
        data object LastThreeDays : DateRangePreset
        data object LastWeek : DateRangePreset
        data object LastMonth : DateRangePreset
        data class Custom(val startDate: Instant, val endDate: Instant) : DateRangePreset{
            init {
                require(startDate < endDate) { "Start date must be before end date" }
            }

        }
    }
    
    val selectedDateTime: StateFlow<DateRangePreset?>

    val startDate: StateFlow<Instant>

    val endDate: StateFlow<Instant>
    
    fun onDateTimeSelected(preset: DateRangePreset?)

    fun onStartDateChanged(date: Instant)

    fun onEndDateChanged(date: Instant)

    fun onErrorFeedback(exception: Exception)
}