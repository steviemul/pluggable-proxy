package net.stevemul.proxy.modules;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.stevemul.proxy.modules.api.AbstractModule;
import net.stevemul.proxy.modules.api.ModuleSetting;
import net.stevemul.proxy.modules.api.ModuleSettingType;
import net.stevemul.proxy.processors.ResponseProcessor;
import net.stevemul.proxy.processors.TrafficOutputResponseProcessor;

/**
 * The Class TrafficOutputModule.
 */
public class TrafficOutputModule extends AbstractModule {
  
  /** The Constant NAMESPACE. */
  public static final String NAMESPACE = "stevemul.net.trafficoutput";
  
  /** The Constant NAME. */
  public static final String NAME = "Console";
  
  public static final String OUTPUT = "output";
  
  private static Map<String, String> mLabels = new HashMap<>();
  
  static {
    mLabels.put(OUTPUT, "Network Traffic");
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
   * @see net.stevemul.proxy.modules.api.AbstractModule#getResponseProcessors()
   */
  @Override
  public List<? extends ResponseProcessor> getResponseProcessors() {
    return Arrays.asList(new TrafficOutputResponseProcessor());
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.AbstractModule#getSettings()
   */
  @Override
  public List<ModuleSetting> getSettings() {
    return Arrays.asList(new ModuleSetting(OUTPUT, ModuleSettingType.CONSOLE));
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
    return 5;
  }
  
  
}
