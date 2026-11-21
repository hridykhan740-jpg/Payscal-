package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.util.BengaliFormat

@Composable
fun NumberedSectionCard(
    stepNumberBn: String,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = RegimentalCard,
        border = BorderStroke(1.dp, RegimentalBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = CeremonialGoldMuted,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.45f)),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = stepNumberBn,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CeremonialGold
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }
            HorizontalDivider(color = RegimentalBorder.copy(alpha = 0.7f))
            content()
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = RegimentalCard,
        border = BorderStroke(1.dp, RegimentalBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (icon != null) {
                    Surface(
                        color = CeremonialGoldMuted,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.45f)),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = CeremonialGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }
            HorizontalDivider(color = RegimentalBorder.copy(alpha = 0.7f))
            content()
        }
    }
}

@Composable
fun <T> SegmentedPillToggle(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    icon: ((T) -> ImageVector?)? = null,
    onSelect: (T) -> Unit,
    testTagPrefix: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = RegimentalFieldBg,
        border = BorderStroke(1.dp, RegimentalBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEachIndexed { idx, item ->
                val isSelected = item == selected
                Surface(
                    onClick = { onSelect(item) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) CeremonialGold else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("${testTagPrefix}_$idx")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val itemIcon = icon?.invoke(item)
                        if (itemIcon != null) {
                            Icon(
                                imageVector = itemIcon,
                                contentDescription = label(item),
                                tint = if (isSelected) CeremonialGoldTextOnGold else TextSecondaryMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                        }
                        Text(
                            text = label(item),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) CeremonialGoldTextOnGold else TextSecondaryMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateBoxRow(
    label: String,
    required: Boolean = true,
    day: String,
    month: String,
    year: String,
    onDayChange: (String) -> Unit,
    onMonthChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    testTagPrefix: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimaryLight
            )
            if (required) {
                Text(
                    text = " *",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = CeremonialGold
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            DateDigitSegment(
                value = day,
                digitCount = 2,
                caption = "দিন",
                onValueChange = onDayChange,
                testTag = "${testTagPrefix}_day",
                modifier = Modifier.weight(2f)
            )
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("-", color = TextSecondaryMuted, fontWeight = FontWeight.Bold)
            }
            DateDigitSegment(
                value = month,
                digitCount = 2,
                caption = "মাস",
                onValueChange = onMonthChange,
                testTag = "${testTagPrefix}_month",
                modifier = Modifier.weight(2f)
            )
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("-", color = TextSecondaryMuted, fontWeight = FontWeight.Bold)
            }
            DateDigitSegment(
                value = year,
                digitCount = 4,
                caption = "বছর",
                onValueChange = onYearChange,
                testTag = "${testTagPrefix}_year",
                modifier = Modifier.weight(4f)
            )
        }
    }
}

@Composable
private fun DateDigitSegment(
    value: String,
    digitCount: Int,
    caption: String,
    onValueChange: (String) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    var focused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            BasicTextField(
                value = TextFieldValue(text = value, selection = TextRange(value.length)),
                onValueChange = { tfv ->
                    val digits = tfv.text.filter { it.isDigit() }
                    val updated = if (digits.length > digitCount) {
                        digits.takeLast(digitCount)
                    } else {
                        digits
                    }
                    onValueChange(updated)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                cursorBrush = SolidColor(CeremonialGold),
                textStyle = TextStyle(color = Color.Transparent, fontSize = 16.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .onFocusChanged { focused = it.isFocused }
                    .testTag(testTag),
                decorationBox = {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (i in 0 until digitCount) {
                            val ch = value.getOrNull(i)?.toString() ?: ""
                            val borderColor = if (focused) CeremonialGold else EmeraldAccent.copy(alpha = 0.35f)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(RegimentalFieldBg)
                                    .border(
                                        width = if (focused) 1.5.dp else 1.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ch.ifEmpty { "0" },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (ch.isEmpty()) TextSecondaryMuted.copy(alpha = 0.35f) else TextPrimaryLight
                                )
                            }
                        }
                    }
                }
            )
        }
        Text(
            text = caption,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StepperControl(
    label: String,
    valueText: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    testTagPrefix: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = RegimentalFieldBg,
            border = BorderStroke(1.dp, RegimentalBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(onClick = onDecrement)
                        .testTag("${testTagPrefix}_minus"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "−",
                        style = MaterialTheme.typography.titleLarge,
                        color = CeremonialGold,
                        fontWeight = FontWeight.Bold
                    )
                }
                VerticalDivider(
                    modifier = Modifier.height(26.dp),
                    color = RegimentalBorder
                )
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = valueText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                }
                VerticalDivider(
                    modifier = Modifier.height(26.dp),
                    color = RegimentalBorder
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(onClick = onIncrement)
                        .testTag("${testTagPrefix}_plus"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleLarge,
                        color = CeremonialGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryHeroCard(
    badgeText: String,
    primaryLabel: String,
    primaryValue: String,
    secondaryItems: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF103024), Color(0xFF1A4333))
    )
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.4f)),
        shadowElevation = 4.dp,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = CeremonialGold.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelMedium,
                        color = CeremonialGold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = primaryLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimaryLight.copy(alpha = 0.85f)
                )
                Text(
                    text = primaryValue,
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimaryLight,
                    fontWeight = FontWeight.Bold
                )
                if (secondaryItems.isNotEmpty()) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.16f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        secondaryItems.forEach { (label, value) ->
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = CeremonialGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormulaBreakdownCard(
    steps: List<String>,
    testTag: String,
    initiallyExpanded: Boolean = true
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.4f)),
        color = RegimentalCard
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .minimumInteractiveComponentSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = "হিসাবের ব্যাখ্যা",
                    tint = CeremonialGold,
                    modifier = Modifier.size(20.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "হিসাবের ব্যাখ্যা",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    Text(
                        text = "Step-by-Step Formula Breakdown",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryMuted
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "সংকুচিত করুন" else "বিস্তারিত দেখুন",
                    tint = TextSecondaryMuted
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(color = RegimentalBorder)
                    Spacer(modifier = Modifier.height(2.dp))
                    steps.forEach { step ->
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimaryLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoticeBanner(
    title: String,
    body: String,
    isWarning: Boolean = false
) {
    val bgColor = if (isWarning) CeremonialGoldMuted.copy(alpha = 0.55f) else EmeraldContainerDark.copy(alpha = 0.7f)
    val tint = if (isWarning) CeremonialGold else EmeraldTextLight
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(1.dp, tint.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted
                )
            }
        }
    }
}

