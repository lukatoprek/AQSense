package hr.ferit.ltoprek.aqsense.components.implementation.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.lifecycle.doOnDestroy
import hr.ferit.ltoprek.aqsense.components.implementation.main.blocks.RealBottomNavBarComponent
import hr.ferit.ltoprek.aqsense.components.implementation.main.blocks.RealCalculateAqiDialogComponent
import hr.ferit.ltoprek.aqsense.components.implementation.main.blocks.RealFilteringChipsComponent
import hr.ferit.ltoprek.aqsense.components.implementation.main.blocks.RealSensorListComponent
import hr.ferit.ltoprek.aqsense.components.implementation.main.blocks.RealTopAppBarComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.SensorScreenComponent
import hr.ferit.ltoprek.aqsense.models.SensorRepository
import hr.ferit.ltoprek.aqsense.models.SensorType
import hr.ferit.ltoprek.aqsense.utilities.Model
import hr.ferit.ltoprek.aqsense.utilities.NavItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RealSensorScreenComponent(
    componentContext: ComponentContext,
    private val sensorRepository: SensorRepository,
    onSensorClickedFeedback: (String) -> Unit,
    onProfileClicked: () -> Unit,
    onOverviewMapClicked: () -> Unit
) : ComponentContext by componentContext, SensorScreenComponent
{
    private val filteredSensors = MutableValue(Model(emptyList()))
    private val _sensors = MutableValue(Model(emptyList()))
    override val sensors = filteredSensors
    override val globalError = MutableStateFlow<Exception?>(null)
    override val selectedCategory = MutableStateFlow<SensorType?>(null)
    override val inProgress = MutableStateFlow(false)

    override val isCalculateAqiDialogVisible = MutableStateFlow(false)

    override val calculatedAqi = MutableStateFlow<Double?>(null)

    private val componentScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    init{
        lifecycle.doOnDestroy{
            componentScope.cancel()
        }
    }

    override fun onResetGlobalError() { globalError.value = null }

    override val topAppBarComponent = RealTopAppBarComponent(
        componentContext =  childContext(key = "topAppBar"),
        onClickedFeedback = { isCalculateAqiDialogVisible.value = true }
    )

    override val filteringChipsComponent = RealFilteringChipsComponent(
        componentContext = childContext(key = "filteringChips")
    ) { category -> selectedCategory.value = category }

    override val sensorListComponent = RealSensorListComponent(
        componentContext =  childContext(key = "sensorList"),
        sensors = filteredSensors.value.sensors,
        onSensorClickedFeedback =  onSensorClickedFeedback
    )

    override val bottomNavBarComponent = RealBottomNavBarComponent(
        componentContext = childContext(key = "bottomNavBar"),
        onHomeClicked = {},
        onProfileClicked = onProfileClicked,
        onOverviewMapClicked = onOverviewMapClicked,
        currentScreen = NavItem.HOME
    )

    override val calculateAqiDialogComponent = RealCalculateAqiDialogComponent(
        componentContext = childContext(key = "calculateAqiDialog"),
        onDismissFeedback = {
            calculatedAqi.value = null
            isCalculateAqiDialogVisible.value = false
        }
    )

    override fun getSensors()
    {
        inProgress.value = true
        onResetGlobalError()

        componentScope.launch {
            try {
                val sensorsData = sensorRepository.getSensors()
                _sensors.value = Model(sensorsData)
                filterSensors()
            } catch(e: Exception){
                globalError.value = e
                _sensors.value = Model(emptyList())
                filterSensors()
            } finally {
                inProgress.value = false
            }
        }
    }

    override fun filterSensors()
    {
        filteredSensors.value = selectedCategory.value?.let { selectedCategory ->
            Model(_sensors.value.copy().sensors.filter { it.type == selectedCategory })
        } ?: Model(_sensors.value.sensors)
    }
}