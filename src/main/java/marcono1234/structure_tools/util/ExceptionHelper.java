package marcono1234.structure_tools.util;

public final class ExceptionHelper {
    private ExceptionHelper() { }
    
    @FunctionalInterface
    public static interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
    
    public static <T> T get(final ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        }
        catch (final Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
