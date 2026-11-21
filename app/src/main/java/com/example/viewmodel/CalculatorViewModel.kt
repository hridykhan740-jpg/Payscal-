package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.local.CalculatorRepository
import com.example.data.local.CalculatorStateEntity
import com.example.data.local.SavedCalculationEntity
import com.example.util.BengaliFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

enum class CalculatorTab(val index: Int, val bnTitle: String, val enSubtitle: String) {
    INFO(0, "তথ্য", "Information"),
    SALARY(1, "বেতন", "Salary"),
    PENSION(2, "অবসর", "Pension"),
    REPORT(3, "রিপোর্ট", "Report")
}

enum class PensionSubTab(val bnLabel: String) {
    EXISTING("পূর্বের অবসরভোগী (অনুচ্ছেদ ২৭)"),
    RETIRING("অবসরগামী সদস্য (অনুচ্ছেদ ৫, ১৫, ২৮)")
}

enum class RetiringType(val bnLabel: String) {
    OFFICER("কমিশন্ড অফিসার (১০+ বৎসর চাকরি)"),
    JCO_OR("জেসিও / ওআর / এনসি(ই) / এমওডিসি (১০+ বৎসর)")
}

data class ProjectedPromotion(
    val id: String = UUID.randomUUID().toString(),
    val rankId: String,
    val rankTitle: String,
    val day: String = "01",
    val month: String = "01",
    val year: String = "2030"
)

data class PayFixationStepItem(
    val stepBn: String,
    val title: String,
    val subtitle: String,
    val formattedValue: String,
    val valueType: StepValueType = StepValueType.NORMAL,
    val isHighlightedRow: Boolean = false
)

enum class StepValueType {
    NORMAL, NEGATIVE, POSITIVE, HIGHLIGHT_GOLD
}

data class PayFixationResult(
    val rankTitle: String,
    val rankShortTitle: String,
    val appendixShortLabel: String,
    val newBasicPay: Long,
    val fixedBeforeIncrementPay: Long,
    val july2026IncrementAmount: Long,
    val july2027BasicPay: Long,
    val oldBasicPay: Long,
    val oldScaleStart: Long,
    val newScaleStart: Long,
    val personalPay: Long = 0L,
    val matchedStepIndex: Int? = null,
    val totalSteps: Int? = null,
    val implementationGroup: ImplementationGroup,
    val allowanceTier: RankTierForAllowances,
    val isOfficer: Boolean,
    val isAfns: Boolean,
    val isSergeantOrBelow: Boolean,
    val isCplOrBelow: Boolean,
    val defaultPrescribedService: Int,
    val eightSteps: List<PayFixationStepItem>,
    val breakdownSteps: List<String>
)

data class PhaseMilestoneDetail(
    val id: String,
    val title: String,
    val dateRange: String,
    val badgeText: String,
    val isFinalPhase: Boolean,
    val basicPay: Long,
    val basicDiffFromPrevious: Long,
    val basicSubtitle: String,
    val netGainTitle: String,
    val netGainAmount: Long,
    val netGainSubtitle: String?,
    val takeHomeTitle: String,
    val takeHomeAfterDeductions: Long,
    val itemBreakdown: List<Pair<String, Long>>,
    val gpfDeduction: Long,
    val grossMonthlyPay: Long
)

data class PhasedArrearsResult(
    val oldBasicPay: Long,
    val newBasicPay: Long,
    val july2027BasicPay: Long,
    val increase2026: Long,
    val group: ImplementationGroup,
    val phase1Pay: Long,
    val phase2Pay: Long,
    val phase3Pay: Long,
    val dateRangeSubtitle: String,
    val arrearsDurationSummary: String,
    val monthsArrearsCount: Double,
    val monthlyBiseshSubidha: Long,
    val biseshAutoRuleNote: String,
    val grossArrears: Long,
    val totalBiseshSubidhaDeducted: Long,
    val netPayableArrears: Long,
    val probableGpfOnArrears: Long,
    val milestones: List<PhaseMilestoneDetail>,
    val breakdownSteps: List<String>
)

data class AllowancesResult(
    val basicPay: Long,
    val serviceOrdnancePay2028: Long,
    val houseRentRate: Double,
    val houseRentAmount: Long,
    val defenceServiceAllowance: Long,
    val medicalAllowance: Long,
    val educationAllowance: Long,
    val disabledChildAllowance: Long,
    val tiffinAllowance: Long,
    val transportAllowance: Long,
    val mobileAllowance: Long,
    val kitAllowance: Long,
    val hillHaorAllowance: Long,
    val proficiencyPay: Long,
    val appointmentPay: Long,
    val goodConductPay: Long,
    val specialSkillPay: Long,
    val otherNewAllowance: Long,
    val grossMonthlyPay: Long,
    val gpfDeduction: Long,
    val netMonthlyPay: Long,
    val noboborshoAnnual: Long,
    val breakdownSteps: List<String>
)

data class ExistingPensionResult(
    val oldNetPension: Long,
    val slabLabel: String,
    val increaseRatePercent: Int,
    val newFullNetPension: Long,
    val isAboveCapProtected: Boolean,
    val group: ImplementationGroup,
    val phase1Pension: Long,
    val phase2Pension: Long,
    val phase3Pension: Long,
    val july2027IncrementedPension: Long,
    val rationAllowance: Long,
    val medicalAllowance: Long,
    val totalMonthlyWithAllowancesFull: Long,
    val noboborshoAnnual: Long,
    val breakdownSteps: List<String>
)

data class RetiringPensionResult(
    val grossPension: Long,
    val capNote: String,
    val surrenderPercent: Int,
    val commutedMonthlyPortion: Long,
    val netMonthlyPension: Long,
    val commutationMultiplier: Double,
    val lumpSumCommutedValue: Long,
    val rationAllowance: Long,
    val medicalAllowance: Long,
    val projectedLastRankTitle: String,
    val projectedQualifyingYears: Double,
    val projectedTotalMonthlyWithAllowances: Long,
    val breakdownSteps: List<String>
)

data class CalculatorUiState(
    val selectedTab: CalculatorTab = CalculatorTab.INFO,
    val useBengaliDigits: Boolean = true,
    val isLoadedFromDb: Boolean = false,
    val lastSavedTimestamp: Long = 0L,

    // Role & Personal Info (Tab 1: তথ্য)
    val isPensionerRole: Boolean = false,
    val userName: String = "",
    val serviceNo: String = "",
    val unitName: String = "",
    val dobDay: String = "01",
    val dobMonth: String = "02",
    val dobYear: String = "2003",
    val joinDay: String = "04",
    val joinMonth: String = "02",
    val joinYear: String = "2022",
    val computedAgeOnEffectiveDate: Int = 23,
    val computedServiceYears: Double = 4.4,

    // Pay Fixation Inputs
    val category: PersonnelCategory = PersonnelCategory.JCO_OR,
    val serviceBranch: ServiceBranch = ServiceBranch.ARMY,
    val officerAppendix: OfficerAppendix = OfficerAppendix.STANDARD,
    val selectedOfficerRankId: String = "maj",
    val commissionedYear: Int = 16,
    val officerOldBasicInput: String = "56500",

    val selectedCadetId: String = "officer_cadet",

    val selectedNonOfficerRankId: String = "sgt",
    val oldBasicPayInput: String = "26120",
    val oldScaleStartOverride: String = "16000",
    val applyJuly2026Increment: Boolean = true,

    // Family & Marriage
    val isMarried: Boolean = true,
    val childrenCount: Int = 0,
    val disabledChildrenCount: Int = 0,

    // Proficiency, Badges & Skill Pays (Card 4)
    val proficiencyPayId: String = "none",
    val appointmentPayId: String = "none",
    val goodConductBadges: Int = 0,
    val selectedSpecialSkills: Set<SpecialSkillPay> = emptySet(),
    val otherNewAllowanceInput: String = "",

    // Phased & Arrears Inputs
    val receiveMonthIndex: Int = 0, // 0 = এখনো পাইনি (আজ পর্যন্ত হিসাব)
    val monthlyBiseshSubidhaInput: String = "", // blank = auto JSI rule

    // Workplace, Accommodation & GPF
    val cityCategory: CityCategory = CityCategory.DHAKA,
    val accommodationType: AccommodationType = AccommodationType.NO_QUARTERS_STANDARD,
    val serviceBracket: ServiceBracket = ServiceBracket.Y0_5,
    val activeMedicalAgeBracket: MedicalAgeBracket = MedicalAgeBracket.UP_TO_50,
    val receiveFreeTiffin: Boolean = false,
    val hillHaorCategory: HillHaorCategory = HillHaorCategory.NONE,
    val oldServiceOrdnanceInput: String = "",
    val gpfPercent: Int = 5,

    // Pension Inputs (Tab 3: অবসর)
    val pensionSubTab: PensionSubTab = PensionSubTab.RETIRING,
    val oldNetPensionInput: String = "18500",
    val pensionerAgeBracket: MedicalAgeBracket = MedicalAgeBracket.AGE_60_TO_70,
    val adultRationMembers: Int = 2,
    val childRationMembers: Int = 0,

    val retiringType: RetiringType = RetiringType.JCO_OR,
    val retiringLastPayInput: String = "45100",
    val retiringQualifyingYearsInput: String = "22",
    val rankPrescribedYearsInput: String = "22",
    val retiringAgeNextBirthday: Int = 52,
    val commutationSurrenderPercent: Int = 50,

    // Projected Future Retirement & Promotions (Screenshot 6)
    val retireDay: String = "04",
    val retireMonth: String = "02",
    val retireYear: String = "2043",
    val projectedPromotions: List<ProjectedPromotion> = emptyList(),

    // Saved Snapshots from Room
    val savedCalculations: List<SavedCalculationEntity> = emptyList(),

    // Computed Results
    val payFixationResult: PayFixationResult? = null,
    val phasedArrearsResult: PhasedArrearsResult? = null,
    val allowancesResult: AllowancesResult? = null,
    val existingPensionResult: ExistingPensionResult? = null,
    val retiringPensionResult: RetiringPensionResult? = null
)

