package hr.ferit.ltoprek.aqsense.components.inteface.main

import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.BottomNavBarComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.ChangePasswordDialogComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.DeleteAccountDialogComponent
import hr.ferit.ltoprek.aqsense.models.User
import kotlinx.coroutines.flow.StateFlow

interface ProfileScreenComponent
{
    val user: User?

    val inProgress: StateFlow<Boolean>

    val globalError: StateFlow<Exception?>

    val isChangePasswordDialogVisible: StateFlow<Boolean>

    val isDeleteAccountDialogVisible: StateFlow<Boolean>

    val bottomNavBarComponent: BottomNavBarComponent

    val changePasswordDialogComponent: ChangePasswordDialogComponent

    val deleteAccountDialogComponent: DeleteAccountDialogComponent

    fun onAuthorizationFailure(exception: Exception)

    fun onChangePasswordClicked()

    fun onDeleteAccountClicked()

    fun onResetGlobalError()

    fun onLogoutClicked()
}