package com.example.data

enum class ServiceBranch(val bnLabel: String, val enLabel: String) {
    ARMY("সেনাবাহিনী", "Army"),
    NAVY("নৌবাহিনী", "Navy"),
    AIR_FORCE("বিমানবাহিনী", "Air Force"),
    AFNS("এএফএনএস (AFNS)", "AFNS"),
    MODC("এমওডিসি (MODC)", "MODC"),
    RELIGIOUS("ধর্মীয় পরামর্শদাতা", "Religious")
}

enum class PersonnelCategory(val bnLabel: String, val enLabel: String) {
    OFFICER("কমিশন্ড অফিসার", "Commissioned Officers (Ch. 1)"),
    CADET("ক্যাডেট / মিডশীপম্যান", "Cadets (Appendix Ja)"),
    JCO_OR("জেসিও ও ওআর / সমতুল্য", "JCO, OR, NC(E), MODC & Religious")
}

enum class OfficerAppendix(val bnTitle: String, val enSubtitle: String) {
    STANDARD("ক্রোড়পত্র-ক / ঘ / ঙ / চ (সাধারণ কমিশন্ড)", "Standard Army/Navy/Air Force"),
    DIRECT_CAPT("ক্রোড়পত্র-খ (এএমসি/এডিসি/ইঞ্জি./সিগন্যালস/ইএমই সরাসরি ক্যাপ্টেন)", "Direct Capt (Medical/Engr)"),
    DIRECT_LT("ক্রোড়পত্র-গ (প্রাক-কমিশন এমএসসি/এইসি/জেএজি/আরভিএফসি)", "Direct Lt (Special Corps)"),
    AFNS("ক্রোড়পত্র-ছ (এএফএনএস অফিসারবৃন্দ)", "AFNS Officers")
}

enum class ImplementationGroup(val bnLabel: String, val phase1Rate: Double, val phase2Rate: Double) {
    GROUP_A("গ্রুপ 'ক' (৪০% / ৭০% / ১০০%)", 0.40, 0.70),
    GROUP_B("গ্রুপ 'খ' (৫০% / ৭৫% / ১০০%)", 0.50, 0.75)
}

enum class RankTierForAllowances {
    GEN, LT_GEN, MAJ_GEN, BRIG_GEN, COL, LT_COL, MAJ, CAPT, LT, SECOND_LT,
    HONY_CAPT, HONY_LT, MWO, CHIEF_ARTIFICER, SWO, WO,
    SGT, CPL, LCPL, SAINIK, NCE, RECRUIT, CADET, RELIGIOUS
}

data class OfficerRankOption(
    val id: String,
    val bnName: String,
    val enName: String,
    val isFixed: Boolean = false,
    val fixedPay: Long = 0L,
    val yearToPay: Map<Int, Long> = emptyMap(),
    val allowanceTier: RankTierForAllowances,
    val defaultPrescribedService: Int = 22
)

data class CadetOption(
    val id: String,
    val bnName: String,
    val enName: String,
    val fixedPay: Long,
    val oldFixedPay: Long
)

data class NonOfficerRankOption(
    val id: String,
    val bnName: String,
    val enName: String,
    val isHonoraryFixed: Boolean = false,
    val isRecruitFixed: Boolean = false,
    val fixedPay: Long = 0L,
    val oldScaleStart: Long = 0L,
    val steps: List<Long> = emptyList(),
    val group: ImplementationGroup,
    val allowanceTier: RankTierForAllowances,
    val isSergeantOrBelow: Boolean = false,
    val isCplOrBelowForTiffinTransport: Boolean = false,
    val isOfficerOrJcoAboveWo: Boolean = false
)

enum class CityCategory(val bnLabel: String, val enLabel: String) {
    DHAKA("ঢাকা উত্তর ও দক্ষিণ সিটি কর্পোরেশন", "Dhaka North & South City Corp"),
    OTHER_CITY_CORP("অন্যান্য ১১টি সিটি কর্পোরেশন + কক্সবাজার ও সাভার", "Other City Corps + Cox's Bazar & Savar"),
    OTHER_AREA("অন্যান্য স্থান / ক্যান্টনমেন্ট / মফস্বল", "Other Areas")
}

enum class AccommodationType(val bnLabel: String, val enLabel: String) {
    NO_QUARTERS_STANDARD("সরকারি বাসা বরাদ্দ নেই (সাধারণ হার)", "No Govt Quarters (Standard)"),
    NO_QUARTERS_EXTRA_FACILITY("সরকারি বাসা নেই (বিনামূল্যে আসবাব/রেয়াতি বিদ্যুৎ-গ্যাস)", "Extra Facilities (Officers)"),
    MARRIED_GOVT_QUARTERS("বিবাহিত ও সরকারি পারিবারিক বাসস্থান বরাদ্দপ্রাপ্ত", "Married with Govt Family Quarters"),
    UNMARRIED_OR_MESS("অবিবাহিত / মেস বা ব্যারাকে অবস্থান", "Unmarried / Mess / Barracks")
}

enum class ServiceBracket(val bnLabel: String, val index: Int) {
    Y0_5("০ – ৫ বৎসর", 0),
    Y5_10("৫+ – ১০ বৎসর", 1),
    Y10_15("১০+ – ১৫ বৎসর", 2),
    Y15_20("১৫+ – ২০ বৎসর", 3),
    Y20_PLUS("২০+ বৎসর ও তদূর্ধ্ব", 4)
}

enum class MedicalAgeBracket(val bnLabel: String, val amount: Long) {
    UP_TO_50("৫০ বৎসর বয়স পর্যন্ত (৩,০০০ টাকা)", 3000L),
    AGE_50_TO_60("৫০ বৎসর ১ দিন হতে ৬০ বৎসর পর্যন্ত (৪,০০০ টাকা)", 4000L),
    AGE_60_TO_70("৬০ বৎসর ১ দিন হতে ৭০ বৎসর (অবসরভোগী: ৫,০০০ টাকা)", 5000L),
    AGE_70_PLUS("৭০ বৎসরের ঊর্ধ্বে (অবসরভোগী: ৬,০০০ টাকা)", 6000L)
}

