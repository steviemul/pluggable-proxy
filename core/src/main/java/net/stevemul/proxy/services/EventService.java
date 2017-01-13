package net.stevemul.proxy.services;

import java.util.List;

import net.stevemul.proxy.data.ModuleSettingData;
import net.stevemul.proxy.events.EventType;
import net.stevemul.proxy.events.ProxyEventListener;
import net.stevemul.proxy.modules.api.Module;

/**
 * The Class EventService.
 */
public class EventService {
  
  /**
   * Dispatch event.
   *
   * @param pListener the listener
   * @param pEventType the event type
   * @param pModule the module
   */
  public void dispatchEvent(ProxyEventListener pListener, EventType pEventType, Module pModule,  List<ModuleSettingData> pSettingsData) {
    
    pListener.eventOccurred(pEventType, pModule, pSettingsData);
  }
}
