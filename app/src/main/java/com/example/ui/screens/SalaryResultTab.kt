package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ImplementationGroup
import com.example.data.JsiPayData
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.util.BengaliFormat
import com.example.util.PrintAndShareHelper
import com.example.viewmodel.*

@Composable
fun SalaryResultTab(
    state: CalculatorUiState,
    viewModel: CalculatorViewModel
) {
    val bn = state.useBengaliDigits
    val context = LocalContext.current
    val pay = state.payFixationResult ?: return
    val phased = state.phasedArrearsResult ?: return
    val allow = state.allowancesResult ?: return

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 1. Top Hero Card (Matches Screenshot 1 Top)
        val heroGradient = Brush.linearGradient(
            colors = listOf(Color(0xFF172C20), Color(0xFF1F3823))
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("salary_hero_card"),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.45f)),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .background(heroGradient)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.userName.ifBlank { "সদস্য (${state.serviceBranch.bnLabel})" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    Text(
                        text = pay.rankShortTitle,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = CeremonialGold
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "জানুয়ারি ২০২৮ থেকে সকল ভাতাসহ মাসিক বেতন পাবেন",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryMuted
                    )
                    Text(
                        text = BengaliFormat.formatBdt(allow.netMonthlyPay.toDouble(), bn),
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 36.sp),
                        fontWeight = FontWeight.Bold,
                        color = CeremonialGold,
                        modifier = Modifier.testTag("hero_jan_2028_net_pay")
                    )
                }

                // 3 Sub-boxes: Jul 2026 | Jan 2027 | Jul 2027
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HeroPhaseMiniBox(
                        title = "জুলাই ২০২৬ (${if (phased.group == ImplementationGroup.GROUP_A) "৪০%" else "৫০%"})",
                        basicAmountText = "মূল: ${BengaliFormat.formatBdt(phased.phase1Pay.toDouble(), bn)}",
                        netAmountText = "নিট: ${BengaliFormat.formatBdt(phased.phase1TakeHome.toDouble(), bn)}",
                        modifier = Modifier.weight(1f)
                    )
                    HeroPhaseMiniBox(
                        title = "জানুয়ারি ২০২৭ (${if (phased.group == ImplementationGroup.GROUP_A) "৭০%" else "৭৫%"})",
                        basicAmountText = "মূল: ${BengaliFormat.formatBdt(phased.phase2Pay.toDouble(), bn)}",
                        netAmountText = "নিট: ${BengaliFormat.formatBdt(phased.phase2TakeHome.toDouble(), bn)}",
                        modifier = Modifier.weight(1f)
                    )
                    HeroPhaseMiniBox(
                        title = "জুলাই ২০২৭ (১০০%)",
                        basicAmountText = "মূল: ${BengaliFormat.formatBdt(phased.phase3Pay.toDouble(), bn)}",
                        netAmountText = "নিট: ${BengaliFormat.formatBdt(phased.phase3TakeHome.toDouble(), bn)}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (pay.personalPay > 0L) {
            NoticeBanner(
                title = "ব্যক্তিগত বেতন (Personal Pay)",
                body = "পূর্বের মূল বেতন স্কেলের সর্বোচ্চ ধাপ অতিক্রম করায় অতিরিক্ত ${BengaliFormat.formatBdt(pay.personalPay.toDouble(), bn)} ব্যক্তিগত বেতন হিসাবে প্রদেয় হইবে।",
                isWarning = true
            )
        }

        // 2. Arrears Card ("বকেয়া হিসাব" — Matches Screenshot 1 Bottom)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("arrears_summary_card"),
            shape = RoundedCornerShape(20.dp),
            color = RegimentalCard,
            border = BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = EmeraldContainerDark,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.5f)),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "বকেয়া হিসাব",
                                tint = EmeraldTextLight,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "বকেয়া হিসাব",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                        Text(
                            text = phased.dateRangeSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = BengaliFormat.formatBdt(phased.netPayableArrears.toDouble(), bn),
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 34.sp),
                        fontWeight = FontWeight.Bold,
                        color = EmeraldTextLight,
                        modifier = Modifier.testTag("net_arrears_value")
                    )
                    Text(
                        text = phased.arrearsDurationSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = RegimentalFieldBg,
                    border = BorderStroke(1.dp, RegimentalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ArrearsBreakdownRow(
                            title = "মূল বেতন বৃদ্ধির বকেয়া",
                            subtitle = "নতুন মূল বেতন − পুরোনো মূল বেতন, প্রতি মাসে",
                            amount = "+ ${BengaliFormat.formatBdt(phased.grossArrears.toDouble(), bn)}",
                            amountColor = EmeraldTextLight
                        )
                        ArrearsBreakdownRow(
                            title = "বিশেষ সুবিধা (প্রণোদনা) সমন্বয়",
                            subtitle = "বিলুপ্ত হলেও জুলাই ২০২৬ থেকে তোলা হয়েছে (${BengaliFormat.formatBdt(phased.monthlyBiseshSubidha.toDouble(), bn)}/মাস), তাই বকেয়া থেকে কাটা যাবে",
                            amount = "− ${BengaliFormat.formatBdt(phased.totalBiseshSubidhaDeducted.toDouble(), bn)}",
                            amountColor = AlertCrimson
                        )
                        HorizontalDivider(color = RegimentalBorder)
                        ArrearsBreakdownRow(
                            title = "মোট বকেয়া",
                            subtitle = null,
                            amount = BengaliFormat.formatBdt(phased.netPayableArrears.toDouble(), bn),
                            amountColor = EmeraldTextLight,
                            titleColor = EmeraldTextLight,
                            boldTitle = true
                        )
                        HorizontalDivider(color = RegimentalBorder.copy(alpha = 0.6f))
                        ArrearsBreakdownRow(
                            title = "সম্ভাব্য জিপিএফ কর্তন",
                            subtitle = "মূল বেতন বৃদ্ধির বকেয়ার ${BengaliFormat.formatNumber(state.gpfPercent.toLong(), bn)}%; এটি আপনার জিপিএফ হিসাবে জমা থাকবে",
                            amount = "− ${BengaliFormat.formatBdt(phased.probableGpfOnArrears.toDouble(), bn)}",
                            amountColor = TextSecondaryMuted
                        )
                    }
                }

                val selectedMonth = JsiPayData.implementationStartMonths.getOrNull(state.receiveMonthIndex)
                    ?: JsiPayData.implementationStartMonths.first()
                DropdownSelectorField(
                    label = "নতুন হারে প্রথম বেতন কোন মাসে পেয়েছেন?",
                    selectedText = selectedMonth.first,
                    options = JsiPayData.implementationStartMonths.indices.toList(),
                    optionLabel = { JsiPayData.implementationStartMonths[it].first },
                    onSelect = { viewModel.selectReceiveMonthIndex(it) },
                    testTag = "salary_tab_arrears_month_dropdown"
                )
            }
        }

        // 3. 8-Step Pay Fixation Card ("মূল বেতন নির্ধারণ বিবরণী" — Matches Screenshot 4)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("pay_fixation_steps_card"),
            shape = RoundedCornerShape(20.dp),
            color = RegimentalCard,
            border = BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = "মূল বেতন নির্ধারণ বিবরণী",
                            style = MaterialTheme.typography.labelMedium,
                            color = CeremonialGold,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "০১ জুলাই ২০২৬-এ মূল বেতন নির্ধারণ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CeremonialGoldMuted.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.7f))
                    ) {
                        Text(
                            text = pay.appendixShortLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CeremonialGold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.userName.ifBlank { "সদস্য" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryMuted
                    )
                    Text(
                        text = pay.rankShortTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondaryMuted
                    )
                }

                HorizontalDivider(color = RegimentalBorder)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    pay.eightSteps.forEach { step ->
                        val rowBg = if (step.isHighlightedRow) Color(0xFF1E2D24) else Color.Transparent
                        val amtColor = when (step.valueType) {
                            StepValueType.NORMAL -> TextPrimaryLight
                            StepValueType.NEGATIVE -> AlertCrimson
                            StepValueType.POSITIVE -> EmeraldTextLight
                            StepValueType.HIGHLIGHT_GOLD -> CeremonialGold
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = rowBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    color = CeremonialGoldMuted.copy(alpha = 0.65f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.45f)),
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = step.stepBn,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = CeremonialGold
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = step.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimaryLight
                                    )
                                    Text(
                                        text = step.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondaryMuted
                                    )
                                }
                                Text(
                                    text = step.formattedValue,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = amtColor
                                )
                            }
                        }
                    }
                }

                // Bottom Highlight Box ("০১ জুলাই ২০২৬ থেকে নতুন মূল বেতন (ইনক্রিমেন্টসহ)")
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CeremonialGoldMuted.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "০১ জুলাই ২০২৬ থেকে নতুন মূল বেতন\n(ইনক্রিমেন্টসহ)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = CeremonialGold,
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        )
                        Text(
                            text = BengaliFormat.formatBdt(pay.newBasicPay.toDouble(), bn),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = CeremonialGold
                        )
                    }
                }
            }
        }

        // 4. Total Basic Pay Increase Comparison Table Card (Matches Screenshot 5)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("comparison_table_card"),
            shape = RoundedCornerShape(20.dp),
            color = RegimentalCard,
            border = BorderStroke(1.dp, RegimentalBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RegimentalFieldBg,
                    border = BorderStroke(1.dp, RegimentalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .background(CeremonialGold)
                        )
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "আপনার মোট মূল বেতন বৃদ্ধি = নতুন মূল বেতন − পুরোনো মূল বেতন",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimaryLight
                            )
                            Text(
                                text = "= ${BengaliFormat.formatBdt(pay.newBasicPay.toDouble(), bn)} − ${BengaliFormat.formatBdt(pay.oldBasicPay.toDouble(), bn)} = ${BengaliFormat.formatBdt(phased.increase2026.toDouble(), bn)}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CeremonialGold
                            )
                        }
                    }
                }

                // 4-Column Table Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("সময়কাল", style = MaterialTheme.typography.labelSmall, color = TextSecondaryMuted, modifier = Modifier.weight(1.4f))
                    Text("বৃদ্ধির হার", style = MaterialTheme.typography.labelSmall, color = TextSecondaryMuted, textAlign = TextAlign.Center, modifier = Modifier.weight(0.9f))
                    Text("বৃদ্ধির পরিমাণ", style = MaterialTheme.typography.labelSmall, color = TextSecondaryMuted, textAlign = TextAlign.End, modifier = Modifier.weight(1.1f))
                    Text("প্রাপ্য মূল বেতন", style = MaterialTheme.typography.labelSmall, color = TextSecondaryMuted, textAlign = TextAlign.End, modifier = Modifier.weight(1.2f))
                }
                HorizontalDivider(color = RegimentalBorder)

                val p1RateStr = if (phased.group == ImplementationGroup.GROUP_A) "৪০%" else "৫০%"
                val p2RateStr = if (phased.group == ImplementationGroup.GROUP_A) "৭০%" else "৭৫%"
                val p1Inc = (phased.phase1Pay - phased.oldBasicPay).coerceAtLeast(0L)
                val p2Inc = (phased.phase2Pay - phased.oldBasicPay).coerceAtLeast(0L)
                val p3Inc = (phased.phase3Pay - phased.oldBasicPay).coerceAtLeast(0L)

                ComparisonTableRow(
                    period = "জুলাই ২০২৬",
                    sub = "প্রথম ধাপ",
                    rate = p1RateStr,
                    inc = "+ ${BengaliFormat.formatBdt(p1Inc.toDouble(), bn)}",
                    basic = BengaliFormat.formatBdt(phased.phase1Pay.toDouble(), bn)
                )
                HorizontalDivider(color = RegimentalBorder.copy(alpha = 0.5f))
                ComparisonTableRow(
                    period = "জানুয়ারি ২০২৭",
                    sub = null,
                    rate = p2RateStr,
                    inc = "+ ${BengaliFormat.formatBdt(p2Inc.toDouble(), bn)}",
                    basic = BengaliFormat.formatBdt(phased.phase2Pay.toDouble(), bn)
                )
                HorizontalDivider(color = RegimentalBorder.copy(alpha = 0.5f))
                ComparisonTableRow(
                    period = "জুলাই ২০২৭",
                    sub = if (phased.phase3Pay > phased.newBasicPay) "পূর্ণ বেতন + ইনক্রিমেন্ট" else "পূর্ণ বেতন",
                    rate = "১০০%",
                    inc = "+ ${BengaliFormat.formatBdt(p3Inc.toDouble(), bn)}",
                    basic = BengaliFormat.formatBdt(phased.phase3Pay.toDouble(), bn)
                )

                Text(
                    text = "বৃদ্ধির পরিমাণ = মোট মূল বেতন বৃদ্ধির নির্ধারিত হার, যা ৩০ জুন ২০২৬-এর মূল বেতনের সাথে যোগ হয়।",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldContainerDark.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = EmeraldTextLight,
                            modifier = Modifier.size(18.dp)
                        )
                        val groupExplain = if (phased.group == ImplementationGroup.GROUP_B) {
                            "সার্জেন্ট ও তদনিম্ন পদের জন্য বর্ধিত মূল বেতন ধাপে ধাপে দেওয়া হবে: ৫০% → ৭৫% → ১০০%। বিশেষ প্রণোদনা ০১ জুলাই ২০২৬ থেকে বাদ যাবে।"
                        } else {
                            "অফিসার ও জেসিও (ডব্লিউও এবং তদূর্ধ্ব) পদের জন্য বর্ধিত মূল বেতন ধাপে ধাপে দেওয়া হবে: ৪০% → ৭০% → ১০০%। বিশেষ প্রণোদনা ০১ জুলাই ২০২৬ থেকে বাদ যাবে।"
                        }
                        Text(
                            text = groupExplain,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimaryLight
                        )
                    }
                }
            }
        }

        // 4B. Separated All-In Phased Grand Totals Card (Basic + Allowances - GPF per % phase)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("phased_all_in_totals_card"),
            shape = RoundedCornerShape(20.dp),
            color = RegimentalCard,
            border = BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "সব মিলিয়ে মোট টাকার হিসাব (যখন যত % কার্যকর হবে)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CeremonialGold
                    )
                    Text(
                        text = "মূল বেতন + সকল ভাতা − জিপিএফ কর্তন (${BengaliFormat.formatNumber(state.gpfPercent.toLong(), bn)}%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryMuted
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    phased.milestones.forEach { m ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (m.isFinalPhase) EmeraldContainerDark.copy(alpha = 0.55f) else RegimentalFieldBg,
                            border = BorderStroke(
                                1.dp,
                                if (m.isFinalPhase) EmeraldAccent.copy(alpha = 0.6f) else RegimentalBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("phased_total_item_${m.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                        Text(
                                            text = m.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimaryLight
                                        )
                                        Text(
                                            text = m.dateRange,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondaryMuted
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = if (m.isFinalPhase) EmeraldContainerDark else CeremonialGoldMuted.copy(alpha = 0.6f),
                                        border = BorderStroke(
                                            1.dp,
                                            if (m.isFinalPhase) EmeraldAccent.copy(alpha = 0.6f) else CeremonialGold.copy(alpha = 0.6f)
                                        )
                                    ) {
                                        Text(
                                            text = m.badgeText,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (m.isFinalPhase) EmeraldTextLight else CeremonialGold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(color = RegimentalBorder.copy(alpha = 0.55f))

                                KeyValueRow("মূল বেতন", BengaliFormat.formatBdt(m.basicPay.toDouble(), bn))
                                KeyValueRow("সকল ভাতা (মোট)", BengaliFormat.formatBdt(m.allowancesTotal.toDouble(), bn))
                                KeyValueRow("মোট (Gross)", BengaliFormat.formatBdt(m.grossMonthlyPay.toDouble(), bn))
                                KeyValueRow(
                                    "বাদ: জিপিএফ (${BengaliFormat.formatNumber(state.gpfPercent.toLong(), bn)}%)",
                                    "− ${BengaliFormat.formatBdt(m.gpfDeduction.toDouble(), bn)}"
                                )
                                HorizontalDivider(color = RegimentalBorder.copy(alpha = 0.55f))
                                KeyValueRow(
                                    "সর্বমোট নিট (হাতে পাবেন)",
                                    BengaliFormat.formatBdt(m.takeHomeAfterDeductions.toDouble(), bn),
                                    highlight = true
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Vertical Timeline ("প্রতিটি ধাপের বেতন ও ভাতা" — Matches Screenshots 3, 5, 7)
        Text(
            text = "প্রতিটি ধাপের বেতন ও ভাতা",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryLight,
            modifier = Modifier.padding(top = 4.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            phased.milestones.forEachIndexed { index, milestone ->
                TimelineMilestoneCard(
                    milestone = milestone,
                    bn = bn,
                    gpfPercent = state.gpfPercent,
                    isLast = index == phased.milestones.lastIndex
                )
            }
        }

        // 6. Annual Allowances Card (Screenshot 2 Top)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = RegimentalCard,
            border = BorderStroke(1.dp, RegimentalBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "বাংলা নববর্ষ ভাতা (মূল বেতনের ১৫%)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryMuted,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    Text(
                        text = BengaliFormat.formatBdt(allow.noboborshoAnnual.toDouble(), bn),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                }
                HorizontalDivider(color = RegimentalBorder.copy(alpha = 0.6f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "শ্রান্তি ও বিনোদন ভাতা",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryMuted
                    )
                    Text(
                        text = "প্রচলিত বিধি অনুযায়ী",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryLight
                    )
                }
            }
        }

        // 7. Action Row: [PDF], [অবসরের হিসাব ->], and [সংরক্ষণ] (Screenshot 2 Middle + Local DB Snapshot)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { PrintAndShareHelper.printSummary(context, viewModel.buildFullSummaryText()) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CeremonialGold,
                    contentColor = CeremonialGoldTextOnGold
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("salary_pdf_btn")
            ) {
                Icon(Icons.Default.Download, contentDescription = "পিডিএফ", modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("পিডিএফ", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    viewModel.syncPayToAllowancesAndPension()
                    viewModel.selectTab(CalculatorTab.PENSION)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldAccent,
                    contentColor = Color(0xFF062116)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1.25f)
                    .height(50.dp)
                    .testTag("salary_to_pension_btn")
            ) {
                Text("অবসরের হিসাব", fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }

        OutlinedButton(
            onClick = {
                viewModel.saveCurrentSnapshot()
                Toast.makeText(context, "হিসাবটি লোকাল ডাটাবেজে সংরক্ষণ করা হয়েছে", Toast.LENGTH_SHORT).show()
            },
            border = BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.6f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldTextLight),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_snapshot_btn")
        ) {
            Icon(Icons.Default.BookmarkAdd, contentDescription = "হিসাব সংরক্ষণ", modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("লোকাল ডাটাবেজে সংরক্ষণ করুন (রিপোর্ট ট্যাবে দেখুন)", fontWeight = FontWeight.SemiBold)
        }

        // 8. Share With Colleagues & Developer Credit Card (Preserving Àbdüllāh Aĺ Hỗŝŝâîň)
        ShareWithColleaguesCard(
            onShareClick = {
                PrintAndShareHelper.shareSummary(context, viewModel.buildFullSummaryText())
            }
        )

        DeveloperCreditCard()
    }
}

@Composable
private fun HeroPhaseMiniBox(
    title: String,
    basicAmountText: String,
    netAmountText: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F1B14),
        border = BorderStroke(1.dp, RegimentalBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryMuted,
                lineHeight = 14.sp,
                minLines = 2
            )
            Text(
                text = basicAmountText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight,
                maxLines = 1
            )
            Text(
                text = netAmountText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = EmeraldTextLight,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ArrearsBreakdownRow(
    title: String,
    subtitle: String?,
    amount: String,
    amountColor: Color,
    titleColor: Color = TextPrimaryLight,
    boldTitle: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (boldTitle) FontWeight.Bold else FontWeight.Medium,
                color = titleColor
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted
                )
            }
        }
        Text(
            text = amount,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}

@Composable
private fun ComparisonTableRow(
    period: String,
    sub: String?,
    rate: String,
    inc: String,
    basic: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.4f)) {
            Text(
                text = period,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryLight
            )
            if (!sub.isNullOrBlank()) {
                Text(
                    text = sub,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
            }
        }
        Text(
            text = rate,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimaryLight,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.9f)
        )
        Text(
            text = inc,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = EmeraldTextLight,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.1f)
        )
        Text(
            text = basic,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryLight,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.2f)
        )
    }
}