enum class HillHaorCategory(val bnLabel: String, val maxCap: Long) {
    NONE("প্রযোজ্য নয়", 0L),
    SADAR("পার্বত্য জেলা সদর ও সদর উপজেলা (২০%, সর্বোচ্চ ৫,০০০)", 5000L),
    OTHER_UPAZILA("পার্বত্য অন্যান্য উপজেলা / হাওড়-দ্বীপ-চর (২০%, সর্বোচ্চ ৫,৫০০)", 5500L)
}

data class PayElementOption(
    val id: String,
    val bnLabel: String,
    val amount: Long,
    val oldAmount: Long = 0L
)

enum class SpecialSkillPay(val bnLabel: String) {
    PARACHUTE("প্যারাসুট"),
    COMMANDO("কমান্ডো"),
    SWADS("সোয়াডস"),
    SUBMARINE("সাবমেরিন"),
    SCUBA("স্কুবা ডাইভিং"),
    SNIPER("স্নাইপার"),
    FLYING("উড্ডয়ন (ফ্লাইট ক্রু)")
}

data class PayScale2015Grade(
    val id: String,
    val bnLabel: String,
    val startPay: Long,
    val rate: Double,
    val count: Int,
    val maxCap: Long?
)

data class Grade2015Context(
    val grade: PayScale2015Grade,
    val displayLabel: String,
    val startPay: Long,
    val steps: List<Long>
)

object JsiPayData {

    // Proficiency Pay Options (অধ্যায়-৪, অনুচ্ছেদ ১৫, ক্রমিক ২)
    val proficiencyPayOptions = listOf(
        PayElementOption("none", "প্রযোজ্য নয়", 0L, 0L),
        PayElementOption("high_g1", "উচ্চ দক্ষতা বেতন গ্রেড-১ (সার্জেন্ট ও তদূর্ধ্ব) — ৭৪৬ ৳", 746L, 574L),
        PayElementOption("high_g2", "উচ্চ দক্ষতা বেতন গ্রেড-২ (কর্পোরাল ও তদনিম্ন) — ৫৪৪ ৳", 544L, 418L),
        PayElementOption("prof_g3", "দক্ষতা বেতন গ্রেড-৩ (সার্জেন্ট ও তদূর্ধ্ব) — ৪১৯ ৳", 419L, 322L),
        PayElementOption("prof_g4", "দক্ষতা বেতন গ্রেড-৪ (কর্পোরাল ও তদনিম্ন) — ২৯২ ৳", 292L, 225L)
    )

    // Appointment Pay Options (অধ্যায়-৪, অনুচ্ছেদ ১৫, ক্রমিক ১)
    val appointmentPayOptions = listOf(
        PayElementOption("none", "প্রযোজ্য নয়", 0L, 0L),
        PayElementOption("bsm_rsm", "বিএসএম / আরএসএম — ২৯২ ৳", 292L, 225L),
        PayElementOption("bqms", "বিকিউএমএস — ২৪০ ৳", 240L, 185L),
        PayElementOption("cqms_csm", "সিকিউএমএস / সিএসএম / পিও ইনচার্জ / ডব্লিউওআইসি — ২০২ ৳", 202L, 155L)
    )

    fun getSpecialSkillAmount(skill: SpecialSkillPay, isOfficer: Boolean, tier: RankTierForAllowances): Long {
        val isJcoOrAbove = tier in setOf(
            RankTierForAllowances.HONY_CAPT, RankTierForAllowances.HONY_LT,
            RankTierForAllowances.MWO, RankTierForAllowances.CHIEF_ARTIFICER,
            RankTierForAllowances.SWO, RankTierForAllowances.WO
        )
        val isSgt = tier == RankTierForAllowances.SGT

        return when (skill) {
            SpecialSkillPay.PARACHUTE -> if (isOfficer) 664L else 354L
            SpecialSkillPay.COMMANDO -> when {
                isOfficer -> 1982L
                isJcoOrAbove || isSgt -> 582L
                else -> 468L
            }
            SpecialSkillPay.SWADS -> when {
                isOfficer -> 1982L
                isJcoOrAbove || isSgt -> 582L
                else -> 468L
            }
            SpecialSkillPay.SUBMARINE -> when {
                isOfficer -> 4500L
                isJcoOrAbove -> 2000L
                isSgt -> 1500L
                else -> 1000L
            }
            SpecialSkillPay.SCUBA -> if (isOfficer) 664L else 354L
            SpecialSkillPay.SNIPER -> if (isOfficer) 715L else 374L
            SpecialSkillPay.FLYING -> if (isOfficer) 10000L else 4000L
        }
    }

    fun getOldSpecialSkillAmount(skill: SpecialSkillPay, isOfficer: Boolean, tier: RankTierForAllowances): Long {
        val isJcoOrAbove = tier in setOf(
            RankTierForAllowances.HONY_CAPT, RankTierForAllowances.HONY_LT,
            RankTierForAllowances.MWO, RankTierForAllowances.CHIEF_ARTIFICER,
            RankTierForAllowances.SWO, RankTierForAllowances.WO
        )
        val isSgt = tier == RankTierForAllowances.SGT

        return when (skill) {
            SpecialSkillPay.PARACHUTE -> if (isOfficer) 510L else 272L
            SpecialSkillPay.COMMANDO -> when {
                isOfficer -> 1525L
                isJcoOrAbove || isSgt -> 448L
                else -> 360L
            }
            SpecialSkillPay.SWADS -> when {
                isOfficer -> 1525L
                isJcoOrAbove || isSgt -> 448L
                else -> 360L
            }
            SpecialSkillPay.SUBMARINE -> when {
                isOfficer -> 3500L
                isJcoOrAbove -> 1500L
                isSgt -> 1200L
                else -> 800L
            }
            SpecialSkillPay.SCUBA -> if (isOfficer) 510L else 272L
            SpecialSkillPay.SNIPER -> if (isOfficer) 550L else 288L
            SpecialSkillPay.FLYING -> if (isOfficer) 7500L else 3000L
        }
    }

