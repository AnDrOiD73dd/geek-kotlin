package ru.geekbrains.geekkotlin.ui.main

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import ru.geekbrains.geekkotlin.R
import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.ui.note.NoteActivity
import ru.geekbrains.geekkotlin.ui.note.NoteViewModel

class MainActivityTest {

    @get:Rule
    val activityTestRule = IntentsTestRule(MainActivity::class.java, true, false)

    private val EXTRA_NOTE = NoteActivity::class.java.name + "extra.NOTE"
    private val model: MainViewModel = mockk(relaxed = true)
    private val viewStateChannel = BroadcastChannel<List<Note>?>(Channel.CONFLATED)
    private val testNotes = listOf(
            Note("afafaf", "first title", "first body"),
            Note("bfbfbf", "second title", "second body"),
            Note("cfcfcf", "third title", "third body")
    )

    @Before
    fun setUp() {
        loadKoinModules(
                listOf(
                        module {
                            viewModel { model }
                            viewModel { mockk<NoteViewModel>(relaxed = true) }
                        }
                )
        )
        every { model.getViewState() } returns viewStateChannel.openSubscription()
        activityTestRule.launchActivity(null)
        runBlocking {
            viewStateChannel.send(testNotes)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
        viewStateChannel.close()
    }

    @Test
    fun check_data_is_displayed() {
        onView(withId(R.id.rv_notes)).perform(scrollToPosition<NotesRVAdapter.NoteViewHolder>(1))
        onView(withText(testNotes[1].text)).check(matches(isDisplayed()))
    }

    @Test
    fun check_detail_activity_intent_sent() {
        onView(withId(R.id.rv_notes)).perform(actionOnItemAtPosition<NotesRVAdapter.NoteViewHolder>(1, click()))
        intended(allOf(hasComponent(NoteActivity::class.java.name), hasExtra(EXTRA_NOTE, testNotes[1].id)))
    }
}