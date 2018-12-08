package marcono1234.structure_tools.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

public final class InputStreamHelper {
    private InputStreamHelper() { }
    
    public static void handleFile(final Path path, final Consumer<InputStream> streamConsumer, final OpenOption... openOptions) throws IOException {
        try (InputStream inputStream = Files.newInputStream(path, openOptions)) {
            streamConsumer.accept(inputStream);
        }
    }
    
    public static <T> T handleFile(final Path path, final Function<InputStream, T> streamFunction, final OpenOption... openOptions) throws IOException {
        try (InputStream inputStream = Files.newInputStream(path, openOptions)) {
            return streamFunction.apply(inputStream);
        }
    }
}