    // Standard Commissioned Officer Sequences (ক্রোড়পত্র-ক/ঘ/ঙ/চ)
    private val stdSecondLt = mapOf(1 to 46200L, 2 to 48600L)
    private val stdLt = mapOf(1 to 48300L, 2 to 50800L, 3 to 53300L, 4 to 56000L, 5 to 58800L)
    private val stdCapt = mapOf(
        4 to 58000L, 5 to 60900L, 6 to 64000L, 7 to 67200L, 8 to 70500L,
        9 to 74100L, 10 to 77800L, 11 to 81700L, 12 to 85700L, 13 to 90000L
    )
    private val stdMajor = mapOf(
        9 to 86000L, 10 to 89500L, 11 to 93100L, 12 to 96800L, 13 to 100700L,
        14 to 104700L, 15 to 108900L, 16 to 113200L, 17 to 117700L, 18 to 122500L,
        19 to 127400L, 20 to 132400L, 21 to 139700L
    )
    private val stdLtCol = mapOf(
        11 to 100000L, 12 to 103500L, 13 to 107200L, 14 to 110900L, 15 to 114800L,
        16 to 118800L, 17 to 123000L, 18 to 127300L, 19 to 131700L, 20 to 136300L,
        21 to 142400L
    )
    private val stdCol = mapOf(
        14 to 113000L, 15 to 117000L, 16 to 121100L, 17 to 125300L, 18 to 129700L,
        19 to 134300L, 20 to 139000L, 21 to 143800L, 22 to 148800L
    )
    private val stdBrigGen = mapOf(
        17 to 132000L, 18 to 135700L, 19 to 139400L, 20 to 143200L,
        21 to 147200L, 22 to 151200L, 23 to 153000L
    )

    private fun shiftScale(base: Map<Int, Long>, targetStartYear: Int): Map<Int, Long> {
        val sorted = base.entries.sortedBy { it.key }
        return sorted.mapIndexed { index, entry -> (targetStartYear + index) to entry.value }.toMap()
    }

    private val khaCapt = shiftScale(stdCapt, 1)
    private val khaMaj = shiftScale(stdMajor, 6)
    private val khaLtCol = shiftScale(stdLtCol, 8)
    private val khaCol = shiftScale(stdCol, 11)
    private val khaBrigGen = shiftScale(stdBrigGen, 14)

    private val gaLt = stdLt
    private val gaCapt = shiftScale(stdCapt, 3)
    private val gaMaj = shiftScale(stdMajor, 8)
    private val gaLtCol = shiftScale(stdLtCol, 10)
    private val gaCol = shiftScale(stdCol, 12)
    private val gaBrigGen = shiftScale(stdBrigGen, 15)

    private val afnsLt = mapOf(
        1 to 46200L, 2 to 48600L, 3 to 50900L, 4 to 53500L, 5 to 56200L, 6 to 59000L
    )
    private val afnsCapt = mapOf(
        2 to 48300L, 3 to 50800L, 4 to 53300L, 5 to 56000L, 6 to 58800L, 7 to 61700L,
        8 to 64800L, 9 to 68000L, 10 to 71400L, 11 to 75000L, 12 to 78700L
    )
    private val afnsMaj = mapOf(
        5 to 58000L, 6 to 60900L, 7 to 64000L, 8 to 67200L, 9 to 70500L, 10 to 74100L,
        11 to 77800L, 12 to 81700L, 13 to 85700L, 14 to 90000L, 15 to 94500L,
        16 to 99200L, 17 to 104200L, 18 to 109400L
    )
    private val afnsLtCol = mapOf(
        11 to 86000L, 12 to 89500L, 13 to 93100L, 14 to 96800L, 15 to 100700L,
        16 to 104700L, 17 to 108900L, 18 to 113200L, 19 to 117700L, 20 to 122500L
    )
    private val afnsCol = mapOf(
        13 to 100000L, 14 to 103500L, 15 to 107200L, 16 to 110900L, 17 to 114800L,
        18 to 118800L, 19 to 123000L, 20 to 127300L
    )

