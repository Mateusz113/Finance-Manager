package com.mateusz113.financemanager.presentation.common.components

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.properties.Delegates

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class PhotoContainerTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private var wasPhotoClicked by Delegates.notNull<Boolean>()
    private var wasDeleteClicked by Delegates.notNull<Boolean>()
    private var isDeleteEnabled = true
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = composeRule.activity.applicationContext
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                PhotoContainer(
                    photo = "",
                    isDeleteEnabled = isDeleteEnabled,
                    onDeleteClick = { wasDeleteClicked = true },
                    onPhotoClick = { wasPhotoClicked = true }
                )
            }
        }
    }

    @Test
    fun deleteEnabled_deleteIconIsVisible() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.remove_photo))
            .assertIsDisplayed()
    }

    @Test
    fun deleteDisabled_deleteIconIsNotVisible() {
        isDeleteEnabled = false
        setUp()
        composeRule.onNodeWithContentDescription(context.getString(R.string.add_new_photo))
            .assertDoesNotExist()
    }

    @Test
    fun photoClicked_photoClickedFlagIsTrue() {
        composeRule.onRoot()
            .performClick()
        assertThat(wasPhotoClicked).isTrue()

    }

    @Test
    fun deleteClicked_deleteClickedFlagIsTrue() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.remove_photo))
            .performClick()
        assertThat(wasDeleteClicked).isTrue()
    }
}
