package marcono1234.structure_tools.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import marcono1234.structure_tools.structure.BlockInfo;
import marcono1234.structure_tools.util.Int3D;
import net.querz.nbt.CompoundTag;

class BlockInfoTest {
    private final BlockInfo blockInfo;
    private final CompoundTag blockInfoNbt;
    
    private final BlockInfo blockInfoNoNbt;
    private final CompoundTag blockInfoNbtNoNbt;
    
    BlockInfoTest() {
        final Int3D pos = new Int3D(1, 2, 3);
        final int stateIndex = 5;
        final CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("test", true);
        nbt.putString("test2", "test2Value");
        
        blockInfoNoNbt = new BlockInfo(pos, stateIndex, (CompoundTag) null);
        blockInfoNbtNoNbt = new CompoundTag();
        blockInfoNbtNoNbt.put("pos", pos.toNbt());
        blockInfoNbtNoNbt.putInt("state", stateIndex);
        
        blockInfo = new BlockInfo(pos, stateIndex, nbt);
        blockInfoNbt = blockInfoNbtNoNbt.clone();
        blockInfoNbt.put("nbt", nbt);
    }
    
    @Test
    void testFromNbt() {
        assertEquals(blockInfo, BlockInfo.fromNbt(blockInfoNbt));
    }
    
    @Test
    void testToNbt() {
        assertEquals(blockInfoNbt, blockInfo.toNbt());
    }
    
    @Test
    void testFromNbtNoNbt() {
        assertEquals(blockInfoNoNbt, BlockInfo.fromNbt(blockInfoNbtNoNbt));
    }
    
    @Test
    void testToNbtNoNbt() {
        assertEquals(blockInfoNbtNoNbt, blockInfoNoNbt.toNbt());
    }
}
