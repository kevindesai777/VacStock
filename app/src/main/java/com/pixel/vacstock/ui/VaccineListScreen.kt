package com.pixel.vacstock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.pixel.vacstock.R
import com.pixel.vacstock.data.Vaccine
import com.pixel.vacstock.data.hasName
import com.pixel.vacstock.ui.theme.stockRow
import kotlin.math.roundToInt

@Composable
fun VaccineListScreen(
    vaccines: List<Vaccine>,
    onIncrement: (id: String) -> Unit,
    onDecrement: (id: String) -> Unit,
    onAdd: (name: String, count: Int) -> Unit,
    onEdit: (id: String, name: String, count: Int) -> Unit,
    onDelete: (id: String) -> Unit,
) {
    var adding by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { StockTopBar(vaccines = vaccines, onAddClick = { adding = true }) },
    ) { innerPadding ->
        if (vaccines.isEmpty()) {
            EmptyState(Modifier.padding(innerPadding))
        } else {
            StockList(
                vaccines = vaccines,
                contentPadding = innerPadding,
                onIncrement = onIncrement,
                onDecrement = onDecrement,
                onRowClick = { editingId = it },
                onDelete = onDelete,
            )
        }
    }

    if (adding) {
        VaccineDialog(
            title = stringResource(R.string.add_vaccine),
            confirmLabel = stringResource(R.string.add),
            initialName = "",
            initialCount = null,
            isNameTaken = { vaccines.hasName(it) },
            onConfirm = { name, count ->
                onAdd(name, count)
                adding = false
            },
            onDismiss = { adding = false },
        )
    }

    vaccines.find { it.id == editingId }?.let { vaccine ->
        key(vaccine.id) {
            VaccineDialog(
                title = stringResource(R.string.edit_vaccine),
                confirmLabel = stringResource(R.string.save),
                initialName = vaccine.name,
                initialCount = vaccine.count,
                isNameTaken = { vaccines.hasName(it, exceptId = vaccine.id) },
                onConfirm = { name, count ->
                    onEdit(vaccine.id, name, count)
                    editingId = null
                },
                onDismiss = { editingId = null },
                onDelete = {
                    editingId = null
                    onDelete(vaccine.id)
                },
            )
        }
    }
}

@Composable
private fun StockTopBar(vaccines: List<Vaccine>, onAddClick: () -> Unit) {
    val doses = vaccines.sumOf { it.count }
    val summary = pluralStringResource(R.plurals.vaccine_count, vaccines.size, vaccines.size) +
        " · " + pluralStringResource(R.plurals.dose_count, doses, doses)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
            )
            // End inset lines the add button up with the rows' + buttons (16dp list + 12dp row).
            .padding(start = 16.dp, end = 28.dp, top = 8.dp, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        FilledIconButton(onClick = onAddClick, modifier = Modifier.size(48.dp)) {
            Icon(VacStockIcons.Add, contentDescription = stringResource(R.string.add_vaccine))
        }
    }
}

@Composable
private fun StockList(
    vaccines: List<Vaccine>,
    contentPadding: PaddingValues,
    onIncrement: (id: String) -> Unit,
    onDecrement: (id: String) -> Unit,
    onRowClick: (id: String) -> Unit,
    onDelete: (id: String) -> Unit,
) {
    val layoutDirection = LocalLayoutDirection.current
    // At most one row shows its Delete button at a time, and scrolling closes it.
    var revealedId by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { scrolling -> if (scrolling) revealedId = null }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = contentPadding.calculateStartPadding(layoutDirection) + 16.dp,
            end = contentPadding.calculateEndPadding(layoutDirection) + 16.dp,
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
    ) {
        itemsIndexed(vaccines, key = { _, vaccine -> vaccine.id }) { index, vaccine ->
            val shape = rowShape(index, vaccines.lastIndex)
            SwipeToRevealDelete(
                revealed = revealedId == vaccine.id,
                onRevealedChange = { open ->
                    if (open) revealedId = vaccine.id else if (revealedId == vaccine.id) revealedId = null
                },
                onDelete = { onDelete(vaccine.id) },
                shape = shape,
                modifier = Modifier.animateItem(),
            ) {
                VaccineRow(
                    vaccine = vaccine,
                    shape = shape,
                    showDivider = index > 0,
                    onIncrement = { onIncrement(vaccine.id) },
                    onDecrement = { onDecrement(vaccine.id) },
                    onClick = { onRowClick(vaccine.id) },
                )
            }
        }
    }
}

