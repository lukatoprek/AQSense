package hr.ferit.ltoprek.aqsense.components.implementation.authorization

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import hr.ferit.ltoprek.aqsense.components.inteface.authorization.AuthorizationComponent
import hr.ferit.ltoprek.aqsense.models.AuthorizationRepository
import hr.ferit.ltoprek.aqsense.models.User
import hr.ferit.ltoprek.aqsense.utilities.toStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

class RealAuthorizationComponent(
    componentContext: ComponentContext,
    private val authorizationRepository: AuthorizationRepository,
    val onAuthorizationFinished: (User?) -> Unit
    ) : ComponentContext by componentContext, AuthorizationComponent
{
    private val navigation = StackNavigation<ChildConfig>()

    override val childStack: StateFlow<ChildStack<*, AuthorizationComponent.Child>> = childStack(
        source = navigation,
        initialConfiguration = ChildConfig.Login,
        serializer = ChildConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): AuthorizationComponent.Child = when(config)
    {
        is ChildConfig.Login -> {
            AuthorizationComponent.Child.Login(
                RealLoginComponent(
                    componentContext,
                    onAuthorizationSuccess = { user -> onAuthorizationFinished(user) },
                    onRegistrationSwitch = { navigation.replaceAll(ChildConfig.Registration) },
                    authorizationRepository = this.authorizationRepository
                )
            )
        }

        is ChildConfig.Registration ->{
            AuthorizationComponent.Child.Registration(
                RealRegistrationComponent(
                    componentContext,
                    onAuthorizationSuccess = { user -> onAuthorizationFinished(user) },
                    onLoginSwitch = { navigation.replaceAll(ChildConfig.Login) },
                    authorizationRepository = this.authorizationRepository
                )
            )
        }
    }

    @Serializable
    private sealed interface ChildConfig
    {
        @Serializable
        data object Registration: ChildConfig

        @Serializable
        data object Login: ChildConfig
    }
}