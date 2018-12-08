package marcono1234.structure_tools.gluer.palette;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import marcono1234.structure_tools.gluer.palette.PaletteMap;
import marcono1234.structure_tools.gluer.palette.VariantsCountMismatch;
import marcono1234.structure_tools.structure.BlockState;

class PaletteMapTest {
    private final BlockState common1;
    private final BlockState common2;
    private final BlockState common3;
    private final BlockState a;
    private final BlockState b;
    private final BlockState a1;
    private final BlockState a2;
    private final BlockState b1;
    private final BlockState b2;
    
    private final List<List<BlockState>> singleVariant1;
    private final List<List<BlockState>> singleVariant2;
    private final List<List<BlockState>> singleVariantsMerged;
    private final List<List<BlockState>> multiVariant;
    
    PaletteMapTest() {
        common1 = new BlockState("common1");
        common2 = new BlockState("common2");
        common3 = new BlockState("common3");
        a = new BlockState("a");
        b = new BlockState("b");
        a1 = new BlockState("a1");
        a2 = new BlockState("a2");
        b1 = new BlockState("b1");
        b2 = new BlockState("b2");
        
        singleVariant1 = Arrays.asList(Arrays.asList(
            a,
            common1,
            b,
            common2,
            common3
        ));
        singleVariant2 = Arrays.asList(Arrays.asList(
            a1,
            common1,
            b1,
            common2,
            common3
        ));
        
        singleVariantsMerged = Arrays.asList(Arrays.asList(
            a,
            common1,
            b,
            common2,
            common3,
            a1,
            b1
        ));
        
        multiVariant = Arrays.asList(
            Arrays.asList(
                a1,
                common1,
                common2,
                b1,
                common3
            ),
            Arrays.asList(
                common1,
                common1,
                a2,
                b2,
                common3
            )
        );
    }
    
    @Test
    void testCreatePalette() {
        final PaletteMap palette = new PaletteMap();
        palette.putPalettes(singleVariant1);
        palette.putPalettes(singleVariant2);
        
        assertEquals(singleVariantsMerged, palette.createPalette());
    }
    
    @Test
    void testCreatePaletteSingleMulti() {
        final PaletteMap palette = new PaletteMap();
        palette.putPalettes(singleVariant1);
        palette.putPalettes(multiVariant);
        
        final List<List<BlockState>> expected = Arrays.asList(
            Arrays.asList(
                a,
                common1,
                b,
                common2,
                common3,
                a1,
                common2,
                b1
            ),
            Arrays.asList(
                a,
                common1,
                b,
                common2,
                common3,
                common1,
                a2,
                b2
            )
        );
        
        assertEquals(expected, palette.createPalette());
    }
    
    private static void validateIndices(final PaletteMap paletteMap, final List<List<BlockState>> palettesToPut, final List<List<BlockState>> expectedPalettes) {
        final List<Integer> stateIndices = paletteMap.putPalettes(palettesToPut);
        final List<List<BlockState>> createdPalette = paletteMap.createPalette();
        
        assertEquals(palettesToPut.get(0).size(), stateIndices.size());
        
        for (int index = 0; index < stateIndices.size(); index++) {
            assertEquals(palettesToPut.get(0).get(index), createdPalette.get(0).get(stateIndices.get(index)));
        }
    }
    
    @Test
    void testIndicesConversion() {
        final PaletteMap palette = new PaletteMap();
        validateIndices(palette, singleVariant1, singleVariant1);
        validateIndices(palette, singleVariant1, singleVariantsMerged);
    }
    
    @Test
    void testVariantsCountMismatch() {
        final PaletteMap palette = new PaletteMap();
        palette.putPalettes(multiVariant);
        
        final List<List<BlockState>> mismatchMulti = Arrays.asList(
            Arrays.asList(
                a,
                b 
            ),
            Arrays.asList(
                a1,
                b1
            ),
            Arrays.asList(
                a2,
                b2
            )
        );
        
        assertThrows(VariantsCountMismatch.class, () -> palette.putPalettes(mismatchMulti));
    }
    
    @Test
    void testSameVariants() {
        final PaletteMap palette = new PaletteMap();
        palette.putPalettes(multiVariant);
        
        // Effectively single variant
        final List<List<BlockState>> mismatchMulti = Arrays.asList(
            Arrays.asList(
                a,
                b 
            ),
            Arrays.asList(
                a,
                b
            ),
            Arrays.asList(
                a,
                b
            )
        );
        palette.putPalettes(mismatchMulti);
    }
    
    @Test
    void testEmptyPalettes() {
        final PaletteMap palette = new PaletteMap();
        final List<List<BlockState>> invalidPalette = Collections.emptyList();
        
        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> palette.putPalettes(invalidPalette)
        );
        
        assertEquals("Palettes has to contain at least one palette", exception.getMessage());
    }
    
    @Test
    void testPaletteVariantsMismatch() {
        final PaletteMap palette = new PaletteMap();
        final List<List<BlockState>> invalidPalette = Arrays.asList(
            Collections.singletonList(a),
            Arrays.asList(a, b)
        );
        
        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> palette.putPalettes(invalidPalette)
        );
        
        assertEquals("Not all palette variants have same size", exception.getMessage());
    }
}
