package marcono1234.structure_tools.util;

import java.util.stream.Collector;

import net.querz.nbt.ListTag;
import net.querz.nbt.Tag;

public interface NbtCollectors {
    static <T extends Tag<?>> Collector<T, ListTag<T>, ListTag<T>> toList() {
        return Collector.<T, ListTag<T>>of(
            ListTag<T>::new,
            ListTag::add,
            (r1, r2) -> {
                r2.forEach(element -> r1.add(element));
                return r1;
            }
        );
    }
}
