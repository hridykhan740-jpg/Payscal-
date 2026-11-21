package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.local.CalculatorRepository
import com.example.ui.screens.InfoInputTab
import com.example.ui.screens.PensionTab
import com.example.ui.screens.ReportTab
import com.example.ui.screens.SalaryResultTab
import com.example.ui.theme.*
import com.example.util.PrintAndShareHelper
import com.example.viewmodel.CalculatorTab
import com.example.viewmodel.CalculatorViewModel
import com.example.viewmodel.CalculatorViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getInstance(applicationContext)
        val repository = CalculatorRepository(database.calculatorDao())
        val viewModelFactory = CalculatorViewModelFactory(repository)

        setContent {
            JsiPayCalculatorTheme(darkTheme = true) {
                val vm: CalculatorViewModel = viewModel(factory = viewModelFactory)
                JsiCalculatorApp(viewModel = vm)
            }
        }
    }
}

private data class TabNavItem(
    val tab: CalculatorTab,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val tabItems = listOf(
    TabNavItem(CalculatorTab.INFO, Icons.Filled.Edit, Icons.Outlined.Edit),
    TabNavItem(CalculatorTab.SALARY, Icons.Filled.Payments, Icons.Outlined.Payments),
    TabNavItem(
        CalculatorTab.PENSION,
        Icons.AutoMirrored.Filled.TrendingUp,
        Icons.AutoMirrored.Outlined.TrendingUp
    ),
    TabNavItem(CalculatorTab.REPORT, Icons.Filled.Description, Icons.Outlined.Description)
)

@Composable
fun JsiCalculatorApp(
    viewModel: CalculatorViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Navigate back to Info tab on Back press if on secondary tabs
    BackHandler(enabled = state.selectedTab != CalculatorTab.INFO) {
        viewModel.selectTab(CalculatorTab.INFO)
    }

    LaunchedEffect(state.selectedTab) {
        scrollState.scrollTo(0)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(RegimentalBgDark)
    ) {
        val isWideScreen = maxWidth >= 720.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = RegimentalBgDark,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF0F1B15), Color(0xFF0C1510))
                            )
                        )
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Gold Shield Emblem
                        Surface(
                            color = CeremonialGold,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Shield,
                                    contentDescription = "জেএসআই ২০২৬ ক্যালকুলেটর",
                                    tint = CeremonialGoldTextOnGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // Title & Dynamic Branch/Category Subtitle
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "জেএসআই ২০২৬ ক্যালকুলেটর",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${state.serviceBranch.bnLabel} • ${state.category.bnLabel}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Top-right Action Buttons: Gold Download, Share, Refresh
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = {
                                    PrintAndShareHelper.printSummary(context, viewModel.buildFullSummaryText())
                                },
                                color = CeremonialGold,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("top_download_btn")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = "PDF ডাউনলোড / প্রিন্ট",
                                        tint = CeremonialGoldTextOnGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Surface(
                                onClick = {
                                    PrintAndShareHelper.shareSummary(context, viewModel.buildFullSummaryText())
                                },
                                color = RegimentalCard,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, RegimentalBorder),
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("top_share_btn")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "শেয়ার করুন",
                                        tint = TextPrimaryLight,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }

                            Surface(
                                onClick = {
                                    viewModel.resetAll()
                                    Toast.makeText(context, "সকল তথ্য রিসেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                                },
                                color = RegimentalCard,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, RegimentalBorder),
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("top_refresh_btn")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "রিসেট করুন",
                                        tint = TextPrimaryLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = RegimentalBorder.copy(alpha = 0.65f))
                }
            },
            bottomBar = {
                if (!isWideScreen) {
                    Surface(
                        color = Color(0xFF0E1712),
                        tonalElevation = 6.dp
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            HorizontalDivider(color = RegimentalBorder.copy(alpha = 0.7f))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                tabItems.forEach { item ->
                                    val selected = state.selectedTab == item.tab
                                    val bg = if (selected) CeremonialGoldMuted.copy(alpha = 0.65f) else Color.Transparent
                                    val border = if (selected) BorderStroke(1.dp, CeremonialGold.copy(alpha = 0.45f)) else null
                                    val contentColor = if (selected) CeremonialGold else TextSecondaryMuted

                                    Surface(
                                        onClick = { viewModel.selectTab(item.tab) },
                                        shape = RoundedCornerShape(14.dp),
                                        color = bg,
                                        border = border,
                                        modifier = Modifier
                                            .weight(1f)
                                            .minimumInteractiveComponentSize()
                                            .testTag("nav_tab_${item.tab.index}")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.tab.bnTitle,
                                                tint = contentColor,
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Text(
                                                text = item.tab.bnTitle,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                                color = contentColor,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = Color(0xFF0E1712),
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Spacer(Modifier.height(12.dp))
                        tabItems.forEach { item ->
                            val selected = state.selectedTab == item.tab
                            NavigationRailItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(item.tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.tab.bnTitle
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.tab.bnTitle,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = CeremonialGold,
                                    selectedTextColor = CeremonialGold,
                                    indicatorColor = CeremonialGoldMuted,
                                    unselectedIconColor = TextSecondaryMuted,
                                    unselectedTextColor = TextSecondaryMuted
                                ),
                                modifier = Modifier.testTag("rail_tab_${item.tab.index}")
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 680.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Subtle Digit Toggle Bar on top of content
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "প্রতিরক্ষা মন্ত্রণালয় • JSI 01/2026",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldTextLight
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = RegimentalCard,
                                border = BorderStroke(1.dp, RegimentalBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.toggleBengaliDigits() }
                                    .testTag("toggle_digits_button")
                            ) {
                                Text(
                                    text = if (state.useBengaliDigits) "সংখ্যা: ১২৩ → 123" else "Digits: 123 → ১২৩",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CeremonialGold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        when (state.selectedTab) {
                            CalculatorTab.INFO -> InfoInputTab(
                                state = state,
                                viewModel = viewModel
                            )
                            CalculatorTab.SALARY -> SalaryResultTab(
                                state = state,
                                viewModel = viewModel
                            )
                            CalculatorTab.PENSION -> PensionTab(
                                state = state,
                                viewModel = viewModel
                            )
                            CalculatorTab.REPORT -> ReportTab(
                                state = state,
                                viewModel = viewModel
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
