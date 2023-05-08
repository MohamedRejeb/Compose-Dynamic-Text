package com.softylines.composesharetransition

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun DynamicText(
    vararg texts: String,
) {
    val sortedTextList by remember {
        mutableStateOf(texts.sortedByDescending { it.length })
    }
    val textMinWidthMap = remember {
        mutableStateMapOf<Int, Int>()
    }
    var textIndex by remember {
        mutableStateOf(0)
    }
    var textWidthPercentage by remember {
        mutableStateOf(1f)
    }
    var textResult by remember {
        mutableStateOf<TextLayoutResult?>(null)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "-------Debugging Start-------" +
                "\n textResult: ${textResult?.layoutInput?.text} " +
                "\n hasVisualOverflow: ${textResult?.hasVisualOverflow}" +
                "\n textSize: ${textResult?.size}" +
                "\n savedTextSizes: ${textMinWidthMap.toMap()}" +
                "\n -------Debugging End-------" +
                "")

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = sortedTextList[textIndex],
            modifier = Modifier
                .fillMaxWidth(textWidthPercentage)
                .border(1.dp, Color.Red)
            ,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Center,
            maxLines = 1,
            onTextLayout = { textLayoutResult ->
                textResult = textLayoutResult
                if (
                    !textLayoutResult.hasVisualOverflow &&
                    textLayoutResult.layoutInput.text.text == sortedTextList[textIndex]
                ) {
                    textMinWidthMap[textIndex] = min(
                        textMinWidthMap[textIndex] ?: Int.MAX_VALUE,
                        textLayoutResult.size.width
                    )

                    if ((textMinWidthMap[textIndex - 1] ?: Int.MAX_VALUE) < textLayoutResult.size.width) {
                        textIndex--
                    }
                }

                if (
                    textLayoutResult.hasVisualOverflow &&
                    textIndex < sortedTextList.lastIndex &&
                    textLayoutResult.layoutInput.text.text == sortedTextList[textIndex]
                ) {
                    textIndex++
                }
            },
        )

        Spacer(modifier = Modifier.height(20.dp))

        Slider(
            value = textWidthPercentage,
            onValueChange = {
                textWidthPercentage = it
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DynamicTextPreview() {
    DynamicText(
        "Very very long text",
        "Very long text",
        "Medium text",
        "Short",
    )
}
