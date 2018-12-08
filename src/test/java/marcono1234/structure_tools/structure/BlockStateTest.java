package marcono1234.structure_tools.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import marcono1234.structure_tools.structure.BlockState;
import net.querz.nbt.CompoundTag;

class BlockStateTest {
    private final BlockState blockState;
    private final CompoundTag blockStateNbt;
    
    private final BlockState blockInfoNoProperties;
    private final CompoundTag blockInfoNbtNoProperties;
    
    BlockStateTest() {
        final String name = "a";
        final CompoundTag properties = new CompoundTag();
        properties.putString("prop1", "val1");
        properties.putString("prop2", "val2");
        
        blockInfoNoProperties = new BlockState(name);
        blockInfoNbtNoProperties = new CompoundTag();
        blockInfoNbtNoProperties.putString("Name", name);
        
        blockState = new BlockState(name, properties);
        blockStateNbt = blockInfoNbtNoProperties.clone();
        blockStateNbt.put("Properties", properties);
    }
    
    @Test
    void testFromNbt() {
        assertEquals(blockState, BlockState.fromNbt(blockStateNbt));
    }
    
    @Test
    void testToNbt() {
        assertEquals(blockStateNbt, blockState.toNbt());
    }
    
    @Test
    void testFromNbtNoNbt() {
        assertEquals(blockInfoNoProperties, BlockState.fromNbt(blockInfoNbtNoProperties));
    }
    
    @Test
    void testToNbtNoNbt() {
        assertEquals(blockInfoNbtNoProperties, blockInfoNoProperties.toNbt());
    }
}