@Composable
fun <T> DropdownSelectorField(
    label: String,
    selectedText: String,
    options: List<T>,
    optionLabel: (T) -> String,
    optionSubtitle: ((T) -> String?)? = null,
    onSelect: (T) -> Unit,
    testTag: String,
    required: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showPickerDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (required) {
                Text(
                    text = " *",
                    style = MaterialTheme.typography.bodySmall,
                    color = CeremonialGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Surface(
            onClick = { showPickerDialog = true },
            shape = RoundedCornerShape(12.dp),
            color = RegimentalFieldBg,
            border = BorderStroke(1.dp, RegimentalBorder),
            modifier = Modifier
                .fillMaxWidth()
                .minimumInteractiveComponentSize()
                .testTag(testTag)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimaryLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "তালিকা খুলুন",
                    tint = CeremonialGold
                )
            }
        }
    }

    if (showPickerDialog) {
        AlertDialog(
            onDismissRequest = { showPickerDialog = false },
            containerColor = RegimentalCard,
            title = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CeremonialGold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(options) { _, option ->
                        val optLabel = optionLabel(option)
                        val isSelected = optLabel == selectedText
                        Surface(
                            onClick = {
                                showPickerDialog = false
                                onSelect(option)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) CeremonialGoldMuted else RegimentalFieldBg,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) CeremonialGold else RegimentalBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text(
                                        text = optLabel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) CeremonialGold else TextPrimaryLight
                                    )
                                    val sub = optionSubtitle?.invoke(option)
                                    if (!sub.isNullOrBlank()) {
                                        Text(
                                            text = sub,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondaryMuted
                                        )
                                    }
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "নির্বাচিত",
                                        tint = CeremonialGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPickerDialog = false }) {
                    Text("বন্ধ করুন", color = TextSecondaryMuted)
                }
            }
        )
    }
}

@Composable
fun TextInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = if (placeholder.isNotBlank()) {
                { Text(placeholder, color = TextSecondaryMuted.copy(alpha = 0.55f)) }
            } else null,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = RegimentalFieldBg,
                unfocusedContainerColor = RegimentalFieldBg,
                focusedBorderColor = CeremonialGold,
                unfocusedBorderColor = RegimentalBorder,
                focusedTextColor = TextPrimaryLight,
                unfocusedTextColor = TextPrimaryLight,
                cursorColor = CeremonialGold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}

