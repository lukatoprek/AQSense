package hr.ferit.ltoprek.aqsense.components.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import hr.ferit.ltoprek.aqsense.components.inteface.main.MainComponent

@Composable
fun MainUi(component: MainComponent)
{
    val childStack by component.childStack.collectAsState()

    Children(
        stack = childStack,
        animation = stackAnimation{ child ->
            when(child.instance)
            {
                is MainComponent.Child.SensorScreen -> fade(minAlpha = 0.5F)
                is MainComponent.Child.Details -> slide()
                is MainComponent.Child.ProfileScreen -> fade(minAlpha = 0.5F)
                is MainComponent.Child.OverviewMap -> fade(minAlpha = 0.5F)
            }
        }

    ){ child ->
        when(val instance = child.instance){
            is MainComponent.Child.SensorScreen -> SensorScreenUi(instance.component)
            is MainComponent.Child.Details -> DetailsUi(instance.component)
            is MainComponent.Child.ProfileScreen -> ProfileUi(instance.component)
            is MainComponent.Child.OverviewMap -> OverviewMapUi(instance.component)
        }

    }
}