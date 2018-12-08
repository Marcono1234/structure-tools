package marcono1234.structure_tools.util;

import java.util.function.Predicate;
import java.util.stream.Stream;

public final class StreamHelper {
    private StreamHelper() { }
    
    /**
     * Functional interface which makes declaring the type for stream test methods 
     * ({@link Stream#allMatch(Predicate) allMatch}, {@link Stream#anyMatch(Predicate) anyMatch}) 
     * easier by only requiring one type parameter, since it applies to the stream 
     * and the predicate. E.g.:
     *<pre>
     *{@literal StreamTest<T>} streamTest;
     *</pre>
     *instead of
     *<pre>
     *{@literal BiPredicate<Stream<T>, Predicate<T>>} streamTest;
     *</pre>
     * 
     * @param <T>
     *      Type of the stream elements
     */
    @FunctionalInterface
    public static interface StreamTest<T> {
        boolean test(Stream<T> stream, Predicate<T> predicate);
    }
}
