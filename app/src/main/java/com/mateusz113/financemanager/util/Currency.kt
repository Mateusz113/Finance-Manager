package com.mateusz113.financemanager.util

import com.mateusz113.financemanager.R

enum class Currency(
    val isPrefix: Boolean,
    val symbol: String?
) {
    PLN(false, "zł"),
    USD(true, "$"),
    EUR(true, "€"),
    JPY(true, "¥"),
    GBP(true, "£"),
    AUD(true, "A$"),
    CAD(true, "CA$"),
    CHF(true, "CHF");

    companion object {
        val labelMap = mapOf(
            PLN to R.string.curr_pln,
            USD to R.string.curr_usd,
            EUR to R.string.curr_eur,
            JPY to R.string.curr_jpy,
            GBP to R.string.curr_gbp,
            AUD to R.string.curr_aud,
            CAD to R.string.curr_cad,
            CHF to R.string.curr_chf
        )
    }
}
