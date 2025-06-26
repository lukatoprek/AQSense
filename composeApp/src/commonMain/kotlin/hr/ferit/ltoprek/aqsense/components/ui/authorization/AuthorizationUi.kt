package hr.ferit.ltoprek.aqsense.components.ui.authorization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import hr.ferit.ltoprek.aqsense.components.inteface.authorization.AuthorizationComponent

@Composable
fun AuthorizationUi(component: AuthorizationComponent) {

    val childStack by component.childStack.collectAsState()

    Children(
        stack = childStack,
        animation = stackAnimation(fade())
    ) { child ->
        when(val instance = child.instance){
            is AuthorizationComponent.Child.Login -> LoginUi(instance.component)
            is AuthorizationComponent.Child.Registration -> RegistrationUi(instance.component)
        }
    }

}