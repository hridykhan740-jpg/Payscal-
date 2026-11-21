package com.example.util

import kotlin.math.roundToLong

object BengaliFormat {
    private val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    fun toBengaliDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(bengaliDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toEnglishDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            val idx = bengaliDigits.indexOf(ch)
            if (idx >= 0) {
                sb.append(('0' + idx))
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Formats integer/long amount using Bangladeshi/South Asian comma grouping (e.g., 1,56,000).
     */
    fun formatIndianCommas(amount: Long): String {
        val negative = amount < 0
        val absStr = kotlin.math.abs(amount).toString()
        if (absStr.length <= 3) {
            return if (negative) "-$absStr" else absStr
        }
        val lastThree = absStr.takeLast(3)
        val rest = absStr.dropLast(3)
        val groupedRest = rest.reversed().chunked(2).joinToString(",").reversed()
        val res = "$groupedRest,$lastThree"
        return if (negative) "-$res" else res
    }

    fun formatBdt(amount: Double, useBengaliDigits: Boolean = true): String {
        val rounded = amount.roundToLong()
        val formatted = formatIndianCommas(rounded)
        return if (useBengaliDigits) {
            "${toBengaliDigits(formatted)} টাকা"
        } else {
            "$formatted BDT"
        }
    }

    fun formatNumber(value: Long, useBengaliDigits: Boolean = true): String {
        val formatted = formatIndianCommas(value)
        return if (useBengaliDigits) toBengaliDigits(formatted) else formatted
    }

    fun formatDecimal(value: Double, decimals: Int = 3, useBengaliDigits: Boolean = true): String {
        val formatted = String.format(java.util.Locale.US, "%.${decimals}f", value)
        return if (useBengaliDigits) toBengaliDigits(formatted) else formatted
    }

    fun parseSafeNumber(input: String): Long {
        val cleaned = toEnglishDigits(input).filter { it.isDigit() }
        return cleaned.toLongOrNull() ?: 0L
    }

    fun parseSafeDouble(input: String): Double {
        val cleaned = toEnglishDigits(input).filter { it.isDigit() || it == '.' }
        return cleaned.toDoubleOrNull() ?: 0.0
    }
}
