package net.stevemul.proxy.processors;

import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.http.ProxiedHttpRequest;
import net.stevemul.proxy.http.Response;
import net.stevemul.proxy.processors.Processor;

/**
 * The Interface ResponseProcessor.
 * 
 * @author smulrenn
 */
public interface ResponseProcessor extends Processor {

  /**
   * Process response.
   *
   * @param pRequest the request
   * @param pResponse the response
   * @param pSettings the settings
   * @return the response
   */
  public Response processResponse(ProxiedHttpRequest pRequest, Response pResponse, ModuleSettings pSettings);
}
