package ru.geekbrains.geekkotlin.ui.note

import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import io.mockk.*
import junit.framework.Assert.assertTrue
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import ru.geekbrains.geekkotlin.R
import ru.geekbrains.geekkotlin.common.getColorInt
import ru.geekbrains.geekkotlin.data.entity.Note


class NoteActivityTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(NoteActivity::class.java, true, false)
    private val model: NoteViewModel = spyk(NoteViewModel(mockk()))
    private val viewStateLiveData = MutableLiveData<NoteViewState>()
    private val testNote = Note("fach432", "test note", "test body")

    @Before
    fun setUp() {
        loadKoinModules(listOf(module { viewModel { model } }))
        every { model.getViewState() } returns viewStateLiveData
        every { model.loadNote(any()) } just runs
        every { model.save(any()) } just runs
        every { model.deleteNote() } just runs

        Intent().apply {
            putExtra(NoteActivity::class.java.name + "extra.NOTE", testNote.id)
        }.let {
            activityTestRule.launchActivity(it)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun should_show_color_palette() {
        onView(withId(R.id.note_menu_palette)).perform(click())
        onView(withId(R.id.colorPicker)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun should_hide_color_palette() {
        onView(withId(R.id.note_menu_palette)).perform(click()).perform(click())
        onView(withId(R.id.colorPicker)).check(matches(not(isDisplayed())))
    }

    @Test
    fun should_set_toolbar_color() {
        onView(withId(R.id.note_menu_palette)).perform(click())
        onView(withTagValue(`is`(Note.Color.BLUE))).perform(click())

        val colorInt = Note.Color.BLUE.getColorInt(activityTestRule.activity)

        onView(withId(R.id.toolbar)).check { view, noViewFoundException ->
            assertTrue("toolbar background color does noy match", (view.background as? ColorDrawable)?.color == colorInt)
        }
    }

    @Test
    fun should_all_viewModel_loadNote_once() {
        verify(exactly = 1) { model.loadNote(testNote.id) }
    }

    @Test
    fun should_show_note() {
        activityTestRule.launchActivity(null)
        viewStateLiveData.postValue(NoteViewState(NoteViewState.Data(note = testNote)))

        onView(withId(R.id.et_title)).check(matches(withText(testNote.title)))
        onView(withId(R.id.et_body)).check(matches(withText(testNote.text)))
    }

    @Test
    fun should_call_save_note() {
        onView(withId(R.id.et_title)).perform(typeText(testNote.title))
        verify(timeout = 1000) { model.save(any()) }
    }

    @Test
    fun should_call_delete_note() {
        openActionBarOverflowOrOptionsMenu(activityTestRule.activity)
        onView(withText(R.string.note_menu_delete_title)).perform(click())
        onView(withText(R.string.note_delete_dialog_ok)).perform(click())
        verify { model.deleteNote() }
    }

}