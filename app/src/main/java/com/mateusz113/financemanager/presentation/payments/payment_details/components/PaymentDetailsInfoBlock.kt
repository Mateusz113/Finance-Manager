package com.mateusz113.financemanager.presentation.payments.payment_details.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.model.PaymentDetails
import com.mateusz113.financemanager.util.Currency
import java.time.format.DateTimeFormatter

@Composable
fun PaymentDetailsInfoBlock(
    paymentDetails: PaymentDetails,
    currency: Currency,
    isCurrencyPrefix: Boolean?,
    modifier: Modifier = Modifier
) {
    val formattedDate = remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd/MM/yyyy")
                .format(paymentDetails.date)
        }
    }

    val amount by remember {
        mutableStateOf(
            buildString {
                if (isCurrencyPrefix == true || (isCurrencyPrefix == null && currency.isPrefix)) {
                    append("${currency.symbol ?: currency.name} ")
                }
                append(paymentDetails.amount)
                if (isCurrencyPrefix == false || (isCurrencyPrefix == null && !currency.isPrefix)) {
                    append(" ${currency.symbol ?: currency.name}")
                }
            })
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        InfoElement(
            headlineText = stringResource(id = R.string.title),
            infoText = paymentDetails.title
        )

        InfoElement(
            headlineText = stringResource(id = R.string.description),
            infoText = paymentDetails.description
        )

        InfoElement(
            headlineText = stringResource(id = R.string.amount),
            infoText = amount
        )

        InfoElement(
            headlineText = stringResource(id = R.string.date),
            infoText = formattedDate.value
        )

        InfoElement(
            headlineText = stringResource(id = R.string.category),
            infoText = paymentDetails.category.name
        )
    }
}

@Composable
private fun InfoElement(
    headlineText: String,
    infoText: String
) {
    val headlineTextStyle = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
        color = MaterialTheme.colorScheme.onBackground
    )

    val infoTextStyle = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = MaterialTheme.typography.titleLarge.fontSize
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = headlineText,
            style = headlineTextStyle
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = infoText,
            style = infoTextStyle,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))
    }
}
