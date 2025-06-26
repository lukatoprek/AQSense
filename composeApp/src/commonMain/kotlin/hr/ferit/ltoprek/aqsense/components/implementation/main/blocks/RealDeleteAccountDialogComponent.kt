package hr.ferit.ltoprek.aqsense.components.implementation.main.blocks

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.mmk.kmpnotifier.notification.NotifierManager
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.DeleteAccountDialogComponent
import hr.ferit.ltoprek.aqsense.models.AuthorizationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class RealDeleteAccountDialogComponent(
    componentContext: ComponentContext,
    private val authorizationRepository: AuthorizationRepository,
    val onAccountDeletedFeedback: () -> Unit,
    val onCancelFeedback: () -> Unit
) : ComponentContext by componentContext, DeleteAccountDialogComponent
{
    override val inProgress = MutableStateFlow(false)
    override val globalError = MutableStateFlow<Exception?>(null)

    private val componentScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    init{
        lifecycle.doOnDestroy{
            componentScope.cancel()
        }
    }

    override fun onResetGlobalError(){globalError.value = null}

    override fun confirmAccountDeletion()
    {
        if(inProgress.value) return

        inProgress.value = true
        onResetGlobalError()

        componentScope.launch {
            try {
                authorizationRepository.deleteUser()
                val notifier = NotifierManager.getLocalNotifier()
                notifier.notify {
                    id = Random.nextInt(0,Int.MAX_VALUE)
                    title = "Account deleted"
                    body = "Your account has been deleted"
                }
                onAccountDeletedFeedback()
            } catch (e: Exception){
                globalError.value = e
                return@launch
            } finally {
                inProgress.value = false
            }
        }
    }

    override fun onCancelClicked() {
        onCancelFeedback()
    }
}