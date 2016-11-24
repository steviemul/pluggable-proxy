package net.stevemul.proxy.processors;

import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.http.ProxiedHttpRequest;
import net.stevemul.proxy.http.Response;
import net.stevemul.proxy.modules.TrafficOutputModule;
import net.stevemul.proxy.services.ServiceRegistry;

/**
 * The Class TrafficOutputResponseProcessor.
 */
public class TrafficOutputResponseProcessor extends AbstractResponseProcessor {

  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.Processor#accepts(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public boolean accepts(ProxiedHttpRequest pRequest, ModuleSettings pSettings) {
    return true;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.AbstractResponseProcessor#processServerToProxyResponse(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.http.Response, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public Response processServerToProxyResponse(ProxiedHttpRequest pRequest, Response pResponse, ModuleSettings pSettings) {
    
    String uri = pRequest.getUri();

    ServiceRegistry.getMessenger().sendMessage(TrafficOutputModule.NAMESPACE.replace(".", "-") + "-networkevent", "Response : " + uri);
    
    return pResponse;
  }

}
