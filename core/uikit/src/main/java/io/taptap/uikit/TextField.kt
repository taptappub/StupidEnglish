package io.taptap.uikit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.taptap.uikit.theme.NunitoFontFamily

@Composable
fun TextField(
    value: String,
    labelValue: String,
    hintValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth(),
    labelColor: Color = MaterialTheme.colorScheme.secondary,
    isError: Boolean = false
) {
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            label = { Text(text = labelValue) },
            onValueChange = onValueChange,
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = NunitoFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            ),
            shape = RoundedCornerShape(12.dp),
            isError = isError,
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colorScheme.onSurface,
                backgroundColor = if (isError) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
                cursorColor = MaterialTheme.colorScheme.onSurface,
                placeholderColor = MaterialTheme.colorScheme.onSurface,

                disabledLabelColor = labelColor,
                errorLabelColor = labelColor,
                focusedLabelColor = labelColor,
                unfocusedLabelColor = labelColor,

                errorCursorColor = MaterialTheme.colorScheme.onSurface,

                disabledBorderColor = if (isError) Color.Red else Color.Gray,
                focusedBorderColor = if (isError) Color.Red else Color.Gray,
                unfocusedBorderColor = if (isError) Color.Red else Color.Gray,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        if (isError) {
            Text(
                text = hintValue,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}