package hr.ferit.ltoprek.aqsense.components.inteface.main

import com.arkivanov.decompose.value.Value
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.BottomNavBarComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.CalculateAqiDialogComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.FilteringChipsComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.SensorListComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.TopAppBarComponent
import hr.ferit.ltoprek.aqsense.models.SensorType
import hr.ferit.ltoprek.aqsense.utilities.Model
import kotlinx.coroutines.flow.StateFlow

interface SensorScreenComponent
{
    val sensors: Value<Model>

    val globalError: StateFlow<Exception?>

    val selectedCategory: StateFlow<SensorType?>

    val topAppBarComponent: TopAppBarComponent

    val filteringChipsComponent: FilteringChipsComponent

    val sensorListComponent: SensorListComponent

    val bottomNavBarComponent: BottomNavBarComponent

    val calculateAqiDialogComponent: CalculateAqiDialogComponent

    val inProgress: StateFlow<Boolean>

    val isCalculateAqiDialogVisible: StateFlow<Boolean>

    val calculatedAqi: StateFlow<Double?>

    fun onResetGlobalError()

    fun getSensors()

    fun filterSensors()
}