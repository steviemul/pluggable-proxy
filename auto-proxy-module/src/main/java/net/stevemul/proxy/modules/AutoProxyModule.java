package net.stevemul.proxy.modules;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.stevemul.proxy.modules.api.AbstractModule;
import net.stevemul.proxy.modules.api.ModuleSetting;
import net.stevemul.proxy.modules.api.ModuleSettingType;
import net.stevemul.proxy.processors.RequestProcessor;
import net.stevemul.proxy.processors.WPADRequestProcessor;

/**
 * The Class AutoProxyModule.
 */
public class AutoProxyModule extends AbstractModule {
  
  /** The Constant NAMESPACE. */
  public static final String NAMESPACE = "stevemul.net.autoproxy";
  
  /** The Constant NAME. */
  public static final String NAME = "Serve Auto Proxy File";
  
  /** The Constant ENABLED. */
  public static final String ENABLED = "enabled";
  
  /** The Constant HOSTS_TO_INTERCEPT. */
  public static final String HOSTS_TO_INTERCEPT = "hostsToIntercept";
  
  /** The Constant NO_PROXY_FOR. */
  public static final String NO_PROXY_FOR = "noProxyFor";
  
  /** The Constant EXTERNAL_PROXY. */
  public static final String EXTERNAL_PROXY = "externalProxy";
  
  /** The m labels. */
  private static Map<String, String> mLabels = new HashMap<>();
  
  static {
    mLabels.put(ENABLED, "Enabled");
    mLabels.put(HOSTS_TO_INTERCEPT, "Hosts to Intercept (Comma Separated)");
    mLabels.put(NO_PROXY_FOR, "No proxy for (Comma Separated)" );
    mLabels.put(EXTERNAL_PROXY, "External Proxy");
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
    return 20;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.AbstractModule#getRequestProcessors()
   */
  @Override
  public List<? extends RequestProcessor> getRequestProcessors() {
    return Arrays.asList(new WPADRequestProcessor());
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.AbstractModule#getSettings()
   */
  @Override
  public List<ModuleSetting> getSettings() {
    return Arrays.asList(
        new ModuleSetting(ENABLED, ModuleSettingType.CHECKBOX, 1),
        new ModuleSetting(HOSTS_TO_INTERCEPT, ModuleSettingType.TEXT_BOX,2),
        new ModuleSetting(NO_PROXY_FOR, ModuleSettingType.TEXT_BOX, 3),
        new ModuleSetting(EXTERNAL_PROXY, ModuleSettingType.TEXT_BOX, 4));
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.AbstractModule#getSettingLabel(java.lang.String)
   */
  @Override
  public String getSettingLabel(String pKey) {
    return mLabels.get(pKey);
  }

  
}
