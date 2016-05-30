package net.stevemul.proxy.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import net.stevemul.proxy.data.ModuleSettingData;
import net.stevemul.proxy.modules.api.Module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Class ModuleDataHandler.
 */
public class ModuleDataHandler {

  /** The m logger. */
  private static Log mLogger = LogFactory.getLog(ModuleDataHandler.class);
  
  /** The Constant mInstance. */
  private static final ModuleDataHandler mInstance = new ModuleDataHandler();
  
  /** The m db handler. */
  private DBHandler mDBHandler = DBHandler.getInstance();
  
  /** SQL Query statements */
  private static final String MODULE_DATA_QUERY_SQL = "SELECT SETTING_KEY, SETTING_VALUE, SETTING_TYPE, SETTING_ORDER FROM APP.MODULE_SETTINGS WHERE NAMESPACE = '%s' ORDER BY SETTING_ORDER";
  private static final String MODULE_DATA_DELETE_SQL = "DELETE FROM APP.MODULE_SETTINGS WHERE NAMESPACE = '%s'";
  private static final String MODULE_DATA_INSERT_SQL = "INSERT INTO APP.MODULE_SETTINGS VALUES('%s', '%s', '%s', '%s', %d)";
  private static final String MODULE_DATA_UPDATE_SQL = "UPDATE APP.MODULE_SETTINGS SET SETTING_VALUE='%s' WHERE NAMESPACE = '%s' AND SETTING_KEY = '%s'";
  private static final String GET_MODULE_SETTING_SQL = "SELECT SETTING_VALUE FROM APP.MODULE_SETTINGS WHERE NAMESPACE = '%s' AND SETTING_KEY = '%s'";
      
  /**
   * Gets the single instance of ModuleDataHandler.
   *
   * @return single instance of ModuleDataHandler
   */
  public static ModuleDataHandler getInstance() {
    return mInstance;
  }
  
  /**
   * Gets the module data.
   *
   * @param pModule the module
   * @return the module data
   */
  public ModuleSettings getModuleData(Module pModule) {
    
    ModuleSettings data = new ModuleSettings();
    
    try {
      Connection con = mDBHandler.getConnection();
      
      String query = String.format(MODULE_DATA_QUERY_SQL, pModule.getNamespace());
      
      Statement stmt = con.createStatement();
      
      ResultSet results = stmt.executeQuery(query);
      
      while (results.next()) {
        String key = results.getString("SETTING_KEY");
        String value = results.getString("SETTING_VALUE");
        String type = results.getString("SETTING_TYPE");
        int order = results.getInt("SETTING_ORDER");
        
        ModuleSettingData settingData = ModuleSettingData.buildSettingData(key, value, type, order);
        
        data.add(settingData);
      }
    }
    catch (SQLException e) {
      mLogger.error("Unable to query module data", e);
    }
    
    return data;
  }
  
  /**
   * Gets the module setting.
   *
   * @param pNamespace the namespace
   * @param key the key
   * @return the module setting
   */
  public String getModuleSetting(String pNamespace, String pKey) {
    String setting = "";
    
    try {
      Connection con = mDBHandler.getConnection();
      
      String query = String.format(GET_MODULE_SETTING_SQL, pNamespace, pKey);
      
      Statement stmt = con.createStatement();
      
      ResultSet results = stmt.executeQuery(query);
      
      if (results.next()) {
        setting = results.getString("SETTING_VALUE");
      }
    }
    catch (SQLException e) {
      mLogger.error("Unable to query module data", e);
    }
    
    return setting;
  }
  
  /**
   * Save module data.
   *
   * @param pModule the module
   * @param pData the data
   */
  public void saveModuleData(Module pModule, List<ModuleSettingData> pData) {
    
    List<ModuleSettingData> existingData = getModuleData(pModule);
    
    for (ModuleSettingData setting : pData) {
      if (existingData.contains(setting)) {
        updateModuleSetting(pModule, setting.getKey(), setting.getValue());
      }
      else {
        insertModuleSetting(pModule, setting.getKey(), setting.getValue(), setting.getType(), setting.getOrder());
      }
        
    }
  }
  
  /**
   * Save module data if absent.
   *
   * @param pModule the module
   * @param pData the data
   */
  public void loadModuleData(Module pModule, List<ModuleSettingData> pData) {
    
    List<ModuleSettingData> existingData = getModuleData(pModule);
    
    for (ModuleSettingData setting : pData) {
      if (!existingData.contains(setting)) {
        insertModuleSetting(pModule, setting.getKey(), setting.getValue(), setting.getType(), setting.getOrder());
      }
    }
  }
  
  /**
   * Insert module setting.
   *
   * @param pModule the module
   * @param pKey the key
   * @param pValue the value
   */
  public void insertModuleSetting(Module pModule, String pKey, Object pValue, String pType, int pOrder) {
    
    try {
      Connection con = mDBHandler.getConnection();
      Statement stmt = con.createStatement();
      
      String insert = String.format(MODULE_DATA_INSERT_SQL, pModule.getNamespace(), pKey, pValue.toString(), pType, pOrder);
      
      stmt.executeUpdate(insert);
    }
    catch (SQLException e) {
      mLogger.error("Unable to insert value into database", e);
    }
  }
  
  /**
   * Update module setting.
   *
   * @param pModule the module
   * @param pKey the key
   * @param pValue the value
   */
  public void updateModuleSetting(Module pModule, String pKey, Object pValue) {
    
    try {
      Connection con = mDBHandler.getConnection();
      Statement stmt = con.createStatement();
      
      String update = String.format(MODULE_DATA_UPDATE_SQL, pValue.toString(), pModule.getNamespace(), pKey);
      
      stmt.executeUpdate(update);
    }
    catch (SQLException e) {
      mLogger.error("Unable to update value in database", e);
    }
  }
  
  /**
   * Delete module settings.
   *
   * @param pModule the module
   */
  public void deleteModuleSettings(Module pModule) {
    try {
      Connection con = mDBHandler.getConnection();
      Statement stmt = con.createStatement();
      
      String delete = String.format(MODULE_DATA_DELETE_SQL, pModule.getNamespace());
      
      stmt.executeUpdate(delete);
    }
    catch (SQLException e) {
      mLogger.error("Unable to delete settings in database", e);
    }
  }
}
