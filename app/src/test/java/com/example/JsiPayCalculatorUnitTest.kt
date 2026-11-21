package com.example

import com.example.data.*
import com.example.viewmodel.CalculatorTab
import com.example.viewmodel.CalculatorViewModel
import com.example.viewmodel.PensionSubTab
import com.example.viewmodel.RetiringType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class JsiPayCalculatorUnitTest {

    @Test
    fun testGazetteExample2_SergeantFixationAndIncrement() {
        val vm = CalculatorViewModel()
        vm.selectCategory(PersonnelCategory.JCO_OR)
        vm.selectNonOfficerRank("sgt")
        vm.updateOldBasicPay("26120")
        vm.toggleJuly2026Increment(false)
        assertEquals(42900L, vm.uiState.value.payFixationResult?.newBasicPay)

        vm.toggleJuly2026Increment(true)
        assertEquals(45100L, vm.uiState.value.payFixationResult?.newBasicPay)
        assertEquals(47300L, vm.uiState.value.payFixationResult?.july2027BasicPay)
    }

    @Test
    fun testSainikExactFixationPhasesAndProRataArrears() {
        val vm = CalculatorViewModel()
        vm.selectCategory(PersonnelCategory.JCO_OR)
        vm.selectNonOfficerRank("sainik")
        vm.updateOldBasicPay("10960")
        vm.toggleJuly2026Increment(true)
        vm.selectReceiveMonthIndex(0) // এখনো পাইনি (আজ পর্যন্ত হিসাব)
        vm.updateGpfPercent(5)
        vm.setReferenceDateForTest(LocalDate.of(2026, 11, 21))

        val pay = vm.uiState.value.payFixationResult!!
        assertEquals(9000L, pay.oldScaleStart)
        assertEquals(21400L, pay.newScaleStart)
        assertEquals(23600L, pay.fixedBeforeIncrementPay)
        assertEquals(1200L, pay.july2026IncrementAmount)
        assertEquals(24800L, pay.newBasicPay)
        assertEquals(26100L, pay.july2027BasicPay)

        val phased = vm.uiState.value.phasedArrearsResult!!
        assertEquals(17880L, phased.phase1Pay)
        assertEquals(21340L, phased.phase2Pay)
        assertEquals(26100L, phased.phase3Pay)
        assertEquals(2000L, phased.monthlyBiseshSubidha)
        assertEquals(32524L, phased.grossArrears)
        assertEquals(9400L, phased.totalBiseshSubidhaDeducted)
        assertEquals(23124L, phased.netPayableArrears)
        assertEquals(1626L, phased.probableGpfOnArrears)
    }

    @Test
    fun testGazetteExample_Major16thYear() {
        val vm = CalculatorViewModel()
        vm.selectCategory(PersonnelCategory.OFFICER)
        vm.selectOfficerAppendix(OfficerAppendix.STANDARD)
        vm.selectOfficerRank("maj")
        vm.selectCommissionedYear(16)
        assertEquals(113200L, vm.uiState.value.payFixationResult?.newBasicPay)
        assertEquals(117700L, vm.uiState.value.payFixationResult?.july2027BasicPay)
    }

    @Test
    fun testHonoraryCaptainProtectionRule() {
        val vm = CalculatorViewModel()
        vm.selectCategory(PersonnelCategory.JCO_OR)
        vm.selectNonOfficerRank("hony_capt")
        vm.updateOldBasicPay("80000")
        assertEquals(85780L, vm.uiState.value.payFixationResult?.newBasicPay)

        vm.updateOldBasicPay("90000")
        assertEquals(90500L, vm.uiState.value.payFixationResult?.newBasicPay)
    }

    @Test
    fun testExistingPensionerSlabsAndCaps() {
        val vm = CalculatorViewModel()
        vm.selectPensionSubTab(PensionSubTab.EXISTING)

        vm.updateOldNetPension("4500")
        assertEquals(10000L, vm.uiState.value.existingPensionResult?.newFullNetPension)

        vm.updateOldNetPension("10000")
        assertEquals(18001L, vm.uiState.value.existingPensionResult?.newFullNetPension)

        vm.updateOldNetPension("50000")
        assertEquals(70200L, vm.uiState.value.existingPensionResult?.newFullNetPension)

        vm.updateOldNetPension("75000")
        assertEquals(75000L, vm.uiState.value.existingPensionResult?.newFullNetPension)
    }

    @Test
    fun testRetiringPensionAndCommutation() {
        val vm = CalculatorViewModel()
        vm.selectPensionSubTab(PensionSubTab.RETIRING)
        vm.selectRetiringType(RetiringType.JCO_OR)
        vm.updateRetiringLastPay("45100")
        vm.updateRetiringQualifyingYears("25")
        vm.updateRetiringAgeNextBirthday(50)
        vm.updateCommutationSurrenderPercent(50)

        val res = vm.uiState.value.retiringPensionResult!!
        assertEquals(36861L, res.grossPension)
        assertEquals(21.526, res.commutationMultiplier, 0.0001)
    }

    @Test
    fun testFuturePromotionProjection() {
        val vm = CalculatorViewModel()
        vm.updateDob(day = "01", month = "02", year = "2003")
        vm.updateJoinDate(day = "04", month = "02", year = "2022")
        vm.updateRetireDate(day = "04", month = "02", year = "2043")
        vm.addProjectedPromotion("wo", "ওয়ারেন্ট অফিসার", "01", "01", "2035")
        vm.applyProjectedFuturePensionInputs()

        val res = vm.uiState.value.retiringPensionResult!!
        assertEquals(21.0, res.projectedQualifyingYears, 0.1)
        assertTrue(res.grossPension > 0L)
        assertEquals("ওয়ারেন্ট অফিসার", res.projectedLastRankTitle)
    }

    @Test
    fun testNewAllowances_ProficiencySkillBadgeAndDisabledChild() {
        val vm = CalculatorViewModel()
        vm.selectCategory(PersonnelCategory.JCO_OR)
        vm.selectNonOfficerRank("sgt")
        vm.selectProficiencyPay("high_g1") // 746
        vm.selectAppointmentPay("bsm_rsm") // 292
        vm.updateGoodConductBadges(2) // 2 * 35 = 70
        vm.toggleSpecialSkill(SpecialSkillPay.PARACHUTE) // 354 for JCO/OR
        vm.selectDisabledChildrenCount(1) // 3000
        vm.updateOtherNewAllowance("1000")

        val allow = vm.uiState.value.allowancesResult!!
        assertEquals(746L, allow.proficiencyPay)
        assertEquals(292L, allow.appointmentPay)
        assertEquals(70L, allow.goodConductPay)
        assertEquals(354L, allow.specialSkillPay)
        assertEquals(3000L, allow.disabledChildAllowance)
        assertEquals(1000L, allow.otherNewAllowance)
    }

    @Test
    fun testDateSyncAndRoleNavigation() {
        val vm = CalculatorViewModel()
        vm.updateDob(day = "01", month = "02", year = "1970")
        assertEquals(56, vm.uiState.value.computedAgeOnEffectiveDate)
        assertEquals(MedicalAgeBracket.AGE_50_TO_60, vm.uiState.value.activeMedicalAgeBracket)

        vm.updateJoinDate(day = "01", month = "01", year = "2010")
        assertEquals(ServiceBracket.Y15_20, vm.uiState.value.serviceBracket)

        vm.setRolePensioner(false)
        vm.submitAndNavigate()
        assertEquals(CalculatorTab.SALARY, vm.uiState.value.selectedTab)

        vm.setRolePensioner(true)
        vm.submitAndNavigate()
        assertEquals(CalculatorTab.PENSION, vm.uiState.value.selectedTab)

        vm.updateUserName("মোঃ হোসেন আলী")
        assertTrue(vm.buildFullSummaryText().contains("Àbdüllāh Aĺ Hỗŝŝâîň"))
        vm.resetAll()
        assertEquals("", vm.uiState.value.userName)
        assertEquals(CalculatorTab.INFO, vm.uiState.value.selectedTab)
    }

    @Test
    fun testServiceBranchAndCategorySwitchNormalization() {
        val vm = CalculatorViewModel()

        // AFNS sets AFNS appendix and officer rank, switching back to ARMY must reset to STANDARD
        vm.selectServiceBranch(ServiceBranch.AFNS)
        assertEquals(OfficerAppendix.AFNS, vm.uiState.value.officerAppendix)
        assertEquals(PersonnelCategory.OFFICER, vm.uiState.value.category)
        assertEquals("afns_lt", vm.uiState.value.selectedOfficerRankId)

        vm.selectServiceBranch(ServiceBranch.ARMY)
        assertEquals(OfficerAppendix.STANDARD, vm.uiState.value.officerAppendix)
        assertEquals("maj", vm.uiState.value.selectedOfficerRankId)

        // Switching to MODC keeps valid MODC rank ("sgt"), falls back to "nce" from non-MODC ranks ("hony_capt")
        vm.selectServiceBranch(ServiceBranch.MODC)
        assertEquals(PersonnelCategory.JCO_OR, vm.uiState.value.category)
        assertEquals("sgt", vm.uiState.value.selectedNonOfficerRankId)
        assertEquals("maj", vm.uiState.value.selectedOfficerRankId)

        vm.selectNonOfficerRank("hony_capt")
        vm.selectServiceBranch(ServiceBranch.MODC)
        assertEquals("nce", vm.uiState.value.selectedNonOfficerRankId)

        vm.selectServiceBranch(ServiceBranch.RELIGIOUS)
        assertEquals("wo", vm.uiState.value.selectedNonOfficerRankId)

        // Switching to OFFICER from MODC normalizes branch to ARMY
        vm.selectCategory(PersonnelCategory.OFFICER)
        assertEquals(ServiceBranch.ARMY, vm.uiState.value.serviceBranch)

        // syncPayToAllowancesAndPension enforces RETIRING sub-tab
        vm.selectPensionSubTab(PensionSubTab.EXISTING)
        vm.syncPayToAllowancesAndPension()
        assertEquals(PensionSubTab.RETIRING, vm.uiState.value.pensionSubTab)
    }
}
