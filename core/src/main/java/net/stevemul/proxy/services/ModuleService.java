package net.stevemul.proxy.services;

import java.util.List;

import net.stevemul.proxy.data.ModuleSettingData;
import net.stevemul.proxy.modules.api.Module;

/**
 * The Interface ModuleService.
 */
public interface ModuleService {

  /**
   * Gets the module data.
   *
   * @param pModule the module
   * @return the module data
   */
  public List<ModuleSettingData> getModuleData(Module pModule);
  
  /**
   * Save module data.
   *
   * @param pModule the module
   * @param pData the data
   */
  public void saveModuleData(Module pModule, List<ModuleSettingData> pData);
  
  /**
   * Gets the modules.
   *
   * @return the modules
   */
  public List<Module> getModules();
  
  /**
   * Gets the module.
   *
   * @param pNamespace the namespace
   * @return the module
   */
  public Module getModule(String pNamespace);
}
