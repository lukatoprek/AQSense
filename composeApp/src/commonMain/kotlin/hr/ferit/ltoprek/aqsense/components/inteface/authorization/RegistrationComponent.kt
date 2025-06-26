package hr.ferit.ltoprek.aqsense.components.inteface.authorization

import kotlinx.coroutines.flow.StateFlow

interface RegistrationComponent : LoginComponent
{
    val name: StateFlow<String>
    val passwordConfirmation: StateFlow<String>

    fun onNameChanged(name: String)
    fun onPasswordConfirmationChanged(passwordConfirmation: String)
}