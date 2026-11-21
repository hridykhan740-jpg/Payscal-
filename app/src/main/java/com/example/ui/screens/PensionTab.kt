package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.data.JsiPayData
import com.example.data.MedicalAgeBracket
import com.example.data.PersonnelCategory
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.util.BengaliFormat
import com.example.util.PrintAndShareHelper
import com.example.viewmodel.CalculatorUiState
import com.example.viewmodel.CalculatorViewModel
import com.example.viewmodel.PensionSubTab
import com.example.viewmodel.RetiringType

@Composable
fun PensionTab(
    state: CalculatorUiState,
    viewModel: CalculatorViewModel
) {
    val bn = state.useBengaliDigits
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SegmentedPillToggle(
            options = PensionSubTab.entries,
            selected = state.pensionSubTab,
            label = {
                if (it == PensionSubTab.EXISTING) "পূর্বের অবসরভোগী (অনুঃ ২৭)" else "সম্ভাব্য / অবসরগামী (অনুঃ ৫, ১৫, ২৮)"
            },
            onSelect = { viewModel.selectPensionSubTab(it) },
            testTagPrefix = "pension_subtab"
        )

        if (state.pensionSubTab == PensionSubTab.EXISTING) {
            ExistingPensionerSection(state = state, viewModel = viewModel, bn = bn)
        } else {
            RetiringPersonnelSection(state = state, viewModel = viewModel, bn = bn)
        }

        ShareWithColleaguesCard(
            onShareClick = {
                PrintAndShareHelper.shareSummary(context, viewModel.buildFullSummaryText())
            }
        )

        DeveloperCreditCard()
    }
}

