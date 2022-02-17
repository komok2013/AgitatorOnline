package ru.edinros.agitator.features.task

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import timber.log.Timber
import kotlin.random.Random

val data = listOf("☕️", "🙂", "🥛", "🎉"/*, "📐", "🎯", "🧩", "😄", "🥑"*/)




@ExperimentalFoundationApi
@Composable
fun TwoTexts(modifier: Modifier = Modifier, text1: String, text2: String) {
//    ('А'..'Я').forEach { Timber.d("->%s",it) }

    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
        contentPadding = PaddingValues(1.dp)
    ) {
        items(data.count()) { item ->
            Card(
                modifier = Modifier.padding(1.dp),
                backgroundColor = Color(
                    red = Random.nextInt(0, 255),
                    green = Random.nextInt(0, 255),
                    blue = Random.nextInt(0, 255)
                )
            ) {
                Text(
                    text = data[item],
                    fontSize = 42.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }
//    Column() {
//        Row(modifier = modifier.height(IntrinsicSize.Min)) {
//            Text(
//
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(8.dp)
//                    .wrapContentWidth(Alignment.CenterHorizontally)
//                    .wrapContentHeight(Alignment.CenterVertically),
//                text = text1,
//            )
//
//            Divider(color = Color.Black, modifier = Modifier
//                .fillMaxHeight()
//                .width(1.dp))
//            Text(
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(8.dp)
//                    .wrapContentWidth(Alignment.CenterHorizontally)
//                    .wrapContentHeight(Alignment.CenterVertically),
//                text = text2
//            )
//        }
//        Row(modifier = modifier.height(IntrinsicSize.Min)) {
//            Text(
//
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(8.dp)
//                    .wrapContentWidth(Alignment.CenterHorizontally)
//                    .wrapContentHeight(Alignment.CenterVertically),
//                text = text1,
//            )
//
//            Divider(color = Color.Black, modifier = Modifier.padding(8.dp)
//                .fillMaxHeight()
//                .width(1.dp)
//            )
//            Text(
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(8.dp)
//                    .wrapContentWidth(Alignment.CenterHorizontally)
//                    .wrapContentHeight(Alignment.CenterVertically),
//                text = text2
//            )
//        }
//    }

}

@ExperimentalFoundationApi
@Preview
@Composable
fun TwoTextsPreview() {

        Surface {
            TwoTexts(text1 = "Hiweweweweweewewewewewewewe", text2 = "there ")
        }
}

