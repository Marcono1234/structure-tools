package marcono1234.structure_tools.structure;

import java.util.Objects;

import marcono1234.structure_tools.util.Double3D;
import marcono1234.structure_tools.util.Int3D;
import net.querz.nbt.CompoundTag;

public class EntityInfo {
    private static final String POS_KEY = "pos";
    private static final String BLOCK_POS_KEY = "blockPos";
    private static final String NBT_KEY = "nbt";
    
    private final Double3D pos;
    private final Int3D blockPos;
    /*
     * When Minecraft reads entity info it checks if "nbt" is present, 
     * however when it is not present it ignores the entry; therefore 
     * it is not optional here
     */
    private final CompoundTag nbt;
    
    public EntityInfo(final Double3D pos, final Int3D blockPos, final CompoundTag nbt) {
        this.pos = Objects.requireNonNull(pos);
        this.blockPos = Objects.requireNonNull(blockPos);
        this.nbt = Objects.requireNonNull(nbt);
    }
    
    public Double3D getPos() {
        return pos;
    }
    
    public Int3D getBlockPos() {
        return blockPos;
    }
    
    public CompoundTag getNbt() {
        return nbt;
    }
    
    public CompoundTag toNbt() {
        final CompoundTag compound = new CompoundTag();
        
        compound.put(POS_KEY, pos.toNbt());
        compound.put(BLOCK_POS_KEY, blockPos.toNbt());
        compound.put(NBT_KEY, nbt);
        
        return compound;
    }
    
    public static EntityInfo fromNbt(final CompoundTag compound) {
        return new EntityInfo(
            Double3D.fromNbt(compound.getListTag(POS_KEY).asDoubleTagList()),
            Int3D.fromNbt(compound.getListTag(BLOCK_POS_KEY).asIntTagList()),
            compound.getCompoundTag(NBT_KEY)
        );
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        else if (obj instanceof EntityInfo) {
            final EntityInfo other = (EntityInfo) obj;
            
            return pos.equals(other.pos)
                && blockPos.equals(other.blockPos)
                && nbt.equals(other.nbt);
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(pos, blockPos, nbt);
    }
}
