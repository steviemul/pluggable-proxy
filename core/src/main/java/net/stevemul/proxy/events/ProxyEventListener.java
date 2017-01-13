package net.stevemul.proxy.events;

import java.util.List;

import net.stevemul.proxy.data.ModuleSettingData;
import net.stevemul.proxy.modules.api.Module;

/**
 * The listener interface for receiving proxyEvent events.
 * The class that is interested in processing a proxyEvent
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addProxyEventListener<code> method. When
 * the proxyEvent event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ProxyEventEvent
 */
public interface ProxyEventListener {

  public void eventOccurred(EventType pEventType, Module pModule, List<ModuleSettingData> pSettingsData);
}
