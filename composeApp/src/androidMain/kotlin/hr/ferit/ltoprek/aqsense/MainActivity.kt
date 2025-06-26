package hr.ferit.ltoprek.aqsense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.retainedComponent
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.permissionUtil
import hr.ferit.ltoprek.aqsense.components.implementation.RealRootComponent
import hr.ferit.ltoprek.aqsense.models.SharedAuthorizationRepository
import hr.ferit.ltoprek.aqsense.models.SharedSensorRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.initialize(this)
        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_launcher_foreground,
                showPushNotification = true
            )
        )

        val permissionUtil by permissionUtil()
        permissionUtil.askNotificationPermission()

        val sensorRepository = SharedSensorRepository()
        val authRepository = SharedAuthorizationRepository()

        val root = retainedComponent {
            RealRootComponent(
                componentContext = it,
                sensorRepository = sensorRepository,
                authorizationRepository = authRepository,
            )
        }

        setContent {
            App(root)
        }
    }
}