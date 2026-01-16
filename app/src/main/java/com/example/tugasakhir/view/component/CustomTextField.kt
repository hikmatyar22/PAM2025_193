package com.example.tugasakhir.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue     
import androidx.compose.runtime.remember 
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation        
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugasakhir.R

/**
 * Custom Text Field with consistent styling and error handling.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true
) {
    val primaryColor = colorResource(id = R.color.primary_blue)
    val errorColor = colorResource(id = R.color.error_red)
    val grayLightColor = colorResource(id = R.color.gray_light)
    val strokeWidth = androidx.compose.ui.res.dimensionResource(id = R.dimen.input_stroke_width)

    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = if (isError) errorColor else colorResource(id = R.color.gray)) },
            leadingIcon = { 
                Icon(
                    leadingIcon, 
                    contentDescription = null, 
                    tint = if (isError) errorColor else primaryColor
                ) 
            },
            trailingIcon = trailingIcon,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            interactionSource = interactionSource,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent, // Hide default
                unfocusedIndicatorColor = Color.Transparent, // Hide default
                errorIndicatorColor = Color.Transparent, // Hide default
                cursorColor = primaryColor,
            ),
            isError = isError
        )
        
        // Custom Indicator Line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp) // Adjust if needed to match text field internal padding
                .height(strokeWidth)
                .background(
                    color = when {
                        isError -> errorColor
                        isFocused -> primaryColor
                        else -> grayLightColor
                    }
                )
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = errorColor,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
