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
import net.stevemul.proxy.processors.AlternateServerRequestProcessor;
import net.stevemul.proxy.processors.LocalCachedRequestProcessor;
import net.stevemul.proxy.processors.PagesEndpointResponseProcessor;
import net.stevemul.proxy.processors.RequestProcessor;
import net.stevemul.proxy.processors.ResponseCachingProcessor;
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
  
  public static final String TEMPLATE_DIRECTORY = "templateDirectory";
  
  public static final String ALLOW_TEMPLATE_OVERRIDES = "allowTemplateOverrides";
  
  public static final String DUMP_TEMPLATES = "dumpTemplates";
  
  public static final String ENABLE_ALTERNATE_SERVER_LOOKUPS = "alternateServerLookups";
  
  public static final String ALTERNATE_SERVER_RESOURCES = "alternateServerResources";
  
  public static final String ALTERNATE_SERVER_URL = "alternateServerUrl";
  
  private static Map<String, String> mLabels = new HashMap<>();
  
  static {
    mLabels.put(CONTENT_DIRECTORY, "Content Directory");
    mLabels.put(SERVING_LOCAL, "Serve locally captured traffic");
    mLabels.put(CAPTURING, "Capturing");
    mLabels.put(CLEAR_DIRECTORY, "Clear Content Directory");
    mLabels.put(CAPTURING_PATTERN, "Capturing Pattern");
    mLabels.put(TEMPLATE_DIRECTORY, "Template Directory");
    mLabels.put(ALLOW_TEMPLATE_OVERRIDES, "Allow Template Overrides");
    mLabels.put(DUMP_TEMPLATES, "Dump Templates");
    mLabels.put(ENABLE_ALTERNATE_SERVER_LOOKUPS, "Enable Alternative Server Lookups");
    mLabels.put(ALTERNATE_SERVER_RESOURCES, "Alternative Server Resources (Comma Separated)");
    mLabels.put(ALTERNATE_SERVER_URL, "Alternative Server URL");
  }
  
  /* (non-Javadoc)
   * @see stevemul.net.proxy.Module#getRequestProcessors()
   */
  @Override
  public List<? extends RequestProcessor> getRequestProcessors() {
    return Arrays.asList(
        new AlternateServerRequestProcessor(),
        new LocalCachedRequestProcessor());
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
        new ModuleSetting(CAPTURING_PATTERN, ModuleSettingType.TEXT_BOX, 0),
        new ModuleSetting(CAPTURING, ModuleSettingType.CHECKBOX, "false", 1),
        new ModuleSetting(SERVING_LOCAL, ModuleSettingType.CHECKBOX, "false", 2),
        new ModuleSetting(CONTENT_DIRECTORY, ModuleSettingType.FILE, 3),
        new ModuleSetting(TEMPLATE_DIRECTORY, ModuleSettingType.FILE, 4),
        new ModuleSetting(DUMP_TEMPLATES, ModuleSettingType.CHECKBOX, "false", 5),
        new ModuleSetting(ALLOW_TEMPLATE_OVERRIDES, ModuleSettingType.CHECKBOX, "false", 6),
        new ModuleSetting(CLEAR_DIRECTORY, ModuleSettingType.ACTION, "false", 7),
        new ModuleSetting(ENABLE_ALTERNATE_SERVER_LOOKUPS, ModuleSettingType.CHECKBOX, "false", 8),
        new ModuleSetting(ALTERNATE_SERVER_RESOURCES, ModuleSettingType.TEXT_BOX, 9),
        new ModuleSetting(ALTERNATE_SERVER_URL, ModuleSettingType.TEXT_BOX, 10));
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
