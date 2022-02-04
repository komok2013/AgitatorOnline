package ru.edinros.agitator.features.auth

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle

class PhoneVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 10) text.text.substring(0, 10) else text.text
        val out = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Gray)) {
                append("+7 (")
            }
            for (i in trimmed.indices) {
                append(trimmed[i])
                if (i == 2)
                    withStyle(style = SpanStyle(color = Color.Gray)) { append(") ") }
                if (i == 5 || i == 7)
                    withStyle(style = SpanStyle(color = Color.Gray)) {
                        append("-")
                    }
            }
        }
        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset < 3) return offset + 4
                //if (offset == 3) return offset + 6
                if (offset < 6) return offset + 6
                //if (offset == 6) return offset + 7
                if (offset < 8) return offset + 7
                //if (offset == 8) return offset + 8
                if (offset <= 10) return offset + 8
                return 17
            }

            override fun transformedToOriginal(offset: Int): Int {
                return 17
            }
        }
        return TransformedText(out, offsetTranslator)
    }
}

