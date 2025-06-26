package hr.ferit.ltoprek.aqsense.components.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Person
import hr.ferit.ltoprek.aqsense.components.inteface.main.ProfileScreenComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.ChangePasswordDialogComponent
import hr.ferit.ltoprek.aqsense.components.inteface.main.blocks.DeleteAccountDialogComponent
import hr.ferit.ltoprek.aqsense.utilities.RegistrationData
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileUi(component: ProfileScreenComponent) {
    val globalError by component.globalError.collectAsState(Dispatchers.Main.immediate)
    val inProgress by component.inProgress.collectAsState(Dispatchers.Main.immediate)
    val isChangePasswordDialogVisible by component.isChangePasswordDialogVisible.collectAsState(Dispatchers.Main.immediate)
    val isDeleteAccountDialogVisible by component.isDeleteAccountDialogVisible.collectAsState(Dispatchers.Main.immediate)

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(80.dp),
                title = {
                    Row {

                        Text(
                            text = "My Profile",
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
            )
        },
        bottomBar = { BottomNavBarUi(component.bottomNavBarComponent) }
    ) { innerPadding ->
        if (isChangePasswordDialogVisible) {
            ChangePasswordDialog(component.changePasswordDialogComponent)
        }
        if (isDeleteAccountDialogVisible) {
            DeleteAccountDialog(component.deleteAccountDialogComponent)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .size(150.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = EvaIcons.Outline.Person,
                    contentDescription = "Profile Icon",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoRow(label = "ID", value = component.user?.id ?: "N/A")
                    Divider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                    InfoRow(label = "Name", value = component.user?.name ?: "N/A")
                    Divider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                    InfoRow(label = "Email", value = component.user?.email ?: "N/A")
                }
            }

            globalError?.let { error ->
                Text(
                    text = error.message ?: "An error occurred",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = component::onChangePasswordClicked,
                    enabled = !inProgress,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    if (inProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    } else {
                        Text("Change Password")
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = component::onLogoutClicked,
                    enabled = !inProgress,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    if (inProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    } else {
                        Text("Logout")
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = component::onDeleteAccountClicked,
                    enabled = !inProgress,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    if (inProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onError
                        )
                    } else {
                        Text("Delete Account")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ChangePasswordDialog(component: ChangePasswordDialogComponent) {
    val password by component.password.collectAsState(Dispatchers.Main.immediate)
    val passwordConfirmation by component.passwordConfirmation.collectAsState(Dispatchers.Main.immediate)

    var isPasswordError by remember { mutableStateOf(false) }
    var isPasswordConfirmationError by remember { mutableStateOf(false) }
    var validationErrorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = component::onCancelClicked,
        title = {
            Text(
                "Change Password",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = password,
                    onValueChange = component::onPasswordChanged,
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = isPasswordError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = passwordConfirmation,
                    onValueChange = component::onPasswordConfirmationChanged,
                    label = { Text("Confirm New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = isPasswordConfirmationError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                )

                if (validationErrorMessage.isNotEmpty() || component.globalError.value?.message != null) {
                    Text(
                        text = component.globalError.value?.message ?: validationErrorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val data = RegistrationData(
                        name = component.getUserName(),
                        email = component.getUserEmail(),
                        password = component.password.value
                    )
                    val validationErrors = RegistrationData.validateRegistrationData(data)

                    validationErrorMessage = if (validationErrors.isNotEmpty()) {
                        validationErrors.joinToString(", ")
                    } else { "" }

                    isPasswordError = validationErrors.contains("Password")
                    isPasswordConfirmationError = password != passwordConfirmation

                    if (validationErrors.isEmpty() && !isPasswordConfirmationError) {
                        component.onConfirmChangePasswordClicked()
                    }
                },
                enabled = !component.inProgress.value,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors()
            ) {
                if (component.inProgress.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Change Password")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = component::onCancelClicked,
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Cancel")
            }
        },
        shape = MaterialTheme.shapes.medium,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
fun DeleteAccountDialog(component: DeleteAccountDialogComponent) {
    AlertDialog(
        onDismissRequest = component::onCancelClicked,
        title = {
            Text(
                "Delete Account?",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Column {
                Text(
                    "This action cannot be undone. All your data will be permanently deleted.",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (component.globalError.value?.message != null) {
                    Text(
                        text = component.globalError.value?.message ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = component::confirmAccountDeletion,
                enabled = !component.inProgress.value,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                if (component.inProgress.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onError
                    )
                } else {
                    Text("Delete Account")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = component::onCancelClicked,
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Cancel")
            }
        },
        shape = MaterialTheme.shapes.medium,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
    )
}