@Composable
private fun TimelineMilestoneCard(
    milestone: PhaseMilestoneDetail,
    bn: Boolean,
    gpfPercent: Int,
    isLast: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val nodeColor = if (milestone.isFinalPhase) EmeraldAccent else CeremonialGold

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Vertical Rail + Circle Ring
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(22.dp)
                .fillMaxHeight()
        ) {
            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(RegimentalBgDark)
                    .border(2.5.dp, nodeColor, CircleShape)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(nodeColor.copy(alpha = 0.6f), EmeraldAccent.copy(alpha = 0.35f))
                            )
                        )
                )
            }
        }

        // Phase Card Body
        Surface(
            modifier = Modifier
                .weight(1f)
                .testTag("milestone_card_${milestone.id}"),
            shape = RoundedCornerShape(18.dp),
            color = RegimentalCard,
            border = BorderStroke(
                1.dp,
                if (milestone.isFinalPhase) EmeraldAccent.copy(alpha = 0.45f) else RegimentalBorder
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = milestone.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                        Text(
                            text = milestone.dateRange,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (milestone.isFinalPhase) EmeraldContainerDark else CeremonialGoldMuted.copy(alpha = 0.45f),
                        border = BorderStroke(
                            1.dp,
                            if (milestone.isFinalPhase) EmeraldAccent.copy(alpha = 0.6f) else CeremonialGold.copy(alpha = 0.6f)
                        )
                    ) {
                        Text(
                            text = milestone.badgeText,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (milestone.isFinalPhase) EmeraldTextLight else CeremonialGold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }

                // Tile 1: Basic Pay under JSI
                TimelineInnerTile(
                    numberBn = if (bn) "১" else "1",
                    badgeBg = CeremonialGoldMuted,
                    badgeColor = CeremonialGold,
                    title = "জেএসআই অনুসারে প্রাপ্য মূল বেতন",
                    value = BengaliFormat.formatBdt(milestone.basicPay.toDouble(), bn),
                    valueColor = TextPrimaryLight,
                    sideNote = if (milestone.basicDiffFromPrevious > 0L) {
                        "+${BengaliFormat.formatBdt(milestone.basicDiffFromPrevious.toDouble(), bn)}"
                    } else if (milestone.isFinalPhase) {
                        BengaliFormat.formatBdt(0.0, bn)
                    } else null,
                    subtitle = milestone.basicSubtitle
                )

                // Tile 2: Net gain vs previous period
                val gainText = if (milestone.netGainAmount >= 0L) {
                    "+${BengaliFormat.formatBdt(milestone.netGainAmount.toDouble(), bn)}"
                } else {
                    "−${BengaliFormat.formatBdt(kotlin.math.abs(milestone.netGainAmount).toDouble(), bn)}"
                }
                TimelineInnerTile(
                    numberBn = if (bn) "২" else "2",
                    badgeBg = EmeraldContainerDark,
                    badgeColor = EmeraldTextLight,
                    title = milestone.netGainTitle,
                    value = gainText,
                    valueColor = if (milestone.netGainAmount >= 0L) EmeraldTextLight else AlertCrimson,
                    sideNote = null,
                    subtitle = milestone.netGainSubtitle
                )

                // Tile 3: Total take-home pay with allowances
                TimelineInnerTile(
                    numberBn = if (bn) "৩" else "3",
                    badgeBg = Color(0xFF1D2834),
                    badgeColor = Color(0xFF7CB7F6),
                    title = milestone.takeHomeTitle,
                    value = BengaliFormat.formatBdt(milestone.takeHomeAfterDeductions.toDouble(), bn),
                    valueColor = TextPrimaryLight,
                    sideNote = null,
                    subtitle = null
                )

                // Expandable Detailed Item Breakdown Button
                Surface(
                    onClick = { expanded = !expanded },
                    shape = RoundedCornerShape(12.dp),
                    color = RegimentalFieldBg,
                    border = BorderStroke(1.dp, RegimentalBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("milestone_expand_${milestone.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (expanded) "বিস্তারিত হিসাব সংকুচিত করুন" else "বিস্তারিত হিসাব দেখুন",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = CeremonialGold
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "বিস্তারিত হিসাব",
                            tint = CeremonialGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = expanded) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = RegimentalFieldBg,
                        border = BorderStroke(1.dp, RegimentalBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            milestone.itemBreakdown.forEach { (label, amt) ->
                                KeyValueRow(label, BengaliFormat.formatBdt(amt.toDouble(), bn))
                            }
                            HorizontalDivider(color = RegimentalBorder)
                            KeyValueRow(
                                "মোট বেতন-ভাতা (Gross)",
                                BengaliFormat.formatBdt(milestone.grossMonthlyPay.toDouble(), bn)
                            )
                            KeyValueRow(
                                "বাদ: জিপিএফ কর্তন (${BengaliFormat.formatNumber(gpfPercent.toLong(), bn)}%)",
                                "− ${BengaliFormat.formatBdt(milestone.gpfDeduction.toDouble(), bn)}"
                            )
                            KeyValueRow(
                                "নিট প্রদেয় (হাতে পাবেন)",
                                BengaliFormat.formatBdt(milestone.takeHomeAfterDeductions.toDouble(), bn),
                                highlight = true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineInnerTile(
    numberBn: String,
    badgeBg: Color,
    badgeColor: Color,
    title: String,
    value: String,
    valueColor: Color,
    sideNote: String?,
    subtitle: String?
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = RegimentalFieldBg,
        border = BorderStroke(1.dp, RegimentalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = badgeBg,
                shape = RoundedCornerShape(9.dp),
                modifier = Modifier.size(30.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = numberBn,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = valueColor
                    )
                    if (!sideNote.isNullOrBlank()) {
                        Text(
                            text = sideNote,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldTextLight
                        )
                    }
                }
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryMuted
                    )
                }
            }
        }
    }
}
