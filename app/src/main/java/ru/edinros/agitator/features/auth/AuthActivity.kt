package ru.edinros.agitator.features.auth

import android.content.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import dagger.hilt.android.AndroidEntryPoint
import ru.edinros.agitator.TaskActivity
import ru.edinros.agitator.core.prefs.AuthPref
import ru.edinros.agitator.features.auth.receiver.SystemBroadcastReceiver
import ru.edinros.agitator.ui.theme.AgitatorOnlineTheme
import timber.log.Timber

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgitatorOnlineTheme {
                val scaffoldState = rememberScaffoldState()
                val model: AuthVM = hiltViewModel()
                Scaffold(
                    scaffoldState=scaffoldState,
                    modifier = Modifier.fillMaxSize()){
                    when (val res = model.state.collectAsState().value) {
                        is AuthState.AuthError -> {
                            LaunchedEffect(scaffoldState){scaffoldState.snackbarHostState.showSnackbar(res.message) }
                            TextEditWithActionButton(
                                state=res,
                                initText = "",
                                label="Код подтверждения",
                                textFilter = CodeRules.filter,
                                maxLength = CodeRules.maxLength,
                                action = { s -> model.proceedAuthentication(s) }
                            )
                        }
                        AuthState.AuthSuccess -> {
                            startActivity(Intent(this, TaskActivity::class.java))
                            finish()
                        }
                        AuthState.Init -> TextEditWithActionButton(
                            state=res,
                            visualTransformation = PhoneVisualTransformation(),
                            initText = AuthPref.phone,
                            textFilter = PhoneRules.filter,
                            maxLength = PhoneRules.maxLength,
                            label="Номер телефона",
                            action = { s -> model.proceedPhoneNumber(s) }
                        )
                        is AuthState.PhoneError -> {
                            LaunchedEffect(scaffoldState){scaffoldState.snackbarHostState.showSnackbar(res.message) }
                            //scope.launch { snackBarHostState.showSnackbar(res.message) }
                            TextEditWithActionButton(
                                state=res,
                                visualTransformation = PhoneVisualTransformation(),
                                label="Номер телефона",
                                initText = AuthPref.phone,
                                textFilter = PhoneRules.filter,
                                maxLength = PhoneRules.maxLength,
                                action = { s -> model.proceedPhoneNumber(s) }

                            )
                        }
                        AuthState.PhoneSuccess -> {
                            SmsRetrieverUserConsentBroadcast(onCodeReceived = { s ->
                                model.proceedAuthentication(s)
                            })
                            TextEditWithActionButton(
                                state=res,
                                textFilter = CodeRules.filter,
                                maxLength = CodeRules.maxLength,
                                label = "Код подтверждения",
                                action = { s -> model.proceedAuthentication(s) }
                            )
                        }
                        AuthState.Progress -> TextEditWithActionButton()
                    }
                }
            }
        }
    }
}

private fun fetchVerificationCode(message: String): String {
    return Regex("(\\d{6})").find(message)?.value ?: ""
}

@Composable
private fun SmsRetrieverUserConsentBroadcast(onCodeReceived: (code: String) -> Unit) {
    val context = LocalContext.current
    var shouldRegisterReceiver by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        SmsRetriever.getClient(context).also {
            it.startSmsUserConsent(null)
                .addOnSuccessListener {
                    Timber.d("LISTENING_SUCCESS")
                    shouldRegisterReceiver = true
                }
                .addOnFailureListener {
                    Timber.d("LISTENING_FAILURE")
                }
        }
    }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ComponentActivity.RESULT_OK) {
                result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)?.let { s ->
                    val code = fetchVerificationCode(s)
                    onCodeReceived(code)
                }
            } else {
                Timber.d("Canceled")
            }
        }

    if (shouldRegisterReceiver) {
        SystemBroadcastReceiver(systemAction = SmsRetriever.SMS_RETRIEVED_ACTION) { intent ->
            if (intent != null && SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val consentIntent =
                            extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        try {
                            launcher.launch(consentIntent)
                        } catch (e: ActivityNotFoundException) {
                            Timber.e(e, "Activity Not found for SMS consent API")
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> Timber.d("Timeout in sms verification receiver")
                }
            }
        }
    }
}

/**
 * Фильтр на длину и буквы
 */
interface PhoneRules {
    companion object {
        const val maxLength = 10
        val filter = { s: String -> s.length <= maxLength && s.count { c -> c.isLetter() } == 0 }
    }
}

interface CodeRules {
    companion object {
        const val maxLength = 6
        val filter = { s: String -> s.length <= maxLength && s.count { c -> c.isLetter() } == 0 }
    }
}

@Composable
private fun TextEditWithActionButton(
    state: AuthState=AuthState.Progress,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    label: String="",
    initText: String = "",
    textFilter: ((String) -> Boolean)? = null,
    maxLength: Int = 0,
    action: ((String) -> Unit)? = null
) {
    //('А'..'Я').forEach { Timber.d("->%s",it) }
    var text by rememberSaveable { mutableStateOf(initText) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TextField(
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Transparent,
                unfocusedIndicatorColor = Transparent,
                disabledIndicatorColor = Transparent,
                ),
            label = { Text(text = label) },
            enabled = state !is AuthState.Progress,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            singleLine = true,
            value = text,
            onValueChange = { newValue ->
                textFilter?.also { filter ->
                    if (filter(newValue))
                        text = newValue
                }
            },
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    action?.apply {
                        this(text)
                    }
                }
            ),
            trailingIcon = {
                LeadingButton(
                    inProgress = state is AuthState.Progress,
                    visibility = text.length == maxLength
                ) {
                    action?.apply {
                        this(text)
                    }
                }
            }
        )
    }
}

@Composable
private fun LeadingButton(
    inProgress:Boolean,
    visibility: Boolean,
    onClick: () -> Unit,
) {
    return when (inProgress) {
        true -> CircularProgressIndicator(
            modifier = Modifier.padding(8.dp),
        )
        else -> {
            when {
                visibility -> {
                    OutlinedButton(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        onClick = onClick,
                        border = BorderStroke(1.dp, Color.Blue),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Blue, backgroundColor = Transparent)
                    ) {
                        Text(text = "далее")
                    }
                }
                else -> {}
            }
        }

    }
}