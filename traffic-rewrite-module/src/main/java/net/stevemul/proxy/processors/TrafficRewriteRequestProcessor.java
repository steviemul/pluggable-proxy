package net.stevemul.proxy.processors;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.http.ProxiedHttpRequest;

/**
 * The Class TrafficRewriteRequestProcessor.
 */
public class TrafficRewriteRequestProcessor implements RequestProcessor {
  
  public static final String SERVING_NON_MIN = "servingNonMin";
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.Processor#accepts(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public boolean accepts(ProxiedHttpRequest pRequest, ModuleSettings pSettings) {
    
    if (pSettings.getBooleanValue(SERVING_NON_MIN)) {
      String uri = pRequest.getUri();
      
      if (uri.contains("/element/") || uri.contains("/widget/") || uri.contains("/global/")) {
        return uri.contains(".min.js");
      }
    }
    
    return false;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.RequestProcessor#processRequest(net.stevemul.proxy.http.ProxiedHttpRequest, io.netty.handler.codec.http.HttpObject, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public HttpResponse processRequest(ProxiedHttpRequest pRequest, HttpObject pObject, ModuleSettings pSettings) {
    
    if (pObject instanceof HttpRequest) {
      HttpRequest request = (HttpRequest) pObject;
      
      String uri = request.getUri();
      
      if (uri.contains(".min.js")) {
        uri = uri.replace(".min.js", ".js");
      }
      
      request.setUri(uri);
    }
    
    return null;
  }

}
