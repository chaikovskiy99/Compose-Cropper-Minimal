package com.smarttoolfactory.composecropper.preferences

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.RadioButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.slider.ColorfulSlider
import com.smarttoolfactory.slider.MaterialSliderColors
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor

@Composable
internal fun DpSliderSelection(
    value: Dp,
    onValueChange: (Dp) -> Unit,
    lowerBound: Dp,
    upperBound: Dp
) {

    val density = LocalDensity.current
    val strokeWidthPx = density.run { value.toPx() }
    val lowerBoundPx = density.run { lowerBound.toPx() }
    val upperBoundPx = density.run { upperBound.toPx() }

    SliderSelection(
        value = strokeWidthPx,
        onValueChange = {
            onValueChange(
                density.run { it.toDp() }
            )
        },
        valueRange = lowerBoundPx..upperBoundPx
    )
}

@Composable
internal fun SliderWithValueSelection(
    modifier: Modifier = Modifier,
    value: Float,
    title: String = "",
    text: String,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    colors: MaterialSliderColors = MaterialSliderDefaults.materialColors(
        activeTrackColor = SliderBrushColor(MaterialTheme.colorScheme.primary),
        inactiveTrackColor = SliderBrushColor(Color.Transparent),
        thumbColor = SliderBrushColor(MaterialTheme.colorScheme.inversePrimary)
    )
) {
    Column {

        Text(
            text = if (title.isNotEmpty()) "$title $text" else text,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {

            ColorfulSlider(
                modifier = Modifier.weight(1f),
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                colors = colors,
                trackHeight = 11.dp,
                thumbRadius = 14.dp
            )


        }
    }
}

@Composable
internal fun SliderSelection(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    colors: MaterialSliderColors = MaterialSliderDefaults.materialColors(
        activeTrackColor = SliderBrushColor(MaterialTheme.colorScheme.primary),
        inactiveTrackColor = SliderBrushColor(Color.Transparent)
    )
) {
    ColorfulSlider(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        colors = colors,
        borderStroke = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        trackHeight = 11.dp,
        thumbRadius = 14.dp
    )
}

@Composable
internal fun Title(
    text: String,
    fontSize: TextUnit = 20.sp
) {
    Text(
        modifier = Modifier.padding(vertical = 1.dp),
        text = text,
        color = MaterialTheme.colorScheme.primary,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold
    )
}


@Composable
internal fun FullRowSwitch(
    label: String,
    state: Boolean,
    onStateChange: (Boolean) -> Unit
) {

    // Switch with text on right side
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            role = Role.Switch,
            onClick = {
                onStateChange(!state)
            }
        )
        .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(text = label, modifier = Modifier.weight(1f))

        Switch(
            checked = state,
            onCheckedChange = null
        )
    }
}

@Composable
internal fun DialogWithMultipleSelection(
    title: String = "",
    options: List<String>,
    value: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {

    val (selectedOption: Int, onOptionSelected: (Int) -> Unit) = remember {
        mutableStateOf(value)
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {

            // Note that Modifier.selectableGroup()
            // is essential to ensure correct accessibility behavior
            Column(Modifier.selectableGroup()) {
                options.forEachIndexed { index, text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (index == selectedOption),
                                onClick = { onOptionSelected(index) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (index == selectedOption),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = text,
                            fontSize = 18.sp,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedOption)
                }
            ) {
                Text(text = "Accept")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(text = "Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExposedSelectionMenu(
    modifier: Modifier = Modifier,
    index: Int,
    title: String? = null,
    textStyle: TextStyle = TextStyle(
        fontWeight = FontWeight.W600,
        fontSize = 14.sp
    ),
    colors: TextFieldColors = ExposedDropdownMenuDefaults.textFieldColors(
        containerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    ),
    options: List<String>,
    onSelected: (Int) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[index]) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            modifier = modifier,
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { },
            label = {
                title?.let {
                    Text(it)
                }
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = colors,
            textStyle = textStyle
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false

            }
        ) {
            options.forEachIndexed { index: Int, selectionOption: String ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                        onSelected(index)
                    },
                    text = {
                        Text(text = selectionOption)
                    }
                )
            }
        }
    }
}
