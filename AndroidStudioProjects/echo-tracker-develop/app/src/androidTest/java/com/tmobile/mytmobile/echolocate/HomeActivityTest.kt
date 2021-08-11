//package com.tmobile.mytmobile.echolocate
//
//import androidx.test.espresso.Espresso
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
//import androidx.test.espresso.matcher.ViewMatchers.withId
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.filters.MediumTest
//import androidx.test.rule.ActivityTestRule
//import com.tmobile.mytmobile.echolocate.playground.activities.HomeActivity
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@MediumTest
//@RunWith(AndroidJUnit4::class)
//class HomeActivityTest {
//
//    @get:Rule
//    private val activityRule = ActivityTestRule(
//            HomeActivity::class.java, false, false)
//
//
//    @Before
//    fun setup() {
//        activityRule.launchActivity(null)
//
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun ensureViewPresent() {
//        Espresso.onView(withId(R.id.module_grid_view))
//                .check(matches(isDisplayed()))
//    }
//}
//