    fun getOfficerRanks(appendix: OfficerAppendix): List<OfficerRankOption> {
        val fixedSenior = listOf(
            OfficerRankOption(
                id = "maj_gen",
                bnName = "মেজর জেনারেল / রিয়ার এডমিরাল / এয়ার ভাইস মার্শাল",
                enName = "Maj Gen / Rear Admiral / AVM (Fixed)",
                isFixed = true,
                fixedPay = 156000L,
                allowanceTier = RankTierForAllowances.MAJ_GEN,
                defaultPrescribedService = 30
            ),
            OfficerRankOption(
                id = "lt_gen",
                bnName = "লেঃ জেনারেল / ভাইস এডমিরাল / এয়ার মার্শাল",
                enName = "Lt Gen / Vice Admiral / Air Marshal (Fixed)",
                isFixed = true,
                fixedPay = 164000L,
                allowanceTier = RankTierForAllowances.LT_GEN,
                defaultPrescribedService = 30
            ),
            OfficerRankOption(
                id = "chief_lt_gen",
                bnName = "বাহিনী প্রধান (লেঃ জেনারেল / ভাইস এডমিরাল / এয়ার মার্শাল)",
                enName = "Service Chief (3-Star Fixed)",
                isFixed = true,
                fixedPay = 164000L,
                allowanceTier = RankTierForAllowances.GEN,
                defaultPrescribedService = 30
            ),
            OfficerRankOption(
                id = "chief_gen",
                bnName = "বাহিনী প্রধান (জেনারেল / এডমিরাল / এয়ার চীফ মার্শাল)",
                enName = "Service Chief (4-Star Fixed)",
                isFixed = true,
                fixedPay = 172000L,
                allowanceTier = RankTierForAllowances.GEN,
                defaultPrescribedService = 32
            )
        )

        return when (appendix) {
            OfficerAppendix.STANDARD -> listOf(
                OfficerRankOption("2lt", "২য় লেঃ / এ্যাক্টিং সাব লেঃ / পাইলট অফিসার", "2nd Lt / Act Sub Lt / Pilot Officer", yearToPay = stdSecondLt, allowanceTier = RankTierForAllowances.SECOND_LT, defaultPrescribedService = 20),
                OfficerRankOption("lt", "লেঃ / সাব লেঃ / ফ্লাইং অফিসার", "Lt / Sub Lt / Flying Officer", yearToPay = stdLt, allowanceTier = RankTierForAllowances.LT, defaultPrescribedService = 20),
                OfficerRankOption("capt", "ক্যাপ্টেন / লেঃ (নৌ) / ফ্লাইট লেঃ", "Captain / Lt (Navy) / Flight Lt", yearToPay = stdCapt, allowanceTier = RankTierForAllowances.CAPT, defaultPrescribedService = 20),
                OfficerRankOption("maj", "মেজর / লেঃ কমান্ডার / স্কোয়াড্রন লিডার", "Major / Lt Cdr / Sqn Ldr", yearToPay = stdMajor, allowanceTier = RankTierForAllowances.MAJ, defaultPrescribedService = 22),
                OfficerRankOption("lt_col", "লেঃ কর্নেল / কমান্ডার / উইং কমান্ডার", "Lt Col / Commander / Wing Cdr", yearToPay = stdLtCol, allowanceTier = RankTierForAllowances.LT_COL, defaultPrescribedService = 24),
                OfficerRankOption("col", "কর্নেল / ক্যাপ্টেন (নৌ) / গ্রুপ ক্যাপ্টেন", "Colonel / Capt (Navy) / Group Capt", yearToPay = stdCol, allowanceTier = RankTierForAllowances.COL, defaultPrescribedService = 26),
                OfficerRankOption("brig_gen", "ব্রিগেডিয়ার জেনারেল / কমডোর / এয়ার কমডোর", "Brig Gen / Commodore / Air Cdre", yearToPay = stdBrigGen, allowanceTier = RankTierForAllowances.BRIG_GEN, defaultPrescribedService = 28)
            ) + fixedSenior

            OfficerAppendix.DIRECT_CAPT -> listOf(
                OfficerRankOption("capt", "ক্যাপ্টেন / লেঃ (নৌ) / ফ্লাইট লেঃ", "Captain (Direct)", yearToPay = khaCapt, allowanceTier = RankTierForAllowances.CAPT, defaultPrescribedService = 20),
                OfficerRankOption("maj", "মেজর / লেঃ কমান্ডার / স্কোয়াড্রন লিডার", "Major", yearToPay = khaMaj, allowanceTier = RankTierForAllowances.MAJ, defaultPrescribedService = 22),
                OfficerRankOption("lt_col", "লেঃ কর্নেল / কমান্ডার / উইং কমান্ডার", "Lt Col", yearToPay = khaLtCol, allowanceTier = RankTierForAllowances.LT_COL, defaultPrescribedService = 24),
                OfficerRankOption("col", "কর্নেল / ক্যাপ্টেন (নৌ) / গ্রুপ ক্যাপ্টেন", "Colonel", yearToPay = khaCol, allowanceTier = RankTierForAllowances.COL, defaultPrescribedService = 26),
                OfficerRankOption("brig_gen", "ব্রিগেডিয়ার জেনারেল / কমডোর / এয়ার কমডোর", "Brig Gen", yearToPay = khaBrigGen, allowanceTier = RankTierForAllowances.BRIG_GEN, defaultPrescribedService = 28)
            ) + fixedSenior.take(2)

            OfficerAppendix.DIRECT_LT -> listOf(
                OfficerRankOption("lt", "লেঃ / সাব লেঃ / ফ্লাইং অফিসার", "Lt (Direct)", yearToPay = gaLt, allowanceTier = RankTierForAllowances.LT, defaultPrescribedService = 20),
                OfficerRankOption("capt", "ক্যাপ্টেন / লেঃ (নৌ) / ফ্লাইট লেঃ", "Captain", yearToPay = gaCapt, allowanceTier = RankTierForAllowances.CAPT, defaultPrescribedService = 20),
                OfficerRankOption("maj", "মেজর / লেঃ কমান্ডার / স্কোয়াড্রন লিডার", "Major", yearToPay = gaMaj, allowanceTier = RankTierForAllowances.MAJ, defaultPrescribedService = 22),
                OfficerRankOption("lt_col", "লেঃ কর্নেল / কমান্ডার / উইং কমান্ডার", "Lt Col", yearToPay = gaLtCol, allowanceTier = RankTierForAllowances.LT_COL, defaultPrescribedService = 24),
                OfficerRankOption("col", "কর্নেল / ক্যাপ্টেন (নৌ) / গ্রুপ ক্যাপ্টেন", "Colonel", yearToPay = gaCol, allowanceTier = RankTierForAllowances.COL, defaultPrescribedService = 26),
                OfficerRankOption("brig_gen", "ব্রিগেডিয়ার জেনারেল / কমডোর / এয়ার কমডোর", "Brig Gen", yearToPay = gaBrigGen, allowanceTier = RankTierForAllowances.BRIG_GEN, defaultPrescribedService = 28)
            ) + fixedSenior.take(2)

            OfficerAppendix.AFNS -> listOf(
                OfficerRankOption("afns_lt", "এএফএনএস লেফটেন্যান্ট", "AFNS Lieutenant", yearToPay = afnsLt, allowanceTier = RankTierForAllowances.LT, defaultPrescribedService = 20),
                OfficerRankOption("afns_capt", "এএফএনএস ক্যাপ্টেন", "AFNS Captain", yearToPay = afnsCapt, allowanceTier = RankTierForAllowances.CAPT, defaultPrescribedService = 20),
                OfficerRankOption("afns_maj", "এএফএনএস মেজর", "AFNS Major", yearToPay = afnsMaj, allowanceTier = RankTierForAllowances.MAJ, defaultPrescribedService = 22),
                OfficerRankOption("afns_lt_col", "এএফএনএস লেঃ কর্নেল", "AFNS Lt Colonel", yearToPay = afnsLtCol, allowanceTier = RankTierForAllowances.LT_COL, defaultPrescribedService = 24),
                OfficerRankOption("afns_col", "এএফএনএস কর্নেল", "AFNS Colonel", yearToPay = afnsCol, allowanceTier = RankTierForAllowances.COL, defaultPrescribedService = 26)
            )
        }
    }

    val cadetOptions = listOf(
        CadetOption("officer_cadet", "অফিসার ক্যাডেট / ফ্লাইট ক্যাডেট (সেনা/নৌ/বিমান)", "Officer / Flight Cadet", 23000L, 10000L),
        CadetOption("midshipman", "মিডশীপম্যান (নৌবাহিনী)", "Midshipman (Navy)", 23800L, 10500L),
        CadetOption("afns_cadet", "এএফএনএস ক্যাডেট", "AFNS Cadet", 22400L, 9500L)
    )

