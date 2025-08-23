package hr.ferit.ltoprek.aqsense

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import hr.ferit.ltoprek.aqsense.components.implementation.RealRootComponent
import hr.ferit.ltoprek.aqsense.models.SharedAuthorizationRepository
import hr.ferit.ltoprek.aqsense.models.SharedSensorRepository

val lifecycle = LifecycleRegistry()
val authorizationRepository = SharedAuthorizationRepository()
val sensorRepository = SharedSensorRepository()

fun MainViewController() = ComposeUIViewController {
    val root = remember {
        RealRootComponent(
            componentContext = DefaultComponentContext(lifecycle),
            authorizationRepository = authorizationRepository,
            sensorRepository = sensorRepository,
        )
    }
    App(root)
}