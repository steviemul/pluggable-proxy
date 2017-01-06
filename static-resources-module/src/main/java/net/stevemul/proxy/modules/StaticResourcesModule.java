package net.stevemul.proxy.modules;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.stevemul.proxy.modules.api.AbstractModule;
import net.stevemul.proxy.modules.api.ModuleSetting;
import net.stevemul.proxy.modules.api.ModuleSettingType;
import net.stevemul.proxy.processors.RequestProcessor;
import net.stevemul.proxy.processors.StaticResourcesRequestProcessor;

/**
 * The Class StaticResourcesModule.
 */
public class StaticResourcesModule extends AbstractModule {
  
  /** The Constant NAMESPACE. */
  public static final String NAMESPACE = "stevemul.net.staticresources";
  
  /** The Constant NAME. */
  public static final String NAME = "Static Resources";
  
  public static final String ENABLED = "enabled";
  public static final String PATHS_TO_SERVE = "pathsToServe";
  public static final String STATIC_RESOURCES_DIRECTORY = "staticResourcesDirectory";
  
  private static Map<String, String> mLabels = new HashMap<>();
  
  static {
    mLabels.put(ENABLED, "Enable serving static resources locally");
    mLabels.put(PATHS_TO_SERVE, "Comma separated list of paths to serve");
    mLabels.put(STATIC_RESOURCES_DIRECTORY, "Location of local resources");
  }
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getNamespace()
   */
  @Override
  public String getNamespace() {
    return NAMESPACE;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getName()
   */
  @Override
  public String getName() {
    return NAME;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getLoadingPriority()
   */
  @Override
  public int getLoadingPriority() {
    return 40;
  }
  
  @Override
  public List<? extends RequestProcessor> getRequestProcessors() {
    return Arrays.asList(new StaticResourcesRequestProcessor());
  }

  /* (non-Javadoc)
   * @see stevemul.net.proxy.Module#getSettings()
   */
  @Override
  public List<ModuleSetting> getSettings() {
    return Arrays.asList(
        new ModuleSetting(ENABLED, ModuleSettingType.CHECKBOX, 1),
        new ModuleSetting(PATHS_TO_SERVE, ModuleSettingType.TEXT_BOX, 2),
        new ModuleSetting(STATIC_RESOURCES_DIRECTORY, ModuleSettingType.FILE, 3));
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.AbstractModule#getSettingLabel(java.lang.String)
   */
  @Override
  public String getSettingLabel(String pKey) {
    return mLabels.get(pKey);
  }
}
