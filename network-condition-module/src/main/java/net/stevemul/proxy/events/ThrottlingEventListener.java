package net.stevemul.proxy.events;

import java.util.List;

import org.apache.commons.lang.math.NumberUtils;

import net.stevemul.proxy.data.ModuleSettingData;
import net.stevemul.proxy.modules.api.Module;
import net.stevemul.proxy.services.ServiceRegistry;

/**
 * The listener interface for receiving throttlingEvent events.
 * The class that is interested in processing a throttlingEvent
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addThrottlingEventListener<code> method. When
 * the throttlingEvent event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ThrottlingEventEvent
 */
public class ThrottlingEventListener implements ProxyEventListener {

  @Override
  public void eventOccurred(EventType pEventType, Module pModule, List<ModuleSettingData> pSettingsData) {
    
    String throttlingValue = getSetting("bandwidth", pSettingsData);
    
    if (NumberUtils.isNumber(throttlingValue)) {
      long value = Long.parseLong(throttlingValue);
      
      ServiceRegistry.getProxyServer().setThrottle(value, value);
    }
    
  }

  private String getSetting(String pKey, List<ModuleSettingData> pSettingsData) {
    
    for (ModuleSettingData setting : pSettingsData) {
      if (pKey.equals(setting.getKey())) {
        return setting.getValue();
      }
    }
    return null;
  }

  

}
