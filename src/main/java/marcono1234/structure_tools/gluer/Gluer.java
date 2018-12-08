package marcono1234.structure_tools.gluer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import marcono1234.structure_tools.gluer.Layout.StructureInfo;
import marcono1234.structure_tools.gluer.palette.PaletteMap;
import marcono1234.structure_tools.structure.BlockInfo;
import marcono1234.structure_tools.structure.BlockState;
import marcono1234.structure_tools.structure.EntityInfo;
import marcono1234.structure_tools.structure.Structure;
import marcono1234.structure_tools.util.Double3D;
import marcono1234.structure_tools.util.Int3D;

public class Gluer {
    private static final Logger logger = LogManager.getLogger();
    
    @FunctionalInterface
    private static interface StructureCreator {
        Structure create(Int3D size, List<BlockInfo> blocks, List<List<BlockState>> palettes, List<EntityInfo> entities, int dataVersion, String author) throws IllegalArgumentException;
    }
    
    private static final Int3D STRUCTURE_PIECES_SIZE = new Int3D(32, 32, 32);
    
    @SuppressWarnings("serial")
    public class OutOfBoundsException extends IllegalArgumentException {
        private final Int3D bounds;
        
        private OutOfBoundsException(final String description) {
            super(description + " is out of bounds: " + Gluer.this.structureSize);
            this.bounds = Gluer.this.structureSize;
        }
        
        public Int3D getBounds() {
            return bounds;
        }
    }
    
    private final Int3D structureSize;
    private final Config config;
    
    public Gluer(final Config config) {
        this.structureSize = STRUCTURE_PIECES_SIZE;
        this.config = config;
    }
    
    public Structure glue(final Layout layout) {
        final Iterator<StructureInfo> structureInfos = layout.createStructureInfoIterator();
        final PaletteMap palette = new PaletteMap();
        final List<BlockInfo> blocks = new ArrayList<>();
        final List<EntityInfo> entities = new ArrayList<>();
        Integer dataVersion = config.getDataVersion();
        boolean loggedDataVersionWarning = false;
        
        while (structureInfos.hasNext()) {
            final StructureInfo structureInfo = structureInfos.next();
            
            final Int3D structurePos = structureInfo.getPosition();
            final Structure structure = structureInfo.getStructure();
            validateBounds(structure);
            
            final int offsetX = structurePos.getX() * structureSize.getX();
            final int offsetY = structurePos.getY() * structureSize.getY();
            final int offsetZ = structurePos.getZ() * structureSize.getZ();
            final List<Integer> stateIndices = palette.putPalettes(structure.getPalettes());
            
            structure.getBlocks().forEach(
                blockInfo -> blocks.add(new BlockInfo(
                    blockInfo.getPos().add(offsetX, offsetY, offsetZ),
                    stateIndices.get(blockInfo.getState()),
                    blockInfo.getNbt()
                ))
            );
            
            structure.getEntities().ifPresent(entitiesList -> entitiesList.forEach(
                entityInfo -> entities.add(new EntityInfo(
                    entityInfo.getPos().add(offsetX, offsetY, offsetZ),
                    entityInfo.getBlockPos().add(offsetX, offsetY, offsetZ),
                    entityInfo.getNbt()
                ))
            ));
            
            final int structureDataVersion = structure.getDataVersion();
            
            if (dataVersion == null) {
                dataVersion = structureDataVersion;
            }
            else if (dataVersion != structureDataVersion) {
                if (config.getDataVersion() == null) {
                    if (!loggedDataVersionWarning) {
                        loggedDataVersionWarning = true;
                        logger.warn("Not all structures have same DataVersion; using highest one");
                    }
                    
                    dataVersion = Math.max(dataVersion, structureDataVersion);
                }
                else if (!loggedDataVersionWarning) {
                    loggedDataVersionWarning = true;
                    logger.warn("Not all structures have same DataVersion as specified one");
                }
            }
        }
        
        final Int3D dimensions = layout.getDimensions();
        final Int3D actualSize = new Int3D(
            dimensions.getX() * structureSize.getX(),
            dimensions.getY() * structureSize.getY(),
            dimensions.getZ() * structureSize.getZ()
        );
        
        final StructureCreator structureCreator;
        
        if (config.shouldWriteFakeSize()) {
            structureCreator = Structure::createWithFakeSizeIfNeeded;
        }
        else {
            structureCreator = Structure::new;
        }
        
        return structureCreator.create(
            actualSize,
            blocks,
            palette.createPalette(),
            entities.isEmpty() ? null : entities,
            dataVersion,
            config.getAuthor()
        );
    }
    
    private void validateBounds(final Structure structure) throws OutOfBoundsException {
        if (!isInBoundsInclusive(structure.getSize())) {
            throw new OutOfBoundsException(String.format("Structure size %s", structure.getSize()));
        }
        if (!isInBoundsInclusive(structure.getActualSize())) {
            throw new OutOfBoundsException(String.format("Actual structure size %s", structure.getActualSize()));
        }
        
        for (final BlockInfo blockInfo : structure.getBlocks()) {
            final Int3D blockInfoPos = blockInfo.getPos();
            
            if (!isInBounds(blockInfoPos)) {
                throw new OutOfBoundsException(String.format("Block info pos %s", blockInfoPos));
            }
        }
        
        if (structure.getEntities().isPresent()) {
            for (final EntityInfo entityInfo : structure.getEntities().get()) {
                final Double3D entityPos = entityInfo.getPos();
                
                if (!isInBounds(entityPos)) {
                    throw new OutOfBoundsException(String.format("Entity pos %s", entityPos));
                }
                
                final Int3D entityBlockPos = entityInfo.getBlockPos();
                
                if (!isInBounds(entityBlockPos)) {
                    throw new OutOfBoundsException(String.format("Entity block pos %s", entityBlockPos));
                }
                
            }
        }
    }
    
    private boolean isInBounds(final Int3D pos, final BiPredicate<Integer, Integer> predicate) {
        return Int3D.createAllCoordinatesPredicate(
            predicate.and((value, bound) -> value >= 0),
            structureSize
        ).test(pos);
    }
    
    private boolean isInBounds(final Int3D pos) {
        return isInBounds(pos, (value, bound) -> value < bound);
    }
    
    private boolean isInBoundsInclusive(final Int3D pos) {
        return isInBounds(pos, (value, bound) -> value <= bound);
    }
    
    private boolean isInBounds(final Double3D pos, final BiPredicate<Double, Integer> predicate) {
        return predicate.test(pos.getX(), structureSize.getX())
            && predicate.test(pos.getY(), structureSize.getY())
            && predicate.test(pos.getZ(), structureSize.getZ());
    }
    
    private boolean isInBounds(final Double3D pos) {
        return isInBounds(pos, (value, bound) -> value >= 0 && value < bound);
    }
}
