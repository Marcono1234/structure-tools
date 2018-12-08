package marcono1234.structure_tools.util;

import java.util.function.Function;

public class CommandLineArgument<T> {
    private final String name;
    private final String description;
    private final Function<String, T> parser;
    private T value;
    
    public CommandLineArgument(final String name, final String description, final Function<String, T> parser) {
        this.name = name;
        this.description = description;
        this.parser = parser;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void parse(final String arg) throws IllegalArgumentException {
        value = parser.apply(arg);
    }
    
    public T getValue() {
        return value;
    }
}
