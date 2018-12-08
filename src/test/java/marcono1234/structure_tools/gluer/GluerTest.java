package marcono1234.structure_tools.gluer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

import marcono1234.structure_tools.gluer.Config;
import marcono1234.structure_tools.gluer.Gluer;
import marcono1234.structure_tools.gluer.Layout;
import marcono1234.structure_tools.gluer.Gluer.OutOfBoundsException;
import marcono1234.structure_tools.gluer.Layout.StructureInfo;
import marcono1234.structure_tools.structure.BlockInfo;
import marcono1234.structure_tools.structure.BlockState;
import marcono1234.structure_tools.structure.EntityInfo;
import marcono1234.structure_tools.structure.Structure;
import marcono1234.structure_tools.util.Double3D;
import marcono1234.structure_tools.util.Int3D;
import net.querz.nbt.CompoundTag;

class GluerTest {
    private final String author;   
    private final Gluer gluer;
    
    private final Int3D size;
    private final List<BlockInfo> blocks;
    private final List<List<BlockState>> palettes;
    private final List<EntityInfo> entities;
    
    private final List<Int3D> outOfBoundsSizes;
    private final List<Int3D> outOfBoundsInts;
    private final List<Double3D> outOfBoundsDoubles;
    private final List<Int3D> inBoundsInts;
    private final List<Double3D> inBoundsDoubles;
    
    GluerTest() {
        author = "authorValue";
        gluer = new Gluer(new Config(author, null, true));
        
        size = new Int3D(5, 5, 5);
        blocks = Collections.singletonList(new BlockInfo(new Int3D(1, 2, 3), 0));
        palettes = Collections.singletonList(Collections.singletonList(new BlockState("a")));
        entities = Collections.singletonList(new EntityInfo(
            new Double3D(1.5, 2.5, 3.5),
            new Int3D(1, 2, 3),
            new CompoundTag()
        ));
        
        outOfBoundsSizes = Arrays.asList(
            new Int3D(33, 1, 1),
            new Int3D(1, 33, 1),
            new Int3D(1, 1, 33)
        );
        
        outOfBoundsInts = Arrays.asList(
            new Int3D(32, 1, 1),
            new Int3D(1, 32, 1),
            new Int3D(1, 1, 32),
            new Int3D(-1, 1, 1),
            new Int3D(1, -1, 1),
            new Int3D(1, 1, -1)
        );
        
        outOfBoundsDoubles = Arrays.asList(
            new Double3D(32, 1, 1),
            new Double3D(1, 32, 1),
            new Double3D(1, 1, 32),
            new Double3D(-1, 1, 1),
            new Double3D(1, -1, 1),
            new Double3D(1, 1, -1)
        );
        
        inBoundsInts = Arrays.asList(
            new Int3D(31, 0, 0),
            new Int3D(0, 31, 0),
            new Int3D(0, 0, 31)
        );
        
        inBoundsDoubles = Arrays.asList(
            new Double3D(31.999, 0, 0),
            new Double3D(0, 31.999, 0),
            new Double3D(0, 0, 31.999)
        );
    }
    
    @Test
    void testGlue() throws IOException {
        final int sizeX = 32;
        final int sizeY = 32;
        final int sizeZ = 32;
        
        final int highestDataVersion = 1600;
        final Structure structure = new Structure(size, blocks, palettes, entities, 1500, null);
        final Structure structureDataVersion = new Structure(size, blocks, palettes, entities, highestDataVersion, null);
        
        final Layout layout = Layout.fromStructureInfos(Arrays.asList(
            new StructureInfo(new Int3D(0, 0, 0), structure),
            new StructureInfo(new Int3D(1, 1, 1), structureDataVersion)
        ));
        
        final Structure gluedStructure  = gluer.glue(layout);
        assertEquals(new Int3D(sizeX, sizeY, sizeZ), gluedStructure.getSize());
        assertEquals(highestDataVersion, gluedStructure.getDataVersion());
        assertEquals(author, gluedStructure.getAuthor().get());
        assertEquals(palettes, gluedStructure.getPalettes());
        
        final List<BlockInfo> expectedBlocks = Arrays.asList(
            blocks.get(0),
            new BlockInfo(new Int3D(1, 2, 3).add(sizeX, sizeY, sizeZ), 0)
        );
        assertEquals(expectedBlocks, gluedStructure.getBlocks());
        
        final List<EntityInfo> expectedEntities = Arrays.asList(
            entities.get(0),
            new EntityInfo(
                new Double3D(1.5, 2.5, 3.5).add(sizeX, sizeY, sizeZ),
                new Int3D(1, 2, 3).add(sizeX, sizeY, sizeZ),
                new CompoundTag()
            )
        );
        assertEquals(expectedEntities, gluedStructure.getEntities().get());
    }
    
