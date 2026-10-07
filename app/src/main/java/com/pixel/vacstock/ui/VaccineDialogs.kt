package com.pixel.vacstock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pixel.vacstock.R
import com.pixel.vacstock.data.MAX_COUNT
import com.pixel.vacstock.data.MAX_NAME_LENGTH

/**
 * Add or edit one vaccine: its name and an exact dose count (handy when a new box arrives).
 * Pass [onDelete] to show a Delete button; [initialCount] null leaves the count field empty.
 */
@Composable
internal fun VaccineDialog(
    title: String,
    confirmLabel: String,
    initialName: String,
    initialCount: Int?,
    isNameTaken: (String) -> Boolean,
    onConfirm: (name: String, count: Int) -> Unit,
    onDismiss: () -> Unit,
    onDelete: (() -> Unit)? = null,
) {
    var name by rememberSaveable { mutableStateOf(initialName) }
    var countText by rememberSaveable { mutableStateOf(initialCount?.toString().orEmpty()) }
    val trimmedName = name.trim()
    val nameTaken = trimmedName.isNotEmpty() && isNameTaken(trimmedName)
    val canConfirm = trimmedName.isNotEmpty() && !nameTaken
    val confirm = { if (canConfirm) onConfirm(trimmedName, countText.toIntOrNull() ?: 0) }

    val nameFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        if (initialName.isEmpty()) nameFocus.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(MAX_NAME_LENGTH) },
                    label = { Text(stringResource(R.string.vaccine_name)) },
                    singleLine = true,
                    isError = nameTaken,
                    supportingText = if (nameTaken) {
                        { Text(stringResource(R.string.already_in_list)) }
                    } else {
                        null
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth().focusRequester(nameFocus),
                )
                OutlinedTextField(
                    value = countText,
                    onValueChange = { countText = cleanCount(it) },
                    label = { Text(stringResource(R.string.doses_in_stock)) },
                    placeholder = { Text("0") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { confirm() }),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = confirm, enabled = canConfirm) { Text(confirmLabel) }
        },
        dismissButton = {
            Row {
                if (onDelete != null) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                }
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
            }
        },
    )
}

private val MAX_COUNT_DIGITS = MAX_COUNT.toString().length

/** Keeps ASCII digits only, at most five, and drops leading zeros ("010" becomes "10"). */
internal fun cleanCount(input: String): String {
    val digits = input.filter { it in '0'..'9' }.take(MAX_COUNT_DIGITS)
    return if (digits.length > 1) digits.trimStart('0').ifEmpty { "0" } else digits
}
