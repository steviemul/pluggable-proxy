package net.stevemul.proxy.data;

/**
 * The Class ModuleSettingData.
 */
public class ModuleSettingData {

  /** The m key. */
  private String mKey;
  
  /**
   * Gets the key.
   *
   * @return the key
   */
  public String getKey() {
    return mKey;
  }
  
  /**
   * Sets the key.
   *
   * @param pKey the new key
   */
  public void setKey(String pKey) {
    mKey = pKey;
  }
  
  /** The m value. */
  private String mValue;
  
  /**
   * Gets the value.
   *
   * @return the value
   */
  public String getValue() {
    return mValue;
  }
  
  /**
   * Sets the value.
   *
   * @param pValue the new value
   */
  public void setValue(String pValue) {
    mValue = pValue;
  }
  
  /** The m type. */
  private String mType;
  
  /**
   * Gets the type.
   *
   * @return the type
   */
  public String getType() {
    return mType;
  }
  
  /**
   * Sets the type.
   *
   * @param pType the new type
   */
  public void setType(String pType) {
    mType = pType;
  }
  
  private int mOrder = 0;
  
  /**
   * Gets the order.
   *
   * @return the order
   */
  public int getOrder() {
    return mOrder;
  }
  
  /**
   * Sets the order.
   *
   * @param pOrder the new order
   */
  public void setOrder(int pOrder) {
    mOrder = pOrder;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((mKey == null) ? 0 : mKey.hashCode());
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
    ModuleSettingData other = (ModuleSettingData) obj;
    if (mKey == null) {
      if (other.mKey != null)
        return false;
    } else if (!mKey.equals(other.mKey))
      return false;
    return true;
  }

  /**
   * Builds the setting data.
   *
   * @param pKey the key
   * @param pValue the value
   * @param pType the type
   * @return the module setting data
   */
  public static ModuleSettingData buildSettingData(String pKey, String pValue, String pType, int pOrder) {
    
    ModuleSettingData data = new ModuleSettingData();
    
    data.setKey(pKey);
    data.setValue(pValue);
    data.setType(pType);
    data.setOrder(pOrder);
    
    return data;
  }
}
