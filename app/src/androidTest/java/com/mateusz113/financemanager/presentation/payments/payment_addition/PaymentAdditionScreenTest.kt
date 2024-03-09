package com.mateusz113.financemanager.presentation.payments.payment_addition

import android.content.Context
import android.net.Uri
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.AnnotatedString
import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.MainActivity
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.di.RepositoryModule
import com.mateusz113.financemanager.di.SharedPreferencesModule
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.validator.PaymentInfoValidator
import com.mateusz113.financemanager.util.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import kotlin.properties.Delegates

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, SharedPreferencesModule::class)
class PaymentAdditionScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val correctTitleInput: String = "Title"
    private val correctDescriptionInput: String = "Description"
    private val correctAmountInput: String = "15"
    private val initialDate: LocalDate = LocalDate.of(2024, 3, 8)
    private val initialCategory: Category = Category.Housing
    private val emptyUri = Uri.parse("")

    private lateinit var state: MutableState<PaymentAdditionState>
    private lateinit var context: Context
    private lateinit var snackbarHostState: SnackbarHostState
    private var isTitleValid by Delegates.notNull<Boolean>()
    private var isDescriptionValid by Delegates.notNull<Boolean>()
    private var isAmountValid by Delegates.notNull<Boolean>()
    private var isPhotoQuantityValid by Delegates.notNull<Boolean>()

    @Before
    fun setUp() {
        hiltRule.inject()
        state = mutableStateOf(
            PaymentAdditionState(
                date = initialDate,
                category = initialCategory
            )
        )
        context = composeRule.activity.applicationContext
        snackbarHostState = SnackbarHostState()
        isTitleValid = false
        isDescriptionValid = false
        isAmountValid = false
        isPhotoQuantityValid = false


        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                PaymentAdditionScreenContent(
                    state = state.value,
                    topBarLabel = R.string.add_new_payment,
                    snackbarHostState = snackbarHostState,
                    onTitleChange = {
                        state.value = state.value.copy(title = it)
                    },
                    onDescriptionChange = {
                        state.value = state.value.copy(description = it)
                    },
                    onAmountChange = {
                        state.value = state.value.copy(amount = it)
                    },
                    onCategoryChange = {
                        state.value = state.value.copy(category = it)
                    },
                    onDateChange = {
                        state.value = state.value.copy(date = it)
                    },
                    onUploadedPhotoDelete = { deletedPhoto ->
                        handlePhotoDelete(
                            snackbarHostState = snackbarHostState,
                            newPhotosSize = state.value.newPhotos.size,
                            uploadedPhotosSize = state.value.uploadedPhotos.size,
                            deleteImplementation = {
                                state.value = state.value.copy(lastDeletedUrlPhoto = deletedPhoto)
                                val uploadedPhotos = state.value.uploadedPhotos.toMutableList()
                                val deletedPhotos = state.value.deletedPhotos.toMutableList()
                                uploadedPhotos.remove(deletedPhoto)
                                deletedPhotos.add(deletedPhoto)
                                state.value = state.value.copy(uploadedPhotos = uploadedPhotos)
                                state.value = state.value.copy(deletedPhotos = deletedPhotos)
                            },
                            retrieveImplementation = {
                                state.value = state.value.copy(lastDeletedUrlPhoto = null)
                                val uploadedPhotos = state.value.uploadedPhotos.toMutableList()
                                val deletedPhotos = state.value.deletedPhotos.toMutableList()
                                uploadedPhotos.add(deletedPhoto)
                                deletedPhotos.remove(deletedPhoto)
                                state.value = state.value.copy(uploadedPhotos = uploadedPhotos)
                                state.value = state.value.copy(deletedPhotos = deletedPhotos)
                            }
                        )
                    },
                    onNewPhotoDelete = { deletedPhoto ->
                        handlePhotoDelete(
                            snackbarHostState = snackbarHostState,
                            newPhotosSize = state.value.newPhotos.size,
                            uploadedPhotosSize = state.value.uploadedPhotos.size,
                            deleteImplementation = {
                                state.value = state.value.copy(lastDeletedUriPhoto = deletedPhoto)
                                val newPhotos = state.value.newPhotos.toMutableList()
                                newPhotos.remove(deletedPhoto)
                                state.value = state.value.copy(newPhotos = newPhotos)
                            },
                            retrieveImplementation = {
                                state.value = state.value.copy(lastDeletedUriPhoto = null)
                                val newPhotos = state.value.newPhotos.toMutableList()
                                newPhotos.add(deletedPhoto)
                                state.value = state.value.copy(newPhotos = newPhotos)
                            }
                        )
                    },
                    onPhotoAddClick = {
                        //Adds example URI to simulate user picking an image from image picker
                        val newPhotos = state.value.newPhotos.toMutableList()
                        newPhotos.add(emptyUri)
                        state.value = state.value.copy(newPhotos = newPhotos)
                    },
                    onPhotoClick = {
                        state.value = state.value.copy(
                            isPhotoDialogOpen = true,
                            dialogPhoto = it
                        )
                    },
                    onConfirmClick = {
                        if (PaymentInfoValidator.titleValidator(state.value.title)) {
                            isTitleValid = true
                        }
                        if (PaymentInfoValidator.descriptionValidator(state.value.description)) {
                            isDescriptionValid = true
                        }
                        if (PaymentInfoValidator.amountValidator(state.value.amount)) {
                            isAmountValid = true
                        }
                        if (PaymentInfoValidator.photoQuantityValidator(
                                state.value.uploadedPhotos.size,
                                state.value.newPhotos.size
                            )
                        ) {
                            isPhotoQuantityValid = true
                        }
                    },
                    onPhotoDialogDismiss = {
                        state.value = state.value.copy(isPhotoDialogOpen = false)
                    }
                )
            }
        }
    }

    private fun handlePhotoDelete(
        snackbarHostState: SnackbarHostState,
        newPhotosSize: Int,
        uploadedPhotosSize: Int,
        deleteImplementation: () -> Unit,
        retrieveImplementation: () -> Unit
    ) {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        deleteImplementation()
        coroutineScope.launch {
            val result = snackbarHostState
                .showSnackbar(
                    message = context.getString(R.string.deleted_picture),
                    actionLabel = context.getString(R.string.undo),
                    duration = SnackbarDuration.Short
                )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    //The +1 check is taking care if the photo can be retrieved from deletion without breaking the max 5 photos limit
                    if (PaymentInfoValidator.photoQuantityValidator(
                            newPhotosSize + 1,
                            uploadedPhotosSize
                        )
                    ) {
                        retrieveImplementation()
                    } else {
                        snackbarHostState
                            .showSnackbar(
                                message = context.getString(R.string.too_many_pictures),
                                duration = SnackbarDuration.Short
                            )
                    }
                }

                SnackbarResult.Dismissed -> {}
            }
        }
    }

    @Test
    fun insertValueIntoTitleField_titleInStateIsUpdated() {
        composeRule.onNodeWithText(context.getString(R.string.title))
            .performTextInput(correctTitleInput)
        assertThat(state.value.title).contains(correctTitleInput)
    }

    @Test
    fun insertValueIntoDescriptionField_descriptionInStateIsUpdated() {
        composeRule.onNodeWithText(context.getString(R.string.description))
            .performTextInput(correctDescriptionInput)
        assertThat(state.value.description).contains(correctDescriptionInput)

    }

    @Test
    fun insertValueIntoAmountField_amountInStateIsUpdated() {
        composeRule.onNodeWithText(context.getString(R.string.amount))
            .performTextInput(correctAmountInput)
        assertThat(state.value.amount).contains(correctAmountInput)
    }

    @Test
    fun insertCorrectTitleAndPressConfirm_titleIsValidatedAsCorrect() {
        composeRule.onNodeWithText(context.getString(R.string.title))
            .performTextInput(correctTitleInput)
        composeRule.onNodeWithText(context.getString(R.string.confirm)).performClick()
        assertThat(isTitleValid).isTrue()
    }

    @Test
    fun insertCorrectDescriptionAndPressConfirm_descriptionIsValidatedAsCorrect() {
        composeRule.onNodeWithText(context.getString(R.string.description))
            .performTextInput(correctDescriptionInput)
        composeRule.onNodeWithText(context.getString(R.string.confirm)).performClick()
        assertThat(isDescriptionValid).isTrue()
    }

    @Test
    fun insertCorrectAmountAndPressConfirm_amountIsValidatedAsCorrect() {
        composeRule.onNodeWithText(context.getString(R.string.amount))
            .performTextInput(correctAmountInput)
        composeRule.onNodeWithText(context.getString(R.string.confirm)).performClick()
        assertThat(isAmountValid).isTrue()
    }

    @Test
    fun insertInvalidTitleAndPressConfirm_titleIsValidatedAsIncorrect() {
        val invalidTitle = correctTitleInput.repeat(50)
        composeRule.onNodeWithText(context.getString(R.string.title))
            .performTextInput(invalidTitle)
        composeRule.onNodeWithText(context.getString(R.string.confirm)).performClick()
        assertThat(isTitleValid).isFalse()
    }

    @Test
    fun insertInvalidDescriptionAndPressConfirm_descriptionIsValidatedAsIncorrect() {
        val invalidDescription = correctDescriptionInput.repeat(100)
        composeRule.onNodeWithText(context.getString(R.string.description))
            .performTextInput(invalidDescription)
        composeRule.onNodeWithText(context.getString(R.string.confirm)).performClick()
        assertThat(isDescriptionValid).isFalse()
    }

    @Test
    fun insertInvalidAmountAndPressConfirm_amountIsValidatedAsIncorrect() {
        val invalidAmount = correctAmountInput.repeat(10)
        composeRule.onNodeWithText(context.getString(R.string.amount))
            .performTextInput(invalidAmount)
        composeRule.onNodeWithText(context.getString(R.string.confirm)).performClick()
        assertThat(isAmountValid).isFalse()
    }

    @Test
    fun leaveTextFieldsBlankAndPressConfirm_theirValuesAreValidatedAsIncorrect() {
        composeRule.onNodeWithText(context.getString(R.string.confirm)).performClick()
        assertThat(isTitleValid).isFalse()
        assertThat(isDescriptionValid).isFalse()
        assertThat(isAmountValid).isFalse()
    }

    @Test
    fun changeCategoryWithSpinner_categoryIsUpdatedCorrectly() {
        val categories = Category.values().toMutableList()
        categories.remove(initialCategory)
        val randomCategory = categories.random()
        composeRule.onNodeWithContentDescription(context.getString(R.string.option_selection_dropdown))
            .performClick()
        val menu = composeRule.onNodeWithTag(TestTags.DROPDOWN_MENU)
        menu.performScrollToNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.Text, listOf(
                    AnnotatedString(randomCategory.name)
                )
            )
        )
        composeRule.onNodeWithText(randomCategory.name).performClick()

        assertThat(randomCategory).isNotEqualTo(initialCategory)
        assertThat(state.value.category).isEqualTo(randomCategory)
    }

    @Test
    fun changeDateWithPicker_dateIsUpdatedCorrectly() {
        val newDate = state.value.date.minusDays(1)
        composeRule.onNodeWithContentDescription(context.getString(R.string.date_picker))
            .performClick()
        composeRule.onNodeWithTag("dialog_date_selection_${newDate.dayOfMonth}").performClick()
        composeRule.onNodeWithText("APPLY").performClick()

        assertThat(newDate).isNotEqualTo(initialDate)
        assertThat(state.value.date).isEqualTo(newDate)
    }

    @Test
    fun clickOnPhotoAddIcon_newPhotosIsNotEmpty() {
        composeRule.onNodeWithContentDescription(context.getString(R.string.add_new_photo))
            .performClick()
        assertThat(state.value.newPhotos.isNotEmpty()).isTrue()
    }

    @Test
    fun addFivePhotos_addIconIsNotLongerVisible() {
        val newPhotosUpdated = state.value.newPhotos.toMutableList()
        repeat(5) {
            newPhotosUpdated.add(emptyUri)
        }
        state.value = state.value.copy(newPhotos = newPhotosUpdated)
        composeRule.onNodeWithContentDescription(context.getString(R.string.add_new_photo))
            .assertIsNotDisplayed()
    }

    @Test
    fun addBlankUriPhotoReplacement_deleteWorksWithSimulatedUriPhoto() {
        val newPhotosUpdated = state.value.newPhotos.toMutableList()
        newPhotosUpdated.add(emptyUri)
        state.value = state.value.copy(newPhotos = newPhotosUpdated)
        composeRule.onNodeWithContentDescription(context.getString(R.string.remove_photo))
            .performClick()
        assertThat(state.value.newPhotos).isEmpty()
        assertThat(state.value.lastDeletedUriPhoto).isEqualTo(emptyUri)
    }

    @Test
    fun addEmptyStringPhotoReplacement_deleteWorksWithSimulatedStringPhoto() {
        val uploadedPhotosUpdated = state.value.uploadedPhotos.toMutableList()
        uploadedPhotosUpdated.add("")
        state.value = state.value.copy(uploadedPhotos = uploadedPhotosUpdated)
        composeRule.onNodeWithContentDescription(context.getString(R.string.remove_photo))
            .performClick()
        assertThat(state.value.uploadedPhotos).isEmpty()
        assertThat(state.value.lastDeletedUrlPhoto).isEqualTo("")
    }

    @Test
    fun clickOnStringPhoto_dialogWithPhotoIsVisible() {
        val uploadedPhotosUpdated = state.value.uploadedPhotos.toMutableList()
        uploadedPhotosUpdated.add("")
        state.value = state.value.copy(uploadedPhotos = uploadedPhotosUpdated)
        composeRule.onNodeWithTag(TestTags.PHOTO).performClick()
        composeRule.onNodeWithContentDescription(context.getString(R.string.photo))
            .assertIsDisplayed()
        assertThat(state.value.dialogPhoto).isEqualTo("")
    }

    @Test
    fun clickOnUriPhoto_dialogWithPhotoIsVisible() {
        val newPhotosUpdated = state.value.newPhotos.toMutableList()
        newPhotosUpdated.add(emptyUri)
        state.value = state.value.copy(newPhotos = newPhotosUpdated)
        composeRule.onNodeWithTag(TestTags.PHOTO).performClick()
        composeRule.onNodeWithContentDescription(context.getString(R.string.photo))
            .assertIsDisplayed()
        assertThat(state.value.dialogPhoto).isEqualTo(emptyUri)
    }

    @Test
    fun clickOnDeleteStringPhoto_snackbarIsShownAndStateValueIsUpdated() {
        val uploadedPhotosUpdated = state.value.uploadedPhotos.toMutableList()
        uploadedPhotosUpdated.add("")
        state.value = state.value.copy(uploadedPhotos = uploadedPhotosUpdated)
        composeRule.onNodeWithContentDescription(context.getString(R.string.remove_photo))
            .performClick()
        composeRule.onNodeWithText(context.getString(R.string.deleted_picture)).assertIsDisplayed()

        assertThat(state.value.lastDeletedUrlPhoto).isEqualTo("")
        assertThat(state.value.uploadedPhotos).isEmpty()
        assertThat(state.value.deletedPhotos).contains("")
    }

    @Test
    fun clickOnDeleteUriPhoto_snackbarIsShownAndStateValueIsUpdated() {
        val newPhotosUpdated = state.value.newPhotos.toMutableList()
        newPhotosUpdated.add(emptyUri)
        state.value = state.value.copy(newPhotos = newPhotosUpdated)
        composeRule.onNodeWithContentDescription(context.getString(R.string.remove_photo))
            .performClick()
        composeRule.onNodeWithText(context.getString(R.string.deleted_picture)).assertIsDisplayed()

        assertThat(state.value.lastDeletedUriPhoto).isEqualTo(emptyUri)
        assertThat(state.value.newPhotos).isEmpty()
    }

    @Test
    fun clickOnDeleteStringPhotoAndUndoIt_stateValuesAreUpdated() {
        val uploadedPhotosUpdated = state.value.uploadedPhotos.toMutableList()
        uploadedPhotosUpdated.add("")
        state.value = state.value.copy(uploadedPhotos = uploadedPhotosUpdated)
        composeRule.onNodeWithContentDescription(context.getString(R.string.remove_photo))
            .performClick()
        composeRule.onNodeWithText(context.getString(R.string.undo)).performClick()
        composeRule.onNodeWithTag(TestTags.PHOTO).assertIsDisplayed()

        assertThat(state.value.lastDeletedUrlPhoto).isEqualTo(null)
        assertThat(state.value.uploadedPhotos).contains("")
        assertThat(state.value.deletedPhotos).isEmpty()
    }

    @Test
    fun clickOnDeleteUriPhotoAndUndoIt_stateValuesAreUpdated() {
        val newPhotosUpdated = state.value.newPhotos.toMutableList()
        newPhotosUpdated.add(emptyUri)
        state.value = state.value.copy(newPhotos = newPhotosUpdated)
        composeRule.onNodeWithContentDescription(context.getString(R.string.remove_photo))
            .performClick()
        composeRule.onNodeWithText(context.getString(R.string.undo)).performClick()
        composeRule.onNodeWithTag(TestTags.PHOTO).assertIsDisplayed()

        assertThat(state.value.lastDeletedUriPhoto).isEqualTo(null)
        assertThat(state.value.newPhotos).contains(emptyUri)
    }

    @Test
    fun insertAllValidValuesAndClickConfirm_correctlyValidatesValues() {
        composeRule.onNodeWithText(context.getString(R.string.title))
            .performTextInput(correctTitleInput)
        composeRule.onNodeWithText(context.getString(R.string.description))
            .performTextInput(correctDescriptionInput)
        composeRule.onNodeWithText(context.getString(R.string.amount))
            .performTextInput(correctAmountInput)
        composeRule.onNodeWithText(context.getString(R.string.confirm)).performClick()
        assertThat(
            isTitleValid &&
                    isDescriptionValid &&
                    isAmountValid &&
                    isPhotoQuantityValid
        ).isTrue()
    }
}
