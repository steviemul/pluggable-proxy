package net.stevemul.proxy.modules.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.stevemul.proxy.processors.RequestProcessor;
import net.stevemul.proxy.processors.ResponseProcessor;

/**
 * The Class AbstractModule.
 */
public abstract class AbstractModule implements Module {

  /* (non-Javadoc)
   * @see stevemul.net.proxy.Module#getRequestProcessors()
   */
  @Override
  public List<? extends RequestProcessor> getRequestProcessors() {
    return Collections.<RequestProcessor>emptyList();
  }

  /* (non-Javadoc)
   * @see stevemul.net.proxy.Module#getResponseProcessors()
   */
  @Override
  public List<? extends ResponseProcessor> getResponseProcessors() {
    return Collections.<ResponseProcessor>emptyList();
  }

  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getActions()
   */
  @Override
  public Map<String, Action> getActions() {
    return Collections.<String, Action>emptyMap();
  }

  /* (non-Javadoc)
   * @see stevemul.net.proxy.Module#getSettings()
   */
  @Override
  public List<ModuleSetting> getSettings() {
    return Collections.<ModuleSetting>emptyList();
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getSettingLabel(net.stevemul.proxy.modules.api.ModuleSetting)
   */
  @Override
  public String getSettingLabel(String pKey) {
    
    for (ModuleSetting setting : getSettings()) {
      if (setting.getName().equals(pKey)) {
        return setting.getName();
      }
    }
    
    return "";
  }

  
}
