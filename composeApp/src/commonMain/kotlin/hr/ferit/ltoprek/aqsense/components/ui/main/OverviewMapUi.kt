package hr.ferit.ltoprek.aqsense.components.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import dev.sargunv.maplibrecompose.compose.MaplibreMap
import dev.sargunv.maplibrecompose.compose.layer.CircleLayer
import dev.sargunv.maplibrecompose.compose.layer.SymbolLayer
import dev.sargunv.maplibrecompose.compose.source.rememberGeoJsonSource
import dev.sargunv.maplibrecompose.core.GestureOptions
import dev.sargunv.maplibrecompose.core.MapOptions
import dev.sargunv.maplibrecompose.expressions.dsl.const
import dev.sargunv.maplibrecompose.expressions.dsl.convertToColor
import dev.sargunv.maplibrecompose.expressions.dsl.feature
import dev.sargunv.maplibrecompose.expressions.dsl.offset
import hr.ferit.ltoprek.aqsense.components.inteface.main.OverviewMapComponent
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewMapUi(component: OverviewMapComponent)
{
    val inProgress = component.inProgress.collectAsState(Dispatchers.Main.immediate)
    val globalError = component.globalError.collectAsState(Dispatchers.Main.immediate)

    LaunchedEffect(Unit){
        component.getSensors()
    }

    Scaffold(
        topBar = { TopAppBar(
            modifier = Modifier.height(80.dp),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sensor Overview Map",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.secondary,
                actionIconContentColor = MaterialTheme.colorScheme.secondary,
            )
        ) },
        bottomBar = { BottomNavBarUi(component.bottomNavBarComponent) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ){
            if(inProgress.value)
            {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            else
            {
                val cameraState = component.getCameraState()
                val sourceData = component.getSourceData()
                if(cameraState == null || sourceData == null)
                {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No data available")
                        globalError.value?.let {
                            Text(
                                "Error: ${it.message}",
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }

                } else
                {
                    MaplibreMap(
                        styleUri = "https://tiles.openfreemap.org/styles/liberty",
                        options = MapOptions(gestureOptions = GestureOptions.Standard),
                        cameraState = cameraState
                    ){
                        val source = rememberGeoJsonSource(
                            id = "sensors",
                            data = sourceData
                        )

                        CircleLayer(
                            id = "markers",
                            source = source,
                            color = feature.get("type").convertToColor()
                        )

                        SymbolLayer(
                            id = "labels",
                            source = source,
                            textField = feature.get("name").cast(),
                            textFont = const(listOf("Noto Sans Bold")),
                            textOffset = offset(0f.em, (-1.0f).em)
                        )
                    }
                }
            }
        }
    }
}