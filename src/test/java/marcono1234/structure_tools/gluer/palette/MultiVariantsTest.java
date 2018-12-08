package marcono1234.structure_tools.gluer.palette;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import marcono1234.structure_tools.gluer.palette.MultiVariants;
import marcono1234.structure_tools.structure.BlockState;

class MultiVariantsTest {
    private final List<BlockState> blockStates; 
    private final MultiVariants variant;
    private final MultiVariants variantCopy;
    private final MultiVariants variant2;
    
    MultiVariantsTest() {
        blockStates = Arrays.asList(
            new BlockState("a"),
            new BlockState("b"),
            new BlockState("c")
        );
        variant = new MultiVariants(blockStates);
        variantCopy = new MultiVariants(new ArrayList<>(blockStates));
        variant2 = new MultiVariants(Arrays.asList(new BlockState("a1"), new BlockState("b1")));
    }
    
    @Test
    void testGetState() {
        for (int index = 0; index < blockStates.size(); index++) {
            assertEquals(blockStates.get(index), variant.getVariant(index));
        }
    }
    
    @Test
    void testInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> variant.getVariant(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> variant.getVariant(blockStates.size()));
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
