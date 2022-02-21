package ru.edinros.agitator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import ru.edinros.agitator.core.local.entities.TaskEntity
import ru.edinros.agitator.features.task.TaskFetcherStatus
import ru.edinros.agitator.features.task.TaskListVM
import ru.edinros.agitator.ui.theme.AgitatorOnlineTheme
import timber.log.Timber

@AndroidEntryPoint
class TaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgitatorOnlineTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val model: TaskListVM = hiltViewModel()
                    val res by model.list.collectAsState()
                    RefreshPanel()
                    TaskCards(res)
                }
            }
        }
    }
}

@Preview
@ExperimentalMaterialApi
@Composable
private fun TaskShortCard() {
    val modifier = Modifier.fillMaxWidth()
    Card(modifier.padding(8.dp).toggleable(
        value = true,
        onValueChange = {
        }

    )) {


        ConstraintLayout(modifier) {
            val (timeRef, statusRef,dividerRef,taskDescriptionRef) = createRefs()
            TaskStatus(modifier = Modifier
                .wrapContentSize()
                .constrainAs(statusRef) {
                    start.linkTo(parent.start, margin = 8.dp)
                }
            )
            TaskStatus(modifier = Modifier
                .wrapContentSize()
                .constrainAs(timeRef) {
                    end.linkTo(parent.end, margin = 8.dp)
                }
            )
            Divider(modifier = Modifier.constrainAs(dividerRef){
                top.linkTo(timeRef.bottom, margin = 4.dp)
                start.linkTo(parent.start, margin = 8.dp)
                end.linkTo(parent.end, margin = 8.dp)
                width=Dimension.fillToConstraints
            })
            Text(modifier = Modifier
                .fillMaxWidth()
                .constrainAs(taskDescriptionRef) {
                    top.linkTo(dividerRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 8.dp)
                    end.linkTo(parent.end, margin = 8.dp)
                    bottom.linkTo(parent.bottom, margin = 8.dp)
                    width = Dimension.fillToConstraints
                }, text = "Very long text here\nVery long text here Very long text here Very long text hereVery long text here"
            )

        }

    }
}

@Composable
private fun TaskCards(list: List<TaskEntity>) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        list.forEach { taskInfo ->
            Timber.d("Task->%s\n", taskInfo)
            Card(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Row(Modifier.padding(8.dp)) {
                    Text(text = taskInfo.text)
                }
            }
        }
    }
}

//@Preview
@Composable
fun TaskStatus(modifier: Modifier) {
    val dotId = "dot"
    val text = buildAnnotatedString {
        appendInlineContent(dotId, "[icon]")
        append("в работе")
    }
    val inlineContent = mapOf(
        Pair(
            dotId,
            InlineTextContent(
                Placeholder(
                    width = 18.sp,
                    height = 14.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                Dot(14.dp)
            }
        )
    )
    Text(
        fontFamily = FontFamily.Monospace,
        text = text,
        modifier = modifier,
        inlineContent = inlineContent
    )
}
//modifier: Modifier,text:String
//@Composable
//fun TextWithDot() {
//    ConstraintLayout() {
//        val (dotRef, textRef) = createRefs()
//        Dot(Modifier.constrainAs(dotRef){
//            start.linkTo(parent.start, margin = 8.dp)
//            top.linkTo(textRef.top, margin = 8.dp)
//            bottom.linkTo(textRef.bottom, margin = 8.dp)
//        })
//        Text(
//            fontFamily = FontFamily.Monospace,
//            text = "text",
//            modifier = Modifier.constrainAs(textRef){
//                start.linkTo(dotRef.end, margin = 8.dp)
//
//            },
//
//        )
//    }

//}

@Preview
@Composable
fun Chip(
) {
    Surface(
        elevation = 8.dp,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = when {
                true -> colorResource(R.color.teal_200)
                else -> colorResource(R.color.purple_500)
            }
        )
    ) {
        Row(modifier = Modifier
            .toggleable(
                value = true,
                onValueChange = {
                }

            )
        ) {
            Text(
                text = "Some text",
                fontFamily = FontFamily.Monospace,
                color = colorResource(R.color.teal_200),
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )
        }
    }
}

//@Preview
@Composable
fun Dot(size: Dp) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(Color.Black, shape = CircleShape)
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
        Spacer(modifier = Modifier.defaultMinSize(size))
    }
}

@Composable
private fun RefreshPanel(model: TaskListVM = hiltViewModel()) {
    val state = model.refreshTaskStatus.collectAsState()
    when (val r = state.value) {
        is TaskFetcherStatus.Error -> Text(r.errorMessage)
        TaskFetcherStatus.Progress -> Text("progress")
        TaskFetcherStatus.Success -> Text("success")
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun MyChip() {
    Chip(

        onClick = { /* Do something! */ },
        border = BorderStroke(
            ChipDefaults.OutlinedBorderSize,
            Color.Red
        ),
        colors = ChipDefaults.chipColors(
            backgroundColor = Color.White,
            contentColor = Color.Red
        ),
        leadingIcon = {
//            Icon(
//                Icons.Filled.Favorite,
//                contentDescription = "Localized description"
//            )
            Dot(11.dp)
        }
    ) {
        Text("Change settings", fontSize = 12.sp)
    }
}

@Composable
private fun AttachmentItem(
    number: String
) {
    Timber.d("compose element %s", number)
    Text(text = number)


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
            text = "1",
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .padding(4.dp)
                .defaultMinSize(24.dp) //Use a min size for short text.
        )
    }
}