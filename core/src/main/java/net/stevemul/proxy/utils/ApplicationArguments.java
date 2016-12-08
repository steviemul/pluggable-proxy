package net.stevemul.proxy.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class ApplicationArguments.
 */
public class ApplicationArguments {

  public static final String MITM = "mitm";
  public static final String LISTEN_PORT = "listenPort";
  public static final String PROXY_HOST = "proxyHost";
  public static final String PROXY_PORT = "proxyPort";
  public static final String BLIND_TRUST = "blindTrust";
  public static final String SOCKET_IO_PORT = "socketIOPort";
  public static final String OVERRIDE_HOSTNAME = "hostname";
  public static final String HELP = "help";
  
  private static final String DASH = "-";
  private static final String EQUALS = "=";
  private static final String ERROR_INVALID_PORT = "error.invalidPort";
  private static final String ERROR_INVALID_PROXY_HOST = "error.invalidProxyHost";
  private static final String ERROR_INVALID_PROXY_PORT = "error.invalidProxyPort";
  private static final String ERROR_INVALID_SOCKET_IO_PORT = "error.invalidSocketIOPort";
  private static final String ERROR_INVALID_HOST = "error.invalidHostname";
  
  
  /**
   * Parses the args.
   *
   * @param args the args
   * @return the map
   */
  public static Map<String, String> parseArgs(String args[]) {
    Map<String, String> parsedArgs = new HashMap<>();
    
    if (args.length == 1 && asArg(HELP).equals(args[0])) {
      printHelpMessage();
      System.exit(0);
    }
    
    for (int i=0;i<args.length;i++) {
      String arg = args[i];
      
      if (asArg(MITM).equals(arg)) {
        parsedArgs.put(MITM, "true");
      }
      else if (asArg(BLIND_TRUST).equals(arg)) {
        parsedArgs.put(BLIND_TRUST, "true");
      }
      else if (arg.startsWith(asArg(LISTEN_PORT))) {
        String[] options = arg.split(EQUALS);
        
        if (options.length != 2) {
          throw new IllegalArgumentException(AppUtils.getString(ERROR_INVALID_PORT, arg));
        }
        
        parsedArgs.put(LISTEN_PORT, options[1]);
      }
      else if (arg.startsWith(asArg(PROXY_HOST))) {
        
        String[] options = arg.split(EQUALS);
        
        if (options.length != 2) {
          throw new IllegalArgumentException(AppUtils.getString(ERROR_INVALID_PROXY_HOST, arg));
        }
        
        parsedArgs.put(PROXY_HOST, options[1]);
      }
      else if (arg.startsWith(asArg(PROXY_PORT))) {
        
        String[] options = arg.split(EQUALS);
        
        if (options.length != 2) {
          throw new IllegalArgumentException(AppUtils.getString(ERROR_INVALID_PROXY_PORT, arg));
        }
        
        parsedArgs.put(PROXY_PORT, options[1]);
      }
      else if (arg.startsWith(asArg(SOCKET_IO_PORT))) {
        
        String[] options = arg.split(EQUALS);
        
        if (options.length != 2) {
          throw new IllegalArgumentException(AppUtils.getString(ERROR_INVALID_SOCKET_IO_PORT, arg));
        }
        
        parsedArgs.put(SOCKET_IO_PORT, options[1]);
      }
      else if (arg.startsWith(asArg(OVERRIDE_HOSTNAME))) {
        String[] options = arg.split(EQUALS);
        
        if (options.length != 2) {
          throw new IllegalArgumentException(AppUtils.getString(ERROR_INVALID_HOST, arg));
        }
        
        parsedArgs.put(OVERRIDE_HOSTNAME, options[1]);
      }
    }
    
    return parsedArgs;
  }
  
  private static void printHelpMessage() {
    
    StringBuilder message = new StringBuilder();
    
    message.append("usage : app [options]\n");
    message.append("Options:\n");
    message.append(buildParamMessage(HELP));
    message.append(buildParamMessage(MITM));
    message.append(buildParamMessage(LISTEN_PORT));
    message.append(buildParamMessage(PROXY_HOST));
    message.append(buildParamMessage(PROXY_PORT));
    message.append(buildParamMessage(BLIND_TRUST));
    message.append(buildParamMessage(SOCKET_IO_PORT));
    message.append(buildParamMessage(OVERRIDE_HOSTNAME));
    
    System.out.println(message.toString());
  }
  
  /**
   * Builds the param message.
   *
   * @param pParam the param
   * @return the string
   */
  private static String buildParamMessage(String pParam) {
    return "  " + DASH + pParam + " : " + AppUtils.getString("info." + pParam) + "\n";
  }
  
  /**
   * As arg.
   *
   * @param pArgName the arg name
   * @return the string
   */
  private static String asArg(String pArgName) {
    return DASH + pArgName;
  }
}
