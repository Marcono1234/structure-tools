package marcono1234.structure_tools.structure;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import marcono1234.structure_tools.util.Int3D;
import marcono1234.structure_tools.util.NbtCollectors;
import marcono1234.structure_tools.util.NbtUtils;
import net.querz.nbt.CompoundTag;
import net.querz.nbt.ListTag;
import net.querz.nbt.NBTUtil;

public class Structure {
    /**
     * Tests whether all coordinates are > 0
     */
    private static final Predicate<Int3D> VALID_SIZE_PREDICATE = Int3D.createAllCoordinatesPredicate(
       coordinate -> coordinate > 0
    );
    
    private static final Int3D MAX_VANILLA_SIZE = new Int3D(32, 32, 32);
    /**
     * Tests whether all coordinates are <= the respective {@link #MAX_VANILLA_SIZE} 
     * coordinate
     */
    private static final Predicate<Int3D> VALID_VANILLA_SIZE_PREDICATE = Int3D.createAllCoordinatesPredicate(
        (sizeCoordinate, maxSizeCoordinate) -> sizeCoordinate <= maxSizeCoordinate,
        MAX_VANILLA_SIZE
    );
    
    private static final String SIZE_KEY = "size";
    private static final String BLOCKS_KEY = "blocks";
    private static final String MULTI_PALETTES_KEY = "palettes";
    private static final String SINGLE_PALETTE_KEY = "palette";
    private static final String ENTITIES_KEY = "entities";
    private static final String DATA_VERSION_KEY = "DataVersion";
    // Currently not written and read by Minecraft anymore
    private static final String AUTHOR_KEY = "author";
    
    /**
     * Key used for writing custom data
     */
    private static final String STRUCTURE_TOOLS_KEY = "structure-tools";
    
    private final Int3D size;
    private final Optional<Int3D> actualSize;
    private final List<BlockInfo> blocks;
    private final List<List<BlockState>> palettes;
    // entities are only optional because if not present Minecraft returns an empty List
    private final Optional<List<EntityInfo>> entities;
    private final int dataVersion;
    private final Optional<String> author;
    
    public Structure(final Int3D size, final Int3D actualSize, final List<BlockInfo> blocks, final List<List<BlockState>> palettes, final List<EntityInfo> entities, final int dataVersion, final String author) throws IllegalArgumentException {
        this.size = verifyValidSize(size);
        
        if (actualSize == null) {
            this.actualSize = Optional.empty();
        }
        else {
            this.actualSize = Optional.of(verifyActualLarger(verifyValidSize(actualSize)));
        }
        
        this.blocks = Objects.requireNonNull(blocks);
        this.palettes = Objects.requireNonNull(palettes);
        this.entities = Optional.ofNullable(entities);
        this.dataVersion = dataVersion;
        this.author = Optional.ofNullable(author);
    }
    
    public Structure(final Int3D size, final List<BlockInfo> blocks, final List<List<BlockState>> palettes, final List<EntityInfo> entities, final int dataVersion, final String author) {
        this(size, null, blocks, palettes, entities, dataVersion, author);
    }
    
    private static Int3D verifyValidSize(final Int3D size) throws IllegalArgumentException {
        if (VALID_SIZE_PREDICATE.test(size)) {
            return size;
        }
        else {
            throw new IllegalArgumentException(String.format("Size %s is invalid", size));
        }
    }
    
    private Int3D verifyActualLarger(final Int3D actualSize) throws IllegalArgumentException {
        final Predicate<Int3D> actualSizePredicate = Int3D.createAnyCoordinatesPredicate(
            (actualSizeCoordinate, sizeCoordinate) -> actualSizeCoordinate > sizeCoordinate,
            size
        );
        
        if (actualSizePredicate.test(actualSize)) {
            return actualSize;
        }
        else {
            throw new IllegalArgumentException("Actual size is not larger");
        }
    }
    
    public Int3D getSize() {
        return size;
    }
    
    // TODO Doc
    // TODO Use in some parts instead of getSize() (?)
    public Int3D getActualSize() {
        return actualSize.orElse(size);
    }
    
    public List<BlockInfo> getBlocks() {
        return blocks;
    }
    
    public List<List<BlockState>> getPalettes() {
        return palettes;
    }
    
    public Optional<List<EntityInfo>> getEntities() {
        return entities;
    }
    
    public int getDataVersion() {
        return dataVersion;
    }
    
    public Optional<String> getAuthor() {
        return author;
    }
    
