package com.mateusz113.financemanager.presentation.payments.payment_listings

import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.presentation.common.dialog.radio_buttons_dialog.RadioButtonsDialogInfo
import com.mateusz113.financemanager.util.Currency
import com.mateusz113.financemanager.util.SortingMethod

data class PaymentListingsState(
    val payments: List<PaymentListing> = emptyList(),
    val currency: Currency = Currency.PLN,
    val isCurrencyPrefix: Boolean? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isFilterDialogOpen: Boolean = false,
    val filterSettings: FilterSettings = FilterSettings(),
    val isDeleteDialogOpen: Boolean = false,
    val deleteDialogPaymentTitle: String = "",
    val deleteDialogPaymentId: String = "",
    val isSortingMethodDialogOpen: Boolean = false,
    val sortingSettingsInfo: RadioButtonsDialogInfo<SortingMethod> = RadioButtonsDialogInfo(
        label = R.string.sorting_method,
        currentOption = SortingMethod.Alphabetically,
        optionsLabelsMap = SortingMethod.labelMap,
        optionsList = SortingMethod.values().toList()
    )
)
