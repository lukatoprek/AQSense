package hr.ferit.ltoprek.aqsense.components.inteface.authorization

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow

interface AuthorizationComponent
{
    val childStack: StateFlow<ChildStack<*, Child>>

    sealed interface Child
    {
        class Login(val component: LoginComponent) : Child
        class Registration (val component: RegistrationComponent) : Child
    }
}