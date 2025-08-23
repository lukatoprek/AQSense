package hr.ferit.ltoprek.aqsense.components.implementation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.essenty.lifecycle.doOnDestroy
import hr.ferit.ltoprek.aqsense.components.implementation.authorization.RealAuthorizationComponent
import hr.ferit.ltoprek.aqsense.components.implementation.main.RealMainComponent
import hr.ferit.ltoprek.aqsense.components.inteface.RootComponent
import hr.ferit.ltoprek.aqsense.models.AuthorizationRepository
import hr.ferit.ltoprek.aqsense.models.SensorRepository
import hr.ferit.ltoprek.aqsense.models.User
import hr.ferit.ltoprek.aqsense.utilities.toStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class RealRootComponent(
    componentContext: ComponentContext,
    private val authorizationRepository: AuthorizationRepository,
    private val sensorRepository: SensorRepository,
) : ComponentContext by componentContext, RootComponent
{
    private val navigation = StackNavigation<ChildConfig>()
    private val _currentUser = MutableStateFlow<User?>(null)
    private val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val componentScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    init{
        lifecycle.doOnDestroy{
            componentScope.cancel()
        }
        componentScope.launch {
            checkCurrentUser()
        }
    }

    private suspend fun checkCurrentUser(){
        val user = authorizationRepository.getCurrentUser()
        if(user!=null)
        {
            _currentUser.value = user
            navigation.replaceAll(ChildConfig.Main)
        } else {
            navigation.replaceAll(ChildConfig.Authorization)
        }
    }

    override val childStack: StateFlow<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        initialConfiguration = ChildConfig.Authorization,
        serializer = ChildConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)


    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): RootComponent.Child = when (config)
    {
        is ChildConfig.Authorization -> {
            RootComponent.Child.Authorization(
                RealAuthorizationComponent(
                    componentContext = componentContext,
                    onAuthorizationFinished = {
                        user -> _currentUser.value = user
                        navigation.replaceAll(ChildConfig.Main)
                    },
                    authorizationRepository = authorizationRepository
                )
            )
        }

        is ChildConfig.Main -> {
            RootComponent.Child.Main(
                RealMainComponent(
                    componentContext = componentContext,
                    currentUser = currentUser,
                    authorizationRepository = authorizationRepository,
                    sensorRepository = sensorRepository,
                    onLogout = {
                        _currentUser.value = null
                        navigation.replaceAll(ChildConfig.Authorization)
                    }
                )
            )
        }
    }

    @Serializable
    private sealed interface ChildConfig
    {
        @Serializable
        data object Authorization : ChildConfig

        @Serializable
        data object Main : ChildConfig
    }
}