@Composable
fun NumberInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    suffixText: String = "৳",
    supportingText: String? = null,
    allowDecimal: Boolean = false,
    required: Boolean = false,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (required) {
                Text(
                    text = " *",
                    style = MaterialTheme.typography.bodySmall,
                    color = CeremonialGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        OutlinedTextField(
            value = value,
            onValueChange = { raw ->
                val filtered = raw.filter {
                    it.isDigit() || (allowDecimal && it == '.')
                }
                onValueChange(filtered)
            },
            placeholder = if (placeholder.isNotBlank()) {
                { Text(placeholder, color = TextSecondaryMuted.copy(alpha = 0.55f)) }
            } else null,
            suffix = {
                if (suffixText.isNotBlank()) {
                    Text(suffixText, color = CeremonialGold, fontWeight = FontWeight.Bold)
                }
            },
            supportingText = if (!supportingText.isNullOrBlank()) {
                { Text(supportingText, style = MaterialTheme.typography.bodySmall, color = TextSecondaryMuted) }
            } else null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (allowDecimal) KeyboardType.Decimal else KeyboardType.Number
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = RegimentalFieldBg,
                unfocusedContainerColor = RegimentalFieldBg,
                focusedBorderColor = CeremonialGold,
                unfocusedBorderColor = RegimentalBorder,
                focusedTextColor = TextPrimaryLight,
                unfocusedTextColor = TextPrimaryLight,
                cursorColor = CeremonialGold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}

@Composable
fun KeyValueRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (highlight) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
            color = if (highlight) CeremonialGold else TextSecondaryMuted,
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        )
        Text(
            text = value,
            style = if (highlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.SemiBold,
            color = if (highlight) CeremonialGold else TextPrimaryLight
        )
    }
}

@Composable
fun ShareWithColleaguesCard(
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0E2C21), Color(0xFF133A2C))
    )
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.45f)),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .background(gradient)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "সহকর্মীদের সাথে শেয়ার করুন",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
                Text(
                    text = "তারাও জানুক নতুন স্কেলে তাদের বেতন ও পেনশন কত হবে",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted
                )
            }
            Button(
                onClick = onShareClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldAccent,
                    contentColor = Color(0xFF062116)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("colleague_share_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "শেয়ার করুন",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "শেয়ার করুন",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DeveloperCreditCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = RegimentalCard,
        border = BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.developer_avatar),
                    contentDescription = "Àbdüllāh Aĺ Hỗŝŝâîň",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, CeremonialGold.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ডেভেলপার পরিচিতি",
                        style = MaterialTheme.typography.labelSmall,
                        color = CeremonialGold
                    )
                    Text(
                        text = "Àbdüllāh Aĺ Hỗŝŝâîň",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                }
            }

            HorizontalDivider(color = RegimentalBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DeveloperIconOnlyButton(
                    icon = Icons.Default.Public,
                    contentDescription = "Facebook",
                    tint = Color(0xFF60A5FA),
                    containerColor = Color(0xFF172554).copy(alpha = 0.7f),
                    borderColor = Color(0xFF3B82F6).copy(alpha = 0.55f),
                    testTag = "dev_facebook_btn",
                    onClick = {
                        openExternalUrl(context, "https://www.facebook.com/share/14vc4Dkbfsi/")
                    }
                )
                DeveloperIconOnlyButton(
                    icon = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "WhatsApp",
                    tint = EmeraldTextLight,
                    containerColor = EmeraldContainerDark.copy(alpha = 0.75f),
                    borderColor = EmeraldAccent.copy(alpha = 0.55f),
                    testTag = "dev_whatsapp_btn",
                    onClick = {
                        copyTextWithToast(context, "WhatsApp ID", "@ali_98khan")
                    }
                )
                DeveloperIconOnlyButton(
                    icon = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Imo",
                    tint = Color(0xFF38BDF8),
                    containerColor = Color(0xFF0C4A6E).copy(alpha = 0.65f),
                    borderColor = Color(0xFF38BDF8).copy(alpha = 0.55f),
                    testTag = "dev_imo_btn",
                    onClick = {
                        copyTextWithToast(context, "Imo ID", "mhossenali998")
                    }
                )
                DeveloperIconOnlyButton(
                    icon = Icons.Default.Email,
                    contentDescription = "Gmail",
                    tint = Color(0xFFFBBF24),
                    containerColor = CeremonialGoldMuted.copy(alpha = 0.65f),
                    borderColor = CeremonialGold.copy(alpha = 0.6f),
                    testTag = "dev_gmail_btn",
                    onClick = {
                        openEmailIntent(context, "mhossenali740@gmail.com")
                    }
                )
            }
        }
    }
}

@Composable
private fun DeveloperIconOnlyButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    containerColor: Color,
    borderColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .size(48.dp)
            .minimumInteractiveComponentSize()
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

private fun openExternalUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        copyTextWithToast(context, "Facebook Link", url)
    }
}

private fun openEmailIntent(context: Context, email: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        copyTextWithToast(context, "Email", email)
    }
}

private fun copyTextWithToast(context: Context, label: String, text: String) {
    try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(context, "$label কপি করা হয়েছে: $text", Toast.LENGTH_SHORT).show()
    } catch (_: Exception) {
        Toast.makeText(context, "$label: $text", Toast.LENGTH_SHORT).show()
    }
}
