package id.my.storyapp.view.signin

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import id.my.storyapp.R
import id.my.storyapp.util.EspressoIdlingResource
import org.junit.After
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class LoginLogoutTest {
    private val email = "test.dyandra@gmail.com"
    private val password = "12345678"
    private val wrongPassword = "3228494j"

    @get:Rule
    val activityRule = ActivityScenarioRule(SigninActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun login_and_logout_success() {
        // login success
        onView(withId(R.id.emailEditText))
            .perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText))
            .perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.signinButton))
            .perform(click())

        onView(withId(R.id.mainActivityLayout))
            .check(matches(isDisplayed()))

        // logout success
        onView(withContentDescription("More options"))
            .perform(click())
        onView(withText("Logout"))
            .perform(click())

        onView(withId(R.id.signinLayout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun login_wrong_password() {
        onView(withId(R.id.emailEditText))
            .perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText))
            .perform(typeText(wrongPassword), closeSoftKeyboard())
        onView(withId(R.id.signinButton))
            .check(matches(isDisplayed()))
        onView(withId(R.id.signinButton))
            .perform(click())

        onView(withId(R.id.signinStatus))
            .check(matches(withText("Invalid password")))
    }
}