    private val mwoSteps = listOf(
        45000L, 47300L, 49700L, 52100L, 54700L, 57500L, 60300L, 63400L, 66500L, 69900L,
        73300L, 77000L, 80900L, 84900L, 89100L, 93600L, 98300L, 103200L, 106200L
    )
    private val chiefArtificerSteps = listOf(
        44800L, 47100L, 49400L, 51900L, 54500L, 57200L, 60000L, 63000L, 66100L, 69400L,
        72900L, 76500L, 80400L, 84400L, 88600L, 93000L, 97600L, 102500L
    )
    private val swoSteps = listOf(
        44500L, 46800L, 49100L, 51500L, 54100L, 56800L, 59600L, 62600L, 65700L, 69000L,
        72400L, 76000L, 79800L, 83800L, 88000L, 92400L, 97000L, 101200L
    )
    private val woSteps = listOf(
        44000L, 46200L, 48600L, 51000L, 53500L, 56200L, 59000L, 62000L, 65100L, 68300L,
        71700L, 75300L, 79100L, 83000L, 87200L, 91500L, 96100L
    )
    private val sgtSteps = listOf(
        32000L, 33600L, 35300L, 37100L, 38900L, 40900L, 42900L, 45100L, 47300L, 49700L,
        52200L, 54800L, 57500L, 60400L, 63400L, 66600L, 69900L, 73400L, 77300L
    )
    private val cplSteps = listOf(
        24000L, 25200L, 26500L, 27800L, 29200L, 30700L, 32200L, 33800L, 35500L, 37300L,
        39100L, 41100L, 43200L, 45300L, 47600L, 49900L, 52400L, 55100L, 58000L
    )
    private val lcplSteps = listOf(
        23500L, 24700L, 26000L, 27300L, 28600L, 30000L, 31500L, 33100L, 34800L, 36500L,
        38300L, 40200L, 42300L, 44400L, 46600L, 48900L, 51300L, 53900L, 56800L
    )
    private val sainikSteps = listOf(
        21400L, 22500L, 23600L, 24800L, 26100L, 27400L, 28700L, 30200L, 31700L, 33200L,
        34900L, 36700L, 38500L, 40400L, 42400L, 44500L, 46800L, 49100L, 51900L
    )
    private val nceSteps = listOf(
        21000L, 22100L, 23200L, 24400L, 25600L, 26900L, 28200L, 29600L, 31100L, 32600L,
        34300L, 36000L, 37800L, 39600L, 41600L, 43700L, 45900L, 48200L, 50900L
    )

    val nonOfficerRanks = listOf(
        NonOfficerRankOption(
            id = "hony_capt",
            bnName = "অনারারি ক্যাপ্টেন / অনারারি লেঃ (নৌ) / ফ্লাইট লেঃ",
            enName = "Hony Captain / Hony Lt (Navy) / Hony Flight Lt (Fixed)",
            isHonoraryFixed = true,
            fixedPay = 85780L,
            oldScaleStart = 42890L,
            group = ImplementationGroup.GROUP_A,
            allowanceTier = RankTierForAllowances.HONY_CAPT,
            isOfficerOrJcoAboveWo = true
        ),
        NonOfficerRankOption(
            id = "hony_lt",
            bnName = "অনারারি লেঃ / অনারারি সাব লেঃ / ফ্লাইং অফিসার",
            enName = "Hony Lt / Hony Sub Lt / Hony Flying Officer (Fixed)",
            isHonoraryFixed = true,
            fixedPay = 76960L,
            oldScaleStart = 38480L,
            group = ImplementationGroup.GROUP_A,
            allowanceTier = RankTierForAllowances.HONY_LT,
            isOfficerOrJcoAboveWo = true
        ),
        NonOfficerRankOption(
            id = "mwo",
            bnName = "মাস্টার ওয়ারেন্ট অফিসার / এমসিপিও",
            enName = "MWO / MCPO (Army/Navy/Air Force)",
            oldScaleStart = 22500L,
            steps = mwoSteps,
            group = ImplementationGroup.GROUP_A,
            allowanceTier = RankTierForAllowances.MWO,
            isOfficerOrJcoAboveWo = true
        ),
        NonOfficerRankOption(
            id = "chief_artificer",
            bnName = "চীফ আর্টিফিসার (নৌবাহিনী)",
            enName = "Chief Artificer (Navy)",
            oldScaleStart = 22400L,
            steps = chiefArtificerSteps,
            group = ImplementationGroup.GROUP_A,
            allowanceTier = RankTierForAllowances.CHIEF_ARTIFICER,
            isOfficerOrJcoAboveWo = true
        ),
        NonOfficerRankOption(
            id = "swo",
            bnName = "সিনিয়র ওয়ারেন্ট অফিসার / এসসিপিও / আর্টি-১,২ / ধর্মীয় স্কেল-১",
            enName = "SWO / SCPO / Artificer 1-2 / MODC MWO / Religious Scale-1",
            oldScaleStart = 22250L,
            steps = swoSteps,
            group = ImplementationGroup.GROUP_A,
            allowanceTier = RankTierForAllowances.SWO,
            isOfficerOrJcoAboveWo = true
        ),
        NonOfficerRankOption(
            id = "wo",
            bnName = "ওয়ারেন্ট অফিসার / সিপিও / আর্টি-৩ / ধর্মীয় স্কেল-২",
            enName = "WO / CPO / Artificer-3 / MODC SWO / Religious Scale-2",
            oldScaleStart = 22000L,
            steps = woSteps,
            group = ImplementationGroup.GROUP_A,
            allowanceTier = RankTierForAllowances.WO,
            isOfficerOrJcoAboveWo = true
        ),
        NonOfficerRankOption(
            id = "sgt",
            bnName = "সার্জেন্ট / পেটি অফিসার / আর্টি-৪ / এমওডিসি ডব্লিউও",
            enName = "Sergeant / Petty Officer / Artificer-4 / MODC WO",
            oldScaleStart = 16000L,
            steps = sgtSteps,
            group = ImplementationGroup.GROUP_B,
            allowanceTier = RankTierForAllowances.SGT,
            isSergeantOrBelow = true
        ),
        NonOfficerRankOption(
            id = "cpl",
            bnName = "কর্পোরাল / এলএস / এমওডিসি সার্জেন্ট",
            enName = "Corporal / Leading Seaman / MODC Sgt",
            oldScaleStart = 11000L,
            steps = cplSteps,
            group = ImplementationGroup.GROUP_B,
            allowanceTier = RankTierForAllowances.CPL,
            isSergeantOrBelow = true,
            isCplOrBelowForTiffinTransport = true
        ),
        NonOfficerRankOption(
            id = "lcpl",
            bnName = "ল্যান্স কর্পোরাল / এবি / এলএসি / এমওডিসি কর্পোরাল",
            enName = "Lance Corporal / AB / LAC / MODC Cpl",
            oldScaleStart = 10200L,
            steps = lcplSteps,
            group = ImplementationGroup.GROUP_B,
            allowanceTier = RankTierForAllowances.LCPL,
            isSergeantOrBelow = true,
            isCplOrBelowForTiffinTransport = true
        ),
        NonOfficerRankOption(
            id = "sainik",
            bnName = "সৈনিক / ওডি / এসি-১ / এমওডিসি এলসিপিএল",
            enName = "Sainik / OD / AC-1 / MODC LCpl",
            oldScaleStart = 9000L,
            steps = sainikSteps,
            group = ImplementationGroup.GROUP_B,
            allowanceTier = RankTierForAllowances.SAINIK,
            isSergeantOrBelow = true,
            isCplOrBelowForTiffinTransport = true
        ),
        NonOfficerRankOption(
            id = "nce",
            bnName = "এনসি(ই) / এমওডিসি সৈনিক",
            enName = "NC(E) / MODC Sainik",
            oldScaleStart = 8800L,
            steps = nceSteps,
            group = ImplementationGroup.GROUP_B,
            allowanceTier = RankTierForAllowances.NCE,
            isSergeantOrBelow = true,
            isCplOrBelowForTiffinTransport = true
        ),
        NonOfficerRankOption(
            id = "recruit",
            bnName = "রিক্রুট / নাবিক ইউটি / এসি-২ (নির্ধারিত)",
            enName = "Recruit / Sailor UT / AC-2 (Fixed)",
            isRecruitFixed = true,
            fixedPay = 21400L,
            oldScaleStart = 9000L,
            group = ImplementationGroup.GROUP_B,
            allowanceTier = RankTierForAllowances.RECRUIT,
            isSergeantOrBelow = true,
            isCplOrBelowForTiffinTransport = true
        ),
        NonOfficerRankOption(
            id = "modc_recruit",
            bnName = "এমওডিসি রিক্রুট (নির্ধারিত)",
            enName = "MODC Recruit (Fixed)",
            isRecruitFixed = true,
            fixedPay = 21000L,
            oldScaleStart = 8800L,
            group = ImplementationGroup.GROUP_B,
            allowanceTier = RankTierForAllowances.RECRUIT,
            isSergeantOrBelow = true,
            isCplOrBelowForTiffinTransport = true
        )
    )

