package net.stevemul.proxy.modules.api;

import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.http.ProxiedHttpRequest;

/**
 * The Interface Action.
 */
public interface Action {

  /**
   * Execute.
   *
   * @param pSettings the settings
   */
  public void execute(ProxiedHttpRequest pRequest, ModuleSettings pSettings);
}
