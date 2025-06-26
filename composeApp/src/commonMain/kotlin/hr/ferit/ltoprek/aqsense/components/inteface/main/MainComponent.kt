package hr.ferit.ltoprek.aqsense.components.inteface.main

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow

interface MainComponent
{
    val childStack: StateFlow<ChildStack<*, Child>>

    sealed interface Child
    {
        class SensorScreen(val component: SensorScreenComponent) : Child
        class Details(val component: SensorDetailsComponent) : Child
        class ProfileScreen(val component: ProfileScreenComponent) : Child
        class OverviewMap(val component: OverviewMapComponent) : Child
    }
}