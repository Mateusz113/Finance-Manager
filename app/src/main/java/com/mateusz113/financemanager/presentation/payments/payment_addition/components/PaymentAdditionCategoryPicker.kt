package com.mateusz113.financemanager.presentation.payments.payment_addition.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R
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
        Text(
            text = stringResource(id = R.string.category),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        SingleOptionButtonSpinner(
            options = Category.values().toList(),
            selectedItem = category,
            modifier = Modifier
                .width(160.dp)
                .height(56.dp)
                .padding(start = 10.dp),
            selectedOption = { category ->
                categoryChange(category)
            }
        )
    }
}