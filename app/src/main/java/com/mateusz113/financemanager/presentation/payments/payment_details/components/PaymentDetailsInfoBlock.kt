package com.mateusz113.financemanager.presentation.payments.payment_details.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.PaymentDetails
import com.mateusz113.financemanager.ui.theme.FinanceManagerTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PaymentDetailsInfoBlock(
    paymentDetails: PaymentDetails,
    modifier: Modifier = Modifier
) {
    val formattedDate = remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd/MM/yyyy")
                .format(paymentDetails.date)
        }
    }
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.title),
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = paymentDetails.title,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            ),
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.description),
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = paymentDetails.description,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            ),
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.amount),
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = paymentDetails.amount.toString(),
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            ),
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.date),
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formattedDate.value,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            ),
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.category),
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = paymentDetails.category.name,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            ),
            modifier = Modifier.padding(horizontal = 10.dp)
        )
    }
}