    val officerDefenceAllowance = mapOf(
        RankTierForAllowances.GEN to 7200L,
        RankTierForAllowances.LT_GEN to 6930L,
        RankTierForAllowances.MAJ_GEN to 6900L,
        RankTierForAllowances.BRIG_GEN to 6870L,
        RankTierForAllowances.COL to 6840L,
        RankTierForAllowances.LT_COL to 6810L,
        RankTierForAllowances.MAJ to 6780L,
        RankTierForAllowances.CAPT to 6750L,
        RankTierForAllowances.LT to 6720L,
        RankTierForAllowances.SECOND_LT to 6690L,
        RankTierForAllowances.HONY_CAPT to 6660L,
        RankTierForAllowances.HONY_LT to 6630L,
        RankTierForAllowances.MWO to 6600L,
        RankTierForAllowances.CHIEF_ARTIFICER to 6570L,
        RankTierForAllowances.SWO to 6540L,
        RankTierForAllowances.WO to 6510L
    )

    val orDefenceAllowance = mapOf(
        RankTierForAllowances.SGT to listOf(3240L, 3840L, 4560L, 5400L, 6480L),
        RankTierForAllowances.CPL to listOf(2280L, 2760L, 3360L, 4080L, 4800L),
        RankTierForAllowances.LCPL to listOf(2160L, 2640L, 3240L, 3840L, 4560L),
        RankTierForAllowances.SAINIK to listOf(1800L, 2160L, 2640L, 3000L, 3600L),
        RankTierForAllowances.NCE to listOf(1680L, 2040L, 2400L, 2880L, 3360L)
    )

    fun getDefenceServiceAllowance(
        tier: RankTierForAllowances,
        serviceBracket: ServiceBracket,
        isReligious: Boolean = false
    ): Long {
        if (tier == RankTierForAllowances.CADET || tier == RankTierForAllowances.RECRUIT || isReligious) {
            return 0L
        }
        officerDefenceAllowance[tier]?.let { return it }
        orDefenceAllowance[tier]?.let { list ->
            return list.getOrElse(serviceBracket.index) { 0L }
        }
        return 0L
    }

    // Old Defence Service Allowance (30 June 2026 / JSI 01/2016 rates from Gazette Pages 36, 52, 53)
    val oldOfficerDefenceAllowance = mapOf(
        RankTierForAllowances.GEN to 4800L,
        RankTierForAllowances.LT_GEN to 4620L,
        RankTierForAllowances.MAJ_GEN to 4600L,
        RankTierForAllowances.BRIG_GEN to 4580L,
        RankTierForAllowances.COL to 4560L,
        RankTierForAllowances.LT_COL to 4540L,
        RankTierForAllowances.MAJ to 4520L,
        RankTierForAllowances.CAPT to 4500L,
        RankTierForAllowances.LT to 4480L,
        RankTierForAllowances.SECOND_LT to 4460L,
        RankTierForAllowances.HONY_CAPT to 4440L,
        RankTierForAllowances.HONY_LT to 4420L,
        RankTierForAllowances.MWO to 4400L,
        RankTierForAllowances.CHIEF_ARTIFICER to 4380L,
        RankTierForAllowances.SWO to 4360L,
        RankTierForAllowances.WO to 4340L
    )

    val oldOrDefenceAllowance = mapOf(
        RankTierForAllowances.SGT to listOf(2700L, 3200L, 3800L, 4500L, 5400L),
        RankTierForAllowances.CPL to listOf(1900L, 2300L, 2800L, 3400L, 4000L),
        RankTierForAllowances.LCPL to listOf(1800L, 2200L, 2700L, 3200L, 3800L),
        RankTierForAllowances.SAINIK to listOf(1500L, 1800L, 2200L, 2500L, 3000L),
        RankTierForAllowances.NCE to listOf(1400L, 1700L, 2000L, 2400L, 2800L)
    )

