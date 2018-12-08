package marcono1234.structure_tools.util;

import java.util.Objects;

import net.querz.nbt.DoubleTag;
import net.querz.nbt.ListTag;

public class Double3D {
    private final double x;
    private final double y;
    private final double z;
    
    public Double3D(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getZ() {
        return z;
    }
    
    public ListTag<DoubleTag> toNbt() {
        final ListTag<DoubleTag> list = new ListTag<>();
        list.addDouble(x);
        list.addDouble(y);
        list.addDouble(z);
        
        return list;
    }
    
    public static Double3D fromNbt(final ListTag<DoubleTag> list) {
        final int expectedSize = 3;
        final int actualSize = list.size();
        
        if (actualSize != expectedSize) {
            throw new IllegalArgumentException(String.format(
                "Expected list of size %d, actual size is %d",
                expectedSize,
                actualSize
            ));
        }
        
        return new Double3D(
            list.get(0).asDouble(),
            list.get(1).asDouble(),
            list.get(2).asDouble()
        );
    }
    
    public Double3D add(final double x, final double y, final double z) {
        return new Double3D(this.x + x, this.y + y, this.z + z);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        else if (obj instanceof Double3D) {
            final Double3D other = (Double3D) obj;
            
            // Have to use equals because NaN != NaN
            return Double.valueOf(x).equals(other.x)
                && Double.valueOf(y).equals(other.y)
                && Double.valueOf(z).equals(other.z);
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
