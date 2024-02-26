package com.mateusz113.financemanager.presentation.payments.payment_addition.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.presentation.common.option_picker.SingleOptionButtonSpinner

@Composable
fun PaymentAdditionCategoryPicker(
    modifier: Modifier = Modifier,
    category: Category,
    categoryChange: (Category) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SingleOptionButtonSpinner(
            options = Category.values().toList(),
            selectedOption = category,
            modifier = Modifier
                .width(160.dp)
                .height(56.dp),
            onOptionSelect = { category ->
                categoryChange(category)
            }
        )
    }
}
