package marcono1234.structure_tools.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class NullSkippingIterable<T> implements Iterable<T> {
    private final Iterable<T> delegate;
    
    public NullSkippingIterable(final Iterable<T> delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Iterator<T> delegate = NullSkippingIterable.this.delegate.iterator();
            private T next;
            
            private boolean peekNext() {
                while (delegate.hasNext() && (next = delegate.next()) == null) { }
                
                return next != null;
            }
            
            @Override
            public boolean hasNext() {
                return next == null
                    ? peekNext()
                    : true;
            }

            @Override
            public T next() {
                if (hasNext()) {
                    final T current = next;
                    next = null;
                    
                    return current;
                }
                else {
                    throw new NoSuchElementException();
                }
            }
        };
    }
}
