package marcono1234.structure_tools.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import marcono1234.structure_tools.util.StreamHelper.StreamTest;
import net.querz.nbt.IntTag;
import net.querz.nbt.ListTag;

public class Int3D {
    private static final List<Function<Int3D, Integer>> COORDINATE_RETRIEVERS = Arrays.asList(
        Int3D::getX,
        Int3D::getY,
        Int3D::getZ
    );
    
    private final int x;
    private final int y;
    private final int z;
    
    public Int3D(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getZ() {
        return z;
    }
    
    public ListTag<IntTag> toNbt() {
        final ListTag<IntTag> list = new ListTag<>();
        list.addInt(x);
        list.addInt(y);
        list.addInt(z);
        
        return list;
    }
    
    public static Int3D fromNbt(final ListTag<IntTag> list) {
        final int expectedSize = 3;
        final int actualSize = list.size();
        
        if (actualSize != expectedSize) {
            throw new IllegalArgumentException(String.format(
                "Expected list of size %d, actual size is %d",
                expectedSize,
                actualSize
            ));
        }
        
        return new Int3D(
            list.get(0).asInt(),
            list.get(1).asInt(),
            list.get(2).asInt()
        );
    }
    
    public Int3D add(final int x, final int y, final int z) {
        return new Int3D(this.x + x, this.y + y, this.z + z);
    }
    
    // TODO Test method
    private static Predicate<Int3D> createCoordinatesPredicate(final IntPredicate predicate, final StreamTest<Function<Int3D, Integer>> streamTest) {
        return t -> streamTest.test(
            COORDINATE_RETRIEVERS.stream(),
            coordinateRetriever -> predicate.test(coordinateRetriever.apply(t))
        );
    }
    
    public static Predicate<Int3D> createAnyCoordinatesPredicate(final IntPredicate predicate) {
        return createCoordinatesPredicate(predicate, Stream::anyMatch);
    }
    
    public static Predicate<Int3D> createAllCoordinatesPredicate(final IntPredicate predicate) {
        return createCoordinatesPredicate(predicate, Stream::allMatch);
    }
    
    // TODO Test method
    private static Predicate<Int3D> createCoordinatesPredicate(final BiPredicate<Integer, Integer> predicate, final Int3D restrictions, final StreamTest<Function<Int3D, Integer>> streamTest) {
        return t -> {
            final Predicate<Function<Int3D, Integer>> coordinatePredicate = coordinateRetriever ->  predicate.test(coordinateRetriever.apply(t), coordinateRetriever.apply(restrictions));
            
            return streamTest.test(COORDINATE_RETRIEVERS.stream(), coordinatePredicate);
        };
    }
    
    public static Predicate<Int3D> createAnyCoordinatesPredicate(final BiPredicate<Integer, Integer> predicate, final Int3D restrictions) {
        return createCoordinatesPredicate(predicate, restrictions, Stream::anyMatch);
    }
    
    public static Predicate<Int3D> createAllCoordinatesPredicate(final BiPredicate<Integer, Integer> predicate, final Int3D restrictions) {
        return createCoordinatesPredicate(predicate, restrictions, Stream::allMatch);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        else if (obj instanceof Int3D) {
            final Int3D other = (Int3D) obj;
            
            return x == other.x
                && y == other.y
                && z == other.z;
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
    
    @Override
    public String toString() {
        return new StringBuilder("[")
            .append("x=").append(x)
            .append(", ")
            .append("y=").append(y)
            .append(", ")
            .append("z=").append(z)
            .append("]")
            .toString();
    }
}
