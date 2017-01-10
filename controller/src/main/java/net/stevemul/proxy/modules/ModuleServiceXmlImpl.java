package net.stevemul.proxy.modules;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.stevemul.proxy.data.ModuleDataHandler;
import net.stevemul.proxy.data.ModuleSettingData;
import net.stevemul.proxy.modules.api.Module;
import net.stevemul.proxy.modules.api.ModuleSetting;
import net.stevemul.proxy.modules.api.XmlModuleDefinition;
import net.stevemul.proxy.services.ModuleService;
import net.stevemul.proxy.utils.AppUtils;

/**
 * The Class ModuleServiceXmlImpl.
 */
public class ModuleServiceXmlImpl implements ModuleService {

  /** The m logger. */
  private static Log mLogger = LogFactory.getLog(ModuleServiceXmlImpl.class);
  
  /** The m modules. */
  private List<Module> mModules = new ArrayList<>();
  
  /** The m data handler. */
  private ModuleDataHandler mDataHandler = ModuleDataHandler.getInstance();
  
  /**
   * Load modules.
   *
   * @param mDefinitionPath the m definition path
   */
  public void loadModules(String mDefinitionPath) {
    
    try {
      List<InputStream> definitions = AppUtils.loadResources(mDefinitionPath, ModuleServiceXmlImpl.class.getClassLoader());
      
      for (InputStream in : definitions) {
        if (in != null) {
          Module module = new XmlModuleDefinition(in);
          
          mModules.add(module);
          
          persistModuleSettings(module);
          
          mLogger.info("Loaded module " + module.getName() + " with priority " + module.getLoadingPriority());
        }
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
    }
    catch (Exception e) {
      mLogger.error("Error loading modules.", e);
    }
  }
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.services.ModuleService#getModuleData(net.stevemul.proxy.modules.api.Module)
   */
  @Override
  public List<ModuleSettingData> getModuleData(Module pModule) {
    return mDataHandler.getModuleData(pModule);
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.services.ModuleService#saveModuleData(net.stevemul.proxy.modules.api.Module, java.util.List)
   */
  @Override
  public void saveModuleData(Module pModule, List<ModuleSettingData> pData) {
    mDataHandler.saveModuleData(pModule, pData);
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.services.ModuleService#getModules()
   */
  @Override
  public List<Module> getModules() {
    return mModules;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.services.ModuleService#getModule(java.lang.String)
   */
  @Override
  public Module getModule(String pNamespace) {
    for (Module module : getModules()) {
      if (module.getNamespace().equals(pNamespace)) {
        return module;
      }
    }
    
    return null;
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
}
