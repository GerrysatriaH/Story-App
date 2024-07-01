package com.gerrysatria.storyapp.ui.activity

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.gerrysatria.storyapp.R
import com.gerrysatria.storyapp.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddStoryActivityTest{
    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        // Pengguna Login
        onView(withId(R.id.ed_login_email)).perform(typeText("gerry@gmail.com"))
        onView(withId(R.id.ed_login_password)).perform(typeText("12345678"))
        closeSoftKeyboard()
        onView(withId(R.id.btn_login)).perform(click())
        onView(withText("success")).check(matches(isDisplayed()))
        onView(withText(R.string.positive_button)).perform(click())
        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown(){
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun uploadStory_Success(){
        // 1. Pengguna menekan floating button add
        onView(withId(R.id.fab_add_story)).perform(click())

        // 2. Pengguna menekan tombol gallery dan memilih gambar
        onView(withId(R.id.button_gallery)).perform(click())

        // 3. Pengguna mengisi deskripsi gambar
        onView(withId(R.id.ed_add_description)).perform(typeText("test"))
        closeSoftKeyboard()

        // 4. Pengguna menekan tombol upload
        onView(withId(R.id.button_add)).perform(click())

        // 4. Pengguna menekan positive button pada dialog yang muncul
        onView(withText(R.string.positive_button)).perform(click())

        // 5. Halaman kembali ke list story
        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))

        // 6. Story yang baru di upload berada di paling atas list story
        onView(withId(R.id.rv_stories)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.tv_detail_name)).check(matches(withText("gerry")))
    }
}