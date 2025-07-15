package hr.ferit.ltoprek.aqsense.components.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.ArrowBack
import compose.icons.evaicons.outline.Droplet
import compose.icons.evaicons.outline.Thermometer
import dev.sargunv.maplibrecompose.compose.MaplibreMap
import dev.sargunv.maplibrecompose.compose.layer.CircleLayer
import dev.sargunv.maplibrecompose.compose.layer.SymbolLayer
import dev.sargunv.maplibrecompose.compose.source.rememberGeoJsonSource
import dev.sargunv.maplibrecompose.core.GestureOptions
import dev.sargunv.maplibrecompose.core.MapOptions
import dev.sargunv.maplibrecompose.core.source.GeoJsonData
import dev.sargunv.maplibrecompose.expressions.dsl.const
import dev.sargunv.maplibrecompose.expressions.dsl.convertToColor
import dev.sargunv.maplibrecompose.expressions.dsl.feature
import dev.sargunv.maplibrecompose.expressions.dsl.offset
import hr.ferit.ltoprek.aqsense.components.inteface.main.SensorDetailsComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.GraphComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.SensorMapComponent
import hr.ferit.ltoprek.aqsense.models.SensorType
import hr.ferit.ltoprek.aqsense.utilities.hourMinFormater
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.DateTimeFilterComponent.DateRangePreset
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DoubleLinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsUi(component: SensorDetailsComponent) {
    val sensor by component.sensor.collectAsState(Dispatchers.Main.immediate)
    val globalError by component.globalError.collectAsState(Dispatchers.Main.immediate)
    val graphComponent by component.graphComponent.collectAsState(Dispatchers.Main.immediate)
    val mapComponent by component.mapComponent.collectAsState(Dispatchers.Main.immediate)
    val inProgress by component.inProgress.collectAsState(Dispatchers.Main.immediate)
    val selectedDateRange by component.selectedDateRange.collectAsState(Dispatchers.Main.immediate)

    val isDialogOpen = remember { mutableStateOf(false) }

    LaunchedEffect(Unit)
    {
        component.getSensor()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(80.dp),
                title = {
                    if (inProgress) {
                        CircularProgressIndicator()
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = (sensor?.name ?: "Unknown sensor") + " details",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = component::onBackClicked) {
                        Icon(
                            imageVector = EvaIcons.Outline.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.secondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.secondary,
                )
            )
        },

        ) { innerPadding ->
        if (isDialogOpen.value) {
            Box(modifier = Modifier.padding(innerPadding)) {
                mapComponent?.let { SensorMapUi(it) } ?: Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("No data available", Modifier.align(Alignment.CenterHorizontally))
                    globalError?.let {
                        Text(
                            text = it.message ?: "Unknown error",
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Graph",
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    )
                )
                if (inProgress) {
                    Box(modifier = Modifier.fillMaxSize())
                    {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    graphComponent?.let { GraphUi(it) } ?: Box(
                        modifier = Modifier
                            .height(300.dp)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No data available",
                            )
                            globalError?.let {
                                Text(
                                    text = it.message ?: "Unknown error",
                                )
                            }
                        }
                    }
                }

                DateTimeFilterUi(component.dateTimeFilterComponent, selectedDateRange)

                Text(
                    "Sensor name: ${sensor?.name}",
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "Sensor Information",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    SensorInfoRow(label = "Name", value = sensor?.name ?: "Unknown")
                    SensorInfoRow(label = "Type", value = sensor?.type?.toString() ?: "Unknown")
                    SensorInfoRow(label = "Last measurement", value = sensor?.getLatestMeasurement() ?: "No data")

                    sensor?.coordinates?.let { coords ->
                        SensorInfoRow(label = "Latitude", value = coords.latitude.toString())
                        SensorInfoRow(label = "Longitude", value = coords.longitude.toString())
                    }

                    if (sensor?.type == SensorType.VOC || sensor?.type == SensorType.CO || sensor?.type == SensorType.CO2) {
                        Text(
                            "Air Quality Index",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            ),
                            modifier = Modifier
                                .padding(top = 4.dp, bottom = 4.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp)
                        .padding(10.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (sensor?.type == SensorType.VOC || sensor?.type == SensorType.CO || sensor?.type == SensorType.CO2) {
                        ItemAqiReading(listOf(sensor!!), selectedDateRange is DateRangePreset.Custom)
                    } else if (sensor?.type == SensorType.TEMPERATURE) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.secondary)
                        ){
                            Icon(
                                imageVector = EvaIcons.Outline.Thermometer,
                                contentDescription = "${sensor?.name} icon",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    } else if (sensor?.type == SensorType.MOISTURE) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.secondary)
                        ){
                            Icon(
                                imageVector = EvaIcons.Outline.Droplet,
                                contentDescription = "${sensor?.name} icon",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 3.dp)
                        .padding(bottom = 32.dp),
                    onClick = { isDialogOpen.value = !isDialogOpen.value },
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(
                        "View on map",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun GraphUi(component: GraphComponent)
{
    val sensorTimes  = component.getSensorTimes()

    val showLabelIndices = remember(sensorTimes) {
        if (sensorTimes.size <= 1) return@remember emptySet<Int>()

        val visibleIndices = mutableSetOf(0, sensorTimes.lastIndex)
        var lastVisibleTimeIndex = 0

        val totalDuration = sensorTimes.last() - sensorTimes.first()
        val minTimeGap = when{
            totalDuration > 30.days -> 7.days
            totalDuration > 15.days -> 3.days
            totalDuration > 7.days -> 1.days
            totalDuration > 3.days -> 12.hours
            totalDuration > 1.days -> 6.hours
            totalDuration > 12.hours -> 3.hours
            totalDuration > 6.hours -> 1.hours
            totalDuration > 2.hours -> 30.minutes
            totalDuration > 45.minutes -> 15.minutes
            else -> 5.minutes
        }

        for (i in 1 until sensorTimes.lastIndex) {
            val timeElapsedSinceLastLabel = sensorTimes[i] - sensorTimes[lastVisibleTimeIndex]

            if (timeElapsedSinceLastLabel >= minTimeGap-1.minutes) { // 1.minutes represents the tolerance for errors introduced from conversion of Firestore Timestamp to Instant
                visibleIndices.add(i)
                lastVisibleTimeIndex = i
            }
        }
        visibleIndices
    }

    val visibleCategories = remember(showLabelIndices, sensorTimes) {
        showLabelIndices.map { sensorTimes[it] }.toSet()
    }

    val labelStep =
        if(visibleCategories.size > 10)
            (visibleCategories.size/5.0).roundToInt()
        else 1

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        XYGraph(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            xAxisModel = CategoryAxisModel(component.getSensorTimes()),
            yAxisModel = DoubleLinearAxisModel(component.getSensorValueRange()),
            xAxisTitle = {
                Text("Time",
                    modifier = Modifier
                        .align(Alignment.Center)
                ) },
            yAxisTitle =
            {
                Text("Value (${component.getUnitOfMeasurements()})",
                    modifier = Modifier
                        .rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                        .padding(bottom = KoalaPlotTheme.sizes.gap)
                        .align(Alignment.Center))
            },
            xAxisLabels = { time ->
                if(time in visibleCategories)
                {
                    val index = sensorTimes.indexOf(time)
                    if (index % labelStep == 0 || index == sensorTimes.size - 1) {
                        val localDateTime = time.toLocalDateTime(TimeZone.currentSystemDefault())
                        val formatedDate = "${localDateTime.dayOfMonth}.${localDateTime.monthNumber}."
                        val formatedTime = "\n${hourMinFormater(localDateTime.hour)}:${hourMinFormater(localDateTime.minute)}"
                        Column(
                            verticalArrangement = Arrangement.spacedBy((-40).dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = formatedDate,
                                fontSize = 8.sp,
                            )
                            Text(
                                text = formatedTime,
                                fontSize = 8.sp,
                            )
                        }
                    }
                } else {
                    Box(Modifier.size(0.dp))
                }
            },
            yAxisLabels = { value ->
                Text(value.toString(), fontSize = 12.sp)
            }
        ) {
            LinePlot(
                data = component.getMeasurementPointList(),
                lineStyle = LineStyle(
                    brush = SolidColor(component.getColor()),
                    strokeWidth = 2.dp
                )
            )
        }
    }
}

@Composable
fun SensorMapUi(component: SensorMapComponent)
{
    val cameraState = component.getCameraState()
    val sourceData = component.getSourceData()
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        if(cameraState == null || sourceData == null)
        {
            Text(
                text = "${component.error.value}",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.error
            )
        } else
        {
            MaplibreMap(
                styleUri = "https://tiles.openfreemap.org/styles/liberty",
                options = MapOptions(gestureOptions = GestureOptions.Standard),
                cameraState = cameraState
            ){
                val source = rememberGeoJsonSource(
                    id = "sensors",
                    data = GeoJsonData.Features(sourceData.geoJson)
                )

                CircleLayer(
                    id="markers",
                    source = source,
                    color = feature.get("type").convertToColor()
                )

                SymbolLayer(
                    id="labels",
                    source = source,
                    textField = feature.get("name").cast(),
                    textFont = const(listOf("Noto Sans Bold")),
                    textOffset = offset(0f.em, (-1.0f).em)
                )

            }
        }
    }
}

@Composable
private fun SensorInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}