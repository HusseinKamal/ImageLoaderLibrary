package com.hussein.imageloaderlibrary
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import java.util.ArrayList
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.ViewMatchers.withId
import com.hussein.imageloaderlibrary.adapter.ImageAdapter
import com.hussein.imageloaderlibrary.model.Image
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.assertThat
import android.support.test.runner.AndroidJUnit4

class MainActivityTest {
    @Rule
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java, false, false)

    @Test
    fun clickAll() {
        mActivityTestRule.launchActivity(null)
        val dataPoinsts = pullDataPoints(ArrayList(), MainActivity.adapter.items[2])
        for (i in dataPoinsts.indices) {
            val item = dataPoinsts[i]
            if (item.urls!!.raw.isNotEmpty()) {
                continue
            }
            mActivityTestRule.activity
            onView(withId(R.id.rvImage)).perform(scrollToPosition<ImageAdapter.ViewHolder>(i))
            onView(withId(R.id.rvImage)).perform(actionOnItemAtPosition<ImageAdapter.ViewHolder>(i, click()))
            //assertThat<Activity>(nextActivity, `is`(notNullValue()))
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        }
    }

    private fun pullDataPoints(dest: MutableList<Image>, entries: Image): List<Image> {
        for (entry in dest) {
            dest.add(entry)
            if (entry.urls!!.raw.isNotEmpty()) {
                pullDataPoints(dest, entries)
            }
        }
        return dest
    }
}