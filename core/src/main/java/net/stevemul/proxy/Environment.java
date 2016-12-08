package net.stevemul.proxy;

import java.util.Map;

/**
 * The Class Environment.
 */
public class Environment {

  /** The m environment arguments. */
  private static Map<String, String> mEnvironmentArguments;
  
  /**
   * Sets the environment arguments.
   *
   * @param pArgs the args
   */
  public static void setEnvironmentArguments(Map<String, String> pArgs) {
    mEnvironmentArguments = pArgs;
  }
  
  /**
   * Gets the environment argument.
   *
   * @param pKey the key
   * @return the environment argument
   */
  public static String getEnvironmentArgument(String pKey) {
    return mEnvironmentArguments.get(pKey);
  }
}
