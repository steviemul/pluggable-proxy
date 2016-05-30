package net.stevemul.proxy.modules.api;

/**
 * The Enum ModuleSettingType.
 */
public enum ModuleSettingType {
  
  /** The text box. */
  TEXT_BOX("textbox"),
  
  /** The checkbox. */
  CHECKBOX("checkbox"),
  
  /** The file. */
  FILE("file"),
  
  /** The console. */
  CONSOLE("console"),
  
  /** The action. */
  ACTION("action");
  
  /**
   * Instantiates a new module setting type.
   *
   * @param pName the name
   */
  ModuleSettingType(String pName) {
    mName = pName;
  }
  
  /** The m name. */
  private final String mName;
  
  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return mName;
  }
}
