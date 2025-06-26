package hr.ferit.ltoprek.aqsense.components.implementation.authorization

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import hr.ferit.ltoprek.aqsense.components.inteface.authorization.LoginComponent
import hr.ferit.ltoprek.aqsense.models.AuthorizationRepository
import hr.ferit.ltoprek.aqsense.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RealLoginComponent(
    componentContext: ComponentContext,
    val onAuthorizationSuccess: (User?) -> Unit,
    val onRegistrationSwitch: () -> Unit,
    private val authorizationRepository: AuthorizationRepository
) : ComponentContext by componentContext, LoginComponent
{
    override val email = MutableStateFlow("")
    override val password = MutableStateFlow("")
    override val inProgress = MutableStateFlow(false)
    override val globalError = MutableStateFlow<Exception?>(null)

    private val componentScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    init{
        lifecycle.doOnDestroy{
            componentScope.cancel()
        }
    }

    override fun onEmailChanged(email: String){ this.email.value = email }

    override fun onPasswordChanged(password: String){ this.password.value = password }

    override fun onAuthorizationFailure(exception: Exception)
    {
        inProgress.value = false
        globalError.value = exception
    }

    override fun onResetGlobalError(){globalError.value = null}

    override fun onLoginClick()
    {
        if(inProgress.value) return

        inProgress.value = true
        onResetGlobalError()

        componentScope.launch{
            try {
            authorizationRepository.login(
                email.value,
                password.value,
            )
            } catch(e: Exception){
                onAuthorizationFailure(e)
                return@launch
            }

            val user =
                try{
                    authorizationRepository.getCurrentUser()?:throw Exception("User is null")
                } catch(e: Exception){
                    onAuthorizationFailure(e)
                    return@launch
                }
            finally{
                inProgress.value = false
            }
            onAuthorizationSuccess(user)
        }
    }

    override fun onRegistrationClick() { onRegistrationSwitch() }
}