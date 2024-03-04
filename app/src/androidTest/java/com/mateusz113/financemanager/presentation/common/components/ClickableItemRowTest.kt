package com.mateusz113.financemanager.presentation.common.components

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.presentation.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.properties.Delegates

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class ClickableItemRowTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var rowLabel: String
    private var wasClicked by Delegates.notNull<Boolean>()

    @Before
    fun setUp() {
        rowLabel = "Row label"
        wasClicked = false
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                DestinationsNavHost(navGraph = NavGraphs.root)
                ClickableItemRow(
                    label = rowLabel,
                    onClick = {
                        wasClicked = true
                    }
                )
            }
        }
    }

    @Test
    fun clickOnRow_wasClickedFlagIsTrue() {
        composeRule.onNodeWithText(rowLabel).performClick()
        assertThat(wasClicked).isTrue()
    }
}
