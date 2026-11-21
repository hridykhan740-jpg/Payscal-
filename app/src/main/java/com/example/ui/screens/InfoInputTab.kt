package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.util.BengaliFormat
import com.example.util.PrintAndShareHelper
import com.example.viewmodel.CalculatorUiState
import com.example.viewmodel.CalculatorViewModel
import com.example.viewmodel.PensionSubTab
import com.example.viewmodel.RetiringType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InfoInputTab(
    state: CalculatorUiState,
    viewModel: CalculatorViewModel
) {
    val bn = state.useBengaliDigits
    val context = LocalContext.current
    val pay = state.payFixationResult

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Role Header & Segmented Toggle ("আপনি কে?")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "আপনি কে?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight
            )
            SegmentedPillToggle(
                options = listOf(false, true),
                selected = state.isPensionerRole,
                label = { isPensioner -> if (isPensioner) "পেনশনভোগী" else "চাকরিরত সদস্য" },
                icon = { isPensioner -> if (isPensioner) Icons.Default.Person else Icons.Default.Shield },
                onSelect = { viewModel.setRolePensioner(it) },
                testTagPrefix = "role_toggle"
            )
        }

        // Card 1: Personal Info
        NumberedSectionCard(
            stepNumberBn = if (bn) "১" else "1",
            title = "ব্যক্তিগত তথ্য",
            subtitle = "নাম, পদবি, জন্মতারিখ ও যোগদানের তারিখ"
        ) {
            TextInputField(
                label = "নাম",
                value = state.userName,
                onValueChange = { viewModel.updateUserName(it) },
                placeholder = "আপনার পূর্ণ নাম লিখুন",
                testTag = "user_name_input"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextInputField(
                    label = "সেনা / সার্ভিস নং (ঐচ্ছিক)",
                    value = state.serviceNo,
                    onValueChange = { viewModel.updateServiceNo(it) },
                    placeholder = "০",
                    testTag = "service_no_input",
                    modifier = Modifier.weight(1f)
                )
                TextInputField(
                    label = "ইউনিট (ঐচ্ছিক)",
                    value = state.unitName,
                    onValueChange = { viewModel.updateUnitName(it) },
                    placeholder = "ইউনিট নাম",
                    testTag = "unit_name_input",
                    modifier = Modifier.weight(1f)
                )
            }

            DropdownSelectorField(
                label = "সার্ভিস / বাহিনী",
                selectedText = state.serviceBranch.bnLabel,
                options = ServiceBranch.entries,
                optionLabel = { "${it.bnLabel} (${it.enLabel})" },
                onSelect = { viewModel.selectServiceBranch(it) },
                testTag = "service_branch_dropdown"
            )

            // Personnel Category Pills
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "সদস্যের শ্রেণি",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted
                )
                SegmentedPillToggle(
                    options = PersonnelCategory.entries,
                    selected = state.category,
                    label = {
                        when (it) {
                            PersonnelCategory.OFFICER -> "অফিসার"
                            PersonnelCategory.CADET -> "ক্যাডেট"
                            PersonnelCategory.JCO_OR -> "জেসিও / ওআর"
                        }
                    },
                    onSelect = { viewModel.selectCategory(it) },
                    testTagPrefix = "category_pill"
                )
            }

            when (state.category) {
                PersonnelCategory.OFFICER -> {
                    DropdownSelectorField(
                        label = "ক্রোড়পত্র / শাখা তালিকা (Appendix)",
                        selectedText = state.officerAppendix.bnTitle,
                        options = OfficerAppendix.entries,
                        optionLabel = { it.bnTitle },
                        optionSubtitle = { it.enSubtitle },
                        onSelect = { viewModel.selectOfficerAppendix(it) },
                        testTag = "officer_appendix_dropdown"
                    )
                    val ranks = JsiPayData.getOfficerRanks(state.officerAppendix)
                    val selectedRank = ranks.find { it.id == state.selectedOfficerRankId } ?: ranks.first()

                    DropdownSelectorField(
                        label = "বর্তমান পদবি",
                        required = true,
                        selectedText = selectedRank.bnName,
                        options = ranks,
                        optionLabel = { it.bnName },
                        optionSubtitle = { it.enName },
                        onSelect = { viewModel.selectOfficerRank(it.id) },
                        testTag = "officer_rank_dropdown"
                    )

                    if (!selectedRank.isFixed) {
                        val availableYears = selectedRank.yearToPay.keys.sorted()
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "কমিশন্ড চাকরির বর্ষ (Service Year)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                availableYears.forEach { yr ->
                                    val selected = state.commissionedYear == yr
                                    Surface(
                                        onClick = { viewModel.selectCommissionedYear(yr) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (selected) CeremonialGold else RegimentalFieldBg,
                                        border = BorderStroke(
                                            1.dp,
                                            if (selected) CeremonialGold else RegimentalBorder
                                        ),
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .testTag("officer_year_$yr")
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${BengaliFormat.formatNumber(yr.toLong(), bn)}ম বর্ষ",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (selected) CeremonialGoldTextOnGold else TextPrimaryLight
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                PersonnelCategory.CADET -> {
                    val selectedCadet = JsiPayData.cadetOptions.find { it.id == state.selectedCadetId }
                        ?: JsiPayData.cadetOptions.first()
                    DropdownSelectorField(
                        label = "বর্তমান পদবি / ক্যাটাগরি",
                        required = true,
                        selectedText = selectedCadet.bnName,
                        options = JsiPayData.cadetOptions,
                        optionLabel = { it.bnName },
                        optionSubtitle = { it.enName },
                        onSelect = { viewModel.selectCadet(it.id) },
                        testTag = "cadet_dropdown"
                    )
                }

                PersonnelCategory.JCO_OR -> {
                    val selectedRank = JsiPayData.nonOfficerRanks.find { it.id == state.selectedNonOfficerRankId }
                        ?: JsiPayData.nonOfficerRanks.first()
                    DropdownSelectorField(
                        label = "বর্তমান পদবি",
                        required = true,
                        selectedText = selectedRank.bnName,
                        options = JsiPayData.nonOfficerRanks,
                        optionLabel = { it.bnName },
                        optionSubtitle = { it.enName },
                        onSelect = { viewModel.selectNonOfficerRank(it.id) },
                        testTag = "non_officer_rank_dropdown"
                    )
                }
            }

            DateBoxRow(
                label = "জন্মতারিখ",
                required = true,
                day = state.dobDay,
                month = state.dobMonth,
                year = state.dobYear,
                onDayChange = { viewModel.updateDob(day = it) },
                onMonthChange = { viewModel.updateDob(month = it) },
                onYearChange = { viewModel.updateDob(year = it) },
                testTagPrefix = "dob"
            )

            DateBoxRow(
                label = "চাকরিতে যোগদান",
                required = true,
                day = state.joinDay,
                month = state.joinMonth,
                year = state.joinYear,
                onDayChange = { viewModel.updateJoinDate(day = it) },
                onMonthChange = { viewModel.updateJoinDate(month = it) },
                onYearChange = { viewModel.updateJoinDate(year = it) },
                testTagPrefix = "join"
            )

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = EmeraldContainerDark.copy(alpha = 0.55f),
                border = BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "হিসাবকৃত বয়স (১ জুলাই ২৬): ${BengaliFormat.formatNumber(state.computedAgeOnEffectiveDate.toLong(), bn)} বৎসর",
                        style = MaterialTheme.typography.labelMedium,
                        color = EmeraldTextLight,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "চাকরিকাল: ${BengaliFormat.formatDecimal(state.computedServiceYears, 1, bn)} বৎসর",
                        style = MaterialTheme.typography.labelMedium,
                        color = CeremonialGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "জন্মতারিখ দিয়ে চিকিৎসা ভাতা ও অবসরের সময় এককালীন পেনশন (আনুতোষিক) বের করা হয়। যোগদানের তারিখ দিয়ে প্রতিরক্ষা সার্ভিস ভাতার ধাপ ও পেনশনযোগ্য চাকরিকাল হিসাব হয়।",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryMuted
            )
        }

        if (!state.isPensionerRole) {
            // Card 2: 30 June 2026 Basic Pay (NPS 2015 Grade/Step + Auto Placement in JSI 01/2026)
            NumberedSectionCard(
                stepNumberBn = if (bn) "২" else "2",
                title = "৩০ জুন ২০২৬-এর মূল বেতন",
                subtitle = "২০১৫ পে স্কেল / জেএসআই ০১/২০১৬ গ্রেড ও ধাপ অনুযায়ী"
            ) {
                val ctx2015 = JsiPayData.get2015GradeContext(
                    gradeId = state.selectedOld2015GradeId,
                    category = state.category,
                    nonOfficerRankId = state.selectedNonOfficerRankId,
                    useBengaliDigits = bn
                )

                when (state.category) {
                    PersonnelCategory.OFFICER -> {
                        Old2015GradeStepSelector(
                            selectedGradeId = state.selectedOld2015GradeId,
                            currentPayInput = state.officerOldBasicInput,
                            ctx2015 = ctx2015,
                            bn = bn,
                            onSelectGrade = { viewModel.selectOld2015Grade(it) },
                            onSelectStep = { viewModel.selectOld2015Step(it) }
                        )

                        NumberInputField(
                            label = "বর্তমান মূল বেতন (৩০ জুন ২০২৬)",
                            required = true,
                            value = state.officerOldBasicInput,
                            onValueChange = { viewModel.updateOfficerOldBasic(it) },
                            supportingText = "উপরে গ্রেড ও ধাপ বাছাই করলে বা নিজে লিখলে নিচে ২০২৬ নতুন বেতন স্বয়ংক্রিয়ভাবে বসে যাবে",
                            testTag = "officer_old_basic_input"
                        )
                    }

                    PersonnelCategory.CADET -> {
                        val selectedCadet = JsiPayData.cadetOptions.find { it.id == state.selectedCadetId }
                            ?: JsiPayData.cadetOptions.first()
                        NoticeBanner(
                            title = "ক্যাডেট নির্ধারিত বেতন (${selectedCadet.bnName})",
                            body = "পূর্বের নির্ধারিত বেতন ${BengaliFormat.formatBdt(selectedCadet.oldFixedPay.toDouble(), bn)} হতে জেএসআই ০১/২০২৬ অনুযায়ী ${BengaliFormat.formatBdt(selectedCadet.fixedPay.toDouble(), bn)} নির্ধারিত।"
                        )
                    }

                    PersonnelCategory.JCO_OR -> {
                        val selectedRank = JsiPayData.nonOfficerRanks.find { it.id == state.selectedNonOfficerRankId }
                            ?: JsiPayData.nonOfficerRanks.first()

                        if (!selectedRank.isRecruitFixed) {
                            if (!selectedRank.isHonoraryFixed) {
                                Old2015GradeStepSelector(
                                    selectedGradeId = state.selectedOld2015GradeId,
                                    currentPayInput = state.oldBasicPayInput,
                                    ctx2015 = ctx2015,
                                    bn = bn,
                                    onSelectGrade = { viewModel.selectOld2015Grade(it) },
                                    onSelectStep = { viewModel.selectOld2015Step(it) }
                                )
                            }

                            NumberInputField(
                                label = "বর্তমান মূল বেতন (৩০ জুন ২০২৬) (৳)",
                                required = true,
                                value = state.oldBasicPayInput,
                                onValueChange = { viewModel.updateOldBasicPay(it) },
                                supportingText = if (selectedRank.isHonoraryFixed) {
                                    "পূর্বের মূল বেতন নির্ধারিত বেতনের বেশি হলে পূর্বের বেতন + ৫০০ টাকা প্রাপ্য"
                                } else {
                                    "জেএসআই ০১/২০১৬ অনুযায়ী ${selectedRank.bnName} প্রারম্ভিক বেতন: ${BengaliFormat.formatBdt(selectedRank.oldScaleStart.toDouble(), bn)}"
                                },
                                testTag = "non_officer_old_basic_input"
                            )
                        } else {
                            NoticeBanner(
                                title = "রিক্রুট / প্রশিক্ষণার্থী নির্ধারিত বেতন",
                                body = "জেএসআই ০১/২০১৬ প্রারম্ভিক ${BengaliFormat.formatBdt(selectedRank.oldScaleStart.toDouble(), bn)} হতে জেএসআই ০১/২০২৬ অনুযায়ী নির্ধারিত বেতন ${BengaliFormat.formatBdt(selectedRank.fixedPay.toDouble(), bn)}।"
                            )
                        }

                        if (!selectedRank.isHonoraryFixed && !selectedRank.isRecruitFixed) {
                            NumberInputField(
                                label = "জেএসআই ০১/২০১৬ প্রারম্ভিক ধাপ",
                                value = state.oldScaleStartOverride,
                                onValueChange = { viewModel.updateOldScaleStart(it) },
                                supportingText = "গ্রেড/পদবি অনুযায়ী স্বয়ংক্রিয়ভাবে প্রি-ফিল করা হয়েছে",
                                testTag = "old_scale_start_input"
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.toggleJuly2026Increment(!state.applyJuly2026Increment)
                                    }
                                    .minimumInteractiveComponentSize()
                                    .padding(vertical = 4.dp)
                                    .testTag("july_increment_checkbox_row"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = state.applyJuly2026Increment,
                                    onCheckedChange = { viewModel.toggleJuly2026Increment(it) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = CeremonialGold,
                                        checkmarkColor = CeremonialGoldTextOnGold
                                    ),
                                    modifier = Modifier.testTag("july_increment_checkbox")
                                )
                                Text(
                                    text = "০১ জুলাই ২০২৬ তারিখে বার্ষিক ইনক্রিমেন্ট প্রযোজ্য (ন্যূনতম ৬ মাস চাকরি)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimaryLight,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Text(
                                text = "জেএসআই ০১/২০২৬ স্কেলের ধাপসমূহ (${BengaliFormat.formatNumber(selectedRank.steps.size.toLong(), bn)}টি ধাপ):",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondaryMuted
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                selectedRank.steps.forEachIndexed { idx, stepAmount ->
                                    val isMatched = pay?.matchedStepIndex == (idx + 1)
                                    Surface(
                                        onClick = {
                                            Toast.makeText(
                                                context,
                                                "ধাপ ${BengaliFormat.formatNumber((idx + 1).toLong(), bn)}: ${BengaliFormat.formatBdt(stepAmount.toDouble(), bn)} (৩০ জুন ২০২৬-এর বেতন অনুযায়ী স্বয়ংক্রিয়ভাবে নির্ধারিত হয়)",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        color = if (isMatched) CeremonialGold else RegimentalFieldBg,
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, if (isMatched) CeremonialGold else RegimentalBorder)
                                    ) {
                                        Text(
                                            text = "${BengaliFormat.formatNumber((idx + 1).toLong(), bn)}: ${BengaliFormat.formatNumber(stepAmount, bn)}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (isMatched) CeremonialGoldTextOnGold else TextSecondaryMuted,
                                            fontWeight = if (isMatched) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Auto-Fixation Live Preview Card inside Card 2
                if (pay != null) {
                    val increaseDiff = (pay.newBasicPay - pay.oldBasicPay).coerceAtLeast(0L)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = EmeraldContainerDark.copy(alpha = 0.65f),
                        border = BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.55f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auto_new_pay_preview_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "নতুন পে স্কেল ২০২৬ (JSI 01/2026) স্বয়ংক্রিয় নির্ধারণ",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldTextLight,
                                    modifier = Modifier.weight(1f).padding(end = 6.dp)
                                )
                                if (pay.matchedStepIndex != null && pay.totalSteps != null) {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = CeremonialGoldMuted,
                                        border = BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.65f))
                                    ) {
                                        Text(
                                            text = "ধাপ ${BengaliFormat.formatNumber(pay.matchedStepIndex.toLong(), bn)}/${BengaliFormat.formatNumber(pay.totalSteps.toLong(), bn)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = CeremonialGold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PreviewPayPill(
                                    label = "পূর্বের মূল (২০১৫)",
                                    value = BengaliFormat.formatBdt(pay.oldBasicPay.toDouble(), bn),
                                    valueColor = TextPrimaryLight,
                                    modifier = Modifier.weight(1f)
                                )
                                PreviewPayPill(
                                    label = "নতুন মূল (১ জুলাই ২৬)",
                                    value = BengaliFormat.formatBdt(pay.newBasicPay.toDouble(), bn),
                                    valueColor = CeremonialGold,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "মূল বেতন বৃদ্ধি: +${BengaliFormat.formatBdt(increaseDiff.toDouble(), bn)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldTextLight
                                )
                                Text(
                                    text = "জুলাই ২০২৭ ধাপ: ${BengaliFormat.formatBdt(pay.july2027BasicPay.toDouble(), bn)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimaryLight
                                )
                            }
                        }
                    }
                }
            }

            // Card 3: Family (Marital Status & Children)
            NumberedSectionCard(
                stepNumberBn = if (bn) "৩" else "3",
                title = "পরিবার",
                subtitle = "বৈবাহিক অবস্থা ও সন্তান"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "বৈবাহিক অবস্থা",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryMuted
                    )
                    SegmentedPillToggle(
                        options = listOf(true, false),
                        selected = state.isMarried,
                        label = { married -> if (married) "বিবাহিত" else "অবিবাহিত" },
                        onSelect = { viewModel.setMaritalStatus(it) },
                        testTagPrefix = "marital_toggle"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StepperControl(
                        label = "সন্তান সংখ্যা (২৩ বছরের নিচে)",
                        valueText = BengaliFormat.formatNumber(state.childrenCount.toLong(), bn),
                        onDecrement = { viewModel.selectChildrenCount(state.childrenCount - 1) },
                        onIncrement = { viewModel.selectChildrenCount(state.childrenCount + 1) },
                        testTagPrefix = "children_count",
                        modifier = Modifier.weight(1f)
                    )
                    StepperControl(
                        label = "নিবন্ধিত প্রতিবন্ধী সন্তান",
                        valueText = BengaliFormat.formatNumber(state.disabledChildrenCount.toLong(), bn),
                        onDecrement = { viewModel.selectDisabledChildrenCount(state.disabledChildrenCount - 1) },
                        onIncrement = { viewModel.selectDisabledChildrenCount(state.disabledChildrenCount + 1) },
                        testTagPrefix = "disabled_children_count",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Card 4: Proficiency, Badges, Appointment & Special Skill Pay
            NumberedSectionCard(
                stepNumberBn = if (bn) "৪" else "4",
                title = "দক্ষতা ও অন্যান্য বেতন উপাদান",
                subtitle = "দক্ষতা বেতন, ব্যাজ ও নিযুক্তি বেতন"
            ) {
                if (state.category == PersonnelCategory.JCO_OR) {
                    val selectedProf = JsiPayData.proficiencyPayOptions.find { it.id == state.proficiencyPayId }
                        ?: JsiPayData.proficiencyPayOptions.first()
                    DropdownSelectorField(
                        label = "দক্ষতা বেতন",
                        selectedText = selectedProf.bnLabel,
                        options = JsiPayData.proficiencyPayOptions,
                        optionLabel = { it.bnLabel },
                        onSelect = { viewModel.selectProficiencyPay(it.id) },
                        testTag = "proficiency_pay_dropdown"
                    )

                    val selectedAppt = JsiPayData.appointmentPayOptions.find { it.id == state.appointmentPayId }
                        ?: JsiPayData.appointmentPayOptions.first()
                    DropdownSelectorField(
                        label = "নিযুক্তি বেতন",
                        selectedText = selectedAppt.bnLabel,
                        options = JsiPayData.appointmentPayOptions,
                        optionLabel = { it.bnLabel },
                        onSelect = { viewModel.selectAppointmentPay(it.id) },
                        testTag = "appointment_pay_dropdown"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        StepperControl(
                            label = "সদাচরণ ব্যাজ",
                            valueText = BengaliFormat.formatNumber(state.goodConductBadges.toLong(), bn),
                            onDecrement = { viewModel.updateGoodConductBadges(state.goodConductBadges - 1) },
                            onIncrement = { viewModel.updateGoodConductBadges(state.goodConductBadges + 1) },
                            testTagPrefix = "good_conduct_badge",
                            modifier = Modifier.weight(1f)
                        )
                        NumberInputField(
                            label = "সার্ভিস ওয়ার্ডিন্যান্স বেতন (৳)",
                            value = state.oldServiceOrdnanceInput,
                            onValueChange = { viewModel.updateOldServiceOrdnance(it) },
                            placeholder = "না থাকলে ফাঁকা",
                            testTag = "service_ordnance_input",
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "বিশেষ দক্ষতা বেতন (থাকলে নির্বাচন করুন)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryMuted
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SpecialSkillPay.entries.forEach { skill ->
                            val selected = skill in state.selectedSpecialSkills
                            Surface(
                                onClick = { viewModel.toggleSpecialSkill(skill) },
                                shape = RoundedCornerShape(50),
                                color = if (selected) CeremonialGoldMuted else RegimentalFieldBg,
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) CeremonialGold else RegimentalBorder
                                ),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("special_skill_${skill.name.lowercase()}")
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = skill.bnLabel,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) CeremonialGold else TextPrimaryLight
                                    )
                                }
                            }
                        }
                    }
                }

                NumberInputField(
                    label = "অন্যান্য নতুন ভাতা (৳/মাস)",
                    value = state.otherNewAllowanceInput,
                    onValueChange = { viewModel.updateOtherNewAllowance(it) },
                    placeholder = "ঐচ্ছিক",
                    testTag = "other_new_allowance_input"
                )

                HorizontalDivider(color = RegimentalBorder.copy(alpha = 0.6f))

                // Custom Optional Allowances Builder
                var draftCustomName by remember { mutableStateOf("") }
                var draftCustomAmount by remember { mutableStateOf("") }
                var draftAllPhases by remember { mutableStateOf(true) }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = RegimentalFieldBg,
                    border = BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "অন্যান্য ভাতা যোগ করুন (ঐচ্ছিক)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CeremonialGold
                            )
                            Text(
                                text = "রেশন/যাতায়াত/ঝুঁকি বা প্রযোজ্য যেকোনো অতিরিক্ত মাসিক ভাতা যোগ করুন",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = draftCustomName,
                                onValueChange = { draftCustomName = it },
                                placeholder = { Text("ভাতার নাম (যেমন: ঝুঁকি ভাতা)", color = TextSecondaryMuted.copy(alpha = 0.6f)) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = RegimentalCard,
                                    unfocusedContainerColor = RegimentalCard,
                                    focusedBorderColor = CeremonialGold,
                                    unfocusedBorderColor = RegimentalBorder,
                                    focusedTextColor = TextPrimaryLight,
                                    unfocusedTextColor = TextPrimaryLight,
                                    cursorColor = CeremonialGold
                                ),
                                modifier = Modifier
                                    .weight(1.4f)
                                    .testTag("custom_allowance_name_input")
                            )
                            OutlinedTextField(
                                value = draftCustomAmount,
                                onValueChange = { raw -> draftCustomAmount = raw.filter { it.isDigit() } },
                                placeholder = { Text("টাকা", color = TextSecondaryMuted.copy(alpha = 0.6f)) },
                                suffix = { Text("৳", color = CeremonialGold, fontWeight = FontWeight.Bold) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = RegimentalCard,
                                    unfocusedContainerColor = RegimentalCard,
                                    focusedBorderColor = CeremonialGold,
                                    unfocusedBorderColor = RegimentalBorder,
                                    focusedTextColor = TextPrimaryLight,
                                    unfocusedTextColor = TextPrimaryLight,
                                    cursorColor = CeremonialGold
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("custom_allowance_amount_input")
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { draftAllPhases = !draftAllPhases }
                                .minimumInteractiveComponentSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = draftAllPhases,
                                onCheckedChange = { draftAllPhases = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = CeremonialGold,
                                    checkmarkColor = CeremonialGoldTextOnGold
                                ),
                                modifier = Modifier.testTag("custom_allowance_all_phases_checkbox")
                            )
                            Text(
                                text = "জুলাই ২০২৬ থেকে সকল ধাপে যোগ করুন",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimaryLight,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    val amt = BengaliFormat.parseSafeNumber(draftCustomAmount)
                                    if (amt > 0L) {
                                        viewModel.addCustomAllowance(draftCustomName, amt, draftAllPhases)
                                        draftCustomName = ""
                                        draftCustomAmount = ""
                                    } else {
                                        Toast.makeText(context, "ভাতার সঠিক পরিমাণ দিন", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CeremonialGold,
                                    contentColor = CeremonialGoldTextOnGold
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("add_custom_allowance_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "যোগ করুন", modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("যোগ করুন", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (state.customAllowances.isNotEmpty()) {
                            HorizontalDivider(color = RegimentalBorder.copy(alpha = 0.6f))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                state.customAllowances.forEach { custom ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = RegimentalCard,
                                        border = BorderStroke(1.dp, RegimentalBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = custom.name,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = TextPrimaryLight
                                                )
                                                Text(
                                                    text = "${BengaliFormat.formatBdt(custom.amount.toDouble(), bn)}/মাস",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = EmeraldTextLight,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Surface(
                                                onClick = { viewModel.toggleCustomAllowancePhase(custom.id) },
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (custom.applyInAllPhases) EmeraldContainerDark else CeremonialGoldMuted,
                                                border = BorderStroke(
                                                    1.dp,
                                                    if (custom.applyInAllPhases) EmeraldAccent.copy(alpha = 0.5f) else CeremonialGold.copy(alpha = 0.5f)
                                                ),
                                                modifier = Modifier
                                                    .minimumInteractiveComponentSize()
                                                    .testTag("toggle_custom_allowance_${custom.id}")
                                            ) {
                                                Box(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = if (custom.applyInAllPhases) "সব ধাপ" else "২০২৮+",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (custom.applyInAllPhases) EmeraldTextLight else CeremonialGold
                                                    )
                                                }
                                            }
                                            IconButton(
                                                onClick = { viewModel.removeCustomAllowance(custom.id) },
                                                modifier = Modifier
                                                    .minimumInteractiveComponentSize()
                                                    .testTag("remove_custom_allowance_${custom.id}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "মুছুন",
                                                    tint = AlertCrimson
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Card 5: Workplace, Accommodation, Arrears & GPF
            NumberedSectionCard(
                stepNumberBn = if (bn) "৫" else "5",
                title = "কর্মস্থল, বাসস্থান, বকেয়া ও জিপিএফ",
                subtitle = "বাড়ি ভাড়া, জিপিএফ ও বকেয়া সমন্বয়ের তথ্য"
            ) {
                DropdownSelectorField(
                    label = "কর্মস্থল / এলাকা (Location Category)",
                    selectedText = state.cityCategory.bnLabel,
                    options = CityCategory.entries,
                    optionLabel = { it.bnLabel },
                    optionSubtitle = { it.enLabel },
                    onSelect = { viewModel.selectCityCategory(it) },
                    testTag = "city_category_dropdown"
                )

                val accommodationOptions = if (state.category == PersonnelCategory.OFFICER) {
                    listOf(
                        AccommodationType.NO_QUARTERS_STANDARD,
                        AccommodationType.NO_QUARTERS_EXTRA_FACILITY,
                        AccommodationType.MARRIED_GOVT_QUARTERS
                    )
                } else {
                    listOf(
                        AccommodationType.NO_QUARTERS_STANDARD,
                        AccommodationType.MARRIED_GOVT_QUARTERS,
                        AccommodationType.UNMARRIED_OR_MESS
                    )
                }
                val currentAccomm = if (state.accommodationType in accommodationOptions) {
                    state.accommodationType
                } else {
                    accommodationOptions.first()
                }

                DropdownSelectorField(
                    label = "বাসস্থান / কোয়ার্টার্স",
                    selectedText = currentAccomm.bnLabel,
                    options = accommodationOptions,
                    optionLabel = { it.bnLabel },
                    optionSubtitle = { it.enLabel },
                    onSelect = { viewModel.selectAccommodationType(it) },
                    testTag = "accommodation_dropdown"
                )

                DropdownSelectorField(
                    label = "পাহাড়ি / হাওড় / দ্বীপ ভাতা (প্রযোজ্য হলে)",
                    selectedText = state.hillHaorCategory.bnLabel,
                    options = HillHaorCategory.entries,
                    optionLabel = { it.bnLabel },
                    onSelect = { viewModel.selectHillHaorCategory(it) },
                    testTag = "hill_haor_dropdown"
                )

                if (pay?.isCplOrBelow == true) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.toggleFreeTiffin(!state.receiveFreeTiffin) }
                            .minimumInteractiveComponentSize()
                            .padding(vertical = 4.dp)
                            .testTag("free_tiffin_checkbox_row"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.receiveFreeTiffin,
                            onCheckedChange = { viewModel.toggleFreeTiffin(it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = CeremonialGold,
                                checkmarkColor = CeremonialGoldTextOnGold
                            ),
                            modifier = Modifier.testTag("free_tiffin_checkbox")
                        )
                        Text(
                            text = "প্রতিষ্ঠান হইতে বিনামূল্যে টিফিন প্রাপ্ত হন",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimaryLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                val selectedMonth = JsiPayData.implementationStartMonths.getOrNull(state.receiveMonthIndex)
                    ?: JsiPayData.implementationStartMonths[4]
                DropdownSelectorField(
                    label = "নতুন বেতন প্রথম উত্তোলনের মাস (বকেয়া হিসাব)",
                    selectedText = selectedMonth.first,
                    options = JsiPayData.implementationStartMonths.indices.toList(),
                    optionLabel = { JsiPayData.implementationStartMonths[it].first },
                    onSelect = { viewModel.selectReceiveMonthIndex(it) },
                    testTag = "arrears_month_dropdown"
                )

                NumberInputField(
                    label = "জুলাই ২০২৬ হইতে উত্তোলিত মাসিক \"বিশেষ সুবিধা\"",
                    value = state.monthlyBiseshSubidhaInput,
                    onValueChange = { viewModel.updateMonthlyBiseshSubidha(it) },
                    placeholder = "খালি রাখলে স্বয়ংক্রিয়ভাবে হিসাব হবে",
                    supportingText = "সার্জেন্ট ও তদনিম্ন: ১৫% (সর্বনিম্ন ২,০০০ ৳) | জেসিও ও অফিসার: ১০% (সর্বনিম্ন ১,৫০০ ৳)",
                    testTag = "bisesh_subidha_input"
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "সাধারণ ভবিষ্য তহবিল (GPF) চাঁদা",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                        Text(
                            text = "${BengaliFormat.formatNumber(state.gpfPercent.toLong(), bn)}%",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CeremonialGold
                        )
                    }
                    Slider(
                        value = state.gpfPercent.toFloat(),
                        onValueChange = { viewModel.updateGpfPercent(it.toInt()) },
                        valueRange = 5f..25f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = CeremonialGold,
                            activeTrackColor = CeremonialGold,
                            inactiveTrackColor = RegimentalBorder
                        ),
                        modifier = Modifier.testTag("gpf_slider")
                    )
                }
            }
        } else {
            // Card 2 for Pensioner Role
            NumberedSectionCard(
                stepNumberBn = if (bn) "২" else "2",
                title = "পেনশন সংক্রান্ত তথ্য",
                subtitle = "পূর্বের অবসরভোগী বা অবসরগামী তথ্য"
            ) {
                SegmentedPillToggle(
                    options = PensionSubTab.entries,
                    selected = state.pensionSubTab,
                    label = {
                        if (it == PensionSubTab.EXISTING) "পূর্বের অবসরভোগী" else "অবসরগামী সদস্য"
                    },
                    onSelect = { viewModel.selectPensionSubTab(it) },
                    testTagPrefix = "info_pension_subtab"
                )

                if (state.pensionSubTab == PensionSubTab.EXISTING) {
                    NumberInputField(
                        label = "৩০ জুন ২০২৬ তারিখে প্রাপ্ত নিট পেনশন",
                        required = true,
                        value = state.oldNetPensionInput,
                        onValueChange = { viewModel.updateOldNetPension(it) },
                        testTag = "info_old_net_pension_input"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StepperControl(
                            label = "প্রাপ্তবয়স্ক রেশন সদস্য",
                            valueText = BengaliFormat.formatNumber(state.adultRationMembers.toLong(), bn),
                            onDecrement = { viewModel.updateAdultRationMembers(state.adultRationMembers - 1) },
                            onIncrement = { viewModel.updateAdultRationMembers(state.adultRationMembers + 1) },
                            testTagPrefix = "info_adult_ration",
                            modifier = Modifier.weight(1f)
                        )
                        StepperControl(
                            label = "অপ্রাপ্তবয়স্ক (<১২ বৎসর)",
                            valueText = BengaliFormat.formatNumber(state.childRationMembers.toLong(), bn),
                            onDecrement = { viewModel.updateChildRationMembers(state.childRationMembers - 1) },
                            onIncrement = { viewModel.updateChildRationMembers(state.childRationMembers + 1) },
                            testTagPrefix = "info_child_ration",
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    SegmentedPillToggle(
                        options = RetiringType.entries,
                        selected = state.retiringType,
                        label = { if (it == RetiringType.OFFICER) "অফিসার" else "জেসিও / ওআর" },
                        onSelect = { viewModel.selectRetiringType(it) },
                        testTagPrefix = "info_retiring_type"
                    )
                    NumberInputField(
                        label = "শেষ গৃহীত বেতন (Last Drawn Pay)",
                        required = true,
                        value = state.retiringLastPayInput,
                        onValueChange = { viewModel.updateRetiringLastPay(it) },
                        testTag = "info_retiring_last_pay_input"
                    )
                    NumberInputField(
                        label = "অবসরভাতাযোগ্য চাকরিকাল (বৎসর)",
                        required = true,
                        value = state.retiringQualifyingYearsInput,
                        onValueChange = { viewModel.updateRetiringQualifyingYears(it) },
                        suffixText = "বৎসর",
                        allowDecimal = true,
                        testTag = "info_retiring_qual_years_input"
                    )
                }
            }
        }

        // Primary Gold Submit CTA Button (matches Screenshot 4)
        Button(
            onClick = { viewModel.submitAndNavigate() },
            colors = ButtonDefaults.buttonColors(
                containerColor = CeremonialGold,
                contentColor = CeremonialGoldTextOnGold
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("submit_calculator_btn")
        ) {
            Icon(
                imageVector = Icons.Default.Calculate,
                contentDescription = "সাবমিট করুন",
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "সাবমিট করুন",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Share With Colleagues Card (matches Screenshot 4)
        ShareWithColleaguesCard(
            onShareClick = {
                PrintAndShareHelper.shareSummary(context, viewModel.buildFullSummaryText())
            }
        )

        // Developer Info Card (Àbdüllāh Aĺ Hỗŝŝâîň)
        DeveloperCreditCard()
    }
}

@Composable
private fun Old2015GradeStepSelector(
    selectedGradeId: String,
    currentPayInput: String,
    ctx2015: Grade2015Context,
    bn: Boolean,
    onSelectGrade: (String) -> Unit,
    onSelectStep: (Long) -> Unit
) {
    val parsedPay = BengaliFormat.parseSafeNumber(currentPayInput)
    val selectedGradeText = if (selectedGradeId == "AUTO") {
        ctx2015.displayLabel
    } else {
        JsiPayData.nps2015Grades.find { it.id == selectedGradeId }?.bnLabel ?: ctx2015.displayLabel
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DropdownSelectorField(
            label = "২০১৫ পে স্কেল / জেএসআই ০১/২০১৬ গ্রেড (৩০ জুন ২০২৬ পর্যন্ত)",
            selectedText = selectedGradeText,
            options = JsiPayData.nps2015Grades,
            optionLabel = { grade ->
                if (grade.id == "AUTO") ctx2015.displayLabel else grade.bnLabel
            },
            onSelect = { onSelectGrade(it.id) },
            testTag = "old_2015_grade_dropdown"
        )

        val customSentinel = -1L
        val stepOptions = ctx2015.steps + customSentinel
        val matchedIdx2015 = ctx2015.steps.indexOf(parsedPay)
        val selectedStepText = if (matchedIdx2015 >= 0) {
            "ধাপ ${BengaliFormat.formatNumber((matchedIdx2015 + 1).toLong(), bn)} — ${BengaliFormat.formatBdt(parsedPay.toDouble(), bn)}"
        } else {
            "কাস্টম / নিজস্ব অঙ্ক (${BengaliFormat.formatBdt(parsedPay.toDouble(), bn)})"
        }

        DropdownSelectorField(
            label = "৩০ জুন ২০২৬-এর বেতন ধাপ (Step)",
            selectedText = selectedStepText,
            options = stepOptions,
            optionLabel = { stepVal ->
                if (stepVal == customSentinel) {
                    "কাস্টম / নিজস্ব অঙ্ক (নিচে লিখুন)"
                } else {
                    val idx = ctx2015.steps.indexOf(stepVal) + 1
                    "ধাপ ${BengaliFormat.formatNumber(idx.toLong(), bn)} — ${BengaliFormat.formatBdt(stepVal.toDouble(), bn)}"
                }
            },
            onSelect = { chosen ->
                if (chosen != customSentinel) onSelectStep(chosen)
            },
            testTag = "old_2015_step_dropdown"
        )

        if (ctx2015.steps.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ctx2015.steps.forEachIndexed { idx, stepAmt ->
                    val isSelected = parsedPay == stepAmt
                    Surface(
                        onClick = { onSelectStep(stepAmt) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) CeremonialGold else RegimentalFieldBg,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) CeremonialGold else RegimentalBorder
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("old_2015_step_${idx + 1}")
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${BengaliFormat.formatNumber((idx + 1).toLong(), bn)}: ${BengaliFormat.formatNumber(stepAmt, bn)}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CeremonialGoldTextOnGold else TextSecondaryMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewPayPill(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = RegimentalCard.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, RegimentalBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryMuted
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}
