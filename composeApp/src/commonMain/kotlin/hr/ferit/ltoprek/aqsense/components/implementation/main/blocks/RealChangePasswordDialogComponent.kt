package hr.ferit.ltoprek.aqsense.components.implementation.main.blocks

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.mmk.kmpnotifier.notification.NotifierManager
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.ChangePasswordDialogComponent
import hr.ferit.ltoprek.aqsense.models.AuthorizationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class RealChangePasswordDialogComponent(
    componentContext: ComponentContext,
    private val authorizationRepository: AuthorizationRepository,
    val onPasswordChangedFeedback: () -> Unit,
    val onCancelFeedback: () -> Unit
) : ComponentContext by componentContext, ChangePasswordDialogComponent
{
    override val password = MutableStateFlow("")
    override val passwordConfirmation = MutableStateFlow("")
    override val inProgress = MutableStateFlow(false)
    override val globalError = MutableStateFlow<Exception?>(null)

    private val componentScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    init{
        lifecycle.doOnDestroy{
            componentScope.cancel()
        }
    }

    override fun onPasswordChanged(password: String) {this.password.value = password}

    override fun onPasswordConfirmationChanged(passwordConfirmation: String) {this.passwordConfirmation.value = passwordConfirmation}

    override fun onResetGlobalError(){globalError.value = null}

    override fun getUserName(): String{
        var name = "Sample Name"
        componentScope.launch {
            name = authorizationRepository.getCurrentUser()?.name?:"Sample Name"
        }
        return name
    }

    override fun getUserEmail(): String{
        var name = "sample@aqsense.com"
        componentScope.launch {
            name = authorizationRepository.getCurrentUser()?.email?:"sample@aqsense.com"
        }
        return name
    }

    override fun onConfirmChangePasswordClicked()
    {
        if(inProgress.value) return

        inProgress.value = true
        onResetGlobalError()

        componentScope.launch {
            try{
                authorizationRepository.changePassword(password.value)
                val notifier = NotifierManager.getLocalNotifier()
                notifier.notify {
                    id = Random.nextInt(0,Int.MAX_VALUE)
                    title = "Password changed"
                    body = "Password has been changed"
                }
                onPasswordChangedFeedback()
            } catch(e: Exception) {
                globalError.value = e
                return@launch
            } finally{
                inProgress.value = false
            }
        }
    }

    override fun onCancelClicked() {
        onCancelFeedback()
    }
}