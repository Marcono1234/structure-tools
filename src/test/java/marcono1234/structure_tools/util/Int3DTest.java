package marcono1234.structure_tools.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import marcono1234.structure_tools.util.Int3D;
import net.querz.nbt.IntTag;
import net.querz.nbt.ListTag;

class Int3DTest {
    private final Int3D int3d;
    private final ListTag<IntTag> int3dNbt;
    
    Int3DTest() {
        final int x = 1;
        final int y = Integer.MIN_VALUE;
        final int z = Integer.MAX_VALUE;
        
        int3d = new Int3D(x, y, z);
        int3dNbt = new ListTag<>();
        int3dNbt.addInt(x);
        int3dNbt.addInt(y);
        int3dNbt.addInt(z);
    }
    
    @Test
    void testAdd() {
        assertEquals(new Int3D(4, 4, 4), new Int3D(1, 2, 3).add(3, 2, 1));
    }
    
    @Test
    void testHashCode() {
        assertEquals(new Int3D(1, 2, 3).hashCode(), new Int3D(1, 2, 3).hashCode());
    }
    
    @Test
    void testFromNbt() {
        assertEquals(int3d, Int3D.fromNbt(int3dNbt)); 
    }
    
    @Test
    void testToNbt() {
        assertEquals(int3dNbt, int3d.toNbt());
    }
    
    @Test
    void testCreateAllPredicate() {
        final Map<Boolean, List<Int3D>> toTest = new HashMap<>();
        toTest.put(true, Arrays.asList(
            new Int3D(2, 3, 4)
        ));
        toTest.put(false, Arrays.asList(
            new Int3D(1, 3, 4),
            new Int3D(2, 1, 4),
            new Int3D(2, 3, 1)
        ));
        
        final Predicate<Int3D> predicate = Int3D.createAllCoordinatesPredicate(coordinate -> coordinate > 1);
        final Int3D bounds = new Int3D(1, 2, 3);
        final Predicate<Int3D> predicateForBounds = Int3D.createAllCoordinatesPredicate(
            (coordinate, bound) -> coordinate > bound,
            bounds
        );
        
        toTest.forEach(
            (expected, testValues) -> testValues.forEach(testValue -> {
                assertEquals(expected, predicate.test(testValue));
                assertEquals(expected, predicateForBounds.test(testValue));
            })
        );
    }
    
    @Test
    void testCreateAnyPredicate() {
        final Map<Boolean, List<Int3D>> toTest = new HashMap<>();
        toTest.put(true, Arrays.asList(
            new Int3D(1, 3, 4),
            new Int3D(2, 1, 4),
            new Int3D(2, 3, 1)
        ));
        toTest.put(false, Arrays.asList(
            new Int3D(2, 3, 4)
        ));
        
        final Predicate<Int3D> predicate = Int3D.createAnyCoordinatesPredicate(coordinate -> coordinate <= 1);
        final Int3D bounds = new Int3D(1, 2, 3);
        final Predicate<Int3D> predicateForBounds = Int3D.createAnyCoordinatesPredicate(
            (coordinate, bound) -> coordinate <= bound,
            bounds
        );
        
        toTest.forEach(
            (expected, testValues) -> testValues.forEach(testValue -> {
                assertEquals(expected, predicate.test(testValue));
                assertEquals(expected, predicateForBounds.test(testValue));
            })
        );
    }
}
