package hr.ferit.ltoprek.aqsense.components.implementation.main.blocks

import com.arkivanov.decompose.ComponentContext
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.BottomNavBarComponent
import hr.ferit.ltoprek.aqsense.utilities.NavItem

class RealBottomNavBarComponent(
    componentContext: ComponentContext,
    private val onHomeClicked: () -> Unit,
    private val onOverviewMapClicked: () -> Unit,
    private val onProfileClicked: () -> Unit,
    private val currentScreen: NavItem
) : ComponentContext by componentContext, BottomNavBarComponent
{
    override fun onNavItemClicked(item: NavItem){
        when(item){
            NavItem.HOME -> onHomeClicked()
            NavItem.MAP -> onOverviewMapClicked()
            NavItem.PROFILE -> onProfileClicked()
        }
    }

    override fun getCurrentScreen(): NavItem = currentScreen
}