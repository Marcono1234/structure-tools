package marcono1234.structure_tools.gluer.palette;

import java.util.List;

import marcono1234.structure_tools.structure.BlockState;

/**
 * <p>Interface for variants. Variants are a collection of block states used 
 * by structures. When loading a structure a variant index is determined 
 * and the block states for this variant are retrieved from the palette.</br>
 * This interface represents the variants for a palette entry and allows 
 * retrieving the respective block state using {@link #getVariant(int)}.</p>
 * 
 * <p><b>Important:</b> The implementations {@link SingleVariant} and 
 * {@link MultiVariants} are never equal to each other. The only case where 
 * this would be possible is when a {@code MultiVariants} contains only the 
 * same block state multiple times. In this case a {@code SingleVariant} should 
 * have been used.</p>
 */
public interface VariantsBase {
    BlockState getVariant(int variantIndex) throws IndexOutOfBoundsException;
    
    /**
     * Creates a {@code VariantsBase} implementation for the given variants 
     * list.
     * 
     * @param variants
     *      Variants list
     * @return
     *      The created variants implementation
     */
    static VariantsBase toVariants(final List<BlockState> variants) {
        if (variants.isEmpty()) {
            throw new IllegalArgumentException("Cannot create variants for no block states");
        }
        
        final BlockState firstVariant = variants.get(0);
        
        for (int variantIndex = 1; variantIndex < variants.size(); variantIndex++) {
            if (!variants.get(variantIndex).equals(firstVariant)) {
                return new MultiVariants(variants);
            }
        }
        
        return new SingleVariant(firstVariant);
    }
}
