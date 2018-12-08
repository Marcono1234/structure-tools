package marcono1234.structure_tools.gluer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import marcono1234.structure_tools.structure.Structure;
import marcono1234.structure_tools.util.CommandLineArgument;
import marcono1234.structure_tools.util.InputStreamHelper;
import net.querz.nbt.NBTUtil;

public class GluerCommandLine {
    private static final Logger logger = LogManager.getLogger();
    
    private static final String IDENT = "  ";
    private static final Function<String, Path> EXISTING_FILE_PARSER = pathString -> {
        final Path path = Paths.get(pathString);
        
        if (Files.isRegularFile(path)) {
            return path;
        }
        else {
            throw new IllegalArgumentException(String.format("File path '%s' does not locate an existing file", path));
        }
    };
    
    private final List<CommandLineArgument<?>> arguments;
    private final CommandLineArgument<Path> configArg;
    private final CommandLineArgument<Path> layoutArg;
    private final CommandLineArgument<Path> outputPathArg;
    
    private GluerCommandLine() {
        arguments = new ArrayList<>();
        configArg = registerArgument(new CommandLineArgument<>(
            "config",
            "JSON file containing config for creating the structure",
            EXISTING_FILE_PARSER
        ));
        layoutArg = registerArgument(new CommandLineArgument<>(
            "layout",
            "JSON file describing how the single structures should be layed out "
            + "to create the glued structure",
            EXISTING_FILE_PARSER
        ));
        outputPathArg = registerArgument(new CommandLineArgument<>(
            "output path",
            "File path which should be used for the created structure",
            Paths::get
        ));
    }
    
    private <T> CommandLineArgument<T> registerArgument(final CommandLineArgument<T> arg) {
        arguments.add(arg);
        return arg;
    }
    
    private void parseArgs(final String[] args) {
        final int argsLength = args.length;
        final int expectedArgsLength = arguments.size();
        
        if (argsLength > expectedArgsLength) {
            logger.error(String.format("Too many arguments; expected %d, got %d", argsLength, expectedArgsLength));
        }
        else if (argsLength < expectedArgsLength) {
            final StringBuilder errorMessageBuilder = new StringBuilder("Too few arguments; the following arguments are missing:");
            
            for (int index = argsLength; index < expectedArgsLength; index++) {
                final CommandLineArgument<?> arg = arguments.get(index);
                
                errorMessageBuilder.append('\n');
                errorMessageBuilder.append(String.format("- %s", arg.getName()));
                errorMessageBuilder.append('\n');
                errorMessageBuilder.append(IDENT + String.join(IDENT, arg.getDescription().split("\n")));
            }
            
            logger.error(errorMessageBuilder.toString());
        }
        else {
            for (int argIndex = 0; argIndex < arguments.size(); argIndex++) {
                final CommandLineArgument<?> cmdArg = arguments.get(argIndex);
                
                try {
                    cmdArg.parse(args[argIndex]);
                }
                catch (final RuntimeException runtimeException) {
                    logger.error(
                        String.format(
                            "Failed parsing argument '%s' (index %d)",
                            cmdArg.getName(),
                            argIndex
                        ),
                        runtimeException
                    );
                    
                    return;
                }
            }
            
            try {
                final Config config = GsonHelper.fromFile(configArg.getValue(), Config.class);
                final Layout layout = InputStreamHelper.handleFile(layoutArg.getValue(), Layout::fromInputStream);
                final Structure gluedStructure = new Gluer(config).glue(layout);
                
                NBTUtil.writeTag(gluedStructure.toNbt(), outputPathArg.getValue().toFile());
            }
            catch (final Exception exception) {
                logger.error("Could not glue structures", exception);
            }
        }
    }
    
    public static void main(final String[] args) {
        if (!isRunningFromCommandLine()) {
            logger.info("Informing user that program has to be used from command line");
            
            try {
                SwingUtilities.invokeAndWait(() -> {
                    JOptionPane.showMessageDialog(
                        null,
                        "Please run from command line",
                        "structure-gluer",
                        JOptionPane.ERROR_MESSAGE
                    );
                });
            }
            catch (final InvocationTargetException invocationTargetException) {
                logger.error("Could not show message dialog", invocationTargetException);
            }
            catch (final InterruptedException interruptedException) {
                logger.error("Was interrupted while waiting for dialog", interruptedException);
                Thread.currentThread().interrupt();
            }
        }
        else if (args.length == 0) {
            printHelp();
        }
        else {
            final String option = args[0];
            final int optionArgsCount = args.length - 1;
            final String[] optionArgs = new String[optionArgsCount];
            System.arraycopy(args, 1, optionArgs, 0, optionArgsCount);
            
            if (option.equals("help")) {
                if (optionArgsCount > 0) {
                    logger.error(String.format("Expected 0 arguments, but got %d", optionArgsCount));
                }
                else {
                    printHelp();
                }
            }
            else if (option.equals("command")) {
                if (optionArgsCount == 0) {
                    logger.error("Expected command");
                }
                else {
                    final String command = optionArgs[0];
                    final String[] commandArgs = new String[optionArgsCount - 1];
                    System.arraycopy(optionArgs, 1, commandArgs, 0, optionArgsCount - 1);
                    handleCommand(command, commandArgs);
                }
            }
            else {
                logger.error(String.format("Option '%s' is unknown", option));
            }
        }
    }
    
    private static void handleCommand(final String command, final String... args) {
        if ("glue".equals(command)) {
            new GluerCommandLine().parseArgs(args);
        }
        else {
            logger.error(String.format("Command '%s' is unknown", command));
        }
    }
    
    /**
     * <p>Checks if the program is running from command line.</p>
     * 
     * <p><b>Warning:</b> The returned result might be inaccurate and could 
     * also indicate a problem with {@code System.in}.</p>
     * 
     * @return
     *      Whether the program is running from command line
     */
    private static boolean isRunningFromCommandLine() {
        try {
            System.in.available();
            return true;
        }
        catch (final IOException ioException) {
            logger.info("Caught exception while testing if System.in is available; assuming "
                    + "program is not run from command line", ioException);
            
            return false;
        }
    }
    
    private static void printHelp() {
        final String helpFilePath = "/help.txt";
        
        try (InputStream helpInputStream = GluerCommandLine.class.getResourceAsStream(helpFilePath)) {
            inputToOuputStream(helpInputStream, System.out);
            System.out.println(String.format("\n-----\nText is also available as '%s' inside the .jar file", helpFilePath));
        }
        catch (final IOException ioException) {
            logger.warn("Could not close help input stream", ioException);
        }
    }
    
    /*
     * For Java 9 use https://docs.oracle.com/javase/9/docs/api/java/io/InputStream.html#transferTo-java.io.OutputStream-
     * instead
     */
    private static void inputToOuputStream(final InputStream in, final OutputStream out) throws IOException {
        final byte[] buffer = new byte[1024];
        int readBytes;
        
        while ((readBytes = in.read(buffer)) != -1) {
            out.write(buffer, 0, readBytes);
        }
    }
}
