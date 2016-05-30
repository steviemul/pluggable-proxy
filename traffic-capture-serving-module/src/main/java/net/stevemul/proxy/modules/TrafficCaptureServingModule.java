package net.stevemul.proxy.modules;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.stevemul.proxy.actions.ClearContentDirectoryAction;
import net.stevemul.proxy.modules.api.AbstractModule;
import net.stevemul.proxy.modules.api.Action;
import net.stevemul.proxy.modules.api.ModuleSetting;
import net.stevemul.proxy.modules.api.ModuleSettingType;
import net.stevemul.proxy.processors.LocalCachedRequestProcessor;
import net.stevemul.proxy.processors.PagesEndpointResponseProcessor;
import net.stevemul.proxy.processors.ResponseCachingProcessor;
import net.stevemul.proxy.processors.RequestProcessor;
import net.stevemul.proxy.processors.ResponseProcessor;

/**
 * The Class TrafficCaptureServingModule.
 */
public class TrafficCaptureServingModule extends AbstractModule {
  
  /** The Constant NAMESPACE. */
  public static final String NAMESPACE = "stevemul.net.trafficcapture";
  
  /** The Constant NAME. */
  public static final String NAME = "Traffic Capturing / Serving";
  
  /** The Constant CONTENT_DIRECTORY. */
  public static final String CONTENT_DIRECTORY = "contentDirectory";
  
  /** The Constant SERVING_LOCAL. */
  public static final String SERVING_LOCAL = "servingLocal";
  
  /** The Constant CAPTURING. */
  public static final String CAPTURING = "capturing";
  
  /** The Constant CLEAR_DIRECTORY. */
  public static final String CLEAR_DIRECTORY = "clearDirectory";
  
  public static final String CAPTURING_PATTERN = "capturingPattern";
  
  private static Map<String, String> mLabels = new HashMap<>();
  
  static {
    mLabels.put(CONTENT_DIRECTORY, "Content Directory");
    mLabels.put(SERVING_LOCAL, "Serve locally captured traffic");
    mLabels.put(CAPTURING, "Capturing");
    mLabels.put(CLEAR_DIRECTORY, "Clear Content Directory");
    mLabels.put(CAPTURING_PATTERN, "Capturing Pattern");
  }
  
  /* (non-Javadoc)
   * @see stevemul.net.proxy.Module#getRequestProcessors()
   */
  @Override
  public List<? extends RequestProcessor> getRequestProcessors() {
    return Arrays.asList(new LocalCachedRequestProcessor());
  }

  /* (non-Javadoc)
   * @see stevemul.net.proxy.Module#getResponseProcessors()
   */
  @Override
  public List<? extends ResponseProcessor> getResponseProcessors() {
    return Arrays.asList(
        new PagesEndpointResponseProcessor(),
        new ResponseCachingProcessor());
  }

  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.AbstractModule#getActions()
   */
  @Override
  public Map<String, Action> getActions() {
    Map<String, Action> actions = new HashMap<>();
    
    actions.put(CLEAR_DIRECTORY, new ClearContentDirectoryAction());
    
    return actions;
  }

  /* (non-Javadoc)
   * @see stevemul.net.proxy.Module#getNamespace()
   */
  @Override
  public String getNamespace() {
    return NAMESPACE;
  }

  /* (non-Javadoc)
   * @see stevemul.net.proxy.Module#getName()
   */
  @Override
  public String getName() {
    return NAME;
  }

  /* (non-Javadoc)
   * @see stevemul.net.proxy.Module#getSettings()
   */
  @Override
  public List<ModuleSetting> getSettings() {
    return Arrays.asList(
        new ModuleSetting(CONTENT_DIRECTORY, ModuleSettingType.FILE, 5),
        new ModuleSetting(SERVING_LOCAL, ModuleSettingType.CHECKBOX, "false", 2),
        new ModuleSetting(CAPTURING, ModuleSettingType.CHECKBOX, "false", 1),
        new ModuleSetting(CAPTURING_PATTERN, ModuleSettingType.TEXT_BOX, 4),
        new ModuleSetting(CLEAR_DIRECTORY, ModuleSettingType.ACTION, "false", 3));
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.AbstractModule#getSettingLabel(java.lang.String)
   */
  @Override
  public String getSettingLabel(String pKey) {
    return mLabels.get(pKey);
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getLoadingPriority()
   */
  @Override
  public int getLoadingPriority() {
    return 10;
  }
  
}
