package hr.ferit.ltoprek.aqsense.components.inteface.main.blocks

import hr.ferit.ltoprek.aqsense.utilities.NavItem

interface BottomNavBarComponent
{
    fun onNavItemClicked(item: NavItem)

    fun getCurrentScreen(): NavItem
}