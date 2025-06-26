package hr.ferit.ltoprek.aqsense.components.inteface.main.blocks

import kotlinx.coroutines.flow.StateFlow

interface DeleteAccountDialogComponent
{
    val inProgress : StateFlow<Boolean>
    val globalError : StateFlow<Exception?>

    fun confirmAccountDeletion()

    fun onResetGlobalError()

    fun onCancelClicked()
}