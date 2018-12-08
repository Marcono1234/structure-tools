package marcono1234.structure_tools.gluer.palette;

import marcono1234.structure_tools.structure.BlockState;

public class SingleVariant implements VariantsBase {
    private final BlockState blockState;
    
    public SingleVariant(final BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public BlockState getVariant(final int variantIndex) throws IndexOutOfBoundsException {
        if (variantIndex < 0) {
            throw new IndexOutOfBoundsException(Integer.toString(variantIndex));
        }
        
        return blockState;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        else if (obj instanceof SingleVariant) {
            final SingleVariant other = (SingleVariant) obj;
            
            return blockState.equals(other.blockState);
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return blockState.hashCode();
    }
}
