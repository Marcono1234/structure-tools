package marcono1234.structure_tools.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import marcono1234.structure_tools.util.Double3D;
import net.querz.nbt.DoubleTag;
import net.querz.nbt.ListTag;

class Double3DTest {
    private final Double3D double3d;
    private final ListTag<DoubleTag> double3dNbt;
    
    Double3DTest() {
        final double x = 1.0;
        final double y = Double.POSITIVE_INFINITY;
        final double z = Double.NaN;
        
        double3d = new Double3D(x, y, z);
        double3dNbt = new ListTag<>();
        double3dNbt.addDouble(x);
        double3dNbt.addDouble(y);
        double3dNbt.addDouble(z);
    }
    
    @Test
    void testAdd() {
        assertEquals(new Double3D(4, 4, 4), new Double3D(1, 2, 3).add(3, 2, 1));
    }
    
    @Test
    void testEqualsNaN() {
        final Double3D first = new Double3D(Double.NaN, Double.NaN, Double.NaN);
        final Double3D second = new Double3D(Double.NaN, Double.NaN, Double.NaN);
        
        assertEquals(first, second);
    }
    
    @Test
    void testHashCode() {
        assertEquals(new Double3D(1, 2, 3).hashCode(), new Double3D(1, 2, 3).hashCode());
    }
    
    @Test
    void testFromNbt() {
        assertEquals(double3d, Double3D.fromNbt(double3dNbt)); 
    }
    
    @Test
    @Disabled("Querz NBT bug: DoubleTag(NaN) != DoubleTag(NaN)")
    void testToNbt() {
        assertEquals(double3dNbt, double3d.toNbt());
    }
}
