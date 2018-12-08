package marcono1234.structure_tools.gluer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.in;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import marcono1234.structure_tools.gluer.Layout;
import marcono1234.structure_tools.gluer.Layout.StructureInfo;
import marcono1234.structure_tools.structure.BlockInfo;
import marcono1234.structure_tools.structure.BlockState;
import marcono1234.structure_tools.structure.Structure;
import marcono1234.structure_tools.util.Int3D;

class LayoutTest {
    private static Int3D sizeToCoordinates(final Int3D size) {
        return new Int3D(size.getX() - 1, size.getY() - 1, size.getZ() - 1);
    }
    
    @Test
    void testReadLayout() throws IOException {
        final Layout layout;
        
        try (InputStream layoutInputStream = GluerTest.class.getResourceAsStream("/layout.json")) {
            layout = Layout.fromInputStream(layoutInputStream);
        }
        
        final int expectedStructuresCount = 8;
        int structuresCount = 0;
        final Iterator<StructureInfo> structureInfos = layout.createStructureInfoIterator();
        
        while (structureInfos.hasNext()) {
            structuresCount++;
            
            final StructureInfo structureInfo = structureInfos.next();
            // Expected position is stored in size
            assertEquals(
                sizeToCoordinates(structureInfo.getStructure().getSize()),
                structureInfo.getPosition()
            );
        }
        
        assertEquals(expectedStructuresCount, structuresCount);
    }
    
    private void testTrailingRemoval(final String layoutResourcePath) throws IOException {
        final Layout layout;
        
        try (InputStream layoutInputStream = GluerTest.class.getResourceAsStream(layoutResourcePath)) {
            layout = Layout.fromInputStream(layoutInputStream);
        }
        
        final int expectedStructuresCount = 2;
        int structuresCount = 0;
        final Iterator<StructureInfo> structureInfos = layout.createStructureInfoIterator();
        
        while (structureInfos.hasNext()) {
            structuresCount++;
            
            final StructureInfo structureInfo = structureInfos.next();
            // Expected position is stored in size
            assertEquals(
                sizeToCoordinates(structureInfo.getStructure().getSize()),
                structureInfo.getPosition()
            );
        }
        
        assertEquals(expectedStructuresCount, structuresCount);
        
        assertEquals(new Int3D(1, 1, 2), layout.getDimensions());
    }
    
    @Test
    void testRemoveTrailingNulls() throws IOException {
        testTrailingRemoval("/layout_trailing_nulls.json");
    }
    
    @Test
    void testRemoveTrailingEmpties() throws IOException {
        testTrailingRemoval("/layout_trailing_empties.json");
    }
    
    @Test
    void testInvalidDimensions() {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Layout(Arrays.asList(
            Arrays.asList(
                Collections.emptyList()
            ),
            Collections.emptyList()
        )));
        
        assertEquals("Layout dimensions have to be at least 1, 1, 1", exception.getMessage());
    }
    
    @Test
    void testFromStructureInfos() {
        final Structure structure = new Structure(
            new Int3D(1, 1, 1),
            Collections.singletonList(new BlockInfo(new Int3D(0, 0, 0), 0)),
            Collections.singletonList(Collections.singletonList(new BlockState("a"))),
            null,
            1500,
            null
        );
        
        final Collection<StructureInfo> structureInfos = Arrays.asList(
            new StructureInfo(new Int3D(1, 5, 7), structure),
            new StructureInfo(new Int3D(3, 2, 4), structure),
            new StructureInfo(new Int3D(2, 7, 1), structure)
        );
        
        int createdStructureInfosCount = 0;
        final Iterator<StructureInfo> createdStructureInfos = Layout.fromStructureInfos(structureInfos).createStructureInfoIterator();
        
        while (createdStructureInfos.hasNext()) {
            createdStructureInfosCount++;
            
            assertThat(createdStructureInfos.next(), in(structureInfos));
        }
        
        assertEquals(structureInfos.size(), createdStructureInfosCount);
        
        final List<StructureInfo> invalidStructureInfos = new ArrayList<>(structureInfos);
        invalidStructureInfos.add(invalidStructureInfos.get(0));
        final IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> Layout.fromStructureInfos(invalidStructureInfos)
        );
        
        assertEquals("Expected null element", exception.getMessage());
    }
}