    public CompoundTag toNbt() {
        final CompoundTag compound = new CompoundTag();
        
        compound.put(SIZE_KEY, size.toNbt());
        compound.put(BLOCKS_KEY, blocks.stream()
            .map(BlockInfo::toNbt)
            .collect(NbtCollectors.toList())
        );
        
        if (palettes.size() == 1) {
            compound.put(SINGLE_PALETTE_KEY, paletteToNbt(palettes.get(0)));
        }
        else {
            compound.put(MULTI_PALETTES_KEY, palettes.stream()
                .map(Structure::paletteToNbt)
                .collect(NbtCollectors.toList())
            );
        }
        
        if (entities.isPresent()) {
            compound.put(ENTITIES_KEY, entities.get().stream()
                .map(EntityInfo::toNbt)
                .collect(NbtCollectors.toList()));
        }
        
        compound.putInt(DATA_VERSION_KEY, dataVersion);
        
        if (author.isPresent()) {
            compound.putString(AUTHOR_KEY, author.get());
        }
        
        if (actualSize.isPresent()) {
            final CompoundTag structureToolsCompound = new CompoundTag();
            structureToolsCompound.put(SIZE_KEY, actualSize.get().toNbt());
            
            compound.put(STRUCTURE_TOOLS_KEY, structureToolsCompound);
        }
        
        return compound;
    }
    
    private static ListTag<CompoundTag> paletteToNbt(final List<BlockState> palette) {
        return palette.stream()
            .map(BlockState::toNbt)
            .collect(NbtCollectors.toList());
    }
    
    public static Structure fromNbt(final CompoundTag compound) {
        final List<List<BlockState>> palettes;
        
        if (compound.containsKey(MULTI_PALETTES_KEY)) {
            final ListTag<ListTag<?>> palettesList = compound.getListTag(MULTI_PALETTES_KEY).asListTagList();
            palettes = new ArrayList<>(palettesList.size());
            
            palettesList.forEach(palette -> palettes.add(paletteFromNbt(palette.asCompoundTagList())));
        }
        else {
            palettes = Collections.singletonList(paletteFromNbt(compound.getListTag(SINGLE_PALETTE_KEY).asCompoundTagList()));
        }
        
        Int3D actualSize = null;
        
        if (compound.containsKey(STRUCTURE_TOOLS_KEY)) {
            final CompoundTag structureToolsCompound = compound.getCompoundTag(STRUCTURE_TOOLS_KEY);
            
            if (structureToolsCompound.containsKey(SIZE_KEY)) {
                actualSize = Int3D.fromNbt(structureToolsCompound.getListTag(SIZE_KEY).asIntTagList());
            }
        }
        
        return new Structure(
            Int3D.fromNbt(compound.getListTag(SIZE_KEY).asIntTagList()),
            actualSize,
            NbtUtils.fromNbtList(compound.getListTag(BLOCKS_KEY).asCompoundTagList(), BlockInfo::fromNbt),
            palettes,
            compound.containsKey(ENTITIES_KEY) ? NbtUtils.fromNbtList(compound.getListTag(ENTITIES_KEY).asCompoundTagList(), EntityInfo::fromNbt) : null,
            compound.getInt(DATA_VERSION_KEY),
            compound.containsKey(AUTHOR_KEY) ? compound.getString(AUTHOR_KEY) : null
        );
    }
    
    public static Structure readFromFile(final Path path) throws IOException {
        return fromNbt((CompoundTag) NBTUtil.readTag(path.toFile()));
    }
    
    private static List<BlockState> paletteFromNbt(final ListTag<CompoundTag> palette) {
        return NbtUtils.fromNbtList(palette, BlockState::fromNbt);
    }
    
    /**
     * If the given size is larger than the maximum vanilla Minecraft size, the maximum 
     * size is used and the given size is used as {@code actualSize}.
     * 
     * @see #Structure(Int3D, Int3D, List, List, List, int, String)
     */
    public static Structure createWithFakeSizeIfNeeded(final Int3D size, final List<BlockInfo> blocks, final List<List<BlockState>> palettes, final List<EntityInfo> entities, final int dataVersion, final String author) throws IllegalArgumentException {
        final Int3D sizeToUse;
        final Int3D actualSize;
        
        if (VALID_VANILLA_SIZE_PREDICATE.test(size)) {
            sizeToUse = size;
            actualSize = null;
        }
        else {
            sizeToUse = MAX_VANILLA_SIZE;
            actualSize = size;
        }
        
        return new Structure(sizeToUse, actualSize, blocks, palettes, entities, dataVersion, author);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        else if (obj instanceof Structure) {
            final Structure other = (Structure) obj;
            
            return size.equals(other.size)
                && actualSize.equals(other.actualSize)
                && blocks.equals(other.blocks)
                && palettes.equals(other.palettes)
                && entities.equals(other.entities)
                && dataVersion == other.dataVersion
                && author.equals(other.author);
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(
            size,
            actualSize,
            blocks,
            palettes,
            entities,
            dataVersion,
            author
        );
    }
}
