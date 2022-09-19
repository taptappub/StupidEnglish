package io.taptap.uikit.group

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.taptap.uikit.AverageTitle
import io.taptap.uikit.theme.StupidEnglishTheme

@Composable
fun GroupItemHeader(
    title: String,
    button: String,
    onButtonClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        AverageTitle(
            text = title,
            maxLines = 1,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = button,
            textAlign = TextAlign.Left,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable {
                onButtonClicked()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupItemHeaderPreview() {
    StupidEnglishTheme {
        GroupItemHeader(
            title = "title",
            button = "button",
            onButtonClicked = {}
        )
    }
}