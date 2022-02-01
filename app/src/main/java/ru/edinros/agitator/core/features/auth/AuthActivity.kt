package ru.edinros.agitator.core.features.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import ru.edinros.agitator.core.prefs.AuthPref
import ru.edinros.agitator.ui.theme.AgitatorOnlineTheme

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgitatorOnlineTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val model: AuthVM = hiltViewModel()
                    when (val state = model.state.collectAsState().value) {
                        is AuthState.AuthError -> {}//AuthPart(error="")
                        AuthState.AuthSuccess -> {}//finishAuth()
                        AuthState.Init -> PhonePart(/*Modifier.clearFocusOnKeyboardDismiss()*/model)
                        is AuthState.PhoneError -> PhonePart(/*Modifier.clearFocusOnKeyboardDismiss()*/
                            model
                        )
                        AuthState.PhoneSuccess -> {}//AuthPart(state)}
                        AuthState.Progress -> {
                            PhonePart(model)
                        } //ProgressPart()
                    }

                }
            }
        }
    }
}

@Composable
fun PhonePart(model: AuthVM) {
    var text by rememberSaveable {
        mutableStateOf(AuthPref.phone)
    }
    Column(
    ) {
        Row() {
            OutlinedTextField(
                singleLine = true,
                value = text, onValueChange = {
                    if (it.length <= 10 && it.count { c->c.isLetter() } == 0)
                        text = it
                },
                visualTransformation = PhoneVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                isError = model.state.value is AuthState.PhoneError
            )
        }
        if (text.length == 10)
            Row {
                Button(onClick = { model.checkPhone(text) } ){
                    when (model.state.value) {
                        AuthState.Progress -> CircularProgressIndicator(modifier = Modifier.height(18.dp).width(18.dp), color = Color.Red)
                        else-> Text(text = "next")
                    }
                }

            }

    }

    @Composable
    fun AuthPart() {

    }

    @Composable
    fun PhoneField(/*modifier: Modifier*/) {
        val model: AuthVM = hiltViewModel()
        Column(
        ) {
//        Spacer(
//            Modifier
//                .background(Color.Black.copy(alpha = 0.7f))
//                .statusBarsHeight() // Match the height of the status bar
//                .fillMaxWidth()
//        )
            Row() {
                OutlinedTextField(
                    singleLine = true,
                    value = model.phoneText.value, onValueChange = { model.onPhoneChange(it) },
                    visualTransformation = PhoneVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
//                trailingIcon = {
//                    if (model.formattedText.value.length == 10)
//                        Icon(Icons.Rounded.Password, "password")
//                },
                    //modifier = modifier
                )

            }
            Row() {
                Button(onClick = { model.checkPhone(model.phoneText.value) }) {
                    Text(text = "продолжить")
                }
            }

        }
    }
}