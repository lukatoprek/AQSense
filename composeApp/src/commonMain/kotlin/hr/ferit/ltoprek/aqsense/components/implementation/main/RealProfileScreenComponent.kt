package hr.ferit.ltoprek.aqsense.components.implementation.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import hr.ferit.ltoprek.aqsense.components.implementation.main.blocks.RealBottomNavBarComponent
import hr.ferit.ltoprek.aqsense.components.implementation.main.blocks.RealChangePasswordDialogComponent
import hr.ferit.ltoprek.aqsense.components.implementation.main.blocks.RealDeleteAccountDialogComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.ProfileScreenComponent
import hr.ferit.ltoprek.aqsense.models.AuthorizationRepository
import hr.ferit.ltoprek.aqsense.models.User
import hr.ferit.ltoprek.aqsense.utilities.NavItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RealProfileScreenComponent(
    componentContext: ComponentContext,
    currentUser: User,
    private val authorizationRepository: AuthorizationRepository,
    val onLogout: () -> Unit,
    val onAccountDeleted: () -> Unit,
    onHomeClicked: () -> Unit,
    onOverviewMapClicked: () -> Unit
) : ComponentContext by componentContext, ProfileScreenComponent
{
    override val user = currentUser
    override val inProgress = MutableStateFlow(false)
    override val globalError = MutableStateFlow<Exception?>(null)
    override val isChangePasswordDialogVisible = MutableStateFlow(false)
    override val isDeleteAccountDialogVisible = MutableStateFlow(false)

    private val componentScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    init{
        lifecycle.doOnDestroy{
            componentScope.cancel()
        }
    }

    override val bottomNavBarComponent = RealBottomNavBarComponent(
        componentContext = childContext(key = "bottomNavBar"),
        onHomeClicked = onHomeClicked,
        onProfileClicked = {},
        onOverviewMapClicked = onOverviewMapClicked,
        currentScreen = NavItem.PROFILE
    )

    override val changePasswordDialogComponent = RealChangePasswordDialogComponent(
        componentContext = childContext(key = "changePasswordDialog"),
        authorizationRepository = authorizationRepository,
        onPasswordChangedFeedback = { isChangePasswordDialogVisible.value = false},
        onCancelFeedback = { isChangePasswordDialogVisible.value = false}
    )

    override val deleteAccountDialogComponent = RealDeleteAccountDialogComponent(
        componentContext = childContext(key = "deleteAccountDialog"),
        authorizationRepository = authorizationRepository,
        onAccountDeletedFeedback = {
            isDeleteAccountDialogVisible.value = false
            onAccountDeleted()
        },
        onCancelFeedback = { isDeleteAccountDialogVisible.value = false}
    )

    override fun onAuthorizationFailure(exception: Exception)
    {
        inProgress.value = false
        globalError.value = exception
    }

    override fun onChangePasswordClicked() { isChangePasswordDialogVisible.value = true }

    override fun onDeleteAccountClicked() { isDeleteAccountDialogVisible.value = true }

    override fun onResetGlobalError(){ globalError.value = null }

    override fun onLogoutClicked()
    {
        if(inProgress.value) return

        inProgress.value = true
        onResetGlobalError()

        componentScope.launch {
            authorizationRepository.logout()
        }
        inProgress.value = false
        onLogout()
    }

}