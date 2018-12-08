package marcono1234.structure_tools.gluer.palette;

import java.util.List;

import marcono1234.structure_tools.structure.BlockState;

public class MultiVariants implements VariantsBase {
    private final List<BlockState> variants;
    
    public MultiVariants(final List<BlockState> variants) {
        this.variants = variants;
    }
    
    @Override
    public BlockState getVariant(final int variantIndex) throws IndexOutOfBoundsException {
        return variants.get(variantIndex);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        else if (obj instanceof MultiVariants) {
            final MultiVariants other = (MultiVariants) obj;
            
            return variants.equals(other.variants);
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return variants.hashCode();
    }
}
