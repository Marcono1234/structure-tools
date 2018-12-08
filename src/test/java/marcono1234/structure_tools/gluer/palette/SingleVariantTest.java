package marcono1234.structure_tools.gluer.palette;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import marcono1234.structure_tools.gluer.palette.SingleVariant;
import marcono1234.structure_tools.structure.BlockState;

class SingleVariantTest {
    private final BlockState blockState; 
    private final SingleVariant variant;
    private final SingleVariant variantCopy;
    private final SingleVariant variant2;
    
    SingleVariantTest() {
        blockState = new BlockState("a");
        variant = new SingleVariant(blockState);
        variantCopy = new SingleVariant(new BlockState("a"));
        variant2 = new SingleVariant(new BlockState("b"));
    }
    
    @Test
    void testGetState() {
        for (final int index : Arrays.asList(1, 4, 0, 19, Integer.MAX_VALUE)) {
            assertEquals(blockState, variant.getVariant(index));
        }
    }
    
    @Test
    void testInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> variant.getVariant(-1));
    }
    
    @Test
    void testEquals() {
        assertEquals(variant, variantCopy);
        assertNotEquals(variant, variant2);
    }
    
    @Test
    void testHashCode() {
        assertEquals(variant.hashCode(), variantCopy.hashCode());
        assertNotEquals(variant.hashCode(), variant2.hashCode());
    }
}
