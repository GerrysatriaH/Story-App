package com.gerrysatria.storyapp.ui.activity

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
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
class LoginActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown(){
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun login_Success(){
        // 1. Halaman login tampil
        onView(withId(R.id.tv_login)).check(matches(isDisplayed()))

        // 2. Input email dan password
        onView(withId(R.id.ed_login_email)).perform(typeText("gerry@gmail.com"))
        onView(withId(R.id.ed_login_password)).perform(typeText("12345678"))
        closeSoftKeyboard()

        // 3. Tekan button login
        onView(withId(R.id.btn_login)).perform(click())

        // 4. Dialog success muncul
        onView(withText("success")).check(matches(isDisplayed()))
        onView(withText(R.string.positive_button)).perform(click())

        // 5. Halaman list story tampil
        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))
    }

    @Test
    fun logout_Success(){
        // 1. Pengguna telah login
        login_Success()

        // 2. Tekan menu logout
        onView(withId(R.id.action_logout)).perform(click())

        // 3. Tampil Pesan untuk melalukan login
        onView(withText(R.string.alert_main)).check(matches(isDisplayed()))
        onView(withText(R.string.positive_button)).perform(click())

        // 4. Kembali ke halaman login
        onView(withId(R.id.tv_login)).check(matches(isDisplayed()))
    }
}