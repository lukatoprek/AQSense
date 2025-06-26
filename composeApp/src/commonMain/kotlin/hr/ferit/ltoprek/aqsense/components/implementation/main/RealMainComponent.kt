package hr.ferit.ltoprek.aqsense.components.implementation.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import hr.ferit.ltoprek.aqsense.components.inteface.main.MainComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.SensorDetailsComponent
import hr.ferit.ltoprek.aqsense.models.AuthorizationRepository
import hr.ferit.ltoprek.aqsense.models.SensorRepository
import hr.ferit.ltoprek.aqsense.models.User
import hr.ferit.ltoprek.aqsense.utilities.toStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

class RealMainComponent(
    componentContext: ComponentContext,
    private val currentUser: StateFlow<User?>,
    private val authorizationRepository: AuthorizationRepository,
    private val sensorRepository: SensorRepository,
    private val onLogout: () -> Unit,
) : ComponentContext by componentContext, MainComponent
{
    private val navigation = StackNavigation<ChildConfig>()
    override val childStack: StateFlow<ChildStack<*, MainComponent.Child>> = childStack(
        source = navigation,
        initialConfiguration = ChildConfig.SensorScreen,
        serializer = ChildConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): MainComponent.Child = when (config) {
        is ChildConfig.SensorScreen -> {
            MainComponent.Child.SensorScreen(
                RealSensorScreenComponent(
                    componentContext,
                    sensorRepository,
                    onSensorClickedFeedback = { sensorId ->
                        navigation.pushNew(ChildConfig.DetailsScreen(sensorId))
                    },
                    onProfileClicked = { navigation.bringToFront(ChildConfig.ProfileScreen) },
                    onOverviewMapClicked = { navigation.bringToFront(ChildConfig.OverviewMap) }
                )
            )
        }

        is ChildConfig.DetailsScreen -> {
            MainComponent.Child.Details(detailsScreen(componentContext,config))
        }

        is ChildConfig.ProfileScreen -> {
            MainComponent.Child.ProfileScreen(
                RealProfileScreenComponent(
                    componentContext = componentContext,
                    currentUser = currentUser.value!!,
                    authorizationRepository = authorizationRepository,
                    onLogout = onLogout,
                    onHomeClicked = { navigation.bringToFront(ChildConfig.SensorScreen) },
                    onOverviewMapClicked = { navigation.bringToFront(ChildConfig.OverviewMap) },
                    onAccountDeleted = onLogout
                )
            )
        }

        is ChildConfig.OverviewMap -> {
            MainComponent.Child.OverviewMap(
                RealOverviewMapComponent(
                    componentContext = componentContext,
                    sensorRepository = sensorRepository,
                    onHomeClicked = { navigation.bringToFront(ChildConfig.SensorScreen) },
                    onProfileClicked = { navigation.bringToFront(ChildConfig.ProfileScreen) }
                )
            )
        }
    }

    private fun detailsScreen(
        componentContext: ComponentContext,
        config: ChildConfig.DetailsScreen
    ) : SensorDetailsComponent = RealSensorDetailsComponent(
            componentContext = componentContext,
            sensorId = config.sensorId,
            sensorRepository = sensorRepository,
            onExitFeedback = { navigation.pop() }
        )

    @Serializable
    private sealed interface ChildConfig
    {
        @Serializable
        data object SensorScreen : ChildConfig

        @Serializable
        data class DetailsScreen(val sensorId: String) : ChildConfig

        @Serializable
        data object ProfileScreen : ChildConfig

        @Serializable
        data object OverviewMap : ChildConfig
    }
}