package io.taptap.uikit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.taptap.uikit.theme.StupidEnglishTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScope.ResultNotification(
    resultNotificationState: ResultNotification.State,
    modifier: Modifier = Modifier
        .align(Alignment.Center)
        .size(88.dp), //должен быть по центру по умолчанию
) {
    if (resultNotificationState == ResultNotification.State.IDLE) return

    Card(
        shape = RoundedCornerShape(12.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        when (resultNotificationState) {
            ResultNotification.State.SUCCESS -> {
                Image(
                    painter = painterResource(id = R.drawable.ic_tick),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            ResultNotification.State.FAILED -> {
                Icon(
                    tint = MaterialTheme.colorScheme.tertiary,
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            ResultNotification.State.IN_PROGRESS -> {
                CircularProgressIndicator(
                    strokeWidth = 6.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            else -> throw IllegalStateException("Wrong ResultNotification.State ($resultNotificationState)")
        }
    }
}

interface ResultNotification {
    enum class State {
        SUCCESS,
        FAILED,
        IN_PROGRESS,
        IDLE
    }
}

@Preview(showBackground = true)
@Composable
fun ResultNotificationPreview() {
    StupidEnglishTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ResultNotification(
                resultNotificationState = ResultNotification.State.SUCCESS
            )
        }
    }
}