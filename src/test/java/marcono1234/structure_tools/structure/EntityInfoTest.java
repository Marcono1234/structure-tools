package marcono1234.structure_tools.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import marcono1234.structure_tools.structure.EntityInfo;
import marcono1234.structure_tools.util.Double3D;
import marcono1234.structure_tools.util.Int3D;
import net.querz.nbt.CompoundTag;

class EntityInfoTest {
    private final EntityInfo entityInfo;
    private final CompoundTag entityInfoNbt;
    
    EntityInfoTest() {
        final Double3D pos = new Double3D(1.5, 2.5, 3.5);
        final Int3D blockPos = new Int3D(1, 2, 3);
        final CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("test", true);
        nbt.putString("test2", "test2Value");
        
        entityInfo = new EntityInfo(pos, blockPos, nbt);
        entityInfoNbt = new CompoundTag();
        entityInfoNbt.put("pos", pos.toNbt());
        entityInfoNbt.put("blockPos", blockPos.toNbt());
        entityInfoNbt.put("nbt", nbt);
    }
    
    @Test
    void testFromNbt() {
        assertEquals(entityInfo, EntityInfo.fromNbt(entityInfoNbt));
    }
    
    @Test
    void testToNbt() {
        assertEquals(entityInfoNbt, entityInfo.toNbt());
    }
}
