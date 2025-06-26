package hr.ferit.ltoprek.aqsense.components.inteface.main.blocks

import dev.sargunv.maplibrecompose.compose.CameraState
import dev.sargunv.maplibrecompose.core.source.GeoJsonData
import kotlinx.coroutines.flow.StateFlow

interface SensorMapComponent
{
    val error: StateFlow<Exception?>

    fun getCameraState(): CameraState?

    fun getSourceData(): GeoJsonData.Features?
}