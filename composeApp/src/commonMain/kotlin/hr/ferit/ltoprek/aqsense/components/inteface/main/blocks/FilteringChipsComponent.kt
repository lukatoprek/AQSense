package hr.ferit.ltoprek.aqsense.components.inteface.main.blocks

import hr.ferit.ltoprek.aqsense.models.SensorType

import kotlinx.coroutines.flow.StateFlow

interface FilteringChipsComponent
{
    val selectedCategory: StateFlow<SensorType?>

    fun onCategorySelected(category: SensorType?)
}