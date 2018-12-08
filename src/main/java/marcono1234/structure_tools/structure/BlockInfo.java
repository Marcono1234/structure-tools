package marcono1234.structure_tools.structure;

import java.util.Objects;
import java.util.Optional;

import marcono1234.structure_tools.util.Int3D;
import net.querz.nbt.CompoundTag;

public class BlockInfo {
    private static final String POS_KEY = "pos";
    private static final String STATE_KEY = "state";
    private static final String NBT_KEY = "nbt";
    
    private Int3D pos;
    private int state;
    private Optional<CompoundTag> nbt;
    
    public BlockInfo(final Int3D pos, final int state, final Optional<CompoundTag> nbt) {
        this.pos = Objects.requireNonNull(pos);
        this.state = state;
        this.nbt = Objects.requireNonNull(nbt);
    }
    
    public BlockInfo(final Int3D pos, final int state, final CompoundTag nbt) {
        this(pos, state, Optional.ofNullable(nbt));
    }
    
    public BlockInfo(final Int3D pos, final int state) {
        this(pos, state, (CompoundTag) null);
    }
    
    public Int3D getPos() {
        return pos;
    }
    
    public int getState() {
        return state;
    }
    
    public Optional<CompoundTag> getNbt() {
        return nbt;
    }
    
    public CompoundTag toNbt() {
        final CompoundTag compound = new CompoundTag();
        compound.put(POS_KEY, pos.toNbt());
        compound.putInt(STATE_KEY, state);
        
        if (nbt.isPresent()) {
            compound.put(NBT_KEY, nbt.get());
        }
        
        return compound;
    }
    
    public static BlockInfo fromNbt(final CompoundTag compound) {
        return new BlockInfo(
            Int3D.fromNbt(compound.getListTag(POS_KEY).asIntTagList()),
            compound.getInt(STATE_KEY),
            compound.containsKey(NBT_KEY) ? compound.getCompoundTag(NBT_KEY) : null
        );
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        else if (obj instanceof BlockInfo) {
            final BlockInfo other = (BlockInfo) obj;
            
            return pos.equals(other.pos)
                && state == other.state
                && nbt.equals(other.nbt);
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(pos, state, nbt);
    }
}
