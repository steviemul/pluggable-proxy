package net.stevemul.proxy.modules.api;

import java.util.List;
import java.util.Map;

import net.stevemul.proxy.processors.RequestProcessor;
import net.stevemul.proxy.processors.ResponseProcessor;

/**
 * The Interface Module.
 */
public interface Module {

  /**
   * Gets the request processors.
   *
   * @return the request processors
   */
  public List<? extends RequestProcessor> getRequestProcessors();
  
  /**
   * Gets the response processors.
   *
   * @return the response processors
   */
  public List<? extends ResponseProcessor> getResponseProcessors();
  
  /**
   * Gets the actions.
   *
   * @return the actions
   */
  public Map<String, Action> getActions();
  
  /**
   * Gets the namespace.
   *
   * @return the namespace
   */
  public String getNamespace();
  
  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName();
  
  /**
   * Gets the settings.
   *
   * @return the settings
   */
  public List<ModuleSetting> getSettings();
  
  /**
   * Gets the setting label.
   *
   * @param pSettingKey the setting key
   * @return the setting label
   */
  public String getSettingLabel(String pSettingKey) ;
  
  /**
   * Gets the loading priority.
   *
   * @return the loading priority
   */
  public int getLoadingPriority();
}
