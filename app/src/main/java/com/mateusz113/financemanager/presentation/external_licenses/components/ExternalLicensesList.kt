package com.mateusz113.financemanager.presentation.external_licenses.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.mateusz113.financemanager.domain.enumeration.ExternalLicense
import com.mateusz113.financemanager.domain.enumeration.LicenseType
import com.mateusz113.financemanager.presentation.common.components.ClickableItemRow

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.externalLicensesList(
    license: LicenseType,
    externalLicensesMap: Map<LicenseType, List<ExternalLicense>>,
    onLicenseClick: (Int) -> Unit,
) {
    this.stickyHeader {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            ClickableItemRow(
                label = stringResource(id = license.label),
                onClick = {
                    onLicenseClick(license.licenseText)
                }
            )
        }
    }
    externalLicensesMap[license]?.forEach {
        this.item {
            Text(
                modifier = Modifier.fillMaxWidth(0.95f),
                text = "• ${stringResource(id = it.label)} - ${stringResource(id = it.copyright)}",
                textAlign = TextAlign.Start
            )
        }
    }
}
