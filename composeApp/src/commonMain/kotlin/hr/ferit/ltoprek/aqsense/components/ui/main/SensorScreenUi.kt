package hr.ferit.ltoprek.aqsense.components.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Bulb
import compose.icons.evaicons.outline.Droplet
import compose.icons.evaicons.outline.Globe2
import compose.icons.evaicons.outline.Home
import compose.icons.evaicons.outline.Menu
import compose.icons.evaicons.outline.MinusCircle
import compose.icons.evaicons.outline.PauseCircle
import compose.icons.evaicons.outline.Percent
import compose.icons.evaicons.outline.Person
import compose.icons.evaicons.outline.QuestionMarkCircle
import compose.icons.evaicons.outline.Thermometer
import hr.ferit.ltoprek.aqsense.components.inteface.main.SensorScreenComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.BottomNavBarComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.CalculateAqiDialogComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.FilteringChipsComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.SensorListComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.TopAppBarComponent
import hr.ferit.ltoprek.aqsense.models.Sensor
import hr.ferit.ltoprek.aqsense.models.SensorType
import hr.ferit.ltoprek.aqsense.theme.aqi_good
import hr.ferit.ltoprek.aqsense.theme.aqi_moderate
import hr.ferit.ltoprek.aqsense.theme.aqi_unhealthy
import hr.ferit.ltoprek.aqsense.utilities.AqiCalculator
import hr.ferit.ltoprek.aqsense.utilities.AqiCalculator.checkAqiRisk
import hr.ferit.ltoprek.aqsense.utilities.Model
import hr.ferit.ltoprek.aqsense.utilities.NavItem
import kotlinx.coroutines.Dispatchers
import kotlin.math.roundToInt

