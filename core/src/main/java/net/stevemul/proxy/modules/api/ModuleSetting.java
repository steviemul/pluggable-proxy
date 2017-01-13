package net.stevemul.proxy.modules.api;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Class ModuleSetting.
 */
public class ModuleSetting {

  /** The m name. */
  private final String mName;
  
  /** The m type. */
  private final ModuleSettingType mType;
  
  /** The m default. */
  private final String mDefault;
  
  private final int mOrder;
  
  private Map<String, String> mOptions = new LinkedHashMap<>();
  
  /**
   * Instantiates a new module setting.
   *
   * @param pName the name
   * @param pType the type
   */
  public ModuleSetting(String pName, ModuleSettingType pType) {
    mName = pName;
    mType = pType;
    mOrder = 0;
    
    if (mType == ModuleSettingType.FILE) {
      mDefault = System.getProperty("user.home");
    }
    else {
      mDefault = "";
    }
  }
  
  /**
   * Instantiates a new module setting.
   *
   * @param pName the name
   * @param pType the type
   * @param pDefault the default
   */
  public ModuleSetting(String pName, ModuleSettingType pType, String pDefault) {
    mName = pName;
    mType = pType;
    mDefault = pDefault;
    mOrder = 0;
  }
  
  /**
   * Instantiates a new module setting.
   *
   * @param pName the name
   * @param pType the type
   * @param pOrder the order
   */
  public ModuleSetting(String pName, ModuleSettingType pType, int pOrder) {
    mName = pName;
    mType = pType;
    mOrder = pOrder;
    
    if (mType == ModuleSettingType.FILE) {
      mDefault = System.getProperty("user.home");
    }
    else {
      mDefault = "";
    }
  }
  
  /**
   * Instantiates a new module setting.
   *
   * @param pName the name
   * @param pType the type
   * @param pDefault the default
   * @param pOrder the order
   */
  public ModuleSetting(String pName, ModuleSettingType pType, String pDefault, int pOrder) {
    mName = pName;
    mType = pType;
    mDefault = pDefault;
    mOrder = pOrder;
  }
  
  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return mName;
  }
  
  /**
   * Gets the type.
   *
   * @return the type
   */
  public ModuleSettingType getType() {
    return mType;
  }
  
  /**
   * Gets the order.
   *
   * @return the order
   */
  public int getOrder() {
    return mOrder;
  }
  
  /**
   * Gets the default.
   *
   * @return the default
   */
  public String getDefault() {
    return mDefault;
  }

  /**
   * Adds the options.
   *
   * @param pKey the key
   * @param pValue the value
   */
  public void addOption(String pKey, String pValue) {
    mOptions.put(pKey, pValue);
  }
  
  /**
   * Gets the options.
   *
   * @return the options
   */
  public Map<String, String> getOptions() {
    return mOptions;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((mName == null) ? 0 : mName.hashCode());
    result = prime * result + ((mType == null) ? 0 : mType.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ModuleSetting other = (ModuleSetting) obj;
    if (mName == null) {
      if (other.mName != null)
        return false;
    } else if (!mName.equals(other.mName))
      return false;
    if (mType != other.mType)
      return false;
    return true;
  }
  
  
}
