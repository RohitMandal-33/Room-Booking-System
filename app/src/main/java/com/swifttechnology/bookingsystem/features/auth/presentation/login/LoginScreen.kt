package com.swifttechnology.bookingsystem.features.auth.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.R
import com.swifttechnology.bookingsystem.shared.components.LoginTextField
import com.swifttechnology.bookingsystem.shared.components.SwiftTechnologyLogo

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.events.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    val windowInfo = LocalWindowInfo.current
    val isKeyboardOpen = windowInfo.isWindowFocused &&
        WindowInsets.ime.getBottom(LocalDensity.current) > 0

    LaunchedEffect(event) {
        when (val e = event) {
            is LoginEvent.NavigateToHome -> {
                viewModel.onEventConsumed()
                onLoginSuccess()
            }
            is LoginEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(e.message)
                viewModel.onEventConsumed()
            }
            null -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(innerPadding)
                .imePadding()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(92.dp))

                SwiftTechnologyLogo(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                )

                Spacer(modifier = Modifier.height(75.dp))

                LoginTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChanged,
                    placeholder = "Email",
                    errorMessage = uiState.emailError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                LoginTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChanged,
                    placeholder = "Password",
                    errorMessage = uiState.passwordError,
                    trailingIcon = {
                        IconButton(onClick = viewModel::onTogglePasswordVisibility) {
                            Icon(
                                painter = painterResource(
                                    id = if (uiState.isPasswordVisible) R.drawable.eye_off else R.drawable.eye_on
                                ),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    visualTransformation =
                        if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.onLoginClicked()
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Forgot Password",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { viewModel.onForgotPasswordClicked() }
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = uiState.rememberMe,
                            onCheckedChange = { viewModel.onRememberMeToggled() },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "Remember me",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(if (isKeyboardOpen) 16.dp else 32.dp))
            }

            Button(
                onClick = viewModel::onLoginClicked,
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp)
                    .padding(bottom = if (isKeyboardOpen) 10.dp else 40.dp)
                    .height(48.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.customColors.loginBtnBorder,
                        shape = RoundedCornerShape(20.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.customColors.loginBtnBg
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Login")
                }
            }
        }
    }
}
