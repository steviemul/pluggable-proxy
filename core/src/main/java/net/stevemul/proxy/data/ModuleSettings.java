package net.stevemul.proxy.data;

import java.util.ArrayList;

import net.stevemul.proxy.data.ModuleSettingData;

/**
 * The Class ModuleSettingList.
 */
public class ModuleSettings extends ArrayList<ModuleSettingData> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -381395647817181395L;
  
  /**
   * Gets the string value.
   *
   * @param pKey the key
   * @return the string value
   */
  public String getStringValue(String pKey) {
    
    for (ModuleSettingData setting : this) {
      if (setting.getKey().equals(pKey)) {
        return setting.getValue();
      }
    }
    
    return null;
  }
  
  /**
   * Gets the boolean value.
   *
   * @param pKey the key
   * @return the boolean value
   */
  public boolean getBooleanValue(String pKey) {
    
    for (ModuleSettingData setting : this) {
      if (setting.getKey().equals(pKey)) {
        return Boolean.valueOf(setting.getValue());
      }
    }
    
    return false;
  }
}
