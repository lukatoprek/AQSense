package hr.ferit.ltoprek.aqsense.components.implementation.main.blocks

import com.arkivanov.decompose.ComponentContext
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.FilteringChipsComponent
import hr.ferit.ltoprek.aqsense.models.SensorType
import kotlinx.coroutines.flow.MutableStateFlow

class RealFilteringChipsComponent(
    componentContext: ComponentContext,
    val onCategorySelectedFeedback: (SensorType?) -> Unit
) : ComponentContext by componentContext, FilteringChipsComponent
{
    override val selectedCategory = MutableStateFlow<SensorType?>(null)

    override fun onCategorySelected(category: SensorType?) {
        selectedCategory.value = category
        onCategorySelectedFeedback(category)
    }

}