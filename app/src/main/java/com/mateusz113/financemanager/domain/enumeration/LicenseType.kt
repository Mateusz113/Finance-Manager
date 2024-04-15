package com.mateusz113.financemanager.domain.enumeration

import androidx.annotation.StringRes
import com.mateusz113.financemanager.R

enum class LicenseType(
    @StringRes val label: Int,
    @StringRes val licenseTextParts: List<Int>
) {
    APACHE2(R.string.apache_2, listOf(R.string.apache_license_text)),
    META_LICENSE(R.string.meta_license, listOf(R.string.meta_license_text_part_1, R.string.meta_license_text_part_2)),
    ECLIPSE_PUBLIC_LICENSE(R.string.eclipse_license, listOf(R.string.eclipse_license_text))
}