private enum class RowReveal { Closed, Open }

/** Width of the Delete button a swipe uncovers. */
private val DeleteButtonWidth = 96.dp

/**
 * Swipe a row toward the start edge to uncover a Delete button. Nothing is deleted until that
 * button is tapped, so a stray fling can't lose a vaccine. Screen readers get a "Delete" action
 * instead, since swiping is awkward with TalkBack.
 */
@Composable
private fun SwipeToRevealDelete(
    revealed: Boolean,
    onRevealedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val deleteLabel = stringResource(R.string.delete)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val revealPx = with(LocalDensity.current) { DeleteButtonWidth.toPx() }
    val state = remember(revealPx) {
        AnchoredDraggableState(
            initialValue = RowReveal.Closed,
            anchors = DraggableAnchors {
                RowReveal.Closed at 0f
                RowReveal.Open at -revealPx
            },
        )
    }
    val currentOnRevealedChange by rememberUpdatedState(onRevealedChange)
    LaunchedEffect(state) {
        snapshotFlow { state.settledValue }.collect { currentOnRevealedChange(it == RowReveal.Open) }
    }
    // Another row was opened, or the list scrolled: slide this one shut.
    LaunchedEffect(revealed) {
        if (!revealed && state.settledValue == RowReveal.Open) state.animateTo(RowReveal.Closed)
    }
    // Composed only while uncovered, so screen readers don't find a hidden button on every row.
    val uncovered by remember(state) { derivedStateOf { state.offset <= -1f } }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .semantics {
                customActions = listOf(CustomAccessibilityAction(deleteLabel) { onDelete(); true })
            },
    ) {
        if (uncovered) {
            Box(Modifier.matchParentSize(), contentAlignment = Alignment.CenterEnd) {
                Column(
                    modifier = Modifier
                        .width(DeleteButtonWidth)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.error)
                        .clickable(role = Role.Button, onClick = onDelete),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(VacStockIcons.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.onError)
                    Text(
                        text = deleteLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onError,
                    )
                }
            }
        }
        // The row narrows rather than sliding, so the vaccine's name stays readable next to the
        // button. Being narrower also leaves the button's area free to receive the tap.
        Box(
            Modifier
                .layout { measurable, constraints ->
                    val reveal = (-state.requireOffset()).roundToInt().coerceIn(0, constraints.maxWidth)
                    val placeable = measurable.measure(constraints.copy(maxWidth = constraints.maxWidth - reveal))
                    layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
                }
                .anchoredDraggable(state, reverseDirection = isRtl, orientation = Orientation.Horizontal),
        ) {
            content()
        }
    }
}

/** Rows stack into one rounded card: only the first and last rows round their outer corners. */
private fun rowShape(index: Int, lastIndex: Int): Shape {
    val top = if (index == 0) 16.dp else 0.dp
    val bottom = if (index == lastIndex) 16.dp else 0.dp
    return RoundedCornerShape(topStart = top, topEnd = top, bottomStart = bottom, bottomEnd = bottom)
}

@Composable
private fun VaccineRow(
    vaccine: Vaccine,
    shape: Shape,
    showDivider: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val outOfStock = vaccine.count == 0
    Surface(color = MaterialTheme.colorScheme.stockRow, shape = shape, modifier = modifier.fillMaxWidth()) {
        Column {
            if (showDivider) HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Row(
                modifier = Modifier.heightIn(min = 64.dp).padding(end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Only the name opens the editor, so a near-miss on a +/- button can't.
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 64.dp)
                        .clickable(onClickLabel = stringResource(R.string.edit_vaccine), onClick = onClick)
                        .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = vaccine.name,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (outOfStock) {
                        Text(
                            text = stringResource(R.string.out_of_stock),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
                OutlinedIconButton(
                    onClick = onDecrement,
                    enabled = !outOfStock,
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(VacStockIcons.Remove, contentDescription = stringResource(R.string.remove_one, vaccine.name))
                }
                Text(
                    text = vaccine.count.toString(),
                    style = MaterialTheme.typography.titleLarge.copy(fontFeatureSettings = "tnum"),
                    color = if (outOfStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(min = 44.dp),
                )
                FilledTonalIconButton(onClick = onIncrement, modifier = Modifier.size(48.dp)) {
                    Icon(VacStockIcons.Add, contentDescription = stringResource(R.string.add_one, vaccine.name))
                }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.empty_list),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
