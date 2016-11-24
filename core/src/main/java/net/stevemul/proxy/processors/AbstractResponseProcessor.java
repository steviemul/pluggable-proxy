package net.stevemul.proxy.processors;

import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.http.ProxiedHttpRequest;
import net.stevemul.proxy.http.Response;

/**
 * The Class AbstractResponseProcessor.
 */
public abstract class AbstractResponseProcessor implements ResponseProcessor {

  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.ResponseProcessor#processServerToProxyResponse(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.http.Response, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public Response processServerToProxyResponse(ProxiedHttpRequest pRequest, Response pResponse,
      ModuleSettings pSettings) {
    
    return pResponse;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.ResponseProcessor#processProxyToClientResponse(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.http.Response, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public Response processProxyToClientResponse(ProxiedHttpRequest pRequest, Response pResponse,
      ModuleSettings pSettings) {
    
    return pResponse;
  }

}
