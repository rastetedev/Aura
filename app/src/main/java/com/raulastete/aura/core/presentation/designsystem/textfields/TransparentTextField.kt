package com.raulastete.aura.core.presentation.designsystem.textfields

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.raulastete.aura.core.presentation.designsystem.theme.AuraTheme

@Composable
fun TransparentTextField(
    text: String,
    onValueChange: (String) -> Unit,
    hintText: String,
    modifier: Modifier = Modifier,
    hintColor: Color = MaterialTheme.colorScheme.outlineVariant,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    ),
    maxLines: Int = Int.MAX_VALUE,
    singleLine: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    BasicTextField(
        value = text,
        onValueChange = onValueChange,
        singleLine = singleLine,
        maxLines = maxLines,
        textStyle = textStyle,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        modifier = modifier,
        decorationBox = { innerTextField ->
           Box(contentAlignment = Alignment.CenterStart){
               if (text.isBlank()) {
                   Text(
                       text = hintText,
                       style = textStyle,
                       color = hintColor
                   )
               }
               innerTextField()
           }
        }
    )
}

@Preview
@Composable
private fun TransparentTextFieldPreview() {
    AuraTheme {
        TransparentTextField(
            text = "Hello World!",
            onValueChange = {},
            hintText = "Hint text"
        )
    }
}