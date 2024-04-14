package com.mateusz113.financemanager.domain.enumeration

import androidx.annotation.StringRes
import com.mateusz113.financemanager.R

enum class ExternalLicense(
    @StringRes val label: Int,
    @StringRes val copyright: Int,
    val licenseType: LicenseType
) {
    ANDROIDX_CORE_KTX(
        R.string.androidx_core_ktx, R.string.copyright_android_open_source_22, LicenseType.APACHE2
    ),
    ANDROIDX_LIFECYCLE_RUNTIME_KTX(
        R.string.androidx_lifecycle_runtime_ktx,
        R.string.copyright_android_open_source_20,
        LicenseType.APACHE2
    ),
    ANDROIDX_ACTIVITY_COMPOSE(
        R.string.androidx_activity_compose,
        R.string.copyright_android_open_source_22,
        LicenseType.APACHE2
    ),
    ANDROIDX_LIFECYCLE_RUNTIME_COMPOSE(
        R.string.androidx_lifecycle_runtime_compose,
        R.string.copyright_android_open_source_22,
        LicenseType.APACHE2
    ),
    ANDROIDX_CONSTRAINT_LAYOUT_COMPOSE(
        R.string.androidx_constraint_layout_compose,
        R.string.copyright_android_open_source_22,
        LicenseType.APACHE2
    ),
    ANDROIDX_COMPOSE_MATERIAL(
        R.string.androidx_compose_material,
        R.string.copyright_android_open_source_22,
        LicenseType.APACHE2
    ),
    ANDROIDX_COMPOSE_UI(
        R.string.androidx_compose_ui, R.string.copyright_android_open_source_22, LicenseType.APACHE2
    ),
    ANDROIDX_COMPOSE_UI_GRAPHICS(
        R.string.androidx_compose_ui_graphics,
        R.string.copyright_android_open_source_22,
        LicenseType.APACHE2
    ),
    ANDROIDX_COMPOSE_UI_TOOLING_PREVIEW(
        R.string.androidx_compose_ui_tooling_preview,
        R.string.copyright_android_open_source_22,
        LicenseType.APACHE2
    ),
    ANDROIDX_COMPOSE_MATERIAL3(
        R.string.androidx_compose_material3,
        R.string.copyright_android_open_source_22,
        LicenseType.APACHE2
    ),
    ANDROIDX_COMPOSE_UI_TOOLING(
        R.string.androidx_compose_ui_tooling,
        R.string.copyright_android_open_source_22,
        LicenseType.APACHE2
    ),
    ANDROIDX_COMPOSE_UI_TEST_MANIFEST(
        R.string.androidx_compose_ui_test_manifest,
        R.string.copyright_android_open_source_22,
        LicenseType.APACHE2
    ),
    ANDROIDX_COMPOSE_UI_TEST_JUNIT4(
        R.string.androidx_compose_ui_test_junit4,
        R.string.copyright_android_open_source_22,
        LicenseType.APACHE2
    ),
    FIREBASE_AUTH(
        R.string.firebase_auth,
        R.string.copyright_google_22,
        LicenseType.APACHE2
    ),
    FIREBASE_DATABASE(
        R.string.firebase_database, R.string.copyright_google_22, LicenseType.APACHE2
    ),
    FIREBASE_STORAGE(
        R.string.firebase_storage,
        R.string.copyright_google_22,
        LicenseType.APACHE2
    ),
    PLAY_SERVICES_AUTH(
        R.string.play_services_auth, R.string.copyright_google_22, LicenseType.APACHE2
    ),
    FACEBOOK_ANDROID_SDK(
        R.string.facebook_android_sdk, R.string.copyright_facebook, LicenseType.FACEBOOK_LICENSE
    ),
    DAGGER_HILT_ANDROID(
        R.string.dagger_hilt_android, R.string.copyright_dagger_authors_16, LicenseType.APACHE2
    ),
    ANDROIDX_HILT_COMPILER(
        R.string.androidx_hilt_compiler, R.string.copyright_google_22, LicenseType.APACHE2
    ),
    ANDROIDX_HILT_NAVIGATION_COMPOSE(
        R.string.androidx_hilt_navigation_compose, R.string.copyright_google_22, LicenseType.APACHE2
    ),
    ANDROIDX_LIFECYCLE_VIEWMODEL_COMPOSE(
        R.string.androidx_lifecycle_viewmodel_compose,
        R.string.copyright_android_open_source_22,
        LicenseType.APACHE2
    ),
    ANDROIDX_COMPOSE_MATERIAL_ICONS_EXTENDED(
        R.string.androidx_compose_material_icons_extended,
        R.string.copyright_android_open_source_22,
        LicenseType.APACHE2
    ),
    COMPOSE_DESTINATIONS_CORE(
        R.string.compose_destinations_core, R.string.copyright_rafael_costa_21, LicenseType.APACHE2
    ),
    COMPOSE_DESTINATIONS_KSP(
        R.string.compose_destinations_ksp, R.string.copyright_rafael_costa_21, LicenseType.APACHE2
    ),
    COMPOSE_MATERIAL_DIALOGS_DATETIME(
        R.string.compose_material_dialogs_datetime,
        R.string.copyright_henri_van_pra_22,
        LicenseType.APACHE2
    ),
    RETROFIT(
        R.string.retrofit,
        R.string.copyright_square_20,
        LicenseType.APACHE2
    ),
    RETROFIT_CONVERTER_MOSHI(
        R.string.retrofit_converter_moshi, R.string.copyright_square_20, LicenseType.APACHE2
    ),
    OKHTTP(
        R.string.okhttp,
        R.string.copyright_square_20,
        LicenseType.APACHE2
    ),
    OKHTTP_LOGGING_INTERCEPTOR(
        R.string.okhttp_logging_interceptor, R.string.copyright_square_20, LicenseType.APACHE2
    ),
    COIL_COMPOSE(
        R.string.coil_compose,
        R.string.copyright_coil_22,
        LicenseType.APACHE2
    ),
    COMPRESSOR(
        R.string.compressor,
        R.string.copyright_edward_paul_2015,
        LicenseType.APACHE2
    ),
    YCHARTS(
        R.string.ycharts,
        R.string.copyright_ycharts_19,
        LicenseType.APACHE2
    ),
    ANDROIDX_TEST_CORE(
        R.string.androidx_test_core, R.string.copyright_android_open_source_21, LicenseType.APACHE2
    ),
    JUNIT(
        R.string.junit,
        R.string.copyright_imb_and_others_20,
        LicenseType.ECLIPSE_PUBLIC_LICENSE
    ),
    ANDROIDX_ARCH_CORE_TESTING(
        R.string.androidx_arch_core_testing,
        R.string.copyright_android_open_source_20,
        LicenseType.APACHE2
    ),
    KOTLINX_COROUTINES_TEST(
        R.string.kotlinx_coroutines_test, R.string.copyright_jetbrains_17, LicenseType.APACHE2
    ),
    TRUTH(
        R.string.truth,
        R.string.copyright_google_llc_20,
        LicenseType.APACHE2
    ),
    MOCKK(
        R.string.mockk,
        R.string.copyright_oleksiy_pylypenko_15_20,
        LicenseType.APACHE2
    ),
    DAGGER_HILT_ANDROID_TESTING(
        R.string.dagger_hilt_android_testing, R.string.copyright_google_llc_20, LicenseType.APACHE2
    ),
    ESPRESSO_CORE(
        R.string.espresso_core, R.string.copyright_android_open_source_14, LicenseType.APACHE2
    ),
    MOCKK_ANDROID(
        R.string.mockk_android, R.string.copyright_oleksiy_pylypenko_15_20, LicenseType.APACHE2
    ),
    ANDROIDX_TEST_CORE_KTX(
        R.string.androidx_test_core_ktx,
        R.string.copyright_android_open_source_21,
        LicenseType.APACHE2
    ),
    ANDROIDX_TEST_RUNNER(
        R.string.androidx_test_runner,
        R.string.copyright_android_open_source_21,
        LicenseType.APACHE2
    )
}