    fun getOldDefenceServiceAllowance(
        tier: RankTierForAllowances,
        serviceBracket: ServiceBracket,
        isReligious: Boolean = false
    ): Long {
        if (tier == RankTierForAllowances.CADET || tier == RankTierForAllowances.RECRUIT || isReligious) {
            return 0L
        }
        oldOfficerDefenceAllowance[tier]?.let { return it }
        oldOrDefenceAllowance[tier]?.let { list ->
            return list.getOrElse(serviceBracket.index) { 0L }
        }
        return 0L
    }

    fun getHouseRentRate(
        isOfficer: Boolean,
        tier: RankTierForAllowances,
        city: CityCategory,
        accommodation: AccommodationType
    ): Double {
        if (tier == RankTierForAllowances.CADET || tier == RankTierForAllowances.RECRUIT) return 0.0

        if (isOfficer) {
            if (accommodation == AccommodationType.MARRIED_GOVT_QUARTERS) return 0.0
            val isLtColOrAbove = tier in setOf(
                RankTierForAllowances.LT_COL, RankTierForAllowances.COL,
                RankTierForAllowances.BRIG_GEN, RankTierForAllowances.MAJ_GEN,
                RankTierForAllowances.LT_GEN, RankTierForAllowances.GEN
            )
            val hasExtraFacility = accommodation == AccommodationType.NO_QUARTERS_EXTRA_FACILITY
            return when (city) {
                CityCategory.DHAKA -> when {
                    !isLtColOrAbove && !hasExtraFacility -> 0.45
                    !isLtColOrAbove && hasExtraFacility -> 0.40
                    isLtColOrAbove && !hasExtraFacility -> 0.40
                    else -> 0.35
                }
                CityCategory.OTHER_CITY_CORP -> when {
                    !isLtColOrAbove && !hasExtraFacility -> 0.35
                    !isLtColOrAbove && hasExtraFacility -> 0.30
                    isLtColOrAbove && !hasExtraFacility -> 0.30
                    else -> 0.25
                }
                CityCategory.OTHER_AREA -> when {
                    !isLtColOrAbove && !hasExtraFacility -> 0.30
                    !isLtColOrAbove && hasExtraFacility -> 0.25
                    isLtColOrAbove && !hasExtraFacility -> 0.25
                    else -> 0.20
                }
            }
        } else {
            val isSgtOrBelow = tier in setOf(
                RankTierForAllowances.SGT, RankTierForAllowances.CPL,
                RankTierForAllowances.LCPL, RankTierForAllowances.SAINIK, RankTierForAllowances.NCE
            )
            if (accommodation == AccommodationType.UNMARRIED_OR_MESS) {
                return 0.20
            }
            if (accommodation == AccommodationType.MARRIED_GOVT_QUARTERS) {
                return if (isSgtOrBelow) 0.20 else 0.0
            }
            return when (tier) {
                RankTierForAllowances.NCE, RankTierForAllowances.SAINIK -> when (city) {
                    CityCategory.DHAKA -> 0.60
                    CityCategory.OTHER_CITY_CORP -> 0.50
                    CityCategory.OTHER_AREA -> 0.45
                }
                RankTierForAllowances.LCPL, RankTierForAllowances.CPL, RankTierForAllowances.SGT -> when (city) {
                    CityCategory.DHAKA -> 0.50
                    CityCategory.OTHER_CITY_CORP -> 0.40
                    CityCategory.OTHER_AREA -> 0.35
                }
                else -> when (city) {
                    CityCategory.DHAKA -> 0.45
                    CityCategory.OTHER_CITY_CORP -> 0.35
                    CityCategory.OTHER_AREA -> 0.30
                }
            }
        }
    }

    // Exact Commutation Table from Paragraph 28 (Pages 83-84)
    val commutationTable: Map<Int, Double> = mapOf(
        20 to 29.932, 21 to 29.782, 22 to 29.520, 23 to 29.307, 24 to 29.091,
        25 to 28.870, 26 to 28.645, 27 to 28.414, 28 to 28.180, 29 to 27.940,
        30 to 27.695, 31 to 27.444, 32 to 27.189, 33 to 26.927, 34 to 26.660,
        35 to 26.387, 36 to 26.109, 37 to 25.824, 38 to 25.534, 39 to 25.237,
        40 to 24.934, 41 to 24.623, 42 to 24.308, 43 to 23.985, 44 to 23.655,
        45 to 23.317, 46 to 22.974, 47 to 22.623, 48 to 22.263, 49 to 21.898,
        50 to 21.526, 51 to 21.148, 52 to 20.763, 53 to 20.374, 54 to 19.980,
        55 to 19.582, 56 to 19.180, 57 to 18.776, 58 to 18.369, 59 to 17.961,
        60 to 17.553, 61 to 17.164, 62 to 16.771, 63 to 16.380, 64 to 15.994,
        65 to 15.613, 66 to 15.237, 67 to 14.867, 68 to 14.503, 69 to 14.145,
        70 to 13.794, 71 to 13.450, 72 to 13.115, 73 to 12.788, 74 to 12.469,
        75 to 12.161, 76 to 11.861, 77 to 11.573, 78 to 11.294, 79 to 11.027,
        80 to 10.771
    )

    val implementationStartMonths = listOf(
        "এখনো পাইনি (আজ পর্যন্ত হিসাব)" to -1,
        "জুলাই ২০২৬ (০ মাস বকেয়া)" to 0,
        "আগস্ট ২০২৬ (১ মাস)" to 1,
        "সেপ্টেম্বর ২০২৬ (২ মাস)" to 2,
        "অক্টোবর ২০২৬ (৩ মাস)" to 3,
        "নভেম্বর ২০২৬ (৪ মাস)" to 4,
        "ডিসেম্বর ২০২৬ (৫ মাস)" to 5,
        "জানুয়ারি ২০২৭ (৬ মাস)" to 6,
        "ফেব্রুয়ারি ২০২৭ (৭ মাস)" to 7,
        "মার্চ ২০২৭ (৮ মাস)" to 8,
        "এপ্রিল ২০২৭ (৯ মাস)" to 9,
        "মে ২০২৭ (১০ মাস)" to 10,
        "জুন ২০২৭ (১১ মাস)" to 11,
        "জুলাই ২০২৭ (১২ মাস)" to 12
    )

