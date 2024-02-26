package com.mateusz113.financemanager.presentation.spendings.spending_details.components

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.model.Category

object ChartColors {
    fun getColorFromCategory(
        context: Context,
        category: Category
    ): Color {
        return when (category) {
            Category.Housing -> Color(context.getColor(R.color.housing))
            Category.Transportation -> Color(context.getColor(R.color.transportation))
            Category.Groceries -> Color(context.getColor(R.color.groceries))
            Category.Health -> Color(context.getColor(R.color.health))
            Category.Entertainment -> Color(context.getColor(R.color.entertainment))
            Category.Utilities -> Color(context.getColor(R.color.utilities))
            Category.Personal -> Color(context.getColor(R.color.personal))
            Category.Savings -> Color(context.getColor(R.color.savings))
            Category.Education -> Color(context.getColor(R.color.education))
        }
    }
}
