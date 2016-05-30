package net.stevemul.proxy.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.stevemul.proxy.data.ModuleDataHandler;
import net.stevemul.proxy.data.ModuleSettingData;
import net.stevemul.proxy.modules.api.Module;
import net.stevemul.proxy.modules.api.ModuleSetting;
import net.stevemul.proxy.services.ModuleService;

/**
 * The Class ModuleService.
 */
public class ModuleServiceImpl implements ModuleService {
  
  private static Log mLogger = LogFactory.getLog(ModuleServiceImpl.class);
  
  /** The m modules. */
  private List<Module> mModules = new ArrayList<>();
  
  /** The m module loader. */
  private ServiceLoader<Module> mModuleLoader;
  
  private ModuleDataHandler mDataHandler = ModuleDataHandler.getInstance();
  
  /**
   * Load modules.
   */
  public void loadModules() {
    mModuleLoader = ServiceLoader.load(Module.class);
    
    Iterator<Module> itr = mModuleLoader.iterator();
    
    while (itr.hasNext()) {
      Module module = itr.next();
      
      persistModuleSettings(module);
      
      mModules.add(module);
      
    }     
    
    Collections.sort(mModules, new Comparator<Module>(){

      @Override
      public int compare(Module pModule1, Module pModule2) {
        
        if (pModule1.getLoadingPriority() > pModule2.getLoadingPriority()) {
          return 1;
        }
        else if (pModule1.getLoadingPriority() < pModule2.getLoadingPriority()) {
          return -1;
        }
        
        return 0;
      }
      
    });
    
    for (Module module : mModules) {
      mLogger.info("Loaded module " + module.getClass().getName() + " with priority " + module.getLoadingPriority());
    }
  }
  
  /**
   * Persist module settings.
   *
   * @param pModule the module
   */
  private void persistModuleSettings(Module pModule) {
    
    List<ModuleSettingData> moduleData = new ArrayList<>();
    
    for (ModuleSetting setting : pModule.getSettings()) {
      ModuleSettingData settingData = ModuleSettingData.buildSettingData(setting.getName(), setting.getDefault(), setting.getType().getName(), setting.getOrder());
      moduleData.add(settingData);
    }
    
    mDataHandler.loadModuleData(pModule, moduleData);
  }
  
  /**
   * Gets the module data.
   *
   * @param pModule the module
   * @return the module data
   */
  public List<ModuleSettingData> getModuleData(Module pModule) {
    return mDataHandler.getModuleData(pModule);
  }
  
  /**
   * Save module data.
   *
   * @param pModule the module
   * @param pData the data
   */
  public void saveModuleData(Module pModule, List<ModuleSettingData> pData) {
    mDataHandler.saveModuleData(pModule, pData);
  }
  
  /**
   * Gets the modules.
   *
   * @return the modules
   */
  public List<Module> getModules() {
    return mModules;
  }
  
  /**
   * Gets the module.
   *
   * @param pNamespace the namespace
   * @return the module
   */
  public Module getModule(String pNamespace) {
    
    for (Module module : getModules()) {
      if (module.getNamespace().equals(pNamespace)) {
        return module;
      }
    }
    
    return null;
  }
}
