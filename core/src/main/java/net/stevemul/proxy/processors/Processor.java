package net.stevemul.proxy.processors;

import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.http.ProxiedHttpRequest;

/**
 * The Interface Processor.
 * 
 * @author smulrenn
 */
public interface Processor {

  /**
   * Accepts.
   *
   * @param pRequest the request
   * @param pSettings the settings
   * @return true, if successful
   */
  public boolean accepts(ProxiedHttpRequest pRequest, ModuleSettings pSettings);
}
