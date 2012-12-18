package org.hamcrest.core;

import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;

public class IsCollectionContainingTest extends AbstractMatcherTest {
    @Override
    protected Matcher<?> createMatcher() {
        return hasItem(equalTo("irrelevant"));
    }

    public void testMatchesACollectionThatContainsAnElementForTheGivenMatcher() {
        final Matcher<Iterable<? super String>> itemMatcher = hasItem(equalTo("a"));

        assertMatches("list containing 'a'",
                      itemMatcher, asList("a", "b", "c"));
    }

    public void testDoesNotMatchCollectionWithoutAnElementForGivenMatcher() {
        final Matcher<Iterable<? super String>> matcher = hasItem(mismatchable("a"));
        assertMismatchDescription("mismatches were: [mismatched: b, mismatched: c]",
                                  matcher, asList("b", "c"));
        
        assertMismatchDescription("was empty", matcher, new ArrayList<String>());
    }

    public void testDoesNotMatchNull() {
        assertDoesNotMatch("doesn't match null", hasItem(equalTo("a")), null);
    }

    public void testHasAReadableDescription() {
        assertDescription("a collection containing mismatchable: a", hasItem(mismatchable("a")));
    }
    
    public void testCanMatchItemWhenCollectionHoldsSuperclass() // Issue 24
    {
      final Set<Number> s = new HashSet<Number>();
      s.add(2);
      assertThat(s, new IsCollectionContaining<Number>(new IsEqual<Number>(2)));
      assertThat(s, IsCollectionContaining.hasItem(2));
    }

    @SuppressWarnings("unchecked")
    public void testMatchesMultipleItemsInCollection() {
        final Matcher<Iterable<String>> matcher1 = hasItems(equalTo("a"), equalTo("b"), equalTo("c"));
        assertMatches("list containing all items",
                matcher1,
                asList("a", "b", "c"));
        
        final Matcher<Iterable<String>> matcher2 = hasItems("a", "b", "c");
        assertMatches("list containing all items (without matchers)",
                matcher2,
                asList("a", "b", "c"));
        
        final Matcher<Iterable<String>> matcher3 = hasItems(equalTo("a"), equalTo("b"), equalTo("c"));
        assertMatches("list containing all items in any order",
                matcher3,
                asList("c", "b", "a"));
        
        final Matcher<Iterable<String>> matcher4 = hasItems(equalTo("a"), equalTo("b"), equalTo("c"));
        assertMatches("list containing all items plus others",
                matcher4,
                asList("e", "c", "b", "a", "d"));
        
        final Matcher<Iterable<String>> matcher5 = hasItems(equalTo("a"), equalTo("b"), equalTo("c"));
        assertDoesNotMatch("not match list unless it contains all items",
                matcher5,
                asList("e", "c", "b", "d")); // 'a' missing
    }
    
    public void testReportsMismatchWithAReadableDescriptionForMultipleItems() {
        final Matcher<Iterable<Integer>> matcher = hasItems(3, 4);
        
        assertMismatchDescription("a collection containing <4> mismatches were: [was <1>, was <2>, was <3>]",
                                  matcher, asList(1, 2, 3));
    }
    
    private static Matcher<? super String> mismatchable(final String string) {
      return new TypeSafeDiagnosingMatcher<String>() {
        @Override
        protected boolean matchesSafely(String item, Description mismatchDescription) {
          if (string.equals(item)) 
            return true;
          
          mismatchDescription.appendText("mismatched: " + item);
          return false;
        }

        @Override
        public void describeTo(Description description) {
          description.appendText("mismatchable: " + string);
        }
      };
    }
}
