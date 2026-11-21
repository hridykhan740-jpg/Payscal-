package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.util.BengaliFormat
import com.example.util.PrintAndShareHelper
import com.example.viewmodel.CalculatorTab
import com.example.viewmodel.CalculatorUiState
import com.example.viewmodel.CalculatorViewModel
import com.example.viewmodel.PensionSubTab

@Composable
fun ReportTab(
    state: CalculatorUiState,
    viewModel: CalculatorViewModel
) {
    val context = LocalContext.current
    val bn = state.useBengaliDigits
    val pay = state.payFixationResult
    val phased = state.phasedArrearsResult
    val allow = state.allowancesResult

    var showJsonPreview by remember { mutableStateOf(false) }

    val createJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            val ok = PrintAndShareHelper.writeJsonToUri(context, uri, viewModel.buildFullExportJsonString())
            Toast.makeText(
                context,
                if (ok) "JSON ফাইল সফলভাবে সংরক্ষিত হয়েছে" else "JSON ফাইল সংরক্ষণ করা যায়নি",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val openJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val raw = PrintAndShareHelper.readTextFromUri(context, uri)
            val ok = raw != null && viewModel.importFromJson(raw)
            Toast.makeText(
                context,
                if (ok) "JSON থেকে হিসাব সফলভাবে লোড করা হয়েছে" else "সঠিক JSI 01/2026 JSON ফাইল নয়",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Complete Report Preview Card
        SectionCard(
            title = "পূর্ণাঙ্গ বেতন ও পেনশন রিপোর্ট",
            subtitle = "JSI 01/2026 Official Reference Summary",
            icon = Icons.Default.Description
        ) {
            if (state.userName.isNotBlank()) {
                KeyValueRow("নাম", state.userName)
            }
            if (state.serviceNo.isNotBlank()) {
                KeyValueRow("সেনা / সার্ভিস নং", state.serviceNo)
            }
            if (state.unitName.isNotBlank()) {
                KeyValueRow("ইউনিট", state.unitName)
            }
            KeyValueRow("বাহিনী ও শ্রেণি", "${state.serviceBranch.bnLabel} • ${state.category.bnLabel}")
            KeyValueRow(
                "জন্ম ও যোগদানের তারিখ",
                "${state.dobDay}-${state.dobMonth}-${state.dobYear}  |  ${state.joinDay}-${state.joinMonth}-${state.joinYear}"
            )

            HorizontalDivider(color = RegimentalBorder)

            if (!state.isPensionerRole && pay != null && phased != null && allow != null) {
                KeyValueRow("পদবি", pay.rankTitle)
                KeyValueRow("৩০ জুন ২০২৬ মূল বেতন", BengaliFormat.formatBdt(pay.oldBasicPay.toDouble(), bn))
                KeyValueRow("০১ জুলাই ২০২৬ মূল বেতন (ইনক্রিমেন্টসহ)", BengaliFormat.formatBdt(pay.newBasicPay.toDouble(), bn), highlight = true)
                KeyValueRow("১ম ধাপ (জুলাই–ডিসে ২০২৬)", BengaliFormat.formatBdt(phased.phase1Pay.toDouble(), bn))
                KeyValueRow("২য় ধাপ (জানু–জুন ২০২৭)", BengaliFormat.formatBdt(phased.phase2Pay.toDouble(), bn))
                KeyValueRow("৩য় ধাপ (জুলাই ২০২৭ ইনক্রিমেন্টসহ)", BengaliFormat.formatBdt(phased.phase3Pay.toDouble(), bn))
                KeyValueRow("নিট প্রাপ্য বকেয়া", BengaliFormat.formatBdt(phased.netPayableArrears.toDouble(), bn))
                KeyValueRow("সম্ভাব্য জিপিএফ কর্তন (বকেয়া)", "− ${BengaliFormat.formatBdt(phased.probableGpfOnArrears.toDouble(), bn)}")
                HorizontalDivider(color = RegimentalBorder)
                KeyValueRow("মোট মাসিক বেতন-ভাতা (২০২৮)", BengaliFormat.formatBdt(allow.grossMonthlyPay.toDouble(), bn))
                KeyValueRow("জিপিএফ কর্তন", "− ${BengaliFormat.formatBdt(allow.gpfDeduction.toDouble(), bn)}")
                KeyValueRow("নিট মাসিক প্রদেয় বেতন (২০২৮)", BengaliFormat.formatBdt(allow.netMonthlyPay.toDouble(), bn), highlight = true)
            } else if (state.pensionSubTab == PensionSubTab.EXISTING && state.existingPensionResult != null) {
                val ep = state.existingPensionResult
                KeyValueRow("পূর্বের নিট পেনশন", BengaliFormat.formatBdt(ep.oldNetPension.toDouble(), bn))
                KeyValueRow("পূর্ণ বর্ধিত নিট পেনশন", BengaliFormat.formatBdt(ep.newFullNetPension.toDouble(), bn), highlight = true)
                KeyValueRow("১ম ধাপ (জুলাই–ডিসে ২৬)", BengaliFormat.formatBdt(ep.phase1Pension.toDouble(), bn))
                KeyValueRow("২য় ধাপ (জানু–জুন ২৭)", BengaliFormat.formatBdt(ep.phase2Pension.toDouble(), bn))
                KeyValueRow("রেশন ও চিকিৎসা সহ মোট", BengaliFormat.formatBdt(ep.totalMonthlyWithAllowancesFull.toDouble(), bn), highlight = true)
            } else if (state.retiringPensionResult != null) {
                val rp = state.retiringPensionResult
                KeyValueRow("অর্জিত গ্রস পেনশন", BengaliFormat.formatBdt(rp.grossPension.toDouble(), bn), highlight = true)
                KeyValueRow("মাসিক নিট পেনশন", BengaliFormat.formatBdt(rp.netMonthlyPension.toDouble(), bn))
                KeyValueRow("এককালীন কম্যুটেড গ্র্যাচুইটি", BengaliFormat.formatBdt(rp.lumpSumCommutedValue.toDouble(), bn), highlight = true)
            }

            HorizontalDivider(color = RegimentalBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        PrintAndShareHelper.printSummary(context, viewModel.buildFullSummaryText())
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CeremonialGold,
                        contentColor = CeremonialGoldTextOnGold
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("print_summary_btn")
                ) {
                    Icon(Icons.Default.Print, contentDescription = "প্রিন্ট করুন", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("প্রিন্ট / PDF", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        PrintAndShareHelper.copySummaryToClipboard(context, viewModel.buildFullSummaryText())
                    },
                    border = BorderStroke(1.dp, CeremonialGold),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CeremonialGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("copy_summary_btn")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "কপি করুন", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("সামারি কপি", fontWeight = FontWeight.Bold)
                }

                FilledTonalIconButton(
                    onClick = {
                        PrintAndShareHelper.shareSummary(context, viewModel.buildFullSummaryText())
                    },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = EmeraldContainerDark,
                        contentColor = EmeraldTextLight
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("share_summary_btn")
                ) {
                    Icon(Icons.Default.Share, contentDescription = "শেয়ার করুন")
                }
            }
        }

        // JSON Export & Backup Card (GitHub / Device Storage)
        SectionCard(
            title = "JSON এক্সপোর্ট ও ব্যাকআপ (GitHub / স্টোরেজ)",
            subtitle = "সকল ইনপুট ও হিসাব JSON ফরম্যাটে ফাইল সেভ বা কপি করুন",
            icon = Icons.Default.Code
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        createJsonLauncher.launch(viewModel.getSuggestedJsonFilename())
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CeremonialGold,
                        contentColor = CeremonialGoldTextOnGold
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("export_json_file_btn")
                ) {
                    Icon(Icons.Default.Download, contentDescription = "JSON ফাইল সেভ", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("JSON সেভ (.json)", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                OutlinedButton(
                    onClick = {
                        PrintAndShareHelper.copyJsonToClipboard(context, viewModel.buildFullExportJsonString())
                    },
                    border = BorderStroke(1.dp, CeremonialGold),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CeremonialGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("copy_json_btn")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "JSON কপি", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("JSON কপি", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        openJsonLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                    },
                    border = BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldTextLight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("import_json_file_btn")
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = "JSON ফাইল লোড", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("JSON লোড", fontWeight = FontWeight.SemiBold)
                }

                TextButton(
                    onClick = { showJsonPreview = !showJsonPreview },
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("toggle_json_preview_btn")
                ) {
                    Text(
                        text = if (showJsonPreview) "প্রিভিউ লুকান ▲" else "JSON প্রিভিউ ▼",
                        color = TextSecondaryMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (showJsonPreview) {
                val previewJson = remember(state) { viewModel.buildFullExportJsonString() }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = RegimentalFieldBg,
                    border = BorderStroke(1.dp, RegimentalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = previewJson,
                        color = EmeraldTextLight,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .horizontalScroll(rememberScrollState())
                            .padding(12.dp)
                    )
                }
            }
        }

        // Local Room DB Saved Calculations History Card
        SectionCard(
            title = "সংরক্ষিত হিসাবসমূহ (লোকাল ডাটাবেজ)",
            subtitle = "সকল ইনপুট স্বয়ংক্রিয়ভাবে এবং একাধিক সদস্যের হিসাব অফলাইনে সেভ থাকে",
            icon = Icons.Default.Storage
        ) {
            Button(
                onClick = {
                    viewModel.saveCurrentSnapshot()
                    Toast.makeText(context, "বর্তমান হিসাবটি লোকাল ডাটাবেজে সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldContainerDark,
                    contentColor = EmeraldTextLight
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("report_save_snapshot_btn")
            ) {
                Icon(Icons.Default.BookmarkAdd, contentDescription = "সংরক্ষণ করুন", modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("বর্তমান হিসাব নতুন করে সেভ করুন", fontWeight = FontWeight.Bold)
            }

            if (state.savedCalculations.isEmpty()) {
                Text(
                    text = "এখনো আলাদা কোনো স্ন্যাপশট সেভ করা হয়নি। তবে আপনার চলমান ফর্মের সব তথ্য স্বয়ংক্রিয়ভাবে লোকাল ডাটাবেজে সেভ হচ্ছে।",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.savedCalculations.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = RegimentalFieldBg,
                            border = BorderStroke(1.dp, RegimentalBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.memberName} • ${item.rankTitle}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimaryLight,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = item.summaryHighlight,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = EmeraldTextLight
                                    )
                                }
                                TextButton(
                                    onClick = {
                                        viewModel.loadSavedSnapshot(item)
                                        viewModel.selectTab(CalculatorTab.SALARY)
                                        Toast.makeText(context, "সংরক্ষিত হিসাব লোড করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Text("লোড", color = CeremonialGold, fontWeight = FontWeight.Bold)
                                }
                                IconButton(
                                    onClick = { viewModel.deleteSavedSnapshot(item.id) },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
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

        // Share With Colleagues Card
        ShareWithColleaguesCard(
            onShareClick = {
                PrintAndShareHelper.shareSummary(context, viewModel.buildFullSummaryText())
            }
        )

        // Developer Info Card (Àbdüllāh Aĺ Hỗŝŝâîň)
        DeveloperCreditCard()

        // Official Reference Disclaimer Footer
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = RegimentalCard,
            border = BorderStroke(1.dp, RegimentalBorder)
        ) {
            Text(
                text = "বিঃদ্রঃ এই ক্যালকুলেটরটি গণপ্রজাতন্ত্রী বাংলাদেশ সরকার, প্রতিরক্ষা মন্ত্রণালয়ের যৌথ বাহিনী নির্দেশাবলি (JSI 01/2026, বাংলাদেশ গেজেট অতিরিক্ত সংখ্যা ১৫ নভেম্বর ২০২৬) অনুসরণে নির্মিত একটি রেফারেন্স সহায়িকা মাত্র। চূড়ান্ত ও আনুষ্ঠানিক বেতন-পেনশন নির্ধারণ iBAS++ সিস্টেম এবং সংশ্লিষ্ট SFC / FC / SFPO অফিসের মাধ্যমে সম্পন্ন হইবে।",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryMuted,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}
