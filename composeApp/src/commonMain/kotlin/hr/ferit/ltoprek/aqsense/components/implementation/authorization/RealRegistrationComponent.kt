package hr.ferit.ltoprek.aqsense.components.implementation.authorization

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import hr.ferit.ltoprek.aqsense.components.inteface.authorization.RegistrationComponent
import hr.ferit.ltoprek.aqsense.models.AuthorizationRepository
import hr.ferit.ltoprek.aqsense.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RealRegistrationComponent(
    componentContext: ComponentContext,
    val onAuthorizationSuccess: (User?) -> Unit,
    val onLoginSwitch: () -> Unit,
    private val authorizationRepository: AuthorizationRepository
) : ComponentContext by componentContext, RegistrationComponent
{
    override val name = MutableStateFlow("")
    override val email = MutableStateFlow("")
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

    override fun onNameChanged(name: String) {this.name.value = name}

    override fun onEmailChanged(email: String) {this.email.value = email}

    override fun onPasswordChanged(password: String) {this.password.value = password}

    override fun onPasswordConfirmationChanged(passwordConfirmation: String) {this.passwordConfirmation.value = passwordConfirmation}

    override fun onAuthorizationFailure(exception: Exception){
        inProgress.value = false
        globalError.value = exception
    }

    override fun onResetGlobalError(){globalError.value = null}

    override fun onRegistrationClick()
    {
        if(inProgress.value) return

        inProgress.value = true
        onResetGlobalError()

        componentScope.launch {
            try{
                authorizationRepository.register(
                    email.value,
                    password.value,
                    name.value,
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

    override fun onLoginClick() {
        onLoginSwitch()
    }
}