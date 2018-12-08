package marcono1234.structure_tools.gluer.palette;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import marcono1234.structure_tools.structure.BlockState;

public class PaletteMap {
    private int variantsCount;
    private final List<VariantsBase> palette;
    
    public PaletteMap() {
        variantsCount = 1;
        palette = new ArrayList<>();
    }
    
    public List<Integer> putPalettes(final List<List<BlockState>> palettes) {
        validatePalettesSizes(palettes);
        
        final int variantsCount = palettes.size();
        final List<Integer> stateIndices = new ArrayList<>(palettes.size());
        
        if (variantsCount == 1 || isSinglePalette(palettes)) {
            palettes.get(0).forEach(
                blockState -> stateIndices.add(putVariants(new SingleVariant(blockState)))
            );
        }
        else {
            if (this.variantsCount == 1) {
                this.variantsCount = variantsCount;
            }
            else if (this.variantsCount != variantsCount) {
                throw new VariantsCountMismatch(this.variantsCount, variantsCount);
            }
            
            final int paletteSize = palettes.get(0).size();
            
            for (int stateIndex = 0; stateIndex < paletteSize; stateIndex++) {
                final List<BlockState> variants = new ArrayList<>(variantsCount);
                
                for (int variantIndex = 0; variantIndex < variantsCount; variantIndex++) {
                    variants.add(palettes.get(variantIndex).get(stateIndex));
                }
                
                stateIndices.add(putVariants(VariantsBase.toVariants(variants)));
            }
        }
        
        return stateIndices;
    }
    
    private int putVariants(final VariantsBase variants) {
        final int index = palette.indexOf(variants);
        
        if (index == -1) {
            palette.add(variants);
            return palette.size() - 1;
        }
        else {
            return index;
        }
    }
    
    public List<List<BlockState>> createPalette() {
        final List<List<BlockState>> paletteList = new ArrayList<>(palette.size());
        
        for (int variantIndex = 0; variantIndex < variantsCount; variantIndex++) {
            // final copy to be usable within lambda
            final int variantIndexF = variantIndex;
            
            paletteList.add(
                palette.stream()
                    .map(variants -> variants.getVariant(variantIndexF))
                    .collect(Collectors.toList())
            );
        }
        
        return paletteList;
    }
    
    private static void validatePalettesSizes(final List<List<BlockState>> palettes) throws IllegalArgumentException {
        if (palettes.isEmpty()) {
            throw new IllegalArgumentException("Palettes has to contain at least one palette");
        }
        
        int firstPaletteSize = palettes.get(0).size();
        
        for (int paletteIndex = 1; paletteIndex < palettes.size(); paletteIndex++) {
            if (palettes.get(paletteIndex).size() != firstPaletteSize) {
                throw new IllegalArgumentException("Not all palette variants have same size");
            }
        }
    }
    
    /**
     * Checks if all palette variants are the same and therefore the palettes are 
     * effectively a single palette.
     * 
     * @param palettes
     *      The palettes to check
     * @return
     *      Whether all palette variants are the same
     */
    private static boolean isSinglePalette(final List<List<BlockState>> palettes) {
        final List<BlockState> firstPalette = palettes.get(0);
        
        for (int paletteIndex = 1; paletteIndex < palettes.size(); paletteIndex++) {
            if (!palettes.get(paletteIndex).equals(firstPalette)) {
                return false;
            }
        }
        
        return true;
    }
}
