package io.taptap.uikit

import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.taptap.uikit.theme.NunitoFontFamily

@Composable
fun AddTextField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    placeholder: String,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = NunitoFontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = if (value.isEmpty()) {
                TextAlign.Start
            } else {
                TextAlign.Center
            }
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        enabled = enabled,
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onSurface,
            backgroundColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            placeholderColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
            .widthIn(1.dp, Dp.Infinity)
    )
}
