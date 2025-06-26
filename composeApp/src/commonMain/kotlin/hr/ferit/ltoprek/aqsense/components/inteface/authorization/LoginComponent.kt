package hr.ferit.ltoprek.aqsense.components.inteface.authorization

import kotlinx.coroutines.flow.StateFlow

interface LoginComponent {
    val email: StateFlow<String>
    val password: StateFlow<String>
    val inProgress: StateFlow<Boolean>
    val globalError: StateFlow<Exception?>

    fun onEmailChanged(email: String)

    fun onPasswordChanged(password: String)

    fun onAuthorizationFailure(exception: Exception)

    fun onResetGlobalError()

    fun onLoginClick()

    fun onRegistrationClick()
}