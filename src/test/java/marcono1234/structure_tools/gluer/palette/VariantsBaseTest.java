package marcono1234.structure_tools.gluer.palette;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import marcono1234.structure_tools.gluer.palette.MultiVariants;
import marcono1234.structure_tools.gluer.palette.SingleVariant;
import marcono1234.structure_tools.gluer.palette.VariantsBase;
import marcono1234.structure_tools.structure.BlockState;

class VariantsBaseTest {
    @Test
    void testCreateSingle() {
        final BlockState blockState = new BlockState("a");
        assertEquals(
            new SingleVariant(blockState), VariantsBase.toVariants(Collections.singletonList(blockState)));
    }
    
    @Test
    void testCreateMulti() {
        final List<BlockState> blockStates = Arrays.asList(
            new BlockState("a"),
            new BlockState("b"),
            new BlockState("c")
        );
        
        assertEquals(
            new MultiVariants(blockStates),
            VariantsBase.toVariants(blockStates)
        );
    }
    
    @Test
    void testCreateEmpty() {
        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> VariantsBase.toVariants(Collections.emptyList())
        );
        
        assertEquals("Cannot create variants for no block states", exception.getMessage());
    }
}
