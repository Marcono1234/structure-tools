package marcono1234.structure_tools.gluer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class GsonHelper {
    private static final Gson gson;
    
    static {
        /*
         * GSON is effectively parsing lenient due to
         * - https://github.com/google/gson/issues/372
         * - https://github.com/google/gson/issues/1208
         */
        gson = new GsonBuilder()
            .registerTypeAdapter(Path.class, new JsonDeserializer<Path>() {
                @Override
                public Path deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
                    return Paths.get(json.getAsString());
                }
            })
            .create();
    }
    
    public static <T> T fromFile(final Path path, final Class<T> tClass) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, tClass);
        }
    }
    
    public static <T> T fromFile(final Path path, final Type type) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, type);
        }
    }
    
    private static Reader inputStreamToReader(final InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }
    
    public static <T> T fromInputStream(final InputStream inputStream, final Class<T> tClass) {
        return gson.fromJson(inputStreamToReader(inputStream), tClass);
    }
    
    public static <T> T fromInputStream(final InputStream inputStream, final Type type) {
        return gson.fromJson(inputStreamToReader(inputStream), type);
    }
}
