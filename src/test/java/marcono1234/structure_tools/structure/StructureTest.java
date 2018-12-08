package marcono1234.structure_tools.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

import marcono1234.structure_tools.structure.BlockInfo;
import marcono1234.structure_tools.structure.BlockState;
import marcono1234.structure_tools.structure.EntityInfo;
import marcono1234.structure_tools.structure.Structure;
import marcono1234.structure_tools.util.Double3D;
import marcono1234.structure_tools.util.Int3D;
import marcono1234.structure_tools.util.NbtCollectors;
import marcono1234.structure_tools.util.NbtUtils;
import net.querz.nbt.CompoundTag;

class StructureTest {
    private final Int3D size;
    private final List<BlockInfo> blocks;
    private final List<List<BlockState>> singlePalettes;
    private final Structure structure;
    private final CompoundTag structureNbt;
    
    private final Int3D actualSize;
    private final Structure structureOptionals;
    private final CompoundTag structureOptionalsNbt;
    
    private final List<Int3D> invalidSizes;
    
    StructureTest() {
        size = new Int3D(11, 12, 13);
        actualSize = new Int3D(15, 15, 15);
        blocks = Arrays.asList(
            new BlockInfo(new Int3D(1, 2, 3), 0),
            new BlockInfo(new Int3D(7, 8, 9), 2),
            new BlockInfo(new Int3D(4, 5, 6), 1)
        );
        singlePalettes = Arrays.asList(
            Arrays.asList(
                new BlockState("a"),
                new BlockState("b"),
                new BlockState("c")
            )
        );
        final List<List<BlockState>> multiPalettes = Arrays.asList(
            Arrays.asList(
                new BlockState("a"),
                new BlockState("b"),
                new BlockState("c")
            ),
            Arrays.asList(
                new BlockState("a1"),
                new BlockState("b1"),
                new BlockState("c1")
            )
        );
        final List<EntityInfo> entities = Arrays.asList(
            new EntityInfo(new Double3D(1.5, 2.5, 3.5), new Int3D(1, 2, 3), new CompoundTag()),
            new EntityInfo(new Double3D(4.5, 5.5, 6.5), new Int3D(4, 5, 6), new CompoundTag())
        );
        final int dataVersion = 1500;
        final String author = "authorValue";
        
        structure = new Structure(
            size,
            blocks,
            singlePalettes,
            null,
            dataVersion,
            null
        );
        structureNbt = new CompoundTag();
        structureNbt.put("size", size.toNbt());
        structureNbt.put("blocks", NbtUtils.toNbtList(blocks, BlockInfo::toNbt));
        structureNbt.putInt("DataVersion", dataVersion);
        
        structureOptionals = new Structure(
            size,
            actualSize,
            blocks,
            multiPalettes,
            entities,
            dataVersion,
            author
        );
        structureOptionalsNbt = structureNbt.clone();
        structureOptionalsNbt.put("palettes", multiPalettes.stream()
            .map(list -> NbtUtils.toNbtList(list, BlockState::toNbt))
            .collect(NbtCollectors.toList())
        );
        structureOptionalsNbt.put("entities", NbtUtils.toNbtList(entities, EntityInfo::toNbt));
        structureOptionalsNbt.putString("author", author);
        final CompoundTag structureToolsCompound = new CompoundTag();
        structureToolsCompound.put("size", actualSize.toNbt());
        structureOptionalsNbt.put("structure-tools", structureToolsCompound);
        
        // Put palette here to not copy it to structureOptionalsNbt
        structureNbt.put(
            "palette",
            NbtUtils.toNbtList(singlePalettes.get(0), BlockState::toNbt)
        );
        
        invalidSizes = Arrays.asList(
            new Int3D(-1 , 1, 1),
            new Int3D(1 , -1, 1),
            new Int3D(1 , 1, -1)
        );
    }
    
    @Test
    void testFromNbt() {
        assertEquals(structure, Structure.fromNbt(structureNbt));
    }
    
    @Test
    void testToNbt() {
        assertEquals(structureNbt, structure.toNbt());
    }
    
    @Test
    void testFromNbtOptionals() {
        assertEquals(structureOptionals, Structure.fromNbt(structureOptionalsNbt));
    }
    
    @Test
    void testToNbtOptionals() {
        assertEquals(structureOptionalsNbt, structureOptionals.toNbt());
    }
    
    @Test
    void testActualSize() {
        assertEquals(structure.getSize(), structure.getActualSize());
        assertEquals(size, structure.getActualSize());
        assertEquals(actualSize, structureOptionals.getActualSize());
    }
    
    private void testInvalidSize(final Function<Int3D, Structure> structureCreator) {
        for (final Int3D invalidSize : invalidSizes) {
            final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> structureCreator.apply(invalidSize)
            );
            
            assertEquals(String.format("Size %s is invalid", invalidSize), exception.getMessage());
        }
    }
    
    @Test
    void testInvalidSize() {
        testInvalidSize(invalidSize -> new Structure(invalidSize, blocks, singlePalettes, null, 1500, null));
    }
    
    @Test
    void testInvalidActualSize() {
        testInvalidSize(invalidSize -> new Structure(size, invalidSize, blocks, singlePalettes, null, 1500, null));
    }
    
    @Test
    void testSmallerActualSize() {
        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Structure(new Int3D(2, 3, 4), new Int3D(1, 2, 3), blocks, singlePalettes, null, 1500, null)
        );
        
        assertEquals("Actual size is not larger", exception.getMessage());
    }
    
    @Test
    void testNeededActualSize() {
        final Int3D maxVanilla = new Int3D(32, 32, 32);
        final List<Int3D> largerSizes = Arrays.asList(
            new Int3D(maxVanilla.getX() + 1, 1, 1),
            new Int3D(1, maxVanilla.getY() + 1, 1),
            new Int3D(1, 1, maxVanilla.getZ() + 1)
        );
        
        for (final Int3D size : largerSizes) {
            final Structure structure = Structure.createWithFakeSizeIfNeeded(size, blocks, singlePalettes, null, 1500, null);
            assertEquals(maxVanilla, structure.getSize());
            assertEquals(size, structure.getActualSize());
        }
    }
}