    private void testOutOfBounds(final Structure structure) {
        final Layout layout = Layout.fromStructureInfos(Collections.singletonList(
            new StructureInfo(new Int3D(0, 0, 0), structure)
        ));
        
        assertThrows(OutOfBoundsException.class, () -> gluer.glue(layout));
    }
    
    /**
     * Increases the coordinate values if they are positive or 0 to compensate for 
     * size being checked inclusively and coordinates having to be > 1.
     * 
     * @param size
     *      Size to adjust
     * @return
     *      Adjusted size
     */
    private static Int3D adjustSize(final Int3D size) {
        int x = size.getX();
        int y = size.getY();
        int z = size.getZ();
        
        if (x >= 0) {
            x++;
        }
        if (y >= 0) {
            y++;
        }
        if (z >= 0) {
            z++;
        }
        
        return new Int3D(x, y, z);
    }
    
    @Test
    void testOutOfBoundsSize() {
        for (final Int3D outOfBounds : outOfBoundsSizes) {
            testOutOfBounds(new Structure(outOfBounds, blocks, palettes, null, 1500, author));
        }
    }
    
    @Test
    void testOutOfBoundsActualSize() {
        for (final Int3D outOfBounds : outOfBoundsSizes) {
            testOutOfBounds(new Structure(size, outOfBounds, blocks, palettes, null, 1500, author));
        }
    }
    
    @Test
    void testOutOfBoundsBlocks() {
        for (final Int3D outOfBounds : outOfBoundsInts) {
            testOutOfBounds(new Structure(
                size,
                Collections.singletonList(new BlockInfo(outOfBounds, 0)),
                palettes,
                null,
                1500,
                author
            ));
        }
    }
    
    @Test
    void testOutOfBoundsEntitiesPos() {
        for (final Double3D outOfBounds : outOfBoundsDoubles) {
            testOutOfBounds(new Structure(
                size,
                blocks,
                palettes,
                Collections.singletonList(new EntityInfo(
                    outOfBounds,
                    new Int3D(0, 0, 0),
                    new CompoundTag()
                )),
                1500,
                author
            ));
        }
    }
    
    @Test
    void testOutOfBoundsEntitiesBlockPos() {
        for (final Int3D outOfBounds : outOfBoundsInts) {
            testOutOfBounds(new Structure(
                size,
                blocks,
                palettes,
                Collections.singletonList(new EntityInfo(
                    new Double3D(0, 0, 0),
                    outOfBounds,
                    new CompoundTag()
                )),
                1500,
                author
            ));
        }
    }
    
    private void testInBounds(final Structure structure) {
        final Layout layout = Layout.fromStructureInfos(Collections.singletonList(
            new StructureInfo(new Int3D(0, 0, 0), structure)
        ));
        
        assertDoesNotThrow(() -> gluer.glue(layout));
    }
    
    @Test
    void testInBoundsSize() {
        for (final Int3D inBounds : inBoundsInts) {
            testInBounds(new Structure(adjustSize(inBounds), blocks, palettes, null, 1500, author));
        }
    }
    
    @Test
    void testInBoundsEntitiesPos() {
        for (final Double3D inBounds : inBoundsDoubles) {
            testInBounds(new Structure(
                size,
                blocks,
                palettes,
                Collections.singletonList(new EntityInfo(
                    inBounds,
                    new Int3D(0, 0, 0),
                    new CompoundTag()
                )),
                1500,
                author
            ));
        }
    }
    
    @Test
    void testInBoundsEntitiesBlockPos() {
        for (final Int3D inBounds : inBoundsInts) {
            testInBounds(new Structure(
                size,
                blocks,
                palettes,
                Collections.singletonList(new EntityInfo(
                    new Double3D(0, 0, 0),
                    inBounds,
                    new CompoundTag()
                )),
                1500,
                author
            ));
        }
    }
}
