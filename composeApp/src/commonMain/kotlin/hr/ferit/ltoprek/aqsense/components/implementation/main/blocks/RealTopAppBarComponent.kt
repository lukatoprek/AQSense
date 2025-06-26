package hr.ferit.ltoprek.aqsense.components.implementation.main.blocks

import com.arkivanov.decompose.ComponentContext
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.TopAppBarComponent

class RealTopAppBarComponent(
    componentContext: ComponentContext,
    val onClickedFeedback: () -> Unit,
) :
    ComponentContext by componentContext, TopAppBarComponent
{
    override fun onClicked() {
        onClickedFeedback()
    }
}