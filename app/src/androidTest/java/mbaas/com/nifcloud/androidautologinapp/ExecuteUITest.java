package mbaas.com.nifcloud.androidautologinapp;

import android.view.View;

import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.nifcloud.mbaas.core.NCMBException;

import org.hamcrest.Matcher;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.concurrent.TimeoutException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

@RunWith(AndroidJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExecuteUITest {
    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class, true);

    @Test
    public void signUpInBackground() {
        onView(withId(R.id.txtMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.txtLogin)).check(matches(isDisplayed()));
        onView(isRoot()).perform(waitForText("はじめまして", 50000));
        onView(isRoot()).perform(waitForText("１回目ログイン、ありがとうございます。", 50000));
    }

    @Test
    public void validateLoginInBackground() throws NCMBException {
        onView(withId(R.id.txtMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.txtLogin)).check(matches(isDisplayed()));
        onView(isRoot()).perform(waitForText("お帰りなさい！", 50000));
        onView(isRoot()).perform(waitForText("最終ログインは：", 50000));

        Utils.deleteUserIfExist(main.getActivity().getApplicationContext());
    }

    /**
     * Perform action of waiting for a specific view id with text.
     *
     * @param text   The id of the view to wait for.
     * @param millis The timeout of until when to wait for.
     */
    public static ViewAction waitForText(final String text, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with text <" + text + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withText(containsString(text));

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                // timeout happens
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }
}
