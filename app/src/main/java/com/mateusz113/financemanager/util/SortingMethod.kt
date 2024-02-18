package com.mateusz113.financemanager.util

import androidx.annotation.StringRes
import com.mateusz113.financemanager.R

enum class SortingMethod {
    Alphabetically, AmountAscending, AmountDescending, OldToNew, NewToOld;

    companion object {
        val labelMap = mapOf(
            Alphabetically to R.string.alphabetically,
            AmountAscending to R.string.amount_ascending,
            AmountDescending to R.string.amount_descending,
            OldToNew to R.string.old_to_new,
            NewToOld to R.string.new_to_old
        )
    }
}