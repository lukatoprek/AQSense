package hr.ferit.ltoprek.aqsense.components.ui.authorization

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import aqsense.composeapp.generated.resources.Res
import aqsense.composeapp.generated.resources.aqsense_square
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Lock
import hr.ferit.ltoprek.aqsense.components.inteface.authorization.LoginComponent
import hr.ferit.ltoprek.aqsense.utilities.RegistrationData
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginUi(component: LoginComponent) {
    val email by component.email.collectAsState(Dispatchers.Main.immediate)
    val password by component.password.collectAsState(Dispatchers.Main.immediate)
    val inProgress by component.inProgress.collectAsState(Dispatchers.Main.immediate)
    val globalError by component.globalError.collectAsState(Dispatchers.Main.immediate)

    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }
    var validationErrorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                            MaterialTheme.colorScheme.surface,
                        )
                    )
                )
                .align(Alignment.CenterHorizontally)
                .padding(top = 50.dp),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(Res.drawable.aqsense_square),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = EvaIcons.Outline.Lock,
                    contentDescription = "Login",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Welcome back",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Sign in to continue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = component::onEmailChanged,
                    label = { Text("Email address") },
                    isError = isEmailError,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = component::onPasswordChanged,
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = isPasswordError,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                )
            }

            if (validationErrorMessage.isNotEmpty()) {
                Text(
                    text = validationErrorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val data = RegistrationData(email = email, password = password)
                    val validationErrors = RegistrationData.validateLoginData(data)

                    validationErrorMessage = if (validationErrors.isNotEmpty()) {
                        validationErrors.joinToString(", ")
                    } else { "" }

                    isEmailError = validationErrors.contains("Email")
                    isPasswordError = validationErrors.contains("Password")

                    if (validationErrors.isEmpty()) {
                        component.onLoginClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = !inProgress,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (inProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Sign in",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            TextButton(
                onClick = component::onRegistrationClick,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    "Don't have an account? ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Register",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        globalError?.let { error ->
            if (error.message?.isNotEmpty() == true) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    action = {
                        TextButton(
                            onClick = component::onResetGlobalError,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(error.message!!)
                }
            }
        }

    }
}
