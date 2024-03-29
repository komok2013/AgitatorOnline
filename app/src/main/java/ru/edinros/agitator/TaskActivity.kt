package ru.edinros.agitator

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.edinros.agitator.core.local.entities.TaskEntity
import ru.edinros.agitator.core.utils.formatterWithDay
import ru.edinros.agitator.features.task.TaskFetcherStatus
import ru.edinros.agitator.features.task.TaskListVM
import ru.edinros.agitator.ui.theme.AgitatorOnlineTheme
import timber.log.Timber

@ExperimentalMaterialApi
@AndroidEntryPoint
class TaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    AgitatorOnlineTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            modifier = Modifier.fillMaxSize()
        ) {
            NavHost(navController, startDestination = Destinations.TaskList) {
                composable(Destinations.TaskList) {
                    TaskListScreen(navController = navController, scaffoldState = scaffoldState)
                }
                composable("${Destinations.TaskDetail}/{taskId}/{taskName}",
                    arguments = listOf(
                        navArgument("taskId") { type = NavType.LongType },
                        navArgument("taskName") { type = NavType.StringType },
                    )
                ) { entry ->
                    entry.arguments?.getLong("taskId")?.let { id->
                        entry.arguments?.getString("taskName")?.let {name->
                            TaskDetail(
                                taskId = id,
                                taskName = name,
                            )
                        }
                    }
                }
            }

        }
    }
}


object Destinations {
    const val TaskList = "TaskList"
    const val TaskDetail = "TaskDetail"
    const val Profile = "Profile"
}

@Composable
fun TaskListScreen(navController: NavController, scaffoldState: ScaffoldState) {
    val model: TaskListVM = hiltViewModel()
    val res by model.list.collectAsState()
    Column() {
        StatusPanelView(scaffoldState = scaffoldState)
        TaskCards(res, navController)
    }
}

@Composable
fun TaskDetail(
    taskId: Long,
    taskName: String,
) {
    Text(text = "$taskId<->$taskName")
}

@Composable
private fun TaskCard(task: TaskEntity, navController: NavController) {
    val modifier = Modifier.fillMaxWidth()
    Card(modifier
        .padding(8.dp)
        .toggleable(
            value = true,
            onValueChange = {

                navController.navigate("${Destinations.TaskDetail}/${task.id}/${task.text}")
            }

        )) {
        ConstraintLayout(modifier) {
            val (timeRef, statusRef, dividerRef, taskDescriptionRef) = createRefs()
            val barrier = createBottomBarrier(timeRef, statusRef)
            DateTimeTextWithLeadingIcon(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(statusRef) {
                        start.linkTo(parent.start, margin = 8.dp)
                        top.linkTo(parent.top, margin = 8.dp)
                    },
                icon = Icons.Rounded.DateRange,
                text = formatterWithDay(task.assigned_at),
                color = LocalContentColor.current.copy(alpha = 0.6f)
            )
            TaskStatusView(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(timeRef) {
                        end.linkTo(parent.end, margin = 8.dp)
                        top.linkTo(parent.top, margin = 8.dp)
                    },
                task = task
            )
            Divider(modifier = Modifier.constrainAs(dividerRef) {
                top.linkTo(barrier, margin = 4.dp)
                start.linkTo(parent.start, margin = 8.dp)
                end.linkTo(parent.end, margin = 8.dp)
                width = Dimension.fillToConstraints
            })
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(taskDescriptionRef) {
                        top.linkTo(dividerRef.bottom, margin = 8.dp)
                        start.linkTo(parent.start, margin = 8.dp)
                        end.linkTo(parent.end, margin = 8.dp)
                        bottom.linkTo(parent.bottom, margin = 8.dp)
                        width = Dimension.fillToConstraints
                    }, text = task.description.ifEmpty { "Описание отсутствует" }
            )

        }

    }
}

@Composable
private fun TaskCards(list: List<TaskEntity>, navController: NavController) {
    LazyColumn {
        list.map { item { TaskCard(task = it, navController = navController) } }
    }
}

@Composable
fun TaskStatusView(modifier: Modifier, task: TaskEntity) {
    val taskStatus = task.checkStatus()
    val dotId = "dot"
    val text = buildAnnotatedString {
        appendInlineContent(dotId, "[icon]")
        append(taskStatus.text)
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
                Dot(size = 14.dp, color = taskStatus.color)
            }
        )
    )
    Text(
        fontSize = 14.sp,
        color = taskStatus.color,
        fontFamily = FontFamily.Monospace,
        text = text,
        modifier = modifier,
        inlineContent = inlineContent
    )
}

class AssetParamType : NavType<TaskEntity>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): TaskEntity? =
        bundle.getParcelable<TaskEntity>(key)


    override fun parseValue(value: String): TaskEntity =
        //Json.decodeFromString(value)
        Gson().fromJson(value,TaskEntity::class.java)


    override fun put(bundle: Bundle, key: String, value: TaskEntity) {
        bundle.putParcelable(key, value)
    }

}

@Composable
fun DateTimeTextWithLeadingIcon(
    modifier: Modifier,
    icon: ImageVector,
    text: String,
    color: Color,
) {
    val iconId = "dot"
    val annotatedText = buildAnnotatedString {
        appendInlineContent(iconId, "[icon]")
        append(text)
    }
    val inlineContent = mapOf(
        Pair(
            iconId,
            InlineTextContent(
                Placeholder(
                    width = 28.sp,
                    height = 24.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                Icon(imageVector = icon, contentDescription = "", tint = color)
            }
        )
    )
    Text(
        color = color,
        fontSize = 14.sp,
        fontFamily = FontFamily.Monospace,
        text = annotatedText,
        modifier = modifier,
        inlineContent = inlineContent
    )
}

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

@Composable
fun Dot(size: Dp, color: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(color = color, shape = CircleShape)
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
private fun StatusPanelView(model: TaskListVM = hiltViewModel(), scaffoldState: ScaffoldState) {
    val state by model.refreshTaskStatus.collectAsState()
    when (state) {
        is TaskFetcherStatus.Error -> LaunchedEffect(scaffoldState) {
            scaffoldState.snackbarHostState.showSnackbar(
                (state as TaskFetcherStatus.Error).errorMessage
            )
        }
        TaskFetcherStatus.Progress -> LinearProgressIndicator(
            Modifier
                .fillMaxWidth()
                .height(1.dp), color = Color.Red
        )
        TaskFetcherStatus.Success -> {}
    }

}

//@ExperimentalMaterialApi
//@Preview
//@Composable
//fun MyChip() {
//    Chip(
//
//        onClick = { /* Do something! */ },
//        border = BorderStroke(
//            ChipDefaults.OutlinedBorderSize,
//            Color.Red
//        ),
//        colors = ChipDefaults.chipColors(
//            backgroundColor = Color.White,
//            contentColor = Color.Red
//        ),
//        leadingIcon = {
////            Icon(
////                Icons.Filled.Favorite,
////                contentDescription = "Localized description"
////            )
//            Dot(11.dp,)
//        }
//    ) {
//        Text("Change settings", fontSize = 12.sp)
//    }
//}

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