@Composable
private fun ExistingPensionerSection(
    state: CalculatorUiState,
    viewModel: CalculatorViewModel,
    bn: Boolean
) {
    val res = state.existingPensionResult ?: return

    SummaryHeroCard(
        badgeText = "${res.slabLabel} (+${BengaliFormat.formatNumber(res.increaseRatePercent.toLong(), bn)}%)",
        primaryLabel = "নতুন পূর্ণ নিট পেনশন (০১ জুলাই ২০২৭ শতভাগ পর্যায়ে)",
        primaryValue = BengaliFormat.formatBdt(res.newFullNetPension.toDouble(), bn),
        secondaryItems = listOf(
            "রেশন ও চিকিৎসা সহ মোট" to BengaliFormat.formatBdt(res.totalMonthlyWithAllowancesFull.toDouble(), bn),
            "জুলাই ২০২৭ (+৫% বৃদ্ধি)" to BengaliFormat.formatBdt(res.july2027IncrementedPension.toDouble(), bn)
        )
    )

    SectionCard(
        title = "পূর্বের অবসরভোগী ও পারিবারিক পেনশনার (অনুচ্ছেদ ২৭)",
        subtitle = "Retired Before 01 July 2026 / Family Pensioner",
        icon = Icons.Default.Elderly
    ) {
        NumberInputField(
            label = "৩০ জুন ২০২৬ তারিখে প্রাপ্ত নিট পেনশন",
            required = true,
            value = state.oldNetPensionInput,
            onValueChange = { viewModel.updateOldNetPension(it) },
            supportingText = "স্ল্যাব অনুযায়ী ন্যূনতম ও সর্বোচ্চ সীমা স্বয়ংক্রিয়ভাবে প্রযোজ্য হইবে",
            testTag = "old_net_pension_input"
        )

        DropdownSelectorField(
            label = "অবসরভোগীর বয়স (চিকিৎসা ভাতা - অনুচ্ছেদ ২৩)",
            selectedText = state.pensionerAgeBracket.bnLabel,
            options = MedicalAgeBracket.entries,
            optionLabel = { it.bnLabel },
            onSelect = { viewModel.selectPensionerAgeBracket(it) },
            testTag = "pensioner_age_dropdown"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StepperControl(
                label = "প্রাপ্তবয়স্ক (১২+ বৎসর)",
                valueText = BengaliFormat.formatNumber(state.adultRationMembers.toLong(), bn),
                onDecrement = { viewModel.updateAdultRationMembers(state.adultRationMembers - 1) },
                onIncrement = { viewModel.updateAdultRationMembers(state.adultRationMembers + 1) },
                testTagPrefix = "adult_ration",
                modifier = Modifier.weight(1f)
            )
            StepperControl(
                label = "অপ্রাপ্তবয়স্ক (<১২ বৎসর)",
                valueText = BengaliFormat.formatNumber(state.childRationMembers.toLong(), bn),
                onDecrement = { viewModel.updateChildRationMembers(state.childRationMembers - 1) },
                onIncrement = { viewModel.updateChildRationMembers(state.childRationMembers + 1) },
                testTagPrefix = "child_ration",
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider(color = RegimentalBorder)

        KeyValueRow("ধাপ-১ (০১ জুলাই – ৩১ ডিসে ২০২৬)", BengaliFormat.formatBdt(res.phase1Pension.toDouble(), bn))
        KeyValueRow("ধাপ-২ (০১ জানু – ৩০ জুন ২০২৭)", BengaliFormat.formatBdt(res.phase2Pension.toDouble(), bn))
        KeyValueRow("ধাপ-৩ (০১ জুলাই ২০২৭ মূল নিট পেনশন)", BengaliFormat.formatBdt(res.phase3Pension.toDouble(), bn))
        KeyValueRow("০১ জুলাই ২০২৭ (+৫% বার্ষিক ইনক্রিমেন্ট সহ)", BengaliFormat.formatBdt(res.july2027IncrementedPension.toDouble(), bn), highlight = true)
        KeyValueRow("মাসিক রেশন ভাতা (১,০০০ / ৫০০)", BengaliFormat.formatBdt(res.rationAllowance.toDouble(), bn))
        KeyValueRow("মাসিক চিকিৎসা ভাতা (০১ জানু ২০২৮ হইতে)", BengaliFormat.formatBdt(res.medicalAllowance.toDouble(), bn))
        KeyValueRow("সর্বমোট মাসিক প্রাপ্তি (নিট পেনশন + রেশন + চিকিৎসা)", BengaliFormat.formatBdt(res.totalMonthlyWithAllowancesFull.toDouble(), bn), highlight = true)
        KeyValueRow("বাংলা নববর্ষ ভাতা (বৎসরে ১ বার ১৫%)", BengaliFormat.formatBdt(res.noboborshoAnnual.toDouble(), bn))
    }

    FormulaBreakdownCard(
        steps = res.breakdownSteps,
        testTag = "existing_pension_breakdown"
    )
}

@Composable
private fun RetiringPersonnelSection(
    state: CalculatorUiState,
    viewModel: CalculatorViewModel,
    bn: Boolean
) {
    val res = state.retiringPensionResult ?: return
    val context = LocalContext.current
    var showAddPromotionDialog by remember { mutableStateOf(false) }

    // Screenshot 6 Header Pill + Two-tone Title + Projected Retirement Card
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            color = CeremonialGoldMuted.copy(alpha = 0.5f),
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = CeremonialGold,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "অবসর ও পেনশন",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = CeremonialGold
                )
            }
        }

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = TextPrimaryLight)) {
                    append("অবসরে আপনার ")
                }
                withStyle(SpanStyle(color = EmeraldTextLight)) {
                    append("সম্ভাব্য পেনশন")
                }
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "অবসরের সম্ভাব্য তারিখ ও তখনকার পদবি দিন। পথে কোনো পদোন্নতির সম্ভাবনা থাকলে সেটাও যোগ করুন।",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryMuted
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = RegimentalCard,
        border = BorderStroke(1.dp, RegimentalBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DateBoxRow(
                label = "পেনশনে যাওয়ার সম্ভাব্য তারিখ",
                required = true,
                day = state.retireDay,
                month = state.retireMonth,
                year = state.retireYear,
                onDayChange = { viewModel.updateRetireDate(day = it) },
                onMonthChange = { viewModel.updateRetireDate(month = it) },
                onYearChange = { viewModel.updateRetireDate(year = it) },
                testTagPrefix = "retire"
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "সম্ভাব্য পদোন্নতি (তারিখসহ)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimaryLight
                )

                state.projectedPromotions.forEach { promo ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = RegimentalFieldBg,
                        border = BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = promo.rankTitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CeremonialGold
                                )
                                Text(
                                    text = "তারিখ: ${promo.day}-${promo.month}-${promo.year}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted
                                )
                            }
                            IconButton(
                                onClick = { viewModel.removeProjectedPromotion(promo.id) },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "পদোন্নতি মুছুন",
                                    tint = AlertCrimson
                                )
                            }
                        }
                    }
                }

                Surface(
                    onClick = { showAddPromotionDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    color = RegimentalFieldBg,
                    border = BorderStroke(1.dp, RegimentalBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("add_promotion_btn")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "পদোন্নতি যোগ করুন",
                            tint = TextPrimaryLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "পদোন্নতি যোগ করুন",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimaryLight
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StepperControl(
                    label = "রেশন: প্রাপ্তবয়স্ক সদস্য",
                    valueText = BengaliFormat.formatNumber(state.adultRationMembers.toLong(), bn),
                    onDecrement = { viewModel.updateAdultRationMembers(state.adultRationMembers - 1) },
                    onIncrement = { viewModel.updateAdultRationMembers(state.adultRationMembers + 1) },
                    testTagPrefix = "retiring_adult_ration",
                    modifier = Modifier.weight(1f)
                )
                StepperControl(
                    label = "রেশন: ১২ বছরের নিচের সন্তান",
                    valueText = BengaliFormat.formatNumber(state.childRationMembers.toLong(), bn),
                    onDecrement = { viewModel.updateChildRationMembers(state.childRationMembers - 1) },
                    onIncrement = { viewModel.updateChildRationMembers(state.childRationMembers + 1) },
                    testTagPrefix = "retiring_child_ration",
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "নিজেসহ পরিবারের প্রাপ্তবয়স্ক সদস্য গুনুন। ১২ বছর ও তার বেশি বয়সী সন্তানও প্রাপ্তবয়স্ক হিসেবে ধরা হয়।",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryMuted
            )

            Button(
                onClick = {
                    viewModel.applyProjectedFuturePensionInputs()
                    Toast.makeText(context, "সম্ভাব্য অবসর ও পেনশন হিসাব আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldAccent,
                    contentColor = Color(0xFF062116)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("calculate_future_pension_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = "পেনশন হিসাব করুন",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "পেনশন হিসাব করুন",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    SummaryHeroCard(
        badgeText = "${res.projectedLastRankTitle} • চাকরিকাল ${BengaliFormat.formatDecimal(res.projectedQualifyingYears, 1, bn)} বৎসর",
        primaryLabel = "এককালীন কম্যুটেড গ্র্যাচুইটি (Commuted Value)",
        primaryValue = BengaliFormat.formatBdt(res.lumpSumCommutedValue.toDouble(), bn),
        secondaryItems = listOf(
            "অর্জিত গ্রস পেনশন" to BengaliFormat.formatBdt(res.grossPension.toDouble(), bn),
            "মাসিক নিট পেনশন" to BengaliFormat.formatBdt(res.netMonthlyPension.toDouble(), bn),
            "রেশন ও চিকিৎসাসহ মোট" to BengaliFormat.formatBdt(res.projectedTotalMonthlyWithAllowances.toDouble(), bn)
        )
    )

    SectionCard(
        title = "কর্ম উপাদান ও কম্যুটেশন বিস্তারিত (অনুঃ ৫, ১৫, ২৮)",
        subtitle = "Retiring Service Element & Commutation Details",
        icon = Icons.Default.AccountBalance
    ) {
        DropdownSelectorField(
            label = "সদস্যের ধরন",
            selectedText = state.retiringType.bnLabel,
            options = RetiringType.entries,
            optionLabel = { it.bnLabel },
            onSelect = { viewModel.selectRetiringType(it) },
            testTag = "retiring_type_dropdown"
        )

        NumberInputField(
            label = "শেষ গৃহীত বেতন (Last Drawn Pay + উপাদান)",
            required = true,
            value = state.retiringLastPayInput,
            onValueChange = { viewModel.updateRetiringLastPay(it) },
            supportingText = "অবসর তারিখের সম্ভাব্য মূল বেতন বা কাস্টম মান",
            testTag = "retiring_last_pay_input"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NumberInputField(
                label = "যোগ্য চাকরি (বৎসর)",
                value = state.retiringQualifyingYearsInput,
                onValueChange = { viewModel.updateRetiringQualifyingYears(it) },
                suffixText = "বৎসর",
                allowDecimal = true,
                testTag = "retiring_qualifying_years_input",
                modifier = Modifier.weight(1f)
            )

            if (state.retiringType == RetiringType.OFFICER) {
                NumberInputField(
                    label = "পদবির নির্ধারিত চাকরি",
                    value = state.rankPrescribedYearsInput,
                    onValueChange = { viewModel.updateRankPrescribedYears(it) },
                    suffixText = "বৎসর",
                    allowDecimal = true,
                    testTag = "rank_prescribed_years_input",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "পরবর্তী জন্মদিনে বয়স (অনুচ্ছেদ ২৮)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted
                )
                Text(
                    text = "${BengaliFormat.formatNumber(state.retiringAgeNextBirthday.toLong(), bn)} বৎসর (গুণক: ${BengaliFormat.formatDecimal(res.commutationMultiplier, 3, bn)})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = CeremonialGold
                )
            }
            Slider(
                value = state.retiringAgeNextBirthday.toFloat(),
                onValueChange = { viewModel.updateRetiringAgeNextBirthday(it.toInt()) },
                valueRange = 20f..80f,
                steps = 59,
                colors = SliderDefaults.colors(
                    thumbColor = CeremonialGold,
                    activeTrackColor = CeremonialGold,
                    inactiveTrackColor = RegimentalBorder
                ),
                modifier = Modifier.testTag("retiring_age_slider")
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "পেনশন সমর্পণের হার (প্রচলিত ৫০%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted
                )
                Text(
                    text = "${BengaliFormat.formatNumber(state.commutationSurrenderPercent.toLong(), bn)}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = CeremonialGold
                )
            }
            Slider(
                value = state.commutationSurrenderPercent.toFloat(),
                onValueChange = { viewModel.updateCommutationSurrenderPercent(it.toInt()) },
                valueRange = 0f..100f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = CeremonialGold,
                    activeTrackColor = CeremonialGold,
                    inactiveTrackColor = RegimentalBorder
                ),
                modifier = Modifier.testTag("commutation_percent_slider")
            )
        }

        HorizontalDivider(color = RegimentalBorder)

        KeyValueRow("অর্জিত গ্রস পেনশন", BengaliFormat.formatBdt(res.grossPension.toDouble(), bn), highlight = true)
        KeyValueRow("সমর্পিত মাসিক অংশ (${BengaliFormat.formatNumber(res.surrenderPercent.toLong(), bn)}%)", BengaliFormat.formatBdt(res.commutedMonthlyPortion.toDouble(), bn))
        KeyValueRow("মাসিক নিট পেনশন", BengaliFormat.formatBdt(res.netMonthlyPension.toDouble(), bn), highlight = true)
        KeyValueRow("মাসিক রেশন ভাতা", BengaliFormat.formatBdt(res.rationAllowance.toDouble(), bn))
        if (res.medicalAllowance > 0L) {
            KeyValueRow("মাসিক চিকিৎসা ভাতা", BengaliFormat.formatBdt(res.medicalAllowance.toDouble(), bn))
        }
        KeyValueRow("কম্যুটেশন গুণক (বয়স ${BengaliFormat.formatNumber(state.retiringAgeNextBirthday.toLong(), bn)})", BengaliFormat.formatDecimal(res.commutationMultiplier, 3, bn))
        KeyValueRow("এককালীন কম্যুটেড অর্থ", BengaliFormat.formatBdt(res.lumpSumCommutedValue.toDouble(), bn), highlight = true)
    }

    FormulaBreakdownCard(
        steps = res.breakdownSteps,
        testTag = "retiring_pension_breakdown"
    )

    if (showAddPromotionDialog) {
        AddPromotionDialog(
            state = state,
            onDismiss = { showAddPromotionDialog = false },
            onAdd = { rankId, rankTitle, d, m, y ->
                viewModel.addProjectedPromotion(rankId, rankTitle, d, m, y)
                showAddPromotionDialog = false
            }
        )
    }
}

@Composable
private fun AddPromotionDialog(
    state: CalculatorUiState,
    onDismiss: () -> Unit,
    onAdd: (rankId: String, rankTitle: String, day: String, month: String, year: String) -> Unit
) {
    val rankOptions: List<Pair<String, String>> = remember(state.category, state.officerAppendix, state.retiringType) {
        if (state.category == PersonnelCategory.OFFICER || state.retiringType == RetiringType.OFFICER) {
            JsiPayData.getOfficerRanks(state.officerAppendix).map { it.id to it.bnName }
        } else {
            JsiPayData.nonOfficerRanks.map { it.id to it.bnName }
        }
    }
    var selectedRank by remember(rankOptions) { mutableStateOf(rankOptions.first()) }
    var pDay by remember { mutableStateOf("01") }
    var pMonth by remember { mutableStateOf("01") }
    var pYear by remember { mutableStateOf("2030") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RegimentalCard,
        title = {
            Text(
                text = "সম্ভাব্য পদোন্নতি যোগ করুন",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CeremonialGold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DropdownSelectorField(
                    label = "পদোন্নতিপ্রাপ্ত পদবি",
                    selectedText = selectedRank.second,
                    options = rankOptions,
                    optionLabel = { it.second },
                    onSelect = { selectedRank = it },
                    testTag = "promo_rank_dropdown"
                )
                DateBoxRow(
                    label = "পদোন্নতির সম্ভাব্য তারিখ",
                    required = true,
                    day = pDay,
                    month = pMonth,
                    year = pYear,
                    onDayChange = { pDay = it },
                    onMonthChange = { pMonth = it },
                    onYearChange = { pYear = it },
                    testTagPrefix = "promo_date"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(
                        selectedRank.first,
                        selectedRank.second,
                        pDay.ifBlank { "01" },
                        pMonth.ifBlank { "01" },
                        pYear.ifBlank { "2030" }
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldAccent,
                    contentColor = Color(0xFF062116)
                ),
                modifier = Modifier.testTag("promo_dialog_save_btn")
            ) {
                Text("যোগ করুন", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = TextSecondaryMuted)
            }
        }
    )
}