    fun build2015Steps(start: Long, count: Int, rate: Double = 0.05, maxCap: Long? = null): List<Long> {
        if (count <= 1 || rate <= 0.0) return listOf(start)
        val out = mutableListOf(start)
        for (i in 1 until count) {
            var next = (kotlin.math.ceil((out.last() * (1.0 + rate)) / 10.0) * 10.0).toLong()
            if (maxCap != null && next > maxCap) next = maxCap
            out.add(next)
            if (maxCap != null && next >= maxCap) break
        }
        return out
    }

    val nps2015Grades = listOf(
        PayScale2015Grade("AUTO", "পদবি অনুযায়ী স্কেল (JSI 01/2016 স্বয়ংক্রিয়)", 0L, 0.05, 19, null),
        PayScale2015Grade("GRADE_20", "গ্রেড-২০ (৮,২৫০ – ২০,০১০ ৳)", 8250L, 0.05, 19, 20010L),
        PayScale2015Grade("GRADE_19", "গ্রেড-১৯ (৮,৫০০ – ২০,৫৭০ ৳)", 8500L, 0.05, 19, 20570L),
        PayScale2015Grade("GRADE_18", "গ্রেড-১৮ (৮,৮০০ – ২১,৩১০ ৳) • এনসি(ই)", 8800L, 0.05, 19, 21310L),
        PayScale2015Grade("GRADE_17", "গ্রেড-১৭ (৯,০০০ – ২১,৮০০ ৳) • সৈনিক", 9000L, 0.05, 19, 21800L),
        PayScale2015Grade("GRADE_16", "গ্রেড-১৬ (৯,৩০০ – ২২,৪৯০ ৳)", 9300L, 0.05, 19, 22490L),
        PayScale2015Grade("GRADE_15", "গ্রেড-১৫ (৯,৭০০ – ২৩,৪৯০ ৳)", 9700L, 0.05, 19, 23490L),
        PayScale2015Grade("GRADE_14", "গ্রেড-১৪ (১০,২০০ – ২৪,৬৮০ ৳) • এলসিপিএল", 10200L, 0.05, 19, 24680L),
        PayScale2015Grade("GRADE_13", "গ্রেড-১৩ (১১,০০০ – ২৬,৫৯০ ৳) • কর্পোরাল", 11000L, 0.05, 19, 26590L),
        PayScale2015Grade("GRADE_12", "গ্রেড-১২ (১১,৩০০ – ২৭,৩০০ ৳)", 11300L, 0.05, 19, 27300L),
        PayScale2015Grade("GRADE_11", "গ্রেড-১১ (১২,৫০০ – ৩০,২৩০ ৳)", 12500L, 0.05, 19, 30230L),
        PayScale2015Grade("GRADE_10", "গ্রেড-১০ (১৬,০০০ – ৩৮,৬৪০ ৳) • সার্জেন্ট", 16000L, 0.05, 19, 38640L),
        PayScale2015Grade("GRADE_9", "গ্রেড-৯ (২২,০০০ – ৫৩,০৬০ ৳) • ডব্লিউও / ২য় লেঃ", 22000L, 0.05, 19, 53060L),
        PayScale2015Grade("GRADE_8", "গ্রেড-৮ (২৩,০০০ – ৫৫,৪৭০ ৳)", 23000L, 0.05, 19, 55470L),
        PayScale2015Grade("GRADE_7", "গ্রেড-৭ (২৯,০০০ – ৬৩,৪১০ ৳)", 29000L, 0.05, 17, 63410L),
        PayScale2015Grade("GRADE_6", "গ্রেড-৬ (৩৫,৫০০ – ৬৭,০১০ ৳)", 35500L, 0.05, 14, 67010L),
        PayScale2015Grade("GRADE_5", "গ্রেড-৫ (৪৩,০০০ – ৬৯,৮৫০ ৳)", 43000L, 0.045, 12, 69850L),
        PayScale2015Grade("GRADE_4", "গ্রেড-৪ (৫০,০০০ – ৭১,২০০ ৳)", 50000L, 0.04, 10, 71200L),
        PayScale2015Grade("GRADE_3", "গ্রেড-৩ (৫৬,৫০০ – ৭৪,৪০০ ৳)", 56500L, 0.04, 8, 74400L),
        PayScale2015Grade("GRADE_2", "গ্রেড-২ (৬৬,০০০ – ৭৬,৪৯০ ৳)", 66000L, 0.0375, 5, 76490L),
        PayScale2015Grade("GRADE_1", "গ্রেড-১ (৭৮,০০০ ৳ নির্ধারিত)", 78000L, 0.0, 1, 78000L)
    )

    fun get2015GradeContext(
        gradeId: String,
        category: PersonnelCategory,
        nonOfficerRankId: String,
        useBengaliDigits: Boolean
    ): Grade2015Context {
        val gradeObj = nps2015Grades.find { it.id == gradeId } ?: nps2015Grades.first()
        if (gradeObj.id == "AUTO") {
            if (category == PersonnelCategory.OFFICER) {
                val fallbackStart = 22000L
                return Grade2015Context(
                    grade = gradeObj,
                    displayLabel = "অফিসার স্কেল (২০১৫ পে স্কেল — গ্রেড ৯+)",
                    startPay = fallbackStart,
                    steps = build2015Steps(fallbackStart, 19, 0.05, 53060L)
                )
            }
            val rk = nonOfficerRanks.find { it.id == nonOfficerRankId } ?: nonOfficerRanks[6]
            val st = if (rk.oldScaleStart > 0L) rk.oldScaleStart else 16000L
            val steps = if (rk.isHonoraryFixed || rk.isRecruitFixed) listOf(st) else build2015Steps(st, 19, 0.05, null)
            val shortRank = rk.bnName.substringBefore(" /").trim()
            val formattedStart = com.example.util.BengaliFormat.formatNumber(st, useBengaliDigits)
            return Grade2015Context(
                grade = gradeObj,
                displayLabel = "পদবি অনুযায়ী ($shortRank: প্রারম্ভিক $formattedStart ৳)",
                startPay = st,
                steps = steps
            )
        }
        return Grade2015Context(
            grade = gradeObj,
            displayLabel = gradeObj.bnLabel,
            startPay = gradeObj.startPay,
            steps = build2015Steps(gradeObj.startPay, gradeObj.count, gradeObj.rate, gradeObj.maxCap)
        )
    }
}
