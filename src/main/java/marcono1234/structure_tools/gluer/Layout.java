package marcono1234.structure_tools.gluer;

import static marcono1234.structure_tools.util.FunctionHelper.consumerNullable;
import static marcono1234.structure_tools.util.FunctionHelper.functionNullable;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;

import marcono1234.structure_tools.structure.Structure;
import marcono1234.structure_tools.util.ExceptionHelper;
import marcono1234.structure_tools.util.Int3D;
import marcono1234.structure_tools.util.NullSkippingIterable;

public class Layout {
    private static final Type JSON_CONTENT_TYPE = new TypeToken<List<List<List<Path>>>>() {}.getType();
    
    public static class StructureInfo {
        private final Int3D position;
        private final Structure structure;
        
        public StructureInfo(final Int3D position, final Structure structure) {
            this.position = position;
            this.structure = structure;
        }
        
        public Int3D getPosition() {
            return position;
        }
        
        public Structure getStructure() {
            return structure;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            else if (obj instanceof StructureInfo) {
                final StructureInfo other = (StructureInfo) obj;
                
                return position.equals(other.position)
                    && structure.equals(other.structure);
            }
            
            return false;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(position, structure);
        }
    }
    
    private final List<List<List<Structure>>> structures;
    private final Int3D dimensions;
    
    public Layout(final List<List<List<Structure>>> structures) {
        this(structures, true);
    }
    
    private Layout(final List<List<List<Structure>>> structures, final boolean copyLists) {
        final List<List<List<Structure>>> yList;
        
        if (copyLists) {
            yList = copy(structures);
        }
        else {
            yList = structures;
        }
        
        final Int3D dimensions = reverseAndRemoveTrailing(yList);
        
        if (dimensions.getX() == 0 || dimensions.getY() == 0 || dimensions.getZ() == 0) {
            throw new IllegalArgumentException("Layout dimensions have to be at least 1, 1, 1");
        }
        
        this.structures = yList;
        this.dimensions = dimensions;
    }
    
