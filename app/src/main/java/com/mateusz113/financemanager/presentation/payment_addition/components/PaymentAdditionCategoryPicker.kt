package com.mateusz113.financemanager.presentation.payment_addition.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.mateusz113.financemanager.presentation.common.SingleOptionButtonSpinner

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
        Spacer(modifier = Modifier.height(10.dp))

        SingleOptionButtonSpinner(
            options = Category.values().toList(),
            selectedItem = category,
            modifier = Modifier.fillMaxWidth(0.4f).height(56.dp),
            selectedOption = { category ->
                categoryChange(category)
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}