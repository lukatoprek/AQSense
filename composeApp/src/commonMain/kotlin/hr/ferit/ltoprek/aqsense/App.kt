package hr.ferit.ltoprek.aqsense

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import hr.ferit.ltoprek.aqsense.components.inteface.RootComponent
import hr.ferit.ltoprek.aqsense.components.ui.authorization.AuthorizationUi
import hr.ferit.ltoprek.aqsense.components.ui.main.MainUi
import hr.ferit.ltoprek.aqsense.theme.AQSenseTheme

@Composable
fun App(root: RootComponent) {
    AQSenseTheme{
        val childStack by root.childStack.collectAsState()
        Children(
            stack = childStack,
            animation = stackAnimation(fade())
        ){ child ->
            when(val instance = child.instance){
                is RootComponent.Child.Authorization -> AuthorizationUi(instance.component)
                is RootComponent.Child.Main -> MainUi(instance.component)
            }
        }
    }
}