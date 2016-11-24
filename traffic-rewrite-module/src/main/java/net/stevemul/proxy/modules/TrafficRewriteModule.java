package net.stevemul.proxy.modules;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.stevemul.proxy.modules.api.AbstractModule;
import net.stevemul.proxy.modules.api.ModuleSetting;
import net.stevemul.proxy.modules.api.ModuleSettingType;
import net.stevemul.proxy.processors.RequestProcessor;
import net.stevemul.proxy.processors.TrafficRewriteRequestProcessor;

public class TrafficRewriteModule extends AbstractModule {

  /** The Constant NAMESPACE. */
  public static final String NAMESPACE = "stevemul.net.trafficrewrite";
  
  /** The Constant NAME. */
  public static final String NAME = "Traffic Rewrite";
  
  /** The Constant SERVING_LOCAL. */
  public static final String SERVING_NON_MIN = "servingNonMin";
  
  private static Map<String, String> mLabels = new HashMap<>();
  
  static {
    mLabels.put(SERVING_NON_MIN, "Serve Non Minified Element & Widget JS");
  }
  
  @Override
  public String getNamespace() {
    return NAMESPACE;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public int getLoadingPriority() {
    return 15;
  }

  @Override
  public List<? extends RequestProcessor> getRequestProcessors() {
    return Arrays.asList(new TrafficRewriteRequestProcessor());
  }

  /* (non-Javadoc)
   * @see stevemul.net.proxy.Module#getSettings()
   */
  @Override
  public List<ModuleSetting> getSettings() {
    return Arrays.asList(
        new ModuleSetting(SERVING_NON_MIN, ModuleSettingType.CHECKBOX, 1));
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.AbstractModule#getSettingLabel(java.lang.String)
   */
  @Override
  public String getSettingLabel(String pKey) {
    return mLabels.get(pKey);
  }
}
