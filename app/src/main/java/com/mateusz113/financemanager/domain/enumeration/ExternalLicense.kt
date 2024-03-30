package com.mateusz113.financemanager.domain.enumeration

import androidx.annotation.StringRes
import com.mateusz113.financemanager.R

enum class ExternalLicense(
    @StringRes val label: Int,
    @StringRes val licenseText: Int
) {
    YCharts(
        R.string.ycharts_label,
        R.string.ycharts_license_text
    )
}
