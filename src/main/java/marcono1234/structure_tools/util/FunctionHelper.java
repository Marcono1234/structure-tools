package marcono1234.structure_tools.util;

import java.util.function.Consumer;
import java.util.function.Function;

public final class FunctionHelper {
    private FunctionHelper() { }
    
    /**
     * Creates a function which only applies if the argument is not {@code null}.
     * 
     * @param function
     *      The function to apply if the argument is not {@code null}
     * @return
     *      The created wrapping function
     */
    public static <T, R> Function<T, R> functionNullable(final Function<T, R> function) {
        return t -> t == null ? null : function.apply(t);
    }
    
    public static <T> Consumer<T> consumerNullable(final Consumer<T> consumer) {
        return t -> {
            if (t != null) {
                consumer.accept(t);
            }
        };
    }
}
