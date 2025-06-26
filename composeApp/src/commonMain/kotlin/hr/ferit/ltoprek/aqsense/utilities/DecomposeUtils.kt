package hr.ferit.ltoprek.aqsense.utilities

import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import hr.ferit.ltoprek.aqsense.models.Sensor
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow

fun <T : Any> Value<T>.toStateFlow(lifecycle: Lifecycle): StateFlow<T> {
    val state = MutableStateFlow(this.value)

    if (lifecycle.state != Lifecycle.State.DESTROYED) {
        val observer = { value: T -> state.value = value }
        val cancellation = subscribe(observer)
        lifecycle.doOnDestroy {
            cancellation.cancel()
        }
    }

    return state
}

data class Model(
    val sensors: List<Sensor>
)