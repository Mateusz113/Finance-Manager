package com.mateusz113.financemanager.util

import com.mateusz113.financemanager.R

enum class SymbolPlacement {
    InAppControl,
    Prefix,
    Suffix;

    companion object {
        val labelMap = mapOf(
            InAppControl to R.string.sp_inappcontrol,
            Prefix to R.string.sp_prefix,
            Suffix to R.string.sp_suffix
        )
    }
}
