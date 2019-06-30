package com.hussein.imageloaderlibrary
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.Espresso
import android.support.test.rule.ActivityTestRule
import com.hussein.imageloaderlibrary.adapter.ImageAdapter
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
class RecyclerViewTest{
    @Rule
    var mActivityTestRule: ActivityTestRule<MainActivity>?=ActivityTestRule(MainActivity::class.java, false, false)

    @Before
    fun setup() {
        //mActivityTestRule=ActivityTestRule(MainActivity::class.java, false, false)
        assert(true)
    }

    @Test
    fun scrollToPosition() {
        Espresso.onView(ViewMatchers.withId(R.id.rvImage))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ImageAdapter.ViewHolder>(5, ViewActions.click()))
    }

}