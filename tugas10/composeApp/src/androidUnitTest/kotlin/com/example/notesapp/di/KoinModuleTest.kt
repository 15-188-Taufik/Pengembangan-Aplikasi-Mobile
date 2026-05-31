package com.example.notesapp.di

import android.content.Context
import io.mockk.mockk
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class KoinModuleTest : KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkAllModules() {
        org.koin.dsl.koinApplication {
            modules(appModule + platformModule())
        }.checkModules {
            withInstance<Context>(mockk(relaxed = true))
        }
    }
}
