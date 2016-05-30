package org.calber.fixer;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by calber on 30/5/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DialogAddTest {


    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);
    private FixerApi api;
    private List<Product> products;


    @Test
    public void testAdd1() throws Exception {
        api = FixerApi.builder().withBase("GBP").withNetwork().withStaticProductApi().build();
        products = api.getProductsApi().getProducts();

        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.select)).check(matches(isDisplayed()));

        onView(withId(android.R.id.button1)).perform(click());

        Product expected = products.get(0);
        expected.quantity = 1;

        onView(withId(R.id.list)).check(matches(atPosition(0, new DialogProductMatcher(expected))));

    }

    @Test
    public void testEdit1() throws Exception {
        api = FixerApi.builder().withBase("GBP").withNetwork().withStaticProductApi().build();
        products = api.getProductsApi().getProducts();

        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.select)).check(matches(isDisplayed()));

        onView(withId(android.R.id.button1)).perform(click());

        Product expected = products.get(0);
        expected.quantity = 1;

        onView(withId(R.id.list)).check(matches(atPosition(0, new DialogProductMatcher(expected))));

        onView(withId(R.id.card)).perform(click());
        onView(withId(R.id.edit)).check(matches(isDisplayed()));

        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.list)).check(matches(atPosition(0, new DialogProductMatcher(expected))));
    }


    class DialogProductMatcher implements ProductMatcherInterface {
        Product expected;

        public DialogProductMatcher(Product expected) {
            this.expected = expected;
        }

        @Override
        public boolean matchProduct(Product product) {
            return product.equals(expected) && product.quantity == expected.quantity;
        }

        @Override
        public String describe() {
            return expected.toString();
        }
    }

    interface ProductMatcherInterface {
        boolean matchProduct(Product product);

        String describe();
    }

    public static Matcher<View> atPosition(final int position, @NonNull final ProductMatcherInterface itemMatcher) {

        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {

            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("has item at position " + position + ": " + itemMatcher.describe());
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    return false;
                }
                ProductAdapter.ViewHolder v = (ProductAdapter.ViewHolder) viewHolder;
                return itemMatcher.matchProduct(v.product);
            }
        };
    }
}
