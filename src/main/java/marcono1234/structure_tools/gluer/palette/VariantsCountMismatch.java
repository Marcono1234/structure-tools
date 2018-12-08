package marcono1234.structure_tools.gluer.palette;

@SuppressWarnings("serial")
public class VariantsCountMismatch extends IllegalArgumentException {
    private final int previousVariantsCount;
    private final int newVariantsCount;
    
    public VariantsCountMismatch(final int previousVariantsCount, final int newVariantsCount) {
        super(String.format("Variants count mismatch: Previous %d, new %d",
            previousVariantsCount,
            newVariantsCount
        ));
        
        this.previousVariantsCount = previousVariantsCount;
        this.newVariantsCount = newVariantsCount;
    }
    
    public int getPreviousVariantsCount() {
        return previousVariantsCount;
    }
    
    public int getNewVariantsCount() {
        return newVariantsCount;
    }
}