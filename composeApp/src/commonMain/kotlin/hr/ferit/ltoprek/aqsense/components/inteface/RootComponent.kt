package hr.ferit.ltoprek.aqsense.components.inteface

import com.arkivanov.decompose.router.stack.ChildStack
import hr.ferit.ltoprek.aqsense.components.inteface.authorization.AuthorizationComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.MainComponent
import kotlinx.coroutines.flow.StateFlow

interface RootComponent
{
    val childStack: StateFlow<ChildStack<*, Child>>

    sealed interface Child {
        class Authorization(val component: AuthorizationComponent) : Child
        class Main(val component: MainComponent) : Child
    }
}