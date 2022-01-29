package ru.edinros.agitator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import ru.edinros.agitator.ui.theme.AgitatorOnlineTheme
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgitatorOnlineTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val model:AttachItemViewModel= hiltViewModel()
                    val state by model.items.collectAsState()
                    Column() {
                        Attachments(state)
                        Button(onClick = { model.doSomething() }) {
                            Text(text = "очистить")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Attachments(items:List<String>) {
    Timber.d("compose")
    Column(Modifier.verticalScroll(rememberScrollState())) {
        items.forEach {
            Row {
                AttachmentItem(it)
            }
        }
    }


}

@Composable
private fun AttachmentItem(
    number:String) {
    Timber.d("compose element %s",number)
    Text(text=number)



}