    private static List<List<List<Structure>>> copy(final List<List<List<Structure>>> structures) {
        return structures.stream()
            .map(functionNullable(zList -> zList.stream()
                .map(functionNullable(xList -> (List<Structure>) new ArrayList<>(xList)))
                .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());
    }
    
    private static Int3D reverseAndRemoveTrailing(final List<List<List<Structure>>> yList) {
        int dimX = 0;
        int dimZ = 0;
        
        /*
         * Execution order:
         *  1. Reverse list if necessary
         *  2. Remove trailings nulls (and empties)
         *  3. Iterate over non-null elements
         *      Repeat step 1 for them
         *  4. Remove trailing nulls and empties if necessary
         *      -> Removal for child elements might have turned them into 
         *          empty list
         */
        Collections.reverse(yList);
        
        for (final List<List<Structure>> zList : new NullSkippingIterable<>(removeTrailingNullsOrEmpties(yList))) {
            Collections.reverse(zList);
            
            for (final List<Structure> xList : new NullSkippingIterable<>(removeTrailingNullsOrEmpties(zList))) {
                removeTrailingNulls(xList);
                dimX = Math.max(dimX, xList.size());
            }
            
            removeTrailingNullsOrEmpties(zList);
            dimZ = Math.max(dimZ, zList.size());
        }
        
        removeTrailingNullsOrEmpties(yList);
        
        return new Int3D(dimX, yList.size(), dimZ);
    }
    
    private static <T> List<T> removeTrailing(final List<T> list, final Predicate<T> removalPredicate) {
        int index = list.size() - 1;
        
        for (; index >= 0 && removalPredicate.test(list.get(index)); index--) { }
        
        // Increase index to be at the first null element
        index++;
        
        if (index != list.size()) {
            list.subList(index, list.size()).clear();
        }
        
        return list;
    }
    
    /**
     * Removes trailing {@code null} elements and for convenience returns 
     * the list.
     * 
     * @param list
     *      The list to remove trailing {@code null} elements from
     * @return
     *      The modified given list
     */
    private static <T> List<T> removeTrailingNulls(final List<T> list) {
        return removeTrailing(list, Objects::isNull);
    }
    
    private static <T> List<List<T>> removeTrailingNullsOrEmpties(final List<List<T>> list) {
        return removeTrailing(list, ((Predicate<List<T>>) Objects::isNull).or(List::isEmpty));
    }
    
    public Int3D getDimensions() {
        return dimensions;
    }
    
    public Iterator<StructureInfo> createStructureInfoIterator() {
        return createIterator((x, y, z, structure) -> new StructureInfo(new Int3D(x, y, z), structure));
    }
    
    @FunctionalInterface
    private static interface IteratorItemCreator<T> {
        T create(int x, int y, int z, Structure structure);
    }
    
    private <T> Iterator<T> createIterator(final IteratorItemCreator<T> itemCreator) {
        return new Iterator<T>() {
            /*
             * Indices of the next structure; might be out of range. In this case 
             * hasNext() will return false
             */
            private int x = 0;
            private int y = 0;
            private int z = 0;
            
            private Structure next;
            private boolean hasNext;
            private boolean needsUpdate = true;
            
            {
                // Have to adjust indices in case 0, 0, 0 is not an element
                adjustIndices();
            }
            
            private void adjustIndices() {
                while (y < structures.size()) {
                    final List<List<Structure>> zList = structures.get(y);
                    
                    if (zList != null) {
                        while (z < zList.size()) {
                            final List<Structure> xList = zList.get(z);
                            
                            if (xList != null) {
                                while (x < xList.size()) {
                                    if (xList.get(x) != null) {
                                        return;
                                    }
                                    
                                    x++;
                                }
                            }
                            
                            x = 0;
                            z++;
                        }
                    }
                    
                    z = 0;
                    y++;
                }
            }
            
            /**
             * <p>Returns whether a next element exists and updates the cached next element 
             * stored in {@link #next} in case on exists.</p>
             * 
             * <p>If {@link #needsUpdate} and the parameter {@code shouldIncrement} are false, this 
             * method instantly returns, assuming the cached information is up to date. Otherwise 
             * it uses the indices of the next element to set it. If one of the indices is out of 
             * range there is no next element.</p>
             * 
             * <p>If {@code shouldIncrement} is true the indices of the next element are incremented 
             * after setting the current next element.</p>
             * 
             * @param shouldIncrement
             *      Whether the indices of the next element should be incremented after setting the 
             *      next element
             * @return
             *      Whether a next element exists
             */
            private boolean updateNext(final boolean shouldIncrement) {
                if (shouldIncrement || needsUpdate) {
                    needsUpdate = false;
                }
                else {
                    return hasNext;
                }
                
                if (y < structures.size()) {
                    next = structures.get(y).get(z).get(x);
                    
                    if (shouldIncrement) {
                        needsUpdate = true;
                        x++;
                        adjustIndices();
                    }
                    
                    return hasNext = true;
                }
                else {
                    return hasNext = false;
                }
            }
            
            @Override
            public boolean hasNext() {
                return updateNext(false);
            }

            @Override
            public T next() {
                // Have to store current values because updateNext() changes them
                final int oldX = x;
                final int oldY = y;
                final int oldZ = z;
                
                if (updateNext(true)) {
                    return itemCreator.create(
                        oldX,
                        oldY,
                        oldZ,
                        next
                    );
                }
                else {
                    throw new NoSuchElementException();
                }
            }
        };
    }
    
    public static Layout fromPathLists(final List<List<List<Path>>> layout) {
        return new Layout(layout.stream()
            .map(functionNullable(zList -> zList.stream()
                .map(functionNullable(xList -> xList.stream()
                    .map(functionNullable(
                        path -> ExceptionHelper.get(() -> Structure.readFromFile(path))
                    ))
                    .collect(Collectors.toList())
                ))
                .collect(Collectors.toList())
            ))
            .collect(Collectors.toList()),
            false // No need to copy list
        );
    }
    
    public static Layout fromInputStream(final InputStream inputStream) {
        return Layout.fromPathLists(GsonHelper.fromInputStream(inputStream, Layout.JSON_CONTENT_TYPE));
    }
    
    public static Layout fromStructureInfos(final Collection<StructureInfo> structureInfos) throws IllegalStateException {
        final List<List<List<Structure>>> yList = new ArrayList<>();
        
        structureInfos.forEach(structureInfo -> {
            final Int3D pos = structureInfo.getPosition();
            
            fillAndReplace(
                fillAndReplaceList(
                    fillAndReplaceList(
                        yList,
                        pos.getY()
                    ),
                    pos.getZ()
                ),
                pos.getX(),
                () -> structureInfo.getStructure(),
                true
            );
        });
        
        Collections.reverse(yList);
        yList.forEach(consumerNullable(Collections::reverse));
        
        return new Layout(yList, false);
    }
    
    private static <T> List<T> fillAndReplaceList(final List<List<T>> list, final int index ) {
        return fillAndReplace(list, index, ArrayList::new, false);
    }
    
    private static <T> T fillAndReplace(final List<T> list, final int index, final Supplier<T> elementCreator, final boolean expectsNull) throws IllegalStateException {
        while (index >= list.size()) {
            list.add(null);
        }
        
        T element = list.get(index);
        
        if (element == null) {
            element = elementCreator.get();
            list.set(index, element);
        }
        else if (expectsNull) {
            throw new IllegalStateException("Expected null element");
        }
        
        return element;
    }
}
