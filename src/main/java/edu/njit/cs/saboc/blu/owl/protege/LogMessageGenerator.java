package edu.njit.cs.saboc.blu.owl.protege;

/**
 *
 * @author cro3
 */
public class LogMessageGenerator {
    
    public static String createString(
            String toolName,
            String methodName,
            String message) {
        
        return String.format("%s\t%d\t%s\t%s", 
                toolName, 
                System.currentTimeMillis(), 
                methodName, 
                message);
    }
    
    public static String createLiveDiffString(String methodName, String message) {
        return LogMessageGenerator.createString("OAFLiveDiff", methodName, message);
    }
}