@Composable
fun SensorScreenUi(component: SensorScreenComponent)
{
    val globalError by component.globalError.collectAsState(Dispatchers.Main.immediate)
    val data by component.sensors.subscribeAsState()
    val selectedCategory by component.selectedCategory.collectAsState(Dispatchers.Main.immediate)
    val inProgress by component.inProgress.collectAsState(Dispatchers.Main.immediate)
    val isCalculateAqiDialogVisible by component.isCalculateAqiDialogVisible.collectAsState(Dispatchers.Main.immediate)

    LaunchedEffect(Unit){
        component.getSensors()
    }

    LaunchedEffect(selectedCategory){
        component.filterSensors()
    }

    Scaffold(
        topBar = { TopAppBarUi(component.topAppBarComponent) },
        bottomBar = { BottomNavBarUi(component.bottomNavBarComponent) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            if(isCalculateAqiDialogVisible)
            {
                CalculateAqiDialog(component.calculateAqiDialogComponent, data.sensors)
            }
            FilteringChipsUi(component.filteringChipsComponent, selectedCategory)
            if (inProgress) {
                Box(modifier = Modifier.fillMaxSize())
                {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else
            {
                SensorListUi(component.sensorListComponent,data,globalError)
            }
        }

    }

}

@Composable
fun FilteringChipsUi(component: FilteringChipsComponent, selectedCategory: SensorType?)
{
    val chipData = remember {
        listOf(
            null to Pair("All", EvaIcons.Outline.Menu),
            SensorType.TEMPERATURE to Pair("Temperature", EvaIcons.Outline.Thermometer),
            SensorType.MOISTURE to Pair("Moisture", EvaIcons.Outline.Droplet),
            SensorType.CO to Pair("CO", EvaIcons.Outline.MinusCircle),
            SensorType.CO2 to Pair("CO2", EvaIcons.Outline.PauseCircle),
            SensorType.VOC to Pair("VOC", EvaIcons.Outline.Percent),
            SensorType.UNKNOWN to Pair("Unknown", EvaIcons.Outline.QuestionMarkCircle)
        )
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(chipData) { (sensorType, data) ->
            val (label, icon) = data
            FilterChip(
                selected = selectedCategory == sensorType,
                onClick = {component.onCategorySelected(sensorType)},
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                },
                label = {Text(label)}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarUi(component: TopAppBarComponent)
{
    TopAppBar(
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth(),
        title = {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Sensors",
                    modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        },
        actions = {
            IconButton(
                modifier = Modifier.fillMaxHeight(),
                onClick = component::onClicked,
            ){
                Icon(EvaIcons.Outline.Bulb, contentDescription = "Calculate AQI")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.secondary,
            actionIconContentColor = MaterialTheme.colorScheme.secondary,
        )
    )
}

@Composable
fun SensorListUi(component: SensorListComponent,data: Model, globalError: Exception?)
{
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        when{
            globalError != null -> Text(
                "Error: ${globalError.message}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
            data.sensors.isEmpty() ->
                Text("No sensors found",
                    modifier = Modifier.align(Alignment.Center))
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                items(data.sensors){ sensor ->
                    SensorListItem(
                        sensor = sensor,
                        onClick = {component.onSensorClicked(sensor.id)}
                    )
                }
            }
        }
    }
}

@Composable
fun SensorListItem(sensor: Sensor, onClick: () -> Unit)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.primaryContainer),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(8.dp)
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier
                    .height(50.dp)
                    .width(75.dp)
                    .padding(5.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                if(sensor.type == SensorType.CO || sensor.type == SensorType.CO2 || sensor.type == SensorType.VOC)
                {
                    ItemAqiReading(listOf(sensor))
                } else if (sensor.type == SensorType.TEMPERATURE){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.secondary)
                    ){
                        Icon(
                            imageVector = EvaIcons.Outline.Thermometer,
                            contentDescription = "${sensor.name} icon",
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else if (sensor.type == SensorType.MOISTURE) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.secondary)
                    ){
                        Icon(
                            imageVector = EvaIcons.Outline.Droplet,
                            contentDescription = "${sensor.name} icon",
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sensor.name,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Last reading: "+sensor.getLatestMeasurement(),
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun ItemAqiReading(sensors: List<Sensor>)
{
    val aqiValue = AqiCalculator.calculateAqi(sensors)
    val aqiRisk = checkAqiRisk(aqiValue)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = when(aqiRisk){
                    AqiCalculator.AqiRisk.GOOD -> aqi_good
                    AqiCalculator.AqiRisk.MODERATE -> aqi_moderate
                    AqiCalculator.AqiRisk.UNHEALTHY -> aqi_unhealthy
                }),
        contentAlignment = Alignment.Center,
    ){
        Text(aqiValue.roundToInt().toString(), fontSize = 25.sp, color = MaterialTheme.colorScheme.onSecondary)
    }
}

@Composable
fun BottomNavBarUi(component: BottomNavBarComponent)
{
    BottomAppBar(
    ) {
        Row(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary).padding(5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                NavItem.PROFILE to EvaIcons.Outline.Person,
                NavItem.HOME to EvaIcons.Outline.Home,
                NavItem.MAP to EvaIcons.Outline.Globe2
            ).forEach { (navItem, icon) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { component.onNavItemClicked(navItem) },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = navItem.name,
                            modifier = Modifier.size(40.dp),
                            tint = if (navItem == component.getCurrentScreen()) MaterialTheme.colorScheme.onSecondary
                            else MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalculateAqiDialog(component: CalculateAqiDialogComponent, sensors: List<Sensor>)
{
    val globalError by component.globalError.collectAsState(Dispatchers.Main.immediate)
    val calculatedAqi by component.calculatedAqi.collectAsState(Dispatchers.Main.immediate)
    val tempSelectedSensors = remember { mutableStateListOf<Sensor>() }

    AlertDialog(
        onDismissRequest = component::onDismiss,
        title = { Text("Choose sensors to calculate AQI for") },
        text = {
            Column {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    sensors.filter { it.type == SensorType.CO || it.type == SensorType.CO2 || it.type == SensorType.VOC }.forEach { sensor ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = tempSelectedSensors.contains(sensor),
                                onCheckedChange = { isChecked ->
                                    if (isChecked) tempSelectedSensors.add(sensor)
                                    else tempSelectedSensors.remove(sensor)
                                }
                            )
                            Text(text = sensor.name, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
                globalError?.let { Text(
                    "${it.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp)
                )}
                if(calculatedAqi != null)
                {
                    val aqi = calculatedAqi!!.roundToInt()
                    Card(
                        modifier = Modifier
                            .height(100.dp)
                            .width(100.dp)
                            .padding(10.dp)
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(8.dp)
                    ){
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = if(aqi<51) {
                                        aqi_good
                                    } else if(aqi<151) {
                                        aqi_moderate
                                    } else  { aqi_unhealthy }),
                            contentAlignment = Alignment.Center,
                        ){
                            Text(aqi.toString(), fontSize = 25.sp, color = MaterialTheme.colorScheme.onSecondary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    component.onSelectedSensorsChanged(tempSelectedSensors.toList())
                    component.onCalculateAqiClicked()
                },
                content = { Text("Calculate AQI") }
            )
        },
        dismissButton = {
            Button(
                onClick = component::onDismiss,
                content = { Text("Cancel") }
            )
        },
        properties = DialogProperties()
    )
}