package com.begoml.archkit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
internal class ViewStateViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesTestRule = ArchMainCoroutineTestRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MockViewStateViewModel

    @Before
    fun setup() {
        viewModel = MockViewStateViewModel()
    }

    @Test
    fun `check init view state`() {
        assertEquals(false, viewModel.stateValue.isLoading)
    }

    @Test
    fun `run loading clicked`() {
        viewModel.onRunLoadingClicked()
        assertEquals(true, viewModel.stateValue.isLoading)
    }

    @Test
    fun `run loading flow`() {
        runBlocking {
            viewModel.onRunLoadingClicked()
            val actual = viewModel.viewState.first()
            assertEquals(actual.isLoading, true)
        }
    }

    @ExperimentalTime
    @Test
    fun `settings clicked`() {
        runBlocking {
            viewModel.onSettingsClicked()
            viewModel.singleEvents.test {
                val actual = expectItem()
                assertEquals(actual, MockViewStateViewModel.Event.NavigateToSettings)
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @ExperimentalTime
    @Test
    fun `changed user name`() {
        runBlocking {
            val userName1 = "testUser1"
            val userName2 = "testUser2"

            viewModel.changedUserName(userName1)
            assertEquals(viewModel.stateValue.userName, userName1)

            viewModel.changedUserName(userName2)
            assertEquals(viewModel.stateValue.userName, userName2)

            viewModel.viewState.test {
                val actual = expectItem()
                assertEquals(userName2, actual.userName)
                cancelAndConsumeRemainingEvents()
            }
        }
    }
}
