package org.ampathkenya.screen;

import org.ampathkenya.screen.config.ScreenConfig;
import org.ampathkenya.utils.argumentprocessor.ArgumentProcessor;
import org.ampathkenya.utils.IOUtils;
import org.ampathkenya.utils.argumentprocessor.exception.UndefinedFlagException;
import org.ampathkenya.utils.argumentprocessor.exception.UndefinedValueException;
import org.ampathkenya.utils.argumentprocessor.exception.UnsupportedFlagException;

import java.util.HashMap;

public class Screen {

    protected Screen screen ;
    protected ScreenConfig config;

    public Screen() {

    }

    public <T> void displayText(T messageToDisplay) {
        outputToScreen(messageToDisplay.toString()) ;
    }

    public <T> void displayTextWithoutNewLine(T messageToDisplay) {
        outputToScreenWithoutNewLine(messageToDisplay.toString());
    }

    public String receiveInput(String prompt) {
        outputToScreen(prompt);
        return IOUtils.read(System.in) ;
    }

    public HashMap<String, String> getParameters(String commandAndParameters) {
        ArgumentProcessor argumentProcessor = new ArgumentProcessor() ;
        HashMap<String, String> argsAndParams = new HashMap<String, String>() ;

        try {
            argsAndParams = argumentProcessor.extractArgsAndProperties(commandAndParameters) ;
        } catch (UndefinedValueException e) {

        } catch (UndefinedFlagException e) {

        } catch (UnsupportedFlagException e) {

        }

        return argsAndParams;
    }

    private void outputToScreen(String stringToOutput) {
        System.out.println(stringToOutput);
    }

    private void outputToScreenWithoutNewLine(String stringToOutput) {
        System.out.print(stringToOutput);
    }
}
