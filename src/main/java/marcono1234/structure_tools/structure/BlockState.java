package marcono1234.structure_tools.structure;

import java.util.Objects;
import java.util.Optional;

import net.querz.nbt.CompoundTag;

public class BlockState {
    private static final String NAME_KEY = "Name";
    private static final String PROPERTIES_KEY = "Properties";
    
    private final String name;
    private final Optional<CompoundTag> properties;
    
    public BlockState(final String name, final CompoundTag properties) {
        this.name = Objects.requireNonNull(name);
        this.properties = Optional.ofNullable(properties);
    }
    
    public BlockState(final String name) {
        this(name, null);
    }
    
    public CompoundTag toNbt() {
        final CompoundTag compound = new CompoundTag();
        compound.putString(NAME_KEY, name);
        
        if (properties.isPresent()) {
            compound.put(PROPERTIES_KEY, properties.get());
        }
        
        return compound;
    }
    
    public static BlockState fromNbt(final CompoundTag compound) {
        /*
         * Default to air if no name key exists, see 
         * net.minecraft.nbt.NBTUtil.readBlockState(NBTTagCompound)
         */
        if (!compound.containsKey(NAME_KEY)) {
            return new BlockState("minecraft:air", null);
        }
        
        return new BlockState(
            compound.getString(NAME_KEY),
            compound.getCompoundTag(PROPERTIES_KEY) // Can be null
        );
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        else if (obj instanceof BlockState) {
            final BlockState other = (BlockState) obj;
            
            return name.equals(other.name)
                && properties.equals(other.properties);
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, properties);
    }
}
