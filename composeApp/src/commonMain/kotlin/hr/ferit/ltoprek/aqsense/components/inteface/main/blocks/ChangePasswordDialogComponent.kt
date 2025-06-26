package hr.ferit.ltoprek.aqsense.components.inteface.main.blocks

import kotlinx.coroutines.flow.StateFlow

interface ChangePasswordDialogComponent
{
    val password : StateFlow<String>
    val passwordConfirmation: StateFlow<String>
    val inProgress : StateFlow<Boolean>
    val globalError : StateFlow<Exception?>

    fun onPasswordChanged(password: String)

    fun onPasswordConfirmationChanged(passwordConfirmation: String)

    fun onConfirmChangePasswordClicked()

    fun onResetGlobalError()

    fun onCancelClicked()

    fun getUserName(): String

    fun getUserEmail(): String
}