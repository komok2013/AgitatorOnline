package ru.edinros.agitator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import ru.edinros.agitator.core.local.entities.TaskEntity
import ru.edinros.agitator.features.task.TaskFetcherStatus
import ru.edinros.agitator.features.task.TaskListVM
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
                    val model:TaskListVM = hiltViewModel()
                    val res by model.list.collectAsState()
                    RefreshPanel()
                    TaskCards(res)
                    }
                }
            }
        }
    }


@Composable
private fun TaskCards(list:List<TaskEntity>){
    Column(Modifier.verticalScroll(rememberScrollState())) {
        list.forEach { taskInfo ->
            Timber.d("Task->%s\n", taskInfo)
            Card(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth()) {
                Row(Modifier.padding(8.dp)) {
                    Text(text = taskInfo.text)
                }
            }
        }
    }
}
@Preview
@Composable
fun TaskStatus(){
    val dotId = "dot"
    val text = buildAnnotatedString {
        appendInlineContent(dotId, "[icon]")
        append(" status?")
    }
    val inlineContent = mapOf(
        Pair(
            dotId,
            InlineTextContent(
                Placeholder(
                    width = 8.sp,
                    height = 8.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline)
            ) {
                Dot()
            }
        )
    )
    Text(text = text,
        modifier = Modifier.fillMaxWidth(),
        inlineContent = inlineContent)
}
@Preview
@Composable
fun Dot() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(MaterialTheme.colors.error, shape = CircleShape)
            .layout() { measurable, constraints ->
                // Measure the composable
                val placeable = measurable.measure(constraints)

                //get the current max dimension to assign width=height
                val currentHeight = placeable.height
                var heightCircle = currentHeight
                if (placeable.width > heightCircle)
                    heightCircle = placeable.width
                //assign the dimension and the center position
                layout(heightCircle, heightCircle) {
                    // Where the composable gets placed
                    placeable.placeRelative(0, (heightCircle - currentHeight) / 2)
                }
            }
    ) {
        Spacer(modifier = Modifier.defaultMinSize(8.dp))
    }
}
@Composable
private fun RefreshPanel(model:TaskListVM = hiltViewModel()) {
    val state = model.refreshTaskStatus.collectAsState()
    when(val r = state.value){
        is TaskFetcherStatus.Error -> Text(r.errorMessage)
        TaskFetcherStatus.Progress -> Text("progress")
        TaskFetcherStatus.Success -> Text("success")
    }
}

//@Composable
//private fun Attachments(items:List<String>) {
//    Timber.d("compose")
//    Column(Modifier.verticalScroll(rememberScrollState())) {
//        items.forEach {
//            Row {
//                AttachmentItem(it)
//            }
//        }
//    }


//}

@Composable
private fun AttachmentItem(
    number:String) {
    Timber.d("compose element %s",number)
    Text(text=number)



}
@Preview
@Composable
fun TextInCircle() {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(MaterialTheme.colors.primary, shape = CircleShape)
            .layout() { measurable, constraints ->
                // Measure the composable
                val placeable = measurable.measure(constraints)

                //get the current max dimension to assign width=height
                val currentHeight = placeable.height
                var heightCircle = currentHeight
                if (placeable.width > heightCircle)
                    heightCircle = placeable.width

                //assign the dimension and the center position
                layout(heightCircle, heightCircle) {
                    // Where the composable gets placed
                    placeable.placeRelative(0, (heightCircle - currentHeight) / 2)
                }
            }) {

        Text(
            text = "125",
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .padding(4.dp)
                .defaultMinSize(24.dp) //Use a min size for short text.
        )
    }
}