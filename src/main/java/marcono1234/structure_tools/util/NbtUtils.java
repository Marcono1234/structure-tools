package marcono1234.structure_tools.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import net.querz.nbt.ListTag;
import net.querz.nbt.Tag;

public final class NbtUtils {
    private NbtUtils() { }
    
    public static <T, R extends Tag<?>> ListTag<R> toNbtList(final List<T> list, final Function<T, R> converter) {
        return list.stream()
            .map(converter)
            .collect(NbtCollectors.toList());
    }
    
    public static <T extends Tag<?>, R> List<R> fromNbtList(final ListTag<T> list, final Function<T, R> converter) {
        return StreamSupport.stream(list.spliterator(), false)
            .map(converter)
            .collect(Collectors.toList());
    }
}
