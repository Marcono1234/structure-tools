package marcono1234.structure_tools.gluer;

public class Config {
    private final String author;
    private final Integer dataVersion;
    private final boolean writeFakeSize;
    
    public Config(final String author, final Integer dataVersion, final boolean writeFakeSize) {
        this.author = author;
        this.dataVersion = dataVersion;
        this.writeFakeSize = writeFakeSize;
    }
    
    public Config() {
        this(null, null, true);
    }
    
    /**
     * Returns the author to write into the glued structure. If the value is {@code null}, 
     * no author should be written.
     * 
     * @return
     *      The author to write into the glued structure, or {@code null}
     */
    public String getAuthor() {
        return author;
    }
    
    /**
     * Returns the Minecraft data version to write into the glued structure. If the value 
     * is {@code null}, the maximum of the data versions of all structures should be used.
     * 
     * @return
     *      The Minecraft data version to use for the glued structure, or {@code null}
     */
    public Integer getDataVersion() {
        return dataVersion;
    }
    
    /**
     * <p>Returns whether the maximum vanilla Minecraft structure size should be written 
     * into the glued structure instead of the actual total size.</p>
     * 
     * <p>This allows loading the structure using the "Load" button within the GUI. The 
     * actual total size is written into a custom tag not used by the game.</p>
     * 
     * @return
     *      Whether the maximum vanilla Minecraft structure size should be written
     */
    public boolean shouldWriteFakeSize() {
        return writeFakeSize;
    }
}