class CalculatorViewModel(
    private val repository: CalculatorRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private var saveJob: Job? = null
    private var referenceDateOverride: LocalDate? = null
    @Volatile
    private var hasUserInteracted = false

    init {
        syncDatesAndRecompute(updateInputsFromDates = false, persist = false)
        if (repository != null) {
            loadPersistedState()
            observeSavedSnapshots()
        }
    }

    internal fun setReferenceDateForTest(date: LocalDate?) {
        referenceDateOverride = date
        recalculateAll(persist = false)
    }

    private fun observeSavedSnapshots() {
        val repo = repository ?: return
        viewModelScope.launch {
            repo.observeSavedCalculations().collect { list ->
                _uiState.update { it.copy(savedCalculations = list) }
            }
        }
    }

    private fun loadPersistedState() {
        val repo = repository ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val saved = repo.getActiveStateOnce()
            if (hasUserInteracted) {
                _uiState.update { it.copy(isLoadedFromDb = true) }
                return@launch
            }
            if (saved != null) {
                _uiState.update { saved.toUiState(it) }
                syncDatesAndRecompute(updateInputsFromDates = false, persist = false)
            } else {
                _uiState.update { it.copy(isLoadedFromDb = true) }
            }
        }
    }

    private fun schedulePersist() {
        val repo = repository ?: return
        saveJob?.cancel()
        saveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(180)
            val entity = _uiState.value.toEntity()
            repo.saveActiveState(entity)
            _uiState.update { it.copy(lastSavedTimestamp = entity.updatedAtMillis) }
        }
    }

    fun saveCurrentSnapshot() {
        val repo = repository ?: return
        val state = _uiState.value
        val bn = state.useBengaliDigits
        val pay = state.payFixationResult
        val allow = state.allowancesResult
        val name = state.userName.ifBlank { "সদস্য (${state.serviceBranch.bnLabel})" }
        val rank = pay?.rankTitle ?: state.category.bnLabel
        val summary = if (state.isPensionerRole) {
            val netPen = state.existingPensionResult?.newFullNetPension
                ?: state.retiringPensionResult?.netMonthlyPension ?: 0L
            "পেনশন: ${BengaliFormat.formatBdt(netPen.toDouble(), bn)}"
        } else {
            val net2028 = allow?.netMonthlyPay ?: 0L
            "মূল বেতন: ${BengaliFormat.formatBdt((pay?.newBasicPay ?: 0L).toDouble(), bn)} • ২০২৮ নিট: ${BengaliFormat.formatBdt(net2028.toDouble(), bn)}"
        }

        viewModelScope.launch(Dispatchers.IO) {
            repo.saveSnapshot(
                SavedCalculationEntity(
                    memberName = name,
                    rankTitle = rank,
                    summaryHighlight = summary,
                    stateSnapshotJson = serializeStateCompact(state)
                )
            )
        }
    }

    fun deleteSavedSnapshot(id: Long) {
        val repo = repository ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteSnapshot(id)
        }
    }

    fun loadSavedSnapshot(snapshot: SavedCalculationEntity) {
        hasUserInteracted = true
        val parsed = deserializeStateCompact(snapshot.stateSnapshotJson, _uiState.value) ?: return
        _uiState.value = parsed
        syncDatesAndRecompute(updateInputsFromDates = false, persist = true)
    }

    fun selectTab(tab: CalculatorTab) {
        hasUserInteracted = true
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleBengaliDigits() {
        _uiState.update { it.copy(useBengaliDigits = !it.useBengaliDigits) }
        recalculateAll()
    }

    fun setRolePensioner(isPensioner: Boolean) {
        _uiState.update { state ->
            state.copy(
                isPensionerRole = isPensioner,
                pensionSubTab = if (isPensioner) PensionSubTab.EXISTING else PensionSubTab.RETIRING
            )
        }
        recalculateAll()
    }

    fun updateUserName(name: String) {
        hasUserInteracted = true
        _uiState.update { it.copy(userName = name) }
        schedulePersist()
    }

    fun updateServiceNo(no: String) {
        hasUserInteracted = true
        _uiState.update { it.copy(serviceNo = no) }
        schedulePersist()
    }

    fun updateUnitName(unit: String) {
        hasUserInteracted = true
        _uiState.update { it.copy(unitName = unit) }
        schedulePersist()
    }

    fun selectServiceBranch(branch: ServiceBranch) {
        _uiState.update { state ->
            val nextCategory = when (branch) {
                ServiceBranch.AFNS -> if (state.category == PersonnelCategory.CADET) PersonnelCategory.CADET else PersonnelCategory.OFFICER
                ServiceBranch.MODC, ServiceBranch.RELIGIOUS -> PersonnelCategory.JCO_OR
                else -> state.category
            }
            val nextAppendix = when {
                branch == ServiceBranch.AFNS -> OfficerAppendix.AFNS
                state.officerAppendix == OfficerAppendix.AFNS -> OfficerAppendix.STANDARD
                else -> state.officerAppendix
            }
            val appendixRanks = JsiPayData.getOfficerRanks(nextAppendix)
            val nextOfficerRank = if (branch == ServiceBranch.AFNS) {
                appendixRanks.find { it.id == state.selectedOfficerRankId } ?: appendixRanks.first()
            } else {
                appendixRanks.find { it.id == state.selectedOfficerRankId } ?: appendixRanks.find { it.id == "maj" } ?: appendixRanks.first()
            }
            val nextCommissionedYear = if (nextOfficerRank.yearToPay.containsKey(state.commissionedYear)) {
                state.commissionedYear
            } else {
                nextOfficerRank.yearToPay.keys.firstOrNull() ?: 1
            }

            val nextNonOfficerRankId = when (branch) {
                ServiceBranch.MODC -> if (state.selectedNonOfficerRankId in listOf("swo", "wo", "sgt", "cpl", "lcpl", "sainik", "nce", "modc_recruit")) {
                    state.selectedNonOfficerRankId
                } else "nce"
                ServiceBranch.RELIGIOUS -> if (state.selectedNonOfficerRankId in listOf("swo", "wo")) {
                    state.selectedNonOfficerRankId
                } else "wo"
                else -> state.selectedNonOfficerRankId
            }
            val nonOfficerChanged = nextNonOfficerRankId != state.selectedNonOfficerRankId
            val nextNonOfficerRank = JsiPayData.nonOfficerRanks.find { it.id == nextNonOfficerRankId }

            val nextCadetId = if (branch == ServiceBranch.AFNS) "afns_cadet" else state.selectedCadetId

            state.copy(
                serviceBranch = branch,
                category = nextCategory,
                officerAppendix = nextAppendix,
                selectedOfficerRankId = nextOfficerRank.id,
                commissionedYear = nextCommissionedYear,
                selectedCadetId = nextCadetId,
                selectedNonOfficerRankId = nextNonOfficerRankId,
                oldScaleStartOverride = if (nonOfficerChanged && nextNonOfficerRank != null) nextNonOfficerRank.oldScaleStart.toString() else state.oldScaleStartOverride,
                oldBasicPayInput = if (nonOfficerChanged && nextNonOfficerRank != null) {
                    if (nextNonOfficerRank.id == "sgt") "26120" else nextNonOfficerRank.oldScaleStart.toString()
                } else state.oldBasicPayInput
            )
        }
        recalculateAll()
    }

    fun updateDob(day: String? = null, month: String? = null, year: String? = null) {
        _uiState.update { state ->
            state.copy(
                dobDay = (day ?: state.dobDay).filter { it.isDigit() }.take(2),
                dobMonth = (month ?: state.dobMonth).filter { it.isDigit() }.take(2),
                dobYear = (year ?: state.dobYear).filter { it.isDigit() }.take(4)
            )
        }
        syncDatesAndRecompute(updateInputsFromDates = true)
    }

    fun updateJoinDate(day: String? = null, month: String? = null, year: String? = null) {
        _uiState.update { state ->
            state.copy(
                joinDay = (day ?: state.joinDay).filter { it.isDigit() }.take(2),
                joinMonth = (month ?: state.joinMonth).filter { it.isDigit() }.take(2),
                joinYear = (year ?: state.joinYear).filter { it.isDigit() }.take(4)
            )
        }
        syncDatesAndRecompute(updateInputsFromDates = true)
    }

    fun updateRetireDate(day: String? = null, month: String? = null, year: String? = null) {
        _uiState.update { state ->
            state.copy(
                retireDay = (day ?: state.retireDay).filter { it.isDigit() }.take(2),
                retireMonth = (month ?: state.retireMonth).filter { it.isDigit() }.take(2),
                retireYear = (year ?: state.retireYear).filter { it.isDigit() }.take(4)
            )
        }
        applyProjectedFuturePensionInputs()
    }

    fun addProjectedPromotion(rankId: String, rankTitle: String, day: String, month: String, year: String) {
        val promo = ProjectedPromotion(
            rankId = rankId,
            rankTitle = rankTitle,
            day = day.padStart(2, '0'),
            month = month.padStart(2, '0'),
            year = year
        )
        _uiState.update { state ->
            state.copy(projectedPromotions = (state.projectedPromotions + promo).sortedBy {
                "${it.year}${it.month}${it.day}"
            })
        }
        applyProjectedFuturePensionInputs()
    }

    fun removeProjectedPromotion(id: String) {
        _uiState.update { state ->
            state.copy(projectedPromotions = state.projectedPromotions.filterNot { it.id == id })
        }
        applyProjectedFuturePensionInputs()
    }

    fun applyProjectedFuturePensionInputs() {
        val state = _uiState.value
        val pay = state.payFixationResult ?: computePayFixation(state, state.useBengaliDigits)

        val qualYears = computeYearsBetween(
            state.joinDay, state.joinMonth, state.joinYear,
            state.retireDay, state.retireMonth, state.retireYear
        ) ?: BengaliFormat.parseSafeDouble(state.retiringQualifyingYearsInput)

        val ageAtRetire = computeYearsBetween(
            state.dobDay, state.dobMonth, state.dobYear,
            state.retireDay, state.retireMonth, state.retireYear
        )?.let { (it.toInt() + 1).coerceIn(20, 80) } ?: state.retiringAgeNextBirthday

        val retireYr = state.retireYear.toIntOrNull() ?: 2043
        val yearsAfter2026 = (retireYr - 2026).coerceAtLeast(0)

        var projectedPay = pay.newBasicPay
        var projectedPrescribed = pay.defaultPrescribedService
        var projectedIsOfficer = pay.isOfficer

        if (state.projectedPromotions.isNotEmpty()) {
            val lastPromo = state.projectedPromotions.last()
            val nonOfficer = JsiPayData.nonOfficerRanks.find { it.id == lastPromo.rankId }
            val officer = JsiPayData.getOfficerRanks(state.officerAppendix).find { it.id == lastPromo.rankId }
            if (nonOfficer != null) {
                projectedIsOfficer = false
                projectedPrescribed = 25
                projectedPay = if (nonOfficer.isHonoraryFixed || nonOfficer.isRecruitFixed) {
                    max(nonOfficer.fixedPay, projectedPay)
                } else {
                    val steps = nonOfficer.steps
                    val targetIdx = (steps.indexOfFirst { it > projectedPay }.takeIf { it >= 0 } ?: 0)
                        .let { (it + (yearsAfter2026 / 2).coerceAtMost(steps.lastIndex - it)).coerceAtMost(steps.lastIndex) }
                    steps.getOrElse(targetIdx) { projectedPay }
                }
            } else if (officer != null) {
                projectedIsOfficer = true
                projectedPrescribed = officer.defaultPrescribedService
                projectedPay = if (officer.isFixed) {
                    officer.fixedPay
                } else {
                    val yrsAtRetire = (state.commissionedYear + yearsAfter2026).coerceAtMost(officer.yearToPay.keys.maxOrNull() ?: 23)
                    val matchedKey = officer.yearToPay.keys.filter { it <= yrsAtRetire }.maxOrNull()
                        ?: officer.yearToPay.keys.firstOrNull()
                    officer.yearToPay[matchedKey] ?: pay.newBasicPay
                }
            }
        } else if (!pay.isOfficer) {
            val rank = JsiPayData.nonOfficerRanks.find { it.id == state.selectedNonOfficerRankId }
            if (rank != null && rank.steps.isNotEmpty()) {
                val baseIdx = (pay.matchedStepIndex ?: 1) - 1
                val finalIdx = (baseIdx + yearsAfter2026).coerceIn(0, rank.steps.lastIndex)
                projectedPay = rank.steps[finalIdx]
            }
        } else {
            val rank = JsiPayData.getOfficerRanks(state.officerAppendix).find { it.id == state.selectedOfficerRankId }
            if (rank != null && !rank.isFixed && rank.yearToPay.isNotEmpty()) {
                val yrsAtRetire = state.commissionedYear + yearsAfter2026
                val matchedKey = rank.yearToPay.keys.filter { it <= yrsAtRetire }.maxOrNull()
                    ?: rank.yearToPay.keys.maxOrNull()
                if (matchedKey != null) {
                    projectedPay = rank.yearToPay[matchedKey] ?: projectedPay
                }
            }
        }

        _uiState.update {
            it.copy(
                pensionSubTab = PensionSubTab.RETIRING,
                retiringType = if (projectedIsOfficer) RetiringType.OFFICER else RetiringType.JCO_OR,
                retiringLastPayInput = projectedPay.toString(),
                retiringQualifyingYearsInput = String.format(java.util.Locale.US, "%.1f", qualYears),
                rankPrescribedYearsInput = projectedPrescribed.toString(),
                retiringAgeNextBirthday = ageAtRetire
            )
        }
        recalculateAll()
    }

    private fun computeYearsBetween(
        d1Str: String, m1Str: String, y1Str: String,
        d2Str: String, m2Str: String, y2Str: String
    ): Double? {
        if (y1Str.length != 4 || y2Str.length != 4) return null
        val d1 = d1Str.toIntOrNull()?.coerceIn(1, 31) ?: return null
        val m1 = m1Str.toIntOrNull()?.coerceIn(1, 12) ?: return null
        val y1 = y1Str.toIntOrNull() ?: return null
        val d2 = d2Str.toIntOrNull()?.coerceIn(1, 31) ?: return null
        val m2 = m2Str.toIntOrNull()?.coerceIn(1, 12) ?: return null
        val y2 = y2Str.toIntOrNull() ?: return null
        val months = (y2 - y1) * 12 + (m2 - m1) + if (d2 >= d1) 0 else -1
        return (months.coerceAtLeast(0) / 12.0)
    }

    private fun syncDatesAndRecompute(updateInputsFromDates: Boolean, persist: Boolean = true) {
        _uiState.update { state ->
            val age = computeCompletedYearsUntilJuly2026(state.dobDay, state.dobMonth, state.dobYear)
            val serviceYears = computeServiceYearsUntilJuly2026(state.joinDay, state.joinMonth, state.joinYear)

            if (!updateInputsFromDates) {
                return@update state.copy(
                    computedAgeOnEffectiveDate = age ?: state.computedAgeOnEffectiveDate,
                    computedServiceYears = serviceYears ?: state.computedServiceYears
                )
            }

            val nextAge = age ?: state.computedAgeOnEffectiveDate
            val nextSvc = serviceYears ?: state.computedServiceYears

            val nextActiveMedBracket = when {
                nextAge > 50 -> MedicalAgeBracket.AGE_50_TO_60
                else -> MedicalAgeBracket.UP_TO_50
            }
            val nextPensionMedBracket = when {
                nextAge > 70 -> MedicalAgeBracket.AGE_70_PLUS
                nextAge > 60 -> MedicalAgeBracket.AGE_60_TO_70
                nextAge > 50 -> MedicalAgeBracket.AGE_50_TO_60
                else -> MedicalAgeBracket.UP_TO_50
            }
            val nextServiceBracket = when {
                nextSvc < 5.0 -> ServiceBracket.Y0_5
                nextSvc <= 10.0 -> ServiceBracket.Y5_10
                nextSvc <= 15.0 -> ServiceBracket.Y10_15
                nextSvc <= 20.0 -> ServiceBracket.Y15_20
                else -> ServiceBracket.Y20_PLUS
            }
            val eligibleForJulyIncrement = nextSvc >= 0.5

            state.copy(
                computedAgeOnEffectiveDate = nextAge,
                computedServiceYears = nextSvc,
                activeMedicalAgeBracket = if (age != null) nextActiveMedBracket else state.activeMedicalAgeBracket,
                pensionerAgeBracket = if (age != null) nextPensionMedBracket else state.pensionerAgeBracket,
                retiringAgeNextBirthday = if (age != null) (nextAge + 1).coerceIn(20, 80) else state.retiringAgeNextBirthday,
                serviceBracket = if (serviceYears != null) nextServiceBracket else state.serviceBracket,
                applyJuly2026Increment = if (serviceYears != null) eligibleForJulyIncrement else state.applyJuly2026Increment
            )
        }
        recalculateAll(persist = persist)
    }

    private fun computeCompletedYearsUntilJuly2026(dStr: String, mStr: String, yStr: String): Int? {
        if (yStr.length != 4) return null
        val d = dStr.toIntOrNull()?.coerceIn(1, 31) ?: return null
        val m = mStr.toIntOrNull()?.coerceIn(1, 12) ?: return null
        val y = yStr.toIntOrNull() ?: return null
        if (y !in 1930..2020) return null
        var years = 2026 - y
        if (m > 7 || (m == 7 && d > 1)) {
            years -= 1
        }
        return years.coerceAtLeast(0)
    }

    private fun computeServiceYearsUntilJuly2026(dStr: String, mStr: String, yStr: String): Double? {
        if (yStr.length != 4) return null
        val d = dStr.toIntOrNull()?.coerceIn(1, 31) ?: return null
        val m = mStr.toIntOrNull()?.coerceIn(1, 12) ?: return null
        val y = yStr.toIntOrNull() ?: return null
        if (y !in 1950..2026) return null
        val totalMonths = (2026 - y) * 12 + (7 - m) + if (d <= 15) 0 else -1
        return (totalMonths.coerceAtLeast(0) / 12.0)
    }

    fun selectCategory(category: PersonnelCategory) {
        _uiState.update { state ->
            val nextBranch = when {
                category == PersonnelCategory.OFFICER && state.serviceBranch in listOf(ServiceBranch.MODC, ServiceBranch.RELIGIOUS) -> ServiceBranch.ARMY
                category == PersonnelCategory.CADET && state.serviceBranch in listOf(ServiceBranch.MODC, ServiceBranch.RELIGIOUS) -> ServiceBranch.ARMY
                category == PersonnelCategory.JCO_OR && state.serviceBranch == ServiceBranch.AFNS -> ServiceBranch.ARMY
                else -> state.serviceBranch
            }
            val nextAppendix = when {
                nextBranch == ServiceBranch.AFNS -> OfficerAppendix.AFNS
                state.officerAppendix == OfficerAppendix.AFNS -> OfficerAppendix.STANDARD
                else -> state.officerAppendix
            }
            val nextAccomm = when {
                category == PersonnelCategory.OFFICER && state.accommodationType == AccommodationType.UNMARRIED_OR_MESS -> AccommodationType.NO_QUARTERS_STANDARD
                category != PersonnelCategory.OFFICER && state.accommodationType == AccommodationType.NO_QUARTERS_EXTRA_FACILITY -> AccommodationType.NO_QUARTERS_STANDARD
                else -> state.accommodationType
            }
            var nextState = state.copy(
                category = category,
                serviceBranch = nextBranch,
                officerAppendix = nextAppendix,
                accommodationType = nextAccomm
            )
            if (category == PersonnelCategory.OFFICER) {
                val ranks = JsiPayData.getOfficerRanks(nextAppendix)
                val rank = ranks.find { it.id == state.selectedOfficerRankId } ?: ranks.first()
                val validYear = rank.yearToPay.keys.minOrNull() ?: 1
                nextState = nextState.copy(
                    selectedOfficerRankId = rank.id,
                    commissionedYear = if (rank.yearToPay.containsKey(state.commissionedYear)) state.commissionedYear else validYear
                )
            } else if (category == PersonnelCategory.CADET && nextBranch == ServiceBranch.AFNS) {
                nextState = nextState.copy(selectedCadetId = "afns_cadet")
            }
            nextState
        }
        recalculateAll()
    }

    fun selectOfficerAppendix(appendix: OfficerAppendix) {
        _uiState.update { state ->
            val ranks = JsiPayData.getOfficerRanks(appendix)
            val rank = ranks.find { it.id == state.selectedOfficerRankId } ?: ranks.first()
            val year = if (rank.yearToPay.containsKey(state.commissionedYear)) {
                state.commissionedYear
            } else {
                rank.yearToPay.keys.firstOrNull() ?: 1
            }
            state.copy(
                officerAppendix = appendix,
                selectedOfficerRankId = rank.id,
                commissionedYear = year
            )
        }
        recalculateAll()
    }

    fun selectOfficerRank(rankId: String) {
        _uiState.update { state ->
            val ranks = JsiPayData.getOfficerRanks(state.officerAppendix)
            val rank = ranks.find { it.id == rankId } ?: ranks.first()
            val year = if (rank.yearToPay.containsKey(state.commissionedYear)) {
                state.commissionedYear
            } else {
                rank.yearToPay.keys.firstOrNull() ?: 1
            }
            state.copy(
                selectedOfficerRankId = rankId,
                commissionedYear = year,
                rankPrescribedYearsInput = rank.defaultPrescribedService.toString()
            )
        }
        recalculateAll()
    }

    fun selectCommissionedYear(year: Int) {
        _uiState.update { it.copy(commissionedYear = year) }
        recalculateAll()
    }

    fun updateOfficerOldBasic(value: String) {
        _uiState.update { it.copy(officerOldBasicInput = value) }
        recalculateAll()
    }

    fun selectCadet(cadetId: String) {
        _uiState.update { it.copy(selectedCadetId = cadetId) }
        recalculateAll()
    }

    fun selectNonOfficerRank(rankId: String) {
        val rank = JsiPayData.nonOfficerRanks.find { it.id == rankId } ?: JsiPayData.nonOfficerRanks.first()
        _uiState.update { state ->
            val defaultOldPay = if (rank.id == "sgt") "26120" else rank.oldScaleStart.toString()
            state.copy(
                selectedNonOfficerRankId = rankId,
                oldScaleStartOverride = rank.oldScaleStart.toString(),
                oldBasicPayInput = defaultOldPay
            )
        }
        recalculateAll()
    }

    fun updateOldBasicPay(value: String) {
        _uiState.update { it.copy(oldBasicPayInput = value) }
        recalculateAll()
    }

    fun updateOldScaleStart(value: String) {
        _uiState.update { it.copy(oldScaleStartOverride = value) }
        recalculateAll()
    }

    fun toggleJuly2026Increment(checked: Boolean) {
        _uiState.update { it.copy(applyJuly2026Increment = checked) }
        recalculateAll()
    }

    fun setMaritalStatus(married: Boolean) {
        _uiState.update { state ->
            val nextAccomm = when {
                !married && state.category != PersonnelCategory.OFFICER -> AccommodationType.UNMARRIED_OR_MESS
                married && state.accommodationType == AccommodationType.UNMARRIED_OR_MESS -> AccommodationType.NO_QUARTERS_STANDARD
                else -> state.accommodationType
            }
            state.copy(
                isMarried = married,
                accommodationType = nextAccomm
            )
        }
        recalculateAll()
    }

    fun selectChildrenCount(count: Int) {
        _uiState.update { it.copy(childrenCount = count.coerceIn(0, 2)) }
        recalculateAll()
    }

    fun selectDisabledChildrenCount(count: Int) {
        _uiState.update { it.copy(disabledChildrenCount = count.coerceIn(0, 2)) }
        recalculateAll()
    }

    fun selectProficiencyPay(id: String) {
        _uiState.update { it.copy(proficiencyPayId = id) }
        recalculateAll()
    }

    fun selectAppointmentPay(id: String) {
        _uiState.update { it.copy(appointmentPayId = id) }
        recalculateAll()
    }

    fun updateGoodConductBadges(count: Int) {
        _uiState.update { it.copy(goodConductBadges = count.coerceIn(0, 3)) }
        recalculateAll()
    }

    fun toggleSpecialSkill(skill: SpecialSkillPay) {
        _uiState.update { state ->
            val updated = state.selectedSpecialSkills.toMutableSet()
            if (skill in updated) updated.remove(skill) else updated.add(skill)
            state.copy(selectedSpecialSkills = updated)
        }
        recalculateAll()
    }

    fun updateOtherNewAllowance(value: String) {
        _uiState.update { it.copy(otherNewAllowanceInput = value) }
        recalculateAll()
    }

    fun selectReceiveMonthIndex(index: Int) {
        _uiState.update { it.copy(receiveMonthIndex = index) }
        recalculateAll()
    }

    fun updateMonthlyBiseshSubidha(value: String) {
        _uiState.update { it.copy(monthlyBiseshSubidhaInput = value) }
        recalculateAll()
    }

    fun selectCityCategory(city: CityCategory) {
        _uiState.update { it.copy(cityCategory = city) }
        recalculateAll()
    }

    fun selectAccommodationType(type: AccommodationType) {
        _uiState.update { it.copy(accommodationType = type) }
        recalculateAll()
    }

    fun selectServiceBracket(bracket: ServiceBracket) {
        _uiState.update { it.copy(serviceBracket = bracket) }
        recalculateAll()
    }

    fun selectActiveMedicalAgeBracket(bracket: MedicalAgeBracket) {
        _uiState.update { it.copy(activeMedicalAgeBracket = bracket) }
        recalculateAll()
    }

    fun toggleFreeTiffin(free: Boolean) {
        _uiState.update { it.copy(receiveFreeTiffin = free) }
        recalculateAll()
    }

    fun selectHillHaorCategory(cat: HillHaorCategory) {
        _uiState.update { it.copy(hillHaorCategory = cat) }
        recalculateAll()
    }

    fun updateOldServiceOrdnance(value: String) {
        _uiState.update { it.copy(oldServiceOrdnanceInput = value) }
        recalculateAll()
    }

    fun updateGpfPercent(percent: Int) {
        _uiState.update { it.copy(gpfPercent = percent.coerceIn(5, 25)) }
        recalculateAll()
    }

    fun selectPensionSubTab(subTab: PensionSubTab) {
        _uiState.update { it.copy(pensionSubTab = subTab) }
        recalculateAll()
    }

    fun updateOldNetPension(value: String) {
        _uiState.update { it.copy(oldNetPensionInput = value) }
        recalculateAll()
    }

    fun selectPensionerAgeBracket(bracket: MedicalAgeBracket) {
        _uiState.update { it.copy(pensionerAgeBracket = bracket) }
        recalculateAll()
    }

    fun updateAdultRationMembers(count: Int) {
        _uiState.update { it.copy(adultRationMembers = count.coerceIn(0, 10)) }
        recalculateAll()
    }

    fun updateChildRationMembers(count: Int) {
        _uiState.update { it.copy(childRationMembers = count.coerceIn(0, 10)) }
        recalculateAll()
    }

    fun selectRetiringType(type: RetiringType) {
        _uiState.update { it.copy(retiringType = type) }
        recalculateAll()
    }

    fun updateRetiringLastPay(value: String) {
        _uiState.update { it.copy(retiringLastPayInput = value) }
        recalculateAll()
    }

    fun updateRetiringQualifyingYears(value: String) {
        _uiState.update { it.copy(retiringQualifyingYearsInput = value) }
        recalculateAll()
    }

    fun updateRankPrescribedYears(value: String) {
        _uiState.update { it.copy(rankPrescribedYearsInput = value) }
        recalculateAll()
    }

    fun updateRetiringAgeNextBirthday(age: Int) {
        _uiState.update { it.copy(retiringAgeNextBirthday = age.coerceIn(20, 80)) }
        recalculateAll()
    }

    fun updateCommutationSurrenderPercent(percent: Int) {
        _uiState.update { it.copy(commutationSurrenderPercent = percent.coerceIn(0, 100)) }
        recalculateAll()
    }

    fun submitAndNavigate() {
        syncPayToAllowancesAndPension()
        _uiState.update { state ->
            state.copy(
                selectedTab = if (state.isPensionerRole) CalculatorTab.PENSION else CalculatorTab.SALARY
            )
        }
    }

    fun resetAll() {
        hasUserInteracted = true
        val keepBn = _uiState.value.useBengaliDigits
        val keepSaved = _uiState.value.savedCalculations
        _uiState.value = CalculatorUiState(
            useBengaliDigits = keepBn,
            isLoadedFromDb = true,
            savedCalculations = keepSaved
        )
        syncDatesAndRecompute(updateInputsFromDates = false, persist = true)
    }

    fun syncPayToAllowancesAndPension() {
        val pay = _uiState.value.payFixationResult ?: return
        _uiState.update { state ->
            state.copy(
                pensionSubTab = PensionSubTab.RETIRING,
                retiringType = if (pay.isOfficer) RetiringType.OFFICER else RetiringType.JCO_OR,
                retiringLastPayInput = pay.july2027BasicPay.toString(),
                rankPrescribedYearsInput = pay.defaultPrescribedService.toString()
            )
        }
        recalculateAll()
    }

    private fun recalculateAll(persist: Boolean = true) {
        if (persist) {
            hasUserInteracted = true
        }
        val state = _uiState.value
        val bn = state.useBengaliDigits

        val payResult = computePayFixation(state, bn)
        val allowResult = computeAllowances(state, payResult, payResult.july2027BasicPay, bn)
        val phasedResult = computePhasedArrears(state, payResult, allowResult, bn)
        val existingPension = computeExistingPension(state, bn)
        val retiringPension = computeRetiringPension(state, payResult, bn)

        _uiState.update {
            it.copy(
                payFixationResult = payResult,
                phasedArrearsResult = phasedResult,
                allowancesResult = allowResult,
                existingPensionResult = existingPension,
                retiringPensionResult = retiringPension
            )
        }
        if (persist) {
            schedulePersist()
        }
    }

    private fun computePayFixation(state: CalculatorUiState, bn: Boolean): PayFixationResult {
        val steps = mutableListOf<String>()
        val eightSteps = mutableListOf<PayFixationStepItem>()

        when (state.category) {
            PersonnelCategory.OFFICER -> {
                val ranks = JsiPayData.getOfficerRanks(state.officerAppendix)
                val rank = ranks.find { it.id == state.selectedOfficerRankId } ?: ranks.first()
                val shortName = rank.bnName.substringBefore(" /").trim()
                val oldBasic = BengaliFormat.parseSafeNumber(state.officerOldBasicInput)

                if (rank.isFixed) {
                    val fixed = rank.fixedPay
                    eightSteps.add(PayFixationStepItem("১", "৩০ জুন ২০২৬-এ আহরিত মূল বেতন", "পুরোনো স্কেল (জেএসআই ০১/২০১৬)", BengaliFormat.formatBdt(oldBasic.toDouble(), bn)))
                    eightSteps.add(PayFixationStepItem("২", "জেএসআই ০১/২০২৬ নির্ধারিত মূল বেতন", "নির্ধারিত স্কেল (${state.officerAppendix.bnTitle})", BengaliFormat.formatBdt(fixed.toDouble(), bn), StepValueType.POSITIVE, true))

                    steps.add("১. অধ্যায়-১ অনুচ্ছেদ ২(ক) অনুযায়ী ${rank.bnName} পদের বেতন নির্ধারিত।")
                    steps.add("২. জেএসআই ০১/২০২৬ নির্ধারিত মূল বেতন = ${BengaliFormat.formatBdt(fixed.toDouble(), bn)}।")
                    return PayFixationResult(
                        rankTitle = rank.bnName,
                        rankShortTitle = shortName,
                        appendixShortLabel = state.officerAppendix.bnTitle.substringBefore(" ("),
                        newBasicPay = fixed,
                        fixedBeforeIncrementPay = fixed,
                        july2026IncrementAmount = 0L,
                        july2027BasicPay = fixed,
                        oldBasicPay = oldBasic,
                        oldScaleStart = oldBasic,
                        newScaleStart = fixed,
                        implementationGroup = ImplementationGroup.GROUP_A,
                        allowanceTier = rank.allowanceTier,
                        isOfficer = true,
                        isAfns = state.officerAppendix == OfficerAppendix.AFNS,
                        isSergeantOrBelow = false,
                        isCplOrBelow = false,
                        defaultPrescribedService = rank.defaultPrescribedService,
                        eightSteps = eightSteps,
                        breakdownSteps = steps
                    )
                } else {
                    val availableYears = rank.yearToPay.keys.sorted()
                    val selectedYear = if (rank.yearToPay.containsKey(state.commissionedYear)) {
                        state.commissionedYear
                    } else {
                        availableYears.first()
                    }
                    val newBasic = rank.yearToPay[selectedYear] ?: 0L
                    val nextYearBasic = rank.yearToPay[selectedYear + 1] ?: newBasic
                    val startPay = rank.yearToPay[availableYears.first()] ?: newBasic

                    eightSteps.add(PayFixationStepItem("১", "৩০ জুন ২০২৬-এ আহরিত মূল বেতন", "পুরোনো স্কেল (জেএসআই ০১/২০১৬)", BengaliFormat.formatBdt(oldBasic.toDouble(), bn)))
                    eightSteps.add(PayFixationStepItem("২", "উল্লম্ব স্কেল (Vertical Scale) বছর", "${shortName} — ${BengaliFormat.formatNumber(selectedYear.toLong(), bn)}ম বর্ষ", BengaliFormat.formatBdt(newBasic.toDouble(), bn), StepValueType.POSITIVE, true))
                    if (oldBasic > 0L) {
                        eightSteps.add(PayFixationStepItem("৩", "মোট মূল বেতন বৃদ্ধি", "(২) থেকে (১) বিয়োগ", "+ ${BengaliFormat.formatBdt((newBasic - oldBasic).coerceAtLeast(0L).toDouble(), bn)}", StepValueType.POSITIVE))
                    }

                    steps.add("১. অধ্যায়-১ অনুচ্ছেদ ২(খ) অনুযায়ী উল্লম্ব স্কেলে পদবি ও কমিশন্ড চাকরির বৎসরের ভিত্তিতে মূল বেতন নির্ধারিত।")
                    steps.add("২. পদবি: ${rank.bnName}, কমিশন্ড চাকরির বৎসর: ${BengaliFormat.formatNumber(selectedYear.toLong(), bn)}ম বর্ষ = ${BengaliFormat.formatBdt(newBasic.toDouble(), bn)}।")
                    if (nextYearBasic > newBasic) {
                        steps.add("৩. ০১ জুলাই ২০২৭ (পরবর্তী বর্ষে উন্নীত): ${BengaliFormat.formatBdt(nextYearBasic.toDouble(), bn)}।")
                    }

                    return PayFixationResult(
                        rankTitle = "${rank.bnName} (${BengaliFormat.formatNumber(selectedYear.toLong(), bn)}ম বর্ষ)",
                        rankShortTitle = shortName,
                        appendixShortLabel = state.officerAppendix.bnTitle.substringBefore(" ("),
                        newBasicPay = newBasic,
                        fixedBeforeIncrementPay = newBasic,
                        july2026IncrementAmount = 0L,
                        july2027BasicPay = nextYearBasic,
                        oldBasicPay = oldBasic,
                        oldScaleStart = startPay,
                        newScaleStart = startPay,
                        implementationGroup = ImplementationGroup.GROUP_A,
                        allowanceTier = rank.allowanceTier,
                        isOfficer = true,
                        isAfns = state.officerAppendix == OfficerAppendix.AFNS,
                        isSergeantOrBelow = false,
                        isCplOrBelow = false,
                        defaultPrescribedService = rank.defaultPrescribedService,
                        eightSteps = eightSteps,
                        breakdownSteps = steps
                    )
                }
            }

            PersonnelCategory.CADET -> {
                val cadet = JsiPayData.cadetOptions.find { it.id == state.selectedCadetId } ?: JsiPayData.cadetOptions.first()
                val shortName = cadet.bnName.substringBefore(" (").trim()
                eightSteps.add(PayFixationStepItem("১", "পূর্বের নির্ধারিত বেতন", "জেএসআই ০১/২০১৬", BengaliFormat.formatBdt(cadet.oldFixedPay.toDouble(), bn)))
                eightSteps.add(PayFixationStepItem("২", "নতুন নির্ধারিত বেতন", "অধ্যায়-১ ক্রোড়পত্র-জ", BengaliFormat.formatBdt(cadet.fixedPay.toDouble(), bn), StepValueType.POSITIVE, true))

                steps.add("১. অধ্যায়-১ ক্রোড়পত্র-জ অনুযায়ী ক্যাডেট/মিডশীপম্যানদের নির্ধারিত বেতন = ${BengaliFormat.formatBdt(cadet.fixedPay.toDouble(), bn)}।")
                return PayFixationResult(
                    rankTitle = cadet.bnName,
                    rankShortTitle = shortName,
                    appendixShortLabel = "ক্রোড়পত্র-জ",
                    newBasicPay = cadet.fixedPay,
                    fixedBeforeIncrementPay = cadet.fixedPay,
                    july2026IncrementAmount = 0L,
                    july2027BasicPay = cadet.fixedPay,
                    oldBasicPay = cadet.oldFixedPay,
                    oldScaleStart = cadet.oldFixedPay,
                    newScaleStart = cadet.fixedPay,
                    implementationGroup = ImplementationGroup.GROUP_B,
                    allowanceTier = RankTierForAllowances.CADET,
                    isOfficer = false,
                    isAfns = cadet.id == "afns_cadet",
                    isSergeantOrBelow = true,
                    isCplOrBelow = false,
                    defaultPrescribedService = 20,
                    eightSteps = eightSteps,
                    breakdownSteps = steps
                )
            }

            PersonnelCategory.JCO_OR -> {
                val rank = JsiPayData.nonOfficerRanks.find { it.id == state.selectedNonOfficerRankId }
                    ?: JsiPayData.nonOfficerRanks.first()
                val shortName = rank.bnName.substringBefore(" /").trim()
                val oldBasic = BengaliFormat.parseSafeNumber(state.oldBasicPayInput)
                    .let { if (it > 0L) it else rank.oldScaleStart }

                if (rank.isHonoraryFixed) {
                    val protectedPay = if (oldBasic > rank.fixedPay) oldBasic + 500L else rank.fixedPay
                    eightSteps.add(PayFixationStepItem("১", "৩০ জুন ২০২৬-এ আহরিত মূল বেতন", "পুরোনো স্কেল (জেএসআই ০১/২০১৬)", BengaliFormat.formatBdt(oldBasic.toDouble(), bn)))
                    eightSteps.add(PayFixationStepItem("২", "নির্ধারিত স্কেল ও সুরক্ষা নিয়ম", "অনারারি পদের নির্ধারিত মূল বেতন", BengaliFormat.formatBdt(protectedPay.toDouble(), bn), StepValueType.POSITIVE, true))
                    steps.add("১. অধ্যায়-২ অনুচ্ছেদ ২(ক) অনুযায়ী ${rank.bnName} পদের মূল বেতন = ${BengaliFormat.formatBdt(protectedPay.toDouble(), bn)}।")
                    return PayFixationResult(
                        rankTitle = rank.bnName,
                        rankShortTitle = shortName,
                        appendixShortLabel = "ক্রোড়পত্র-ক",
                        newBasicPay = protectedPay,
                        fixedBeforeIncrementPay = protectedPay,
                        july2026IncrementAmount = 0L,
                        july2027BasicPay = protectedPay,
                        oldBasicPay = oldBasic,
                        oldScaleStart = rank.oldScaleStart,
                        newScaleStart = rank.fixedPay,
                        implementationGroup = rank.group,
                        allowanceTier = rank.allowanceTier,
                        isOfficer = false,
                        isAfns = false,
                        isSergeantOrBelow = false,
                        isCplOrBelow = false,
                        defaultPrescribedService = 25,
                        eightSteps = eightSteps,
                        breakdownSteps = steps
                    )
                }

                if (rank.isRecruitFixed) {
                    eightSteps.add(PayFixationStepItem("১", "৩০ জুন ২০২৬-এ আহরিত মূল বেতন", "পুরোনো স্কেল (জেএসআই ০১/২০১৬)", BengaliFormat.formatBdt(rank.oldScaleStart.toDouble(), bn)))
                    eightSteps.add(PayFixationStepItem("২", "জেএসআই ০১/২০২৬ নির্ধারিত বেতন", "প্রশিক্ষণার্থী নির্ধারিত", BengaliFormat.formatBdt(rank.fixedPay.toDouble(), bn), StepValueType.POSITIVE, true))
                    steps.add("১. অধ্যায়-২ অনুযায়ী ${rank.bnName} পদের নির্ধারিত বেতন = ${BengaliFormat.formatBdt(rank.fixedPay.toDouble(), bn)}।")
                    return PayFixationResult(
                        rankTitle = rank.bnName,
                        rankShortTitle = shortName,
                        appendixShortLabel = "ক্রোড়পত্র-ক",
                        newBasicPay = rank.fixedPay,
                        fixedBeforeIncrementPay = rank.fixedPay,
                        july2026IncrementAmount = 0L,
                        july2027BasicPay = rank.fixedPay,
                        oldBasicPay = oldBasic,
                        oldScaleStart = rank.oldScaleStart,
                        newScaleStart = rank.fixedPay,
                        implementationGroup = rank.group,
                        allowanceTier = rank.allowanceTier,
                        isOfficer = false,
                        isAfns = false,
                        isSergeantOrBelow = true,
                        isCplOrBelow = true,
                        defaultPrescribedService = 25,
                        eightSteps = eightSteps,
                        breakdownSteps = steps
                    )
                }

                // Exact 8-step JSI 01/2026 Pay Fixation Algorithm (matching Screenshot 4 & Gazette Page 24)
                val oldScaleStart = BengaliFormat.parseSafeNumber(state.oldScaleStartOverride)
                    .let { if (it > 0L) it else rank.oldScaleStart }
                val scale = rank.steps
                val newScaleStart = scale.first()
                val diff = (oldBasic - oldScaleStart).coerceAtLeast(0L)
                val targetAmount = newScaleStart + diff

                var matchedBeforeIncIdx: Int
                var personalPay = 0L
                val matchReasonSub: String
                if (oldBasic <= oldScaleStart) {
                    matchedBeforeIncIdx = 0
                    matchReasonSub = "স্কেলের ১ম ধাপে নির্ধারণ"
                } else {
                    val foundIdx = scale.indexOfFirst { it >= targetAmount }
                    if (foundIdx >= 0) {
                        matchedBeforeIncIdx = foundIdx
                        matchReasonSub = if (scale[foundIdx] == targetAmount) {
                            "স্কেলে সমান ধাপ থাকায় ${BengaliFormat.formatNumber((foundIdx + 1).toLong(), bn)} নং ধাপে নির্ধারণ"
                        } else {
                            "স্কেলে সমান ধাপ না থাকায় পরবর্তী উচ্চতর ধাপে নির্ধারণ"
                        }
                    } else {
                        matchedBeforeIncIdx = scale.lastIndex
                        personalPay = (oldBasic - scale.last()).coerceAtLeast(0L)
                        matchReasonSub = "সর্বোচ্চ ধাপে নির্ধারণ"
                    }
                }

                val fixedBeforeIncrement = scale[matchedBeforeIncIdx]
                var finalIdx2026 = matchedBeforeIncIdx
                if (state.applyJuly2026Increment && finalIdx2026 < scale.lastIndex) {
                    finalIdx2026 += 1
                }
                val newBasic2026 = scale[finalIdx2026]
                val inc2026 = (newBasic2026 - fixedBeforeIncrement).coerceAtLeast(0L)
                val july2027Idx = (finalIdx2026 + 1).coerceAtMost(scale.lastIndex)
                val july2027Basic = scale[july2027Idx]

                eightSteps.add(
                    PayFixationStepItem(
                        stepBn = if (bn) "১" else "1",
                        title = "৩০ জুন ২০২৬-এ আহরিত মূল বেতন",
                        subtitle = "পুরোনো স্কেল (জেএসআই ০১/২০১৬)",
                        formattedValue = BengaliFormat.formatBdt(oldBasic.toDouble(), bn)
                    )
                )
                eightSteps.add(
                    PayFixationStepItem(
                        stepBn = if (bn) "২" else "2",
                        title = "পুরোনো স্কেলের প্রারম্ভিক ধাপ",
                        subtitle = "$shortName পদের স্কেল শুরু",
                        formattedValue = "− ${BengaliFormat.formatBdt(oldScaleStart.toDouble(), bn)}",
                        valueType = StepValueType.NEGATIVE
                    )
                )
                eightSteps.add(
                    PayFixationStepItem(
                        stepBn = if (bn) "৩" else "3",
                        title = "পার্থক্য",
                        subtitle = "(১) থেকে (২) বিয়োগ",
                        formattedValue = BengaliFormat.formatBdt(diff.toDouble(), bn),
                        valueType = StepValueType.HIGHLIGHT_GOLD,
                        isHighlightedRow = true
                    )
                )
                eightSteps.add(
                    PayFixationStepItem(
                        stepBn = if (bn) "৪" else "4",
                        title = "নতুন স্কেলের প্রারম্ভিক ধাপ",
                        subtitle = "জেএসআই ০১/২০২৬, ক্রোড়পত্র-ক",
                        formattedValue = BengaliFormat.formatBdt(newScaleStart.toDouble(), bn)
                    )
                )
                eightSteps.add(
                    PayFixationStepItem(
                        stepBn = if (bn) "৫" else "5",
                        title = "পার্থক্য যোগ",
                        subtitle = "(৩)-এর অঙ্ক",
                        formattedValue = "+ ${BengaliFormat.formatBdt(diff.toDouble(), bn)}",
                        valueType = StepValueType.POSITIVE
                    )
                )
                eightSteps.add(
                    PayFixationStepItem(
                        stepBn = if (bn) "৬" else "6",
                        title = "নতুন স্কেলে প্রাপ্য অঙ্ক",
                        subtitle = "(৪) + (৫)",
                        formattedValue = BengaliFormat.formatBdt(targetAmount.toDouble(), bn),
                        valueType = StepValueType.HIGHLIGHT_GOLD,
                        isHighlightedRow = true
                    )
                )
                eightSteps.add(
                    PayFixationStepItem(
                        stepBn = if (bn) "৭" else "7",
                        title = "নতুন স্কেলে নির্ধারিত মূল বেতন",
                        subtitle = matchReasonSub,
                        formattedValue = BengaliFormat.formatBdt(fixedBeforeIncrement.toDouble(), bn)
                    )
                )
                eightSteps.add(
                    PayFixationStepItem(
                        stepBn = if (bn) "৮" else "8",
                        title = "০১ জুলাই ২০২৬-এর বার্ষিক ইনক্রিমেন্ট",
                        subtitle = if (state.applyJuly2026Increment && inc2026 > 0L) "পরবর্তী ধাপে উন্নীত" else "প্রযোজ্য নয় / সর্বোচ্চ ধাপ",
                        formattedValue = "+ ${BengaliFormat.formatBdt(inc2026.toDouble(), bn)}",
                        valueType = StepValueType.POSITIVE
                    )
                )

                steps.add("১. ধাপ-১ (পার্থক্য): ${BengaliFormat.formatBdt(oldBasic.toDouble(), bn)} − ${BengaliFormat.formatBdt(oldScaleStart.toDouble(), bn)} = ${BengaliFormat.formatBdt(diff.toDouble(), bn)}।")
                steps.add("২. ধাপ-২ (প্রাপ্য অঙ্ক): ${BengaliFormat.formatBdt(newScaleStart.toDouble(), bn)} + ${BengaliFormat.formatBdt(diff.toDouble(), bn)} = ${BengaliFormat.formatBdt(targetAmount.toDouble(), bn)}।")
                steps.add("৩. ধাপ-৩ (স্কেল ম্যাচিং): $matchReasonSub → ${BengaliFormat.formatBdt(fixedBeforeIncrement.toDouble(), bn)}।")
                steps.add("৪. ধাপ-৪ (০১ জুলাই ২০২৬ ইনক্রিমেন্ট): ${BengaliFormat.formatBdt(newBasic2026.toDouble(), bn)} (জুলাই ২০২৭ পরবর্তী ধাপ: ${BengaliFormat.formatBdt(july2027Basic.toDouble(), bn)})।")

                return PayFixationResult(
                    rankTitle = rank.bnName,
                    rankShortTitle = shortName,
                    appendixShortLabel = "জেএসআই ০১/২০২৬",
                    newBasicPay = newBasic2026,
                    fixedBeforeIncrementPay = fixedBeforeIncrement,
                    july2026IncrementAmount = inc2026,
                    july2027BasicPay = july2027Basic,
                    oldBasicPay = oldBasic,
                    oldScaleStart = oldScaleStart,
                    newScaleStart = newScaleStart,
                    personalPay = personalPay,
                    matchedStepIndex = finalIdx2026 + 1,
                    totalSteps = scale.size,
                    implementationGroup = rank.group,
                    allowanceTier = rank.allowanceTier,
                    isOfficer = false,
                    isAfns = false,
                    isSergeantOrBelow = rank.isSergeantOrBelow,
                    isCplOrBelow = rank.isCplOrBelowForTiffinTransport,
                    defaultPrescribedService = 25,
                    eightSteps = eightSteps,
                    breakdownSteps = steps
                )
            }
        }
    }

    private fun computeOldAllowancesBreakdown(
        state: CalculatorUiState,
        pay: PayFixationResult
    ): List<Pair<String, Long>> {
        val tier = pay.allowanceTier
        val items = mutableListOf<Pair<String, Long>>()
        if (tier == RankTierForAllowances.CADET) return items

        val oldOrd = if (pay.isSergeantOrBelow && tier != RankTierForAllowances.RECRUIT) {
            BengaliFormat.parseSafeNumber(state.oldServiceOrdnanceInput)
        } else 0L
        if (oldOrd > 0L) items.add("সার্ভিস ওয়ার্ডিন্যান্স বেতন (পুরোনো)" to oldOrd)

        val oldBasis = pay.oldBasicPay + oldOrd
        val hrRate = JsiPayData.getHouseRentRate(
            isOfficer = pay.isOfficer,
            tier = tier,
            city = state.cityCategory,
            accommodation = state.accommodationType
        )
        val oldHr = (oldBasis * hrRate).roundToLong()
        if (oldHr > 0L) items.add("বাড়ি ভাড়া ভাতা (${(hrRate * 100).toInt()}%, পুরোনো স্কেল)" to oldHr)

        val isReligious = state.category == PersonnelCategory.JCO_OR && state.serviceBranch == ServiceBranch.RELIGIOUS
        val oldDsa = JsiPayData.getOldDefenceServiceAllowance(tier, state.serviceBracket, isReligious)
        if (oldDsa > 0L) items.add("প্রতিরক্ষা সার্ভিস ভাতা (পুরোনো হার)" to oldDsa)

        val oldMedical = if (pay.isOfficer || tier == RankTierForAllowances.RECRUIT) 0L else 1500L
        if (oldMedical > 0L) items.add("চিকিৎসা ভাতা (পুরোনো হার)" to oldMedical)

        val oldKit = when {
            pay.isOfficer && pay.isAfns -> 800L
            pay.isOfficer -> 1200L
            tier == RankTierForAllowances.RECRUIT -> 0L
            pay.isSergeantOrBelow -> 130L
            else -> 143L
        }
        if (oldKit > 0L) {
            items.add((if (pay.isOfficer) "কিট ভাতা (পুরোনো)" else "কিট রক্ষণাবেক্ষণ / ধোলাই ভাতা") to oldKit)
        }

        val oldEdu = if (state.childrenCount > 0) state.childrenCount.coerceIn(0, 2) * 500L else 0L
        if (oldEdu > 0L) items.add("শিক্ষা সহায়ক ভাতা" to oldEdu)

        val oldTiffin = if (pay.isCplOrBelow && !state.receiveFreeTiffin) 200L else 0L
        if (oldTiffin > 0L) items.add("টিফিন ভাতা (পুরোনো)" to oldTiffin)

        val oldTransport = if (pay.isCplOrBelow &&
            (state.cityCategory == CityCategory.DHAKA || state.cityCategory == CityCategory.OTHER_CITY_CORP)
        ) 300L else 0L
        if (oldTransport > 0L) items.add("যাতায়াত ভাতা (পুরোনো)" to oldTransport)

        val oldHill = when (state.hillHaorCategory) {
            HillHaorCategory.NONE -> 0L
            HillHaorCategory.SADAR -> min((pay.oldBasicPay * 0.20).roundToLong(), 3000L)
            HillHaorCategory.OTHER_UPAZILA -> min((pay.oldBasicPay * 0.20).roundToLong(), 3500L)
        }
        if (oldHill > 0L) items.add("পাহাড়ি / হাওড় ভাতা (পুরোনো)" to oldHill)

        val oldProf = if (!pay.isOfficer && tier != RankTierForAllowances.RECRUIT) {
            JsiPayData.proficiencyPayOptions.find { it.id == state.proficiencyPayId }?.oldAmount ?: 0L
        } else 0L
        if (oldProf > 0L) items.add("দক্ষতা বেতন (পুরোনো)" to oldProf)

        val oldAppt = if (!pay.isOfficer && tier != RankTierForAllowances.RECRUIT) {
            JsiPayData.appointmentPayOptions.find { it.id == state.appointmentPayId }?.oldAmount ?: 0L
        } else 0L
        if (oldAppt > 0L) items.add("নিযুক্তি বেতন (পুরোনো)" to oldAppt)

        val oldBadge = if (pay.isSergeantOrBelow && tier != RankTierForAllowances.RECRUIT) {
            state.goodConductBadges.coerceIn(0, 3) * 30L
        } else 0L
        if (oldBadge > 0L) items.add("সদাচরণ ব্যাজ (পুরোনো)" to oldBadge)

        val oldSkill = if (tier != RankTierForAllowances.RECRUIT) {
            state.selectedSpecialSkills.sumOf { JsiPayData.getOldSpecialSkillAmount(it, pay.isOfficer, tier) }
        } else 0L
        if (oldSkill > 0L) items.add("বিশেষ দক্ষতা বেতন (পুরোনো)" to oldSkill)

        return items
    }

    private fun computePhasedArrears(
        state: CalculatorUiState,
        pay: PayFixationResult,
        allow2028: AllowancesResult,
        bn: Boolean
    ): PhasedArrearsResult {
        val oldBasic = pay.oldBasicPay
        val newBasic2026 = pay.newBasicPay
        val july2027Basic = pay.july2027BasicPay
        val increase2026 = (newBasic2026 - oldBasic).coerceAtLeast(0L)
        val group = pay.implementationGroup

        val p1 = oldBasic + (increase2026 * group.phase1Rate).roundToLong()
        val p2 = oldBasic + (increase2026 * group.phase2Rate).roundToLong()
        val p3 = july2027Basic

        // Special benefit (বিশেষ সুবিধা) auto-calculation if user leaves blank
        val isGrade10To20 = pay.isSergeantOrBelow || pay.allowanceTier == RankTierForAllowances.CADET
        val rateBisesh = if (isGrade10To20) 0.15 else 0.10
        val minBisesh = if (isGrade10To20) 2000L else 1500L
        val computedAutoBisesh = max((oldBasic * rateBisesh).roundToLong(), minBisesh)
        val rawPercentCalc = (oldBasic * rateBisesh).roundToLong()
        val biseshRuleNote = if (rawPercentCalc < minBisesh) {
            "আগের মূল বেতন ${BengaliFormat.formatNumber(oldBasic, bn)} টাকার ${if (isGrade10To20) "১৫%" else "১০%"} ${BengaliFormat.formatNumber(minBisesh, bn)}-এর কম, তাই ন্যূনতম ${BengaliFormat.formatNumber(minBisesh, bn)}"
        } else {
            "আগের মূল বেতন ${BengaliFormat.formatNumber(oldBasic, bn)} টাকার ${if (isGrade10To20) "১৫%" else "১০%"} হারে নির্ধারিত"
        }

        val effectiveBisesh = if (state.monthlyBiseshSubidhaInput.isBlank()) {
            computedAutoBisesh
        } else {
            BengaliFormat.parseSafeNumber(state.monthlyBiseshSubidhaInput)
        }

        // Pro-rata or selected receipt month arrears
        val today = referenceDateOverride ?: LocalDate.now()
        val startOption = JsiPayData.implementationStartMonths.getOrNull(state.receiveMonthIndex)
            ?: JsiPayData.implementationStartMonths.first()

        val fractionMonths: Double
        val dateRangeSubtitle: String
        val durationSummary: String

        if (startOption.second == -1) {
            val effectiveToday = if (today.isBefore(LocalDate.of(2026, 7, 1))) {
                LocalDate.of(2026, 7, 1)
            } else {
                today
            }
            val fullMonthsBeforeCurrent = ((effectiveToday.year - 2026) * 12 + (effectiveToday.monthValue - 7)).coerceAtLeast(0)
            val currentDayCount = effectiveToday.dayOfMonth.coerceIn(1, 30)
            fractionMonths = fullMonthsBeforeCurrent + (currentDayCount / 30.0)
            val monthNamesBn = listOf(
                "", "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
                "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
            )
            val todayMonthBn = monthNamesBn.getOrElse(effectiveToday.monthValue) { "" }
            dateRangeSubtitle = "০১ জুলাই ২০২৬ থেকে আজ ${BengaliFormat.formatNumber(effectiveToday.dayOfMonth.toLong(), bn)} $todayMonthBn ${BengaliFormat.formatNumber(effectiveToday.year.toLong(), bn)} পর্যন্ত"
            val displayTotalMonthCount = fullMonthsBeforeCurrent + 1
            durationSummary = "মোট ${BengaliFormat.formatNumber(displayTotalMonthCount.toLong(), bn)} মাসের সম্ভাব্য বকেয়া (চলতি মাসের ${BengaliFormat.formatNumber(currentDayCount.toLong(), bn)} দিনসহ)"
        } else {
            fractionMonths = startOption.second.toDouble()
            dateRangeSubtitle = "০১ জুলাই ২০২৬ থেকে (${startOption.first})"
            durationSummary = "মোট ${BengaliFormat.formatNumber(startOption.second.toLong(), bn)} পূর্ণ মাসের প্রাপ্য বকেয়া"
        }

        val p1Months = fractionMonths.coerceIn(0.0, 6.0)
        val p2Months = (fractionMonths - 6.0).coerceIn(0.0, 6.0)
        val p3Months = (fractionMonths - 12.0).coerceAtLeast(0.0)

        val p1MonthlyDiff = (p1 - oldBasic).coerceAtLeast(0L)
        val p2MonthlyDiff = (p2 - oldBasic).coerceAtLeast(0L)
        val p3MonthlyDiff = (p3 - oldBasic).coerceAtLeast(0L)

        val grossArrears = ((p1Months * p1MonthlyDiff) + (p2Months * p2MonthlyDiff) + (p3Months * p3MonthlyDiff)).roundToLong()
        val totalBiseshDeducted = (effectiveBisesh * fractionMonths).roundToLong()
        val netArrears = (grossArrears - totalBiseshDeducted).coerceAtLeast(0L)
        val probableGpfOnArrears = (grossArrears * (state.gpfPercent / 100.0)).roundToLong()

        // Build 4 Vertical Timeline Milestones (Screenshots 3, 5, 7)
        val oldAllowItems = computeOldAllowancesBreakdown(state, pay)
        val oldAllowTotal = oldAllowItems.sumOf { it.second }
        val isCadetOrRecruit = pay.allowanceTier == RankTierForAllowances.CADET || pay.allowanceTier == RankTierForAllowances.RECRUIT

        fun calcGpf(basicAmt: Long): Long = if (isCadetOrRecruit) 0L else (basicAmt * (state.gpfPercent / 100.0)).roundToLong()

        val gpf1 = calcGpf(p1)
        val gross1 = p1 + oldAllowTotal
        val takeHome1 = gross1 - gpf1

        val gpf2 = calcGpf(p2)
        val gross2 = p2 + oldAllowTotal
        val takeHome2 = gross2 - gpf2

        val gpf3 = calcGpf(p3)
        val gross3 = p3 + oldAllowTotal
        val takeHome3 = gross3 - gpf3

        val allow2028Items = buildNewAllowancesItemList(state, pay, allow2028)
        val takeHome4 = allow2028.netMonthlyPay

        val p1PctLabel = if (group == ImplementationGroup.GROUP_A) "৪০%" else "৫০%"
        val p2PctLabel = if (group == ImplementationGroup.GROUP_A) "৭০%" else "৭৫%"

        val milestones = listOf(
            PhaseMilestoneDetail(
                id = "jul2026",
                title = "জুলাই ২০২৬",
                dateRange = "০১ জুলাই ২০২৬ – ৩১ ডিসেম্বর ২০২৬",
                badgeText = "বৃদ্ধি $p1PctLabel",
                isFinalPhase = false,
                basicPay = p1,
                basicDiffFromPrevious = (p1 - oldBasic).coerceAtLeast(0L),
                basicSubtitle = "পুরোনো মূল বেতন + বৃদ্ধি $p1PctLabel",
                netGainTitle = "প্রণোদনা বাদ দিয়ে জুন ২০২৬-এর তুলনায় আপনার বেতন বৃদ্ধি পেয়েছে",
                netGainAmount = (p1 - oldBasic - effectiveBisesh),
                netGainSubtitle = "বিশেষ প্রণোদনা ${BengaliFormat.formatBdt(effectiveBisesh.toDouble(), bn)} বাদ ($biseshRuleNote)",
                takeHomeTitle = "পূর্বের ভাতাসহ এখন মোট বেতন পাবেন (কর্তনের পর)",
                takeHomeAfterDeductions = takeHome1,
                itemBreakdown = listOf("ধাপ-১ মূল বেতন" to p1) + oldAllowItems,
                gpfDeduction = gpf1,
                grossMonthlyPay = gross1
            ),
            PhaseMilestoneDetail(
                id = "jan2027",
                title = "জানুয়ারি ২০২৭",
                dateRange = "০১ জানুয়ারি ২০২৭ – ৩০ জুন ২০২৭",
                badgeText = "বৃদ্ধি $p2PctLabel",
                isFinalPhase = false,
                basicPay = p2,
                basicDiffFromPrevious = (p2 - p1).coerceAtLeast(0L),
                basicSubtitle = "পুরোনো মূল বেতন + বৃদ্ধি $p2PctLabel",
                netGainTitle = "জুলাই ২০২৬-এর তুলনায় আপনার বেতন বৃদ্ধি পেয়েছে",
                netGainAmount = (p2 - p1).coerceAtLeast(0L),
                netGainSubtitle = null,
                takeHomeTitle = "পূর্বের ভাতাসহ এখন মোট বেতন পাবেন (কর্তনের পর)",
                takeHomeAfterDeductions = takeHome2,
                itemBreakdown = listOf("ধাপ-২ মূল বেতন" to p2) + oldAllowItems,
                gpfDeduction = gpf2,
                grossMonthlyPay = gross2
            ),
            PhaseMilestoneDetail(
                id = "jul2027",
                title = "জুলাই ২০২৭",
                dateRange = "০১ জুলাই ২০২৭ – ৩১ ডিসেম্বর ২০২৭",
                badgeText = "বৃদ্ধি ১০০%",
                isFinalPhase = false,
                basicPay = p3,
                basicDiffFromPrevious = (p3 - p2).coerceAtLeast(0L),
                basicSubtitle = if (p3 > newBasic2026) "১০০% নতুন মূল বেতন (ইনক্রিমেন্টসহ)" else "১০০% নতুন মূল বেতন",
                netGainTitle = "জানুয়ারি ২০২৭-এর তুলনায় আপনার বেতন বৃদ্ধি পেয়েছে",
                netGainAmount = (p3 - p2).coerceAtLeast(0L),
                netGainSubtitle = null,
                takeHomeTitle = "পূর্বের ভাতাসহ এখন মোট বেতন পাবেন (কর্তনের পর)",
                takeHomeAfterDeductions = takeHome3,
                itemBreakdown = listOf("ধাপ-৩ মূল বেতন" to p3) + oldAllowItems,
                gpfDeduction = gpf3,
                grossMonthlyPay = gross3
            ),
            PhaseMilestoneDetail(
                id = "jan2028",
                title = "জানুয়ারি ২০২৮",
                dateRange = "০১ জানুয়ারি ২০২৮ থেকে",
                badgeText = "নতুন ভাতা চালু",
                isFinalPhase = true,
                basicPay = allow2028.basicPay,
                basicDiffFromPrevious = 0L,
                basicSubtitle = "নতুন মূল বেতন",
                netGainTitle = "জুলাই ২০২৭-এর তুলনায় আপনার বেতন বৃদ্ধি পেয়েছে",
                netGainAmount = (takeHome4 - takeHome3),
                netGainSubtitle = null,
                takeHomeTitle = "সব ভাতাসহ মোট বেতন (কর্তনের পর)",
                takeHomeAfterDeductions = takeHome4,
                itemBreakdown = allow2028Items,
                gpfDeduction = allow2028.gpfDeduction,
                grossMonthlyPay = allow2028.grossMonthlyPay
            )
        )

        val steps = listOf(
            "১. ১ম ধাপ (০১ জুলাই – ৩১ ডিসে ২০২৬): ${BengaliFormat.formatBdt(p1.toDouble(), bn)} (পার্থক্য +${BengaliFormat.formatBdt(p1MonthlyDiff.toDouble(), bn)}/মাস)",
            "২. ২য় ধাপ (০১ জানু – ৩০ জুন ২০২৭): ${BengaliFormat.formatBdt(p2.toDouble(), bn)} (মোট পার্থক্য +${BengaliFormat.formatBdt(p2MonthlyDiff.toDouble(), bn)}/মাস)",
            "৩. ৩য় ধাপ (০১ জুলাই ২০২৭ ইনক্রিমেন্টসহ): ${BengaliFormat.formatBdt(p3.toDouble(), bn)} (মোট পার্থক্য +${BengaliFormat.formatBdt(p3MonthlyDiff.toDouble(), bn)}/মাস)",
            "৪. প্রাপ্য বকেয়া ($dateRangeSubtitle): ${BengaliFormat.formatBdt(grossArrears.toDouble(), bn)}",
            "৫. বিশেষ সুবিধা সমন্বয় (${BengaliFormat.formatBdt(effectiveBisesh.toDouble(), bn)}/মাস): − ${BengaliFormat.formatBdt(totalBiseshDeducted.toDouble(), bn)}",
            "৬. নিট বকেয়া: ${BengaliFormat.formatBdt(netArrears.toDouble(), bn)} (সম্ভাব্য জিপিএফ ${BengaliFormat.formatNumber(state.gpfPercent.toLong(), bn)}%: − ${BengaliFormat.formatBdt(probableGpfOnArrears.toDouble(), bn)})"
        )

        return PhasedArrearsResult(
            oldBasicPay = oldBasic,
            newBasicPay = newBasic2026,
            july2027BasicPay = july2027Basic,
            increase2026 = increase2026,
            group = group,
            phase1Pay = p1,
            phase2Pay = p2,
            phase3Pay = p3,
            dateRangeSubtitle = dateRangeSubtitle,
            arrearsDurationSummary = durationSummary,
            monthsArrearsCount = fractionMonths,
            monthlyBiseshSubidha = effectiveBisesh,
            biseshAutoRuleNote = biseshRuleNote,
            grossArrears = grossArrears,
            totalBiseshSubidhaDeducted = totalBiseshDeducted,
            netPayableArrears = netArrears,
            probableGpfOnArrears = probableGpfOnArrears,
            milestones = milestones,
            breakdownSteps = steps
        )
    }

    private fun buildNewAllowancesItemList(
        state: CalculatorUiState,
        pay: PayFixationResult,
        allow: AllowancesResult
    ): List<Pair<String, Long>> {
        val list = mutableListOf<Pair<String, Long>>()
        list.add("মূল বেতন (JSI 01/2026)" to allow.basicPay)
        if (allow.serviceOrdnancePay2028 > 0L) list.add("সার্ভিস ওয়ার্ডিন্যান্স বেতন (+১০%)" to allow.serviceOrdnancePay2028)
        if (pay.personalPay > 0L) list.add("ব্যক্তিগত বেতন (Personal Pay)" to pay.personalPay)
        if (allow.houseRentAmount > 0L) list.add("বাড়ি ভাড়া ভাতা (${(allow.houseRentRate * 100).toInt()}%)" to allow.houseRentAmount)
        if (allow.defenceServiceAllowance > 0L) list.add("প্রতিরক্ষা সার্ভিস ভাতা" to allow.defenceServiceAllowance)
        if (allow.medicalAllowance > 0L) list.add("চিকিৎসা ভাতা" to allow.medicalAllowance)
        if (allow.kitAllowance > 0L) list.add((if (pay.isOfficer) "কিট ভাতা" else "কিট রক্ষণাবেক্ষণ ভাতা") to allow.kitAllowance)
        if (allow.educationAllowance > 0L) list.add("শিক্ষা সহায়ক ভাতা" to allow.educationAllowance)
        if (allow.disabledChildAllowance > 0L) list.add("নিবন্ধিত প্রতিবন্ধী সন্তান ভাতা" to allow.disabledChildAllowance)
        if (allow.tiffinAllowance > 0L) list.add("টিফিন ভাতা" to allow.tiffinAllowance)
        if (allow.transportAllowance > 0L) list.add("যাতায়াত ভাতা" to allow.transportAllowance)
        if (allow.mobileAllowance > 0L) list.add("মোবাইল ভাতা" to allow.mobileAllowance)
        if (allow.hillHaorAllowance > 0L) list.add("পাহাড়ি / হাওড় ভাতা" to allow.hillHaorAllowance)
        if (allow.proficiencyPay > 0L) list.add("দক্ষতা বেতন" to allow.proficiencyPay)
        if (allow.appointmentPay > 0L) list.add("নিযুক্তি বেতন" to allow.appointmentPay)
        if (allow.goodConductPay > 0L) list.add("সদাচরণ ব্যাজ বেতন" to allow.goodConductPay)
        if (allow.specialSkillPay > 0L) list.add("বিশেষ দক্ষতা বেতন" to allow.specialSkillPay)
        if (allow.otherNewAllowance > 0L) list.add("অন্যান্য নতুন ভাতা" to allow.otherNewAllowance)
        return list
    }

    private fun computeAllowances(
        state: CalculatorUiState,
        pay: PayFixationResult,
        basicFor2028: Long,
        bn: Boolean
    ): AllowancesResult {
        val basic = basicFor2028
        val tier = pay.allowanceTier
        val steps = mutableListOf<String>()

        val oldOrd = if (pay.isSergeantOrBelow && tier != RankTierForAllowances.CADET && tier != RankTierForAllowances.RECRUIT) {
            BengaliFormat.parseSafeNumber(state.oldServiceOrdnanceInput)
        } else 0L
        val serviceOrdnance2028 = if (oldOrd > 0) (oldOrd * 1.10).roundToLong() else 0L
        val payBasisForHouseRent = basic + serviceOrdnance2028 + pay.personalPay

        val houseRentRate = JsiPayData.getHouseRentRate(
            isOfficer = pay.isOfficer,
            tier = tier,
            city = state.cityCategory,
            accommodation = state.accommodationType
        )
        val houseRentAmount = (payBasisForHouseRent * houseRentRate).roundToLong()

        val isReligious = state.category == PersonnelCategory.JCO_OR && state.serviceBranch == ServiceBranch.RELIGIOUS
        val dsa = JsiPayData.getDefenceServiceAllowance(
            tier = tier,
            serviceBracket = state.serviceBracket,
            isReligious = isReligious
        )

        val medical = if (pay.isOfficer || tier == RankTierForAllowances.CADET) {
            0L
        } else {
            state.activeMedicalAgeBracket.amount
        }

        val education = if (tier == RankTierForAllowances.CADET) 0L else (state.childrenCount.coerceIn(0, 2) * 500L)
        val disabledChildAllowance = if (tier == RankTierForAllowances.CADET) 0L else (state.disabledChildrenCount.coerceIn(0, 2) * 3000L)

        val tiffin = if (pay.isCplOrBelow && !state.receiveFreeTiffin) 500L else 0L

        val transport = if (pay.isCplOrBelow &&
            (state.cityCategory == CityCategory.DHAKA || state.cityCategory == CityCategory.OTHER_CITY_CORP)
        ) 600L else 0L

        val mobile = when {
            tier in setOf(
                RankTierForAllowances.GEN, RankTierForAllowances.LT_GEN, RankTierForAllowances.MAJ_GEN,
                RankTierForAllowances.BRIG_GEN, RankTierForAllowances.COL, RankTierForAllowances.LT_COL,
                RankTierForAllowances.MAJ
            ) -> 500L
            tier == RankTierForAllowances.CADET -> 0L
            else -> 150L
        }

        val kit = when {
            pay.isAfns && pay.isOfficer -> 1040L
            pay.isOfficer -> 1320L
            tier == RankTierForAllowances.CADET || tier == RankTierForAllowances.RECRUIT -> 0L
            pay.isSergeantOrBelow -> 143L
            else -> 157L
        }

        val hillHaor = when (state.hillHaorCategory) {
            HillHaorCategory.NONE -> 0L
            HillHaorCategory.SADAR -> min((basic * 0.20).roundToLong(), 5000L)
            HillHaorCategory.OTHER_UPAZILA -> min((basic * 0.20).roundToLong(), 5500L)
        }

        val proficiencyPay = if (!pay.isOfficer && tier != RankTierForAllowances.CADET && tier != RankTierForAllowances.RECRUIT) {
            JsiPayData.proficiencyPayOptions.find { it.id == state.proficiencyPayId }?.amount ?: 0L
        } else 0L

        val appointmentPay = if (!pay.isOfficer && tier != RankTierForAllowances.CADET && tier != RankTierForAllowances.RECRUIT) {
            JsiPayData.appointmentPayOptions.find { it.id == state.appointmentPayId }?.amount ?: 0L
        } else 0L

        val goodConductPay = if (pay.isSergeantOrBelow && tier != RankTierForAllowances.CADET && tier != RankTierForAllowances.RECRUIT) {
            state.goodConductBadges.coerceIn(0, 3) * 35L
        } else 0L

        val specialSkillPay = if (tier != RankTierForAllowances.CADET && tier != RankTierForAllowances.RECRUIT) {
            state.selectedSpecialSkills.sumOf { JsiPayData.getSpecialSkillAmount(it, pay.isOfficer, tier) }
        } else 0L

        val otherNewAllowance = BengaliFormat.parseSafeNumber(state.otherNewAllowanceInput).coerceAtLeast(0L)

        val gross = basic + serviceOrdnance2028 + pay.personalPay + houseRentAmount + dsa +
            medical + education + disabledChildAllowance + tiffin + transport + mobile + kit +
            hillHaor + proficiencyPay + appointmentPay + goodConductPay + specialSkillPay + otherNewAllowance

        val gpf = if (tier == RankTierForAllowances.CADET || tier == RankTierForAllowances.RECRUIT) {
            0L
        } else {
            (basic * (state.gpfPercent / 100.0)).roundToLong()
        }
        val net = gross - gpf
        val noboborsho = (basic * 0.15).roundToLong()

        steps.add("১. প্রযোজ্যতা: জেএসআই ০১/২০২৬ অনুযায়ী সংশোধিত ভাতাসমূহ ০১ জানুয়ারি ২০২৮ হইতে কার্যকর।")
        steps.add("২. বাড়ি ভাড়া ভাতা: ভিত্তি বেতন (${BengaliFormat.formatBdt(payBasisForHouseRent.toDouble(), bn)}) × ${BengaliFormat.formatNumber((houseRentRate * 100).toLong(), bn)}% = ${BengaliFormat.formatBdt(houseRentAmount.toDouble(), bn)}।")
        steps.add("৩. প্রতিরক্ষা সার্ভিস ভাতা: ${BengaliFormat.formatBdt(dsa.toDouble(), bn)}।")
        steps.add("৪. চিকিৎসা ও কিট ভাতা: চিকিৎসা ${BengaliFormat.formatBdt(medical.toDouble(), bn)} | কিট/রক্ষণাবেক্ষণ ${BengaliFormat.formatBdt(kit.toDouble(), bn)}।")
        steps.add("৫. মোট বেতন-ভাতা (Gross) = ${BengaliFormat.formatBdt(gross.toDouble(), bn)} | জিপিএফ (${BengaliFormat.formatNumber(state.gpfPercent.toLong(), bn)}%) = −${BengaliFormat.formatBdt(gpf.toDouble(), bn)} | নিট প্রদেয় = ${BengaliFormat.formatBdt(net.toDouble(), bn)}।")

        return AllowancesResult(
            basicPay = basic,
            serviceOrdnancePay2028 = serviceOrdnance2028,
            houseRentRate = houseRentRate,
            houseRentAmount = houseRentAmount,
            defenceServiceAllowance = dsa,
            medicalAllowance = medical,
            educationAllowance = education,
            disabledChildAllowance = disabledChildAllowance,
            tiffinAllowance = tiffin,
            transportAllowance = transport,
            mobileAllowance = mobile,
            kitAllowance = kit,
            hillHaorAllowance = hillHaor,
            proficiencyPay = proficiencyPay,
            appointmentPay = appointmentPay,
            goodConductPay = goodConductPay,
            specialSkillPay = specialSkillPay,
            otherNewAllowance = otherNewAllowance,
            grossMonthlyPay = gross,
            gpfDeduction = gpf,
            netMonthlyPay = net,
            noboborshoAnnual = noboborsho,
            breakdownSteps = steps
        )
    }

    private fun computeExistingPension(state: CalculatorUiState, bn: Boolean): ExistingPensionResult {
        val oldNet = BengaliFormat.parseSafeNumber(state.oldNetPensionInput).coerceAtLeast(0L)
        val steps = mutableListOf<String>()

        val slabLabel: String
        val ratePercent: Int
        val minCap: Long
        val maxCap: Long

        when {
            oldNet <= 9000L -> {
                slabLabel = "সর্বনিম্ন – ৯,০০০ টাকা"
                ratePercent = 100; minCap = 10000L; maxCap = 18000L
            }
            oldNet <= 20000L -> {
                slabLabel = "৯,০০১ – ২০,০০০ টাকা"
                ratePercent = 75; minCap = 18001L; maxCap = 35000L
            }
            oldNet <= 30000L -> {
                slabLabel = "২০,০০১ – ৩০,০০০ টাকা"
                ratePercent = 65; minCap = 35001L; maxCap = 49000L
            }
            oldNet <= 40000L -> {
                slabLabel = "৩০,০০১ – ৪০,০০০ টাকা"
                ratePercent = 60; minCap = 49001L; maxCap = 62000L
            }
            else -> {
                slabLabel = "৪০,০০১ টাকা ও তদূর্ধ্ব"
                ratePercent = 55; minCap = 62001L; maxCap = 70200L
            }
        }

        val isAboveCapProtected = oldNet > 70200L
        val newFullNet: Long
        if (isAboveCapProtected) {
            newFullNet = oldNet
            steps.add("১. অনুচ্ছেদ ২৭ এর শর্ত অনুযায়ী: ৩০ জুন ২০২৬-এর নিট পেনশন ৭০,২০০ টাকার অধিক হওয়ায় ০১ জুলাই ২০২৭ পর্যন্ত বিদ্যমান পেনশন (${BengaliFormat.formatBdt(oldNet.toDouble(), bn)}) প্রাপ্য হইবেন।")
        } else {
            val rawIncreased = (oldNet * (1.0 + ratePercent / 100.0)).roundToLong()
            newFullNet = rawIncreased.coerceIn(minCap, maxCap)
            steps.add("১. ৩০ জুন ২০২৬ এর নিট পেনশন = ${BengaliFormat.formatBdt(oldNet.toDouble(), bn)} (স্ল্যাব: $slabLabel, বৃদ্ধির হার: ${BengaliFormat.formatNumber(ratePercent.toLong(), bn)}%)।")
            steps.add("২. গাণিতিক বৃদ্ধি = ${BengaliFormat.formatBdt(rawIncreased.toDouble(), bn)}। স্ল্যাব সীমা অনুযায়ী চূড়ান্ত নিট পেনশন = ${BengaliFormat.formatBdt(newFullNet.toDouble(), bn)}।")
        }

        val group = if (oldNet >= 20001L) ImplementationGroup.GROUP_A else ImplementationGroup.GROUP_B
        val increase = (newFullNet - oldNet).coerceAtLeast(0L)
        val p1 = oldNet + (increase * group.phase1Rate).roundToLong()
        val p2 = oldNet + (increase * group.phase2Rate).roundToLong()
        val july2027Incremented = (newFullNet * 1.05).roundToLong()

        val ration = (state.adultRationMembers * 1000L) + (state.childRationMembers * 500L)
        val medical = state.pensionerAgeBracket.amount
        val totalMonthlyFull = newFullNet + ration + medical
        val noboborsho = (newFullNet * 0.15).roundToLong()

        steps.add("৩. ধাপে ধাপে প্রদান: ${group.bnLabel}")
        steps.add("   • ০১ জুলাই – ৩১ ডিসে ২০২৬: ${BengaliFormat.formatBdt(p1.toDouble(), bn)}")
        steps.add("   • ০১ জানু – ৩০ জুন ২০২৭: ${BengaliFormat.formatBdt(p2.toDouble(), bn)}")
        steps.add("   • ০১ জুলাই ২০২৭ (+৫% বৃদ্ধি): ${BengaliFormat.formatBdt(july2027Incremented.toDouble(), bn)}")
        steps.add("৪. রেশন ভাতা: ${BengaliFormat.formatBdt(ration.toDouble(), bn)} | চিকিৎসা ভাতা (জানু ২০২৮): ${BengaliFormat.formatBdt(medical.toDouble(), bn)}।")

        return ExistingPensionResult(
            oldNetPension = oldNet,
            slabLabel = slabLabel,
            increaseRatePercent = ratePercent,
            newFullNetPension = newFullNet,
            isAboveCapProtected = isAboveCapProtected,
            group = group,
            phase1Pension = p1,
            phase2Pension = p2,
            phase3Pension = newFullNet,
            july2027IncrementedPension = july2027Incremented,
            rationAllowance = ration,
            medicalAllowance = medical,
            totalMonthlyWithAllowancesFull = totalMonthlyFull,
            noboborshoAnnual = noboborsho,
            breakdownSteps = steps
        )
    }

    private fun computeRetiringPension(
        state: CalculatorUiState,
        pay: PayFixationResult,
        bn: Boolean
    ): RetiringPensionResult {
        val lastPay = BengaliFormat.parseSafeNumber(state.retiringLastPayInput).coerceAtLeast(0L)
        val qualYears = BengaliFormat.parseSafeDouble(state.retiringQualifyingYearsInput).coerceAtLeast(0.0)
        val prescribedYears = BengaliFormat.parseSafeDouble(state.rankPrescribedYearsInput).coerceAtLeast(1.0)
        val steps = mutableListOf<String>()

        val grossPension: Long
        val capNote: String

        if (qualYears < 10.0) {
            grossPension = 0L
            capNote = "ন্যূনতম ১০ বৎসর অবসরভাতাযোগ্য চাকরি পূর্ণ হয়নি।"
            steps.add("১. অনুচ্ছেদ ৫ ও ১৫ অনুযায়ী কর্ম উপাদান প্রাপ্য হইতে ন্যূনতম ১০ বৎসর যোগ্য চাকরি প্রয়োজন।")
        } else if (state.retiringType == RetiringType.OFFICER) {
            val raw = (lastPay.toDouble() * 9.0 * qualYears) / (10.0 * prescribedYears)
            val ninetyPercentCap = lastPay.toDouble() * 0.90
            val maxStatutoryCap = 154000.0
            val capped = min(raw, min(ninetyPercentCap, maxStatutoryCap))
            grossPension = capped.roundToLong()
            capNote = when {
                raw > maxStatutoryCap && maxStatutoryCap <= ninetyPercentCap -> "সর্বোচ্চ সীমা ১,৫৪,০০০ টাকায় সীমাবদ্ধ"
                raw > ninetyPercentCap -> "শেষ গৃহীত বেতনের ৯০% সীমায় সীমাবদ্ধ"
                else -> "সূত্র অনুযায়ী পূর্ণ অর্জিত গ্রস পেনশন"
            }
            steps.add("১. অফিসার কর্ম উপাদান সূত্র (অনুচ্ছেদ ৫): (৯ × শেষ বেতন × যোগ্য চাকরি) ÷ (১০ × নির্ধারিত চাকরি)")
            steps.add("২. হিসাব: (৯ × ${BengaliFormat.formatNumber(lastPay, bn)} × ${BengaliFormat.formatDecimal(qualYears, 1, bn)}) ÷ (১০ × ${BengaliFormat.formatDecimal(prescribedYears, 1, bn)}) = ${BengaliFormat.formatBdt(raw, bn)}।")
            steps.add("৩. নির্ধারিত অর্জিত গ্রস পেনশন = ${BengaliFormat.formatBdt(grossPension.toDouble(), bn)} ($capNote)।")
        } else {
            val basis = lastPay.toDouble() + 608.0
            val raw = (basis * qualYears) / 31.0
            val ninetyPercentCap = basis * 0.90
            val capped = min(raw, ninetyPercentCap)
            grossPension = capped.roundToLong()
            capNote = if (raw > ninetyPercentCap) "(শেষ গৃহীত বেতন + ৬০৮) এর ৯০% সীমায় সীমাবদ্ধ" else "সূত্র অনুযায়ী পূর্ণ অর্জিত গ্রস পেনশন"
            steps.add("১. জেসিও/ওআর কর্ম উপাদান সূত্র (অনুচ্ছেদ ১৫): ((শেষ বেতন + ৬০৮ টাকা) × যোগ্য চাকরি) ÷ ৩১")
            steps.add("২. হিসাব: ((${BengaliFormat.formatNumber(lastPay, bn)} + ৬০৮) × ${BengaliFormat.formatDecimal(qualYears, 1, bn)}) ÷ ৩১ = ${BengaliFormat.formatBdt(raw, bn)}।")
            steps.add("৩. নির্ধারিত অর্জিত গ্রস পেনশন = ${BengaliFormat.formatBdt(grossPension.toDouble(), bn)} ($capNote)।")
        }

        val surrenderPct = state.commutationSurrenderPercent.coerceIn(0, 100)
        val commutedMonthly = (grossPension * (surrenderPct / 100.0)).roundToLong()
        val netMonthly = (grossPension - commutedMonthly).coerceAtLeast(0L)
        val age = state.retiringAgeNextBirthday.coerceIn(20, 80)
        val multiplier = JsiPayData.commutationTable[age] ?: 17.553
        val lumpSum = (commutedMonthly.toDouble() * 12.0 * multiplier).roundToLong()

        val ration = (state.adultRationMembers * 1000L) + (state.childRationMembers * 500L)
        val medical = if (state.retiringType == RetiringType.OFFICER) 0L else state.pensionerAgeBracket.amount

        val projectedRankTitle = state.projectedPromotions.lastOrNull()?.rankTitle ?: pay.rankTitle

        steps.add("৪. কম্যুটেশন (অনুচ্ছেদ ২৮): পরবর্তী জন্মদিনে বয়স ${BengaliFormat.formatNumber(age.toLong(), bn)} বৎসরের গুণক = ${BengaliFormat.formatDecimal(multiplier, 3, bn)}।")
        steps.add("৫. সমর্পিত অংশ (${BengaliFormat.formatNumber(surrenderPct.toLong(), bn)}%): ${BengaliFormat.formatBdt(commutedMonthly.toDouble(), bn)} | মাসিক নিট পেনশন = ${BengaliFormat.formatBdt(netMonthly.toDouble(), bn)}।")
        steps.add("৬. এককালীন কম্যুটেড গ্র্যাচুইটি = ${BengaliFormat.formatBdt(lumpSum.toDouble(), bn)}।")

        return RetiringPensionResult(
            grossPension = grossPension,
            capNote = capNote,
            surrenderPercent = surrenderPct,
            commutedMonthlyPortion = commutedMonthly,
            netMonthlyPension = netMonthly,
            commutationMultiplier = multiplier,
            lumpSumCommutedValue = lumpSum,
            rationAllowance = ration,
            medicalAllowance = medical,
            projectedLastRankTitle = projectedRankTitle,
            projectedQualifyingYears = qualYears,
            projectedTotalMonthlyWithAllowances = netMonthly + ration + medical,
            breakdownSteps = steps
        )
    }

    fun buildFullSummaryText(): String {
        val state = _uiState.value
        val bn = state.useBengaliDigits
        val pay = state.payFixationResult
        val phased = state.phasedArrearsResult
        val allow = state.allowancesResult
        val sb = StringBuilder()

        sb.appendLine("==========================================")
        sb.appendLine("জেএসআই ০১/২০২৬ (JSI 01/2026) বেতন ও পেনশন সামারি")
        sb.appendLine("বাংলাদেশ প্রতিরক্ষা মন্ত্রণালয় | কার্যকর: ০১ জুলাই ২০২৬")
        sb.appendLine("==========================================")
        if (state.userName.isNotBlank() || state.serviceNo.isNotBlank() || state.unitName.isNotBlank()) {
            sb.appendLine("নাম: ${state.userName.ifBlank { "প্রযোজ্য নয়" }}")
            if (state.serviceNo.isNotBlank()) sb.appendLine("সার্ভিস/সেনা নং: ${state.serviceNo}")
            if (state.unitName.isNotBlank()) sb.appendLine("ইউনিট: ${state.unitName}")
        }
        sb.appendLine("বাহিনী ও শ্রেণি: ${state.serviceBranch.bnLabel} • ${state.category.bnLabel}")
        sb.appendLine("জন্মতারিখ: ${state.dobDay}-${state.dobMonth}-${state.dobYear} | যোগদান: ${state.joinDay}-${state.joinMonth}-${state.joinYear}")
        sb.appendLine()

        if (pay != null) {
            sb.appendLine("[১. মূল বেতন নির্ধারণ]")
            sb.appendLine("পদবি: ${pay.rankTitle}")
            sb.appendLine("৩০ জুন ২০২৬-এ মূল বেতন: ${BengaliFormat.formatBdt(pay.oldBasicPay.toDouble(), bn)}")
            sb.appendLine("০১ জুলাই ২০২৬ নতুন মূল বেতন (ইনক্রিমেন্টসহ): ${BengaliFormat.formatBdt(pay.newBasicPay.toDouble(), bn)}")
            sb.appendLine("০১ জুলাই ২০২৭ মূল বেতন (ইনক্রিমেন্টসহ): ${BengaliFormat.formatBdt(pay.july2027BasicPay.toDouble(), bn)}")
            sb.appendLine()
        }

        if (phased != null) {
            sb.appendLine("[২. ধাপে ধাপে বেতন ও বকেয়া]")
            sb.appendLine("১ম ধাপ (জুলাই–ডিসে ২০২৬): ${BengaliFormat.formatBdt(phased.phase1Pay.toDouble(), bn)}")
            sb.appendLine("২য় ধাপ (জানু–জুন ২০২৭): ${BengaliFormat.formatBdt(phased.phase2Pay.toDouble(), bn)}")
            sb.appendLine("৩য় ধাপ (জুলাই ২০২৭+): ${BengaliFormat.formatBdt(phased.phase3Pay.toDouble(), bn)}")
            sb.appendLine("মোট প্রাপ্য বকেয়া (${phased.dateRangeSubtitle}): ${BengaliFormat.formatBdt(phased.grossArrears.toDouble(), bn)}")
            sb.appendLine("বাদ: বিশেষ সুবিধা সমন্বয়: − ${BengaliFormat.formatBdt(phased.totalBiseshSubidhaDeducted.toDouble(), bn)}")
            sb.appendLine("নিট প্রাপ্য বকেয়া: ${BengaliFormat.formatBdt(phased.netPayableArrears.toDouble(), bn)}")
            sb.appendLine("সম্ভাব্য জিপিএফ কর্তন: − ${BengaliFormat.formatBdt(phased.probableGpfOnArrears.toDouble(), bn)}")
            sb.appendLine()
        }

        if (allow != null) {
            sb.appendLine("[৩. ভাতাদি ও নিট বেতন (০১ জানু ২০২৮ হইতে)]")
            sb.appendLine("মোট বেতন-ভাতা (Gross): ${BengaliFormat.formatBdt(allow.grossMonthlyPay.toDouble(), bn)}")
            sb.appendLine("জিপিএফ কর্তন: − ${BengaliFormat.formatBdt(allow.gpfDeduction.toDouble(), bn)}")
            sb.appendLine("নিট মাসিক বেতন (২০২৮): ${BengaliFormat.formatBdt(allow.netMonthlyPay.toDouble(), bn)}")
            sb.appendLine("বাংলা নববর্ষ ভাতা (১৫%): ${BengaliFormat.formatBdt(allow.noboborshoAnnual.toDouble(), bn)}")
            sb.appendLine()
        }

        if (state.pensionSubTab == PensionSubTab.EXISTING && state.existingPensionResult != null) {
            val ep = state.existingPensionResult
            sb.appendLine("[৪. পেনশন (পূর্বের অবসরভোগী)]")
            sb.appendLine("পূর্বের নিট পেনশন: ${BengaliFormat.formatBdt(ep.oldNetPension.toDouble(), bn)}")
            sb.appendLine("পূর্ণ বর্ধিত নিট পেনশন: ${BengaliFormat.formatBdt(ep.newFullNetPension.toDouble(), bn)}")
            sb.appendLine("রেশন ও চিকিৎসা সহ মোট (পূর্ণ ধাপে): ${BengaliFormat.formatBdt(ep.totalMonthlyWithAllowancesFull.toDouble(), bn)}")
        } else if (state.retiringPensionResult != null) {
            val rp = state.retiringPensionResult
            sb.appendLine("[৪. পেনশন ও কম্যুটেশন (অবসরগামী)]")
            sb.appendLine("অর্জিত গ্রস পেনশন: ${BengaliFormat.formatBdt(rp.grossPension.toDouble(), bn)}")
            sb.appendLine("মাসিক নিট পেনশন: ${BengaliFormat.formatBdt(rp.netMonthlyPension.toDouble(), bn)}")
            sb.appendLine("এককালীন কম্যুটেড গ্র্যাচুইটি: ${BengaliFormat.formatBdt(rp.lumpSumCommutedValue.toDouble(), bn)}")
        }

        sb.appendLine()
        sb.appendLine("ডেভেলপার: Àbdüllāh Aĺ Hỗŝŝâîň | ইমেইল: mhossenali740@gmail.com")
        sb.appendLine("বিঃদ্রঃ এটি জেএসআই ০১/২০২৬ গেজেট (১৫ নভে ২০২৬) অবলম্বনে একটি রেফারেন্স ক্যালকুলেটর। চূড়ান্ত বেতন ও পেনশন নির্ধারণ iBAS++ / SFC / FC / SFPO অফিস কর্তৃক সম্পন্ন হইবে।")
        return sb.toString()
    }

    fun getSuggestedJsonFilename(): String {
        val state = _uiState.value
        val slugSource = state.serviceNo.ifBlank { state.userName }
        val slug = BengaliFormat.toEnglishDigits(slugSource)
            .trim()
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
        val dateStr = java.time.LocalDate.now().toString()
        return if (slug.isNotEmpty()) "jsi-01-2026-$slug-$dateStr.json" else "jsi-01-2026-backup-$dateStr.json"
    }

    fun buildFullExportJsonString(): String {
        val state = _uiState.value
        val pay = state.payFixationResult
        val phased = state.phasedArrearsResult
        val allow = state.allowancesResult
        val existingPen = state.existingPensionResult
        val retiringPen = state.retiringPensionResult

        val root = org.json.JSONObject().apply {
            put("schemaVersion", "jsi-01-2026-v1")
            put("exportedAt", java.time.Instant.now().toString())
            put("appTitle", "জেএসআই ০১/২০২৬ (JSI 01/2026) সশস্ত্র বাহিনী বেতন ও পেনশন ক্যালকুলেটর")
            put("gazetteReference", "Bangladesh Ministry of Defence JSI 01/2026 (Effective 01 July 2026)")

            put("member", org.json.JSONObject().apply {
                put("userName", state.userName.ifBlank { org.json.JSONObject.NULL })
                put("serviceNo", state.serviceNo.ifBlank { org.json.JSONObject.NULL })
                put("unitName", state.unitName.ifBlank { org.json.JSONObject.NULL })
                put("serviceBranch", state.serviceBranch.name)
                put("serviceBranchLabel", state.serviceBranch.bnLabel)
                put("category", state.category.name)
                put("categoryLabel", state.category.bnLabel)
                put("computedAgeOnEffectiveDate", state.computedAgeOnEffectiveDate)
                put("computedServiceYears", state.computedServiceYears)
            })

            put("inputs", org.json.JSONObject().apply {
                put("isPensionerRole", state.isPensionerRole)
                put("userName", state.userName)
                put("serviceNo", state.serviceNo)
                put("unitName", state.unitName)
                put("dobDay", state.dobDay)
                put("dobMonth", state.dobMonth)
                put("dobYear", state.dobYear)
                put("joinDay", state.joinDay)
                put("joinMonth", state.joinMonth)
                put("joinYear", state.joinYear)
                put("category", state.category.name)
                put("serviceBranch", state.serviceBranch.name)
                put("officerAppendix", state.officerAppendix.name)
                put("selectedOfficerRankId", state.selectedOfficerRankId)
                put("commissionedYear", state.commissionedYear)
                put("officerOldBasicInput", state.officerOldBasicInput)
                put("selectedCadetId", state.selectedCadetId)
                put("selectedNonOfficerRankId", state.selectedNonOfficerRankId)
                put("oldBasicPayInput", state.oldBasicPayInput)
                put("oldScaleStartOverride", state.oldScaleStartOverride)
                put("applyJuly2026Increment", state.applyJuly2026Increment)
                put("isMarried", state.isMarried)
                put("childrenCount", state.childrenCount)
                put("disabledChildrenCount", state.disabledChildrenCount)
                put("proficiencyPayId", state.proficiencyPayId)
                put("appointmentPayId", state.appointmentPayId)
                put("goodConductBadges", state.goodConductBadges)
                put("selectedSpecialSkills", org.json.JSONArray(state.selectedSpecialSkills.map { it.name }))
                put("otherNewAllowanceInput", state.otherNewAllowanceInput)
                put("receiveMonthIndex", state.receiveMonthIndex)
                put("monthlyBiseshSubidhaInput", state.monthlyBiseshSubidhaInput)
                put("cityCategory", state.cityCategory.name)
                put("accommodationType", state.accommodationType.name)
                put("serviceBracketIndex", state.serviceBracket.ordinal)
                put("activeMedicalAgeBracketId", state.activeMedicalAgeBracket.name)
                put("receiveFreeTiffin", state.receiveFreeTiffin)
                put("hillHaorCategoryId", state.hillHaorCategory.name)
                put("oldServiceOrdnanceInput", state.oldServiceOrdnanceInput)
                put("gpfPercent", state.gpfPercent)
                put("pensionSubTab", state.pensionSubTab.name)
                put("oldNetPensionInput", state.oldNetPensionInput)
                put("pensionerAgeBracketId", state.pensionerAgeBracket.name)
                put("adultRationMembers", state.adultRationMembers)
                put("childRationMembers", state.childRationMembers)
                put("retiringType", state.retiringType.name)
                put("retiringLastPayInput", state.retiringLastPayInput)
                put("retiringQualifyingYearsInput", state.retiringQualifyingYearsInput)
                put("rankPrescribedYearsInput", state.rankPrescribedYearsInput)
                put("retiringAgeNextBirthday", state.retiringAgeNextBirthday)
                put("commutationSurrenderPercent", state.commutationSurrenderPercent)
                put("retireDay", state.retireDay)
                put("retireMonth", state.retireMonth)
                put("retireYear", state.retireYear)
                put("projectedPromotions", org.json.JSONArray().apply {
                    state.projectedPromotions.forEach { p ->
                        put(org.json.JSONObject().apply {
                            put("id", p.id)
                            put("rankId", p.rankId)
                            put("rankTitle", p.rankTitle)
                            put("day", p.day)
                            put("month", p.month)
                            put("year", p.year)
                        })
                    }
                })
            })

            put("calculations", org.json.JSONObject().apply {
                if (pay != null) {
                    put("payFixation", org.json.JSONObject().apply {
                        put("rankTitle", pay.rankTitle)
                        put("appendixShortLabel", pay.appendixShortLabel)
                        put("oldBasicPay", pay.oldBasicPay)
                        put("fixedBeforeIncrementPay", pay.fixedBeforeIncrementPay)
                        put("july2026IncrementAmount", pay.july2026IncrementAmount)
                        put("newBasicPay", pay.newBasicPay)
                        put("july2027BasicPay", pay.july2027BasicPay)
                        put("personalPay", pay.personalPay)
                        put("group", pay.implementationGroup.name)
                    })
                }
                if (phased != null) {
                    put("phasedArrears", org.json.JSONObject().apply {
                        put("phase1Pay", phased.phase1Pay)
                        put("phase2Pay", phased.phase2Pay)
                        put("phase3Pay", phased.phase3Pay)
                        put("dateRangeSubtitle", phased.dateRangeSubtitle)
                        put("monthsArrearsCount", phased.monthsArrearsCount)
                        put("monthlyBiseshSubidha", phased.monthlyBiseshSubidha)
                        put("grossArrears", phased.grossArrears)
                        put("totalBiseshSubidhaDeducted", phased.totalBiseshSubidhaDeducted)
                        put("netPayableArrears", phased.netPayableArrears)
                        put("probableGpfOnArrears", phased.probableGpfOnArrears)
                    })
                }
                if (allow != null) {
                    put("allowances2028", org.json.JSONObject().apply {
                        put("basicPay", allow.basicPay)
                        put("houseRentAmount", allow.houseRentAmount)
                        put("defenceServiceAllowance", allow.defenceServiceAllowance)
                        put("medicalAllowance", allow.medicalAllowance)
                        put("educationAllowance", allow.educationAllowance)
                        put("disabledChildAllowance", allow.disabledChildAllowance)
                        put("tiffinAllowance", allow.tiffinAllowance)
                        put("transportAllowance", allow.transportAllowance)
                        put("mobileAllowance", allow.mobileAllowance)
                        put("kitAllowance", allow.kitAllowance)
                        put("hillHaorAllowance", allow.hillHaorAllowance)
                        put("grossMonthlyPay", allow.grossMonthlyPay)
                        put("gpfDeduction", allow.gpfDeduction)
                        put("netMonthlyPay", allow.netMonthlyPay)
                        put("noboborshoAnnual", allow.noboborshoAnnual)
                    })
                }
                if (existingPen != null) {
                    put("existingPension", org.json.JSONObject().apply {
                        put("oldNetPension", existingPen.oldNetPension)
                        put("slabLabel", existingPen.slabLabel)
                        put("increaseRatePercent", existingPen.increaseRatePercent)
                        put("phase1Pension", existingPen.phase1Pension)
                        put("phase2Pension", existingPen.phase2Pension)
                        put("newFullNetPension", existingPen.newFullNetPension)
                        put("july2027IncrementedPension", existingPen.july2027IncrementedPension)
                        put("rationAllowance", existingPen.rationAllowance)
                        put("medicalAllowance", existingPen.medicalAllowance)
                        put("totalMonthlyWithAllowancesFull", existingPen.totalMonthlyWithAllowancesFull)
                    })
                }
                if (retiringPen != null) {
                    put("retiringPension", org.json.JSONObject().apply {
                        put("grossPension", retiringPen.grossPension)
                        put("surrenderPercent", retiringPen.surrenderPercent)
                        put("commutedMonthlyPortion", retiringPen.commutedMonthlyPortion)
                        put("netMonthlyPension", retiringPen.netMonthlyPension)
                        put("commutationMultiplier", retiringPen.commutationMultiplier)
                        put("lumpSumCommutedValue", retiringPen.lumpSumCommutedValue)
                        put("rationAllowance", retiringPen.rationAllowance)
                        put("medicalAllowance", retiringPen.medicalAllowance)
                        put("projectedTotalMonthlyWithAllowances", retiringPen.projectedTotalMonthlyWithAllowances)
                    })
                }
            })
        }
        return root.toString(2)
    }

    fun importFromJson(rawJson: String): Boolean {
        return try {
            val parsed = org.json.JSONObject(rawJson)
            val inputs = parsed.optJSONObject("inputs")
                ?: parsed.optJSONObject("stateSnapshot")
                ?: parsed

            if (!inputs.has("serviceBranch") && !inputs.has("category")) {
                return false
            }

            hasUserInteracted = true
            val current = _uiState.value

            val skillsSet = mutableSetOf<SpecialSkillPay>()
            val skillsArr = inputs.optJSONArray("selectedSpecialSkills")
            if (skillsArr != null) {
                for (i in 0 until skillsArr.length()) {
                    runCatching { SpecialSkillPay.valueOf(skillsArr.optString(i)) }.getOrNull()?.let { skillsSet.add(it) }
                }
            }

            val promos = mutableListOf<ProjectedPromotion>()
            val promosArr = inputs.optJSONArray("projectedPromotions")
            if (promosArr != null) {
                for (i in 0 until promosArr.length()) {
                    val obj = promosArr.optJSONObject(i) ?: continue
                    val rankId = obj.optString("rankId")
                    if (rankId.isNotBlank()) {
                        promos.add(
                            ProjectedPromotion(
                                id = obj.optString("id").ifBlank { UUID.randomUUID().toString() },
                                rankId = rankId,
                                rankTitle = obj.optString("rankTitle").ifBlank { rankId },
                                day = obj.optString("day", "01"),
                                month = obj.optString("month", "01"),
                                year = obj.optString("year", "2030")
                            )
                        )
                    }
                }
            }

            val sbIdx = inputs.optInt("serviceBracketIndex", current.serviceBracket.ordinal)
            val bracket = ServiceBracket.entries.getOrElse(sbIdx) { current.serviceBracket }

            _uiState.update { s ->
                s.copy(
                    isPensionerRole = inputs.optBoolean("isPensionerRole", s.isPensionerRole),
                    userName = inputs.optString("userName", s.userName),
                    serviceNo = inputs.optString("serviceNo", s.serviceNo),
                    unitName = inputs.optString("unitName", s.unitName),
                    dobDay = inputs.optString("dobDay", s.dobDay),
                    dobMonth = inputs.optString("dobMonth", s.dobMonth),
                    dobYear = inputs.optString("dobYear", s.dobYear),
                    joinDay = inputs.optString("joinDay", s.joinDay),
                    joinMonth = inputs.optString("joinMonth", s.joinMonth),
                    joinYear = inputs.optString("joinYear", s.joinYear),
                    category = runCatching { PersonnelCategory.valueOf(inputs.optString("category")) }.getOrDefault(s.category),
                    serviceBranch = runCatching { ServiceBranch.valueOf(inputs.optString("serviceBranch")) }.getOrDefault(s.serviceBranch),
                    officerAppendix = runCatching { OfficerAppendix.valueOf(inputs.optString("officerAppendix")) }.getOrDefault(s.officerAppendix),
                    selectedOfficerRankId = inputs.optString("selectedOfficerRankId", s.selectedOfficerRankId),
                    commissionedYear = inputs.optInt("commissionedYear", s.commissionedYear),
                    officerOldBasicInput = inputs.optString("officerOldBasicInput", s.officerOldBasicInput),
                    selectedCadetId = inputs.optString("selectedCadetId", s.selectedCadetId),
                    selectedNonOfficerRankId = inputs.optString("selectedNonOfficerRankId", s.selectedNonOfficerRankId),
                    oldBasicPayInput = inputs.optString("oldBasicPayInput", s.oldBasicPayInput),
                    oldScaleStartOverride = inputs.optString("oldScaleStartOverride", s.oldScaleStartOverride),
                    applyJuly2026Increment = inputs.optBoolean("applyJuly2026Increment", s.applyJuly2026Increment),
                    isMarried = inputs.optBoolean("isMarried", s.isMarried),
                    childrenCount = inputs.optInt("childrenCount", s.childrenCount),
                    disabledChildrenCount = inputs.optInt("disabledChildrenCount", s.disabledChildrenCount),
                    proficiencyPayId = inputs.optString("proficiencyPayId", s.proficiencyPayId),
                    appointmentPayId = inputs.optString("appointmentPayId", s.appointmentPayId),
                    goodConductBadges = inputs.optInt("goodConductBadges", s.goodConductBadges),
                    selectedSpecialSkills = if (skillsArr != null) skillsSet else s.selectedSpecialSkills,
                    otherNewAllowanceInput = inputs.optString("otherNewAllowanceInput", s.otherNewAllowanceInput),
                    receiveMonthIndex = inputs.optInt("receiveMonthIndex", s.receiveMonthIndex),
                    monthlyBiseshSubidhaInput = inputs.optString("monthlyBiseshSubidhaInput", s.monthlyBiseshSubidhaInput),
                    cityCategory = runCatching { CityCategory.valueOf(inputs.optString("cityCategory")) }.getOrDefault(s.cityCategory),
                    accommodationType = runCatching { AccommodationType.valueOf(inputs.optString("accommodationType")) }.getOrDefault(s.accommodationType),
                    serviceBracket = bracket,
                    activeMedicalAgeBracket = runCatching { MedicalAgeBracket.valueOf(inputs.optString("activeMedicalAgeBracketId")) }.getOrDefault(s.activeMedicalAgeBracket),
                    receiveFreeTiffin = inputs.optBoolean("receiveFreeTiffin", s.receiveFreeTiffin),
                    hillHaorCategory = runCatching { HillHaorCategory.valueOf(inputs.optString("hillHaorCategoryId")) }.getOrDefault(s.hillHaorCategory),
                    oldServiceOrdnanceInput = inputs.optString("oldServiceOrdnanceInput", s.oldServiceOrdnanceInput),
                    gpfPercent = inputs.optInt("gpfPercent", s.gpfPercent).coerceIn(5, 25),
                    pensionSubTab = runCatching { PensionSubTab.valueOf(inputs.optString("pensionSubTab")) }.getOrDefault(s.pensionSubTab),
                    oldNetPensionInput = inputs.optString("oldNetPensionInput", s.oldNetPensionInput),
                    pensionerAgeBracket = runCatching { MedicalAgeBracket.valueOf(inputs.optString("pensionerAgeBracketId")) }.getOrDefault(s.pensionerAgeBracket),
                    adultRationMembers = inputs.optInt("adultRationMembers", s.adultRationMembers),
                    childRationMembers = inputs.optInt("childRationMembers", s.childRationMembers),
                    retiringType = runCatching { RetiringType.valueOf(inputs.optString("retiringType")) }.getOrDefault(s.retiringType),
                    retiringLastPayInput = inputs.optString("retiringLastPayInput", s.retiringLastPayInput),
                    retiringQualifyingYearsInput = inputs.optString("retiringQualifyingYearsInput", s.retiringQualifyingYearsInput),
                    rankPrescribedYearsInput = inputs.optString("rankPrescribedYearsInput", s.rankPrescribedYearsInput),
                    retiringAgeNextBirthday = inputs.optInt("retiringAgeNextBirthday", s.retiringAgeNextBirthday).coerceIn(20, 80),
                    commutationSurrenderPercent = inputs.optInt("commutationSurrenderPercent", s.commutationSurrenderPercent).coerceIn(0, 100),
                    retireDay = inputs.optString("retireDay", s.retireDay),
                    retireMonth = inputs.optString("retireMonth", s.retireMonth),
                    retireYear = inputs.optString("retireYear", s.retireYear),
                    projectedPromotions = if (promosArr != null) promos else s.projectedPromotions
                )
            }
            recalculateAll(persist = true)
            true
        } catch (_: Exception) {
            false
        }
    }

    // Room Serialization Helpers
    private fun CalculatorUiState.toEntity(): CalculatorStateEntity = CalculatorStateEntity(
        id = 1,
        useBengaliDigits = useBengaliDigits,
        isPensionerRole = isPensionerRole,
        userName = userName,
        serviceNo = serviceNo,
        unitName = unitName,
        dobDay = dobDay,
        dobMonth = dobMonth,
        dobYear = dobYear,
        joinDay = joinDay,
        joinMonth = joinMonth,
        joinYear = joinYear,
        categoryName = category.name,
        serviceBranchName = serviceBranch.name,
        officerAppendixName = officerAppendix.name,
        selectedOfficerRankId = selectedOfficerRankId,
        commissionedYear = commissionedYear,
        officerOldBasicInput = officerOldBasicInput,
        selectedCadetId = selectedCadetId,
        selectedNonOfficerRankId = selectedNonOfficerRankId,
        oldBasicPayInput = oldBasicPayInput,
        oldScaleStartOverride = oldScaleStartOverride,
        applyJuly2026Increment = applyJuly2026Increment,
        isMarried = isMarried,
        childrenCount = childrenCount,
        disabledChildrenCount = disabledChildrenCount,
        proficiencyPayId = proficiencyPayId,
        appointmentPayId = appointmentPayId,
        goodConductBadges = goodConductBadges,
        selectedSpecialSkillsCsv = selectedSpecialSkills.joinToString(",") { it.name },
        otherNewAllowanceInput = otherNewAllowanceInput,
        receiveMonthIndex = receiveMonthIndex,
        monthlyBiseshSubidhaInput = monthlyBiseshSubidhaInput,
        cityCategoryName = cityCategory.name,
        accommodationTypeName = accommodationType.name,
        serviceBracketName = serviceBracket.name,
        activeMedicalAgeBracketName = activeMedicalAgeBracket.name,
        receiveFreeTiffin = receiveFreeTiffin,
        hillHaorCategoryName = hillHaorCategory.name,
        oldServiceOrdnanceInput = oldServiceOrdnanceInput,
        gpfPercent = gpfPercent,
        pensionSubTabName = pensionSubTab.name,
        oldNetPensionInput = oldNetPensionInput,
        pensionerAgeBracketName = pensionerAgeBracket.name,
        adultRationMembers = adultRationMembers,
        childRationMembers = childRationMembers,
        retiringTypeName = retiringType.name,
        retiringLastPayInput = retiringLastPayInput,
        retiringQualifyingYearsInput = retiringQualifyingYearsInput,
        rankPrescribedYearsInput = rankPrescribedYearsInput,
        retiringAgeNextBirthday = retiringAgeNextBirthday,
        commutationSurrenderPercent = commutationSurrenderPercent,
        retireDay = retireDay,
        retireMonth = retireMonth,
        retireYear = retireYear,
        projectedPromotionsRaw = projectedPromotions.joinToString(";;") {
            "${it.id}||${it.rankId}||${it.rankTitle}||${it.day}||${it.month}||${it.year}"
        },
        updatedAtMillis = System.currentTimeMillis()
    )

    private fun CalculatorStateEntity.toUiState(current: CalculatorUiState): CalculatorUiState {
        val skills = selectedSpecialSkillsCsv.split(",")
            .mapNotNull { runCatching { SpecialSkillPay.valueOf(it) }.getOrNull() }
            .toSet()
        val promos = projectedPromotionsRaw.split(";;")
            .filter { it.isNotBlank() }
            .mapNotNull { chunk ->
                val parts = chunk.split("||")
                if (parts.size == 6) {
                    ProjectedPromotion(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5])
                } else null
            }

        return current.copy(
            isLoadedFromDb = true,
            lastSavedTimestamp = updatedAtMillis,
            useBengaliDigits = useBengaliDigits,
            isPensionerRole = isPensionerRole,
            userName = userName,
            serviceNo = serviceNo,
            unitName = unitName,
            dobDay = dobDay,
            dobMonth = dobMonth,
            dobYear = dobYear,
            joinDay = joinDay,
            joinMonth = joinMonth,
            joinYear = joinYear,
            category = runCatching { PersonnelCategory.valueOf(categoryName) }.getOrDefault(current.category),
            serviceBranch = runCatching { ServiceBranch.valueOf(serviceBranchName) }.getOrDefault(current.serviceBranch),
            officerAppendix = runCatching { OfficerAppendix.valueOf(officerAppendixName) }.getOrDefault(current.officerAppendix),
            selectedOfficerRankId = selectedOfficerRankId,
            commissionedYear = commissionedYear,
            officerOldBasicInput = officerOldBasicInput,
            selectedCadetId = selectedCadetId,
            selectedNonOfficerRankId = selectedNonOfficerRankId,
            oldBasicPayInput = oldBasicPayInput,
            oldScaleStartOverride = oldScaleStartOverride,
            applyJuly2026Increment = applyJuly2026Increment,
            isMarried = isMarried,
            childrenCount = childrenCount,
            disabledChildrenCount = disabledChildrenCount,
            proficiencyPayId = proficiencyPayId,
            appointmentPayId = appointmentPayId,
            goodConductBadges = goodConductBadges,
            selectedSpecialSkills = skills,
            otherNewAllowanceInput = otherNewAllowanceInput,
            receiveMonthIndex = receiveMonthIndex,
            monthlyBiseshSubidhaInput = monthlyBiseshSubidhaInput,
            cityCategory = runCatching { CityCategory.valueOf(cityCategoryName) }.getOrDefault(current.cityCategory),
            accommodationType = runCatching { AccommodationType.valueOf(accommodationTypeName) }.getOrDefault(current.accommodationType),
            serviceBracket = runCatching { ServiceBracket.valueOf(serviceBracketName) }.getOrDefault(current.serviceBracket),
            activeMedicalAgeBracket = runCatching { MedicalAgeBracket.valueOf(activeMedicalAgeBracketName) }.getOrDefault(current.activeMedicalAgeBracket),
            receiveFreeTiffin = receiveFreeTiffin,
            hillHaorCategory = runCatching { HillHaorCategory.valueOf(hillHaorCategoryName) }.getOrDefault(current.hillHaorCategory),
            oldServiceOrdnanceInput = oldServiceOrdnanceInput,
            gpfPercent = gpfPercent,
            pensionSubTab = runCatching { PensionSubTab.valueOf(pensionSubTabName) }.getOrDefault(current.pensionSubTab),
            oldNetPensionInput = oldNetPensionInput,
            pensionerAgeBracket = runCatching { MedicalAgeBracket.valueOf(pensionerAgeBracketName) }.getOrDefault(current.pensionerAgeBracket),
            adultRationMembers = adultRationMembers,
            childRationMembers = childRationMembers,
            retiringType = runCatching { RetiringType.valueOf(retiringTypeName) }.getOrDefault(current.retiringType),
            retiringLastPayInput = retiringLastPayInput,
            retiringQualifyingYearsInput = retiringQualifyingYearsInput,
            rankPrescribedYearsInput = rankPrescribedYearsInput,
            retiringAgeNextBirthday = retiringAgeNextBirthday,
            commutationSurrenderPercent = commutationSurrenderPercent,
            retireDay = retireDay,
            retireMonth = retireMonth,
            retireYear = retireYear,
            projectedPromotions = promos
        )
    }

    private fun serializeStateCompact(state: CalculatorUiState): String {
        val e = state.toEntity()
        return listOf(
            e.isPensionerRole, e.userName, e.serviceNo, e.unitName,
            e.dobDay, e.dobMonth, e.dobYear,
            e.joinDay, e.joinMonth, e.joinYear,
            e.categoryName, e.serviceBranchName, e.officerAppendixName,
            e.selectedOfficerRankId, e.commissionedYear, e.officerOldBasicInput,
            e.selectedCadetId, e.selectedNonOfficerRankId, e.oldBasicPayInput,
            e.oldScaleStartOverride, e.applyJuly2026Increment,
            e.isMarried, e.childrenCount, e.disabledChildrenCount,
            e.proficiencyPayId, e.appointmentPayId, e.goodConductBadges,
            e.selectedSpecialSkillsCsv, e.otherNewAllowanceInput,
            e.receiveMonthIndex, e.monthlyBiseshSubidhaInput,
            e.cityCategoryName, e.accommodationTypeName, e.serviceBracketName,
            e.activeMedicalAgeBracketName, e.receiveFreeTiffin, e.hillHaorCategoryName,
            e.oldServiceOrdnanceInput, e.gpfPercent
        ).joinToString("@@")
    }

    private fun deserializeStateCompact(raw: String, current: CalculatorUiState): CalculatorUiState? {
        val p = raw.split("@@")
        if (p.size < 39) return null
        val skills = p[27].split(",")
            .mapNotNull { runCatching { SpecialSkillPay.valueOf(it) }.getOrNull() }
            .toSet()
        return current.copy(
            isPensionerRole = p[0].toBoolean(),
            userName = p[1],
            serviceNo = p[2],
            unitName = p[3],
            dobDay = p[4],
            dobMonth = p[5],
            dobYear = p[6],
            joinDay = p[7],
            joinMonth = p[8],
            joinYear = p[9],
            category = runCatching { PersonnelCategory.valueOf(p[10]) }.getOrDefault(current.category),
            serviceBranch = runCatching { ServiceBranch.valueOf(p[11]) }.getOrDefault(current.serviceBranch),
            officerAppendix = runCatching { OfficerAppendix.valueOf(p[12]) }.getOrDefault(current.officerAppendix),
            selectedOfficerRankId = p[13],
            commissionedYear = p[14].toIntOrNull() ?: current.commissionedYear,
            officerOldBasicInput = p[15],
            selectedCadetId = p[16],
            selectedNonOfficerRankId = p[17],
            oldBasicPayInput = p[18],
            oldScaleStartOverride = p[19],
            applyJuly2026Increment = p[20].toBoolean(),
            isMarried = p[21].toBoolean(),
            childrenCount = p[22].toIntOrNull() ?: 0,
            disabledChildrenCount = p[23].toIntOrNull() ?: 0,
            proficiencyPayId = p[24],
            appointmentPayId = p[25],
            goodConductBadges = p[26].toIntOrNull() ?: 0,
            selectedSpecialSkills = skills,
            otherNewAllowanceInput = p[28],
            receiveMonthIndex = p[29].toIntOrNull() ?: 0,
            monthlyBiseshSubidhaInput = p[30],
            cityCategory = runCatching { CityCategory.valueOf(p[31]) }.getOrDefault(current.cityCategory),
            accommodationType = runCatching { AccommodationType.valueOf(p[32]) }.getOrDefault(current.accommodationType),
            serviceBracket = runCatching { ServiceBracket.valueOf(p[33]) }.getOrDefault(current.serviceBracket),
            activeMedicalAgeBracket = runCatching { MedicalAgeBracket.valueOf(p[34]) }.getOrDefault(current.activeMedicalAgeBracket),
            receiveFreeTiffin = p[35].toBoolean(),
            hillHaorCategory = runCatching { HillHaorCategory.valueOf(p[36]) }.getOrDefault(current.hillHaorCategory),
            oldServiceOrdnanceInput = p[37],
            gpfPercent = p[38].toIntOrNull() ?: 5
        )
    }
}

class CalculatorViewModelFactory(
    private val repository: CalculatorRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            return CalculatorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
