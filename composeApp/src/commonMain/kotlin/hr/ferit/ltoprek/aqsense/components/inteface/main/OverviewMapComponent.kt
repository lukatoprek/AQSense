package hr.ferit.ltoprek.aqsense.components.inteface.main

import com.arkivanov.decompose.value.Value
import dev.sargunv.maplibrecompose.compose.CameraState
import dev.sargunv.maplibrecompose.core.source.GeoJsonData
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.BottomNavBarComponent
import hr.ferit.ltoprek.aqsense.utilities.Model
import kotlinx.coroutines.flow.MutableStateFlow

interface OverviewMapComponent
{
    val sensors: Value<Model>

    val inProgress: MutableStateFlow<Boolean>

    val globalError: MutableStateFlow<Exception?>

    val bottomNavBarComponent: BottomNavBarComponent

    fun onResetGlobalError()

    fun getSensors()

    fun getCameraState(): CameraState?

    fun getSourceData(): GeoJsonData.Features?
}