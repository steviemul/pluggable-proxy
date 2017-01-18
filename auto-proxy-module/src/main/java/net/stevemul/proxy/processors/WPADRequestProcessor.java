package net.stevemul.proxy.processors;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import net.stevemul.proxy.AutoProxyConstants;
import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.http.ProxiedHttpRequest;
import net.stevemul.proxy.template.TemplateEngine;

/**
 * The Class WPADRequestProcessor.
 */
public class WPADRequestProcessor implements RequestProcessor {

  /** The Constant WPAD_URI. */
  public static final String WPAD_URI = ".auto/proxy.pac";
  public static final String WPAD_TEMPLATE = "templates/wpad.tmpl";
  public static final String INTEREPT_PROXY = "interceptProxy";
  public static final String PROXY_MIME_TYPE = "application/x-ns-proxy-autoconfig";
  
  /** The m template instance. */
  private TemplateEngine mTemplateInstance;
  
  /**
   * Instantiates a new WPAD request processor.
   */
  public WPADRequestProcessor() {
    mTemplateInstance = TemplateEngine.getInstance();
  }
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.Processor#accepts(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public boolean accepts(ProxiedHttpRequest pRequest, ModuleSettings pSettings) {
    
    boolean enabled = pSettings.getBooleanValue(AutoProxyConstants.ENABLED);
    
    if (enabled) {
      if (pRequest.getMethod().equals(HttpMethod.GET)) {
        String uri = pRequest.getUri();
        if (uri.endsWith(WPAD_URI)) {
          return true;
        }
      }
    }
    
    return false;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.RequestProcessor#processRequest(net.stevemul.proxy.http.ProxiedHttpRequest, io.netty.handler.codec.http.HttpObject, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public HttpResponse processRequest(ProxiedHttpRequest pRequest, HttpObject pObject, ModuleSettings pSettings) {
  
    if (pRequest.getMethod().equals(HttpMethod.GET)) {
      String uri = pRequest.getUri();
      
      if (uri.endsWith(WPAD_URI)) {
        Map<String, Object> model = new HashMap<>();
        
        String hostsToIntercept = pSettings.getStringValue(AutoProxyConstants.HOSTS_TO_INTERCEPT);
        String externalProxy = pSettings.getStringValue(AutoProxyConstants.EXTERNAL_PROXY);
        String noProxyFor = pSettings.getStringValue(AutoProxyConstants.NO_PROXY_FOR);
        
        if (!StringUtils.isEmpty(hostsToIntercept)) {
          model.put(AutoProxyConstants.HOSTS_TO_INTERCEPT, hostsToIntercept.split(","));
        }
        
        if (!StringUtils.isEmpty(externalProxy)) {
          model.put(AutoProxyConstants.EXTERNAL_PROXY, externalProxy);
        }
        
        
        if (!StringUtils.isEmpty(noProxyFor)) {
          model.put(AutoProxyConstants.NO_PROXY_FOR, noProxyFor.split(","));
        }
        
        model.put(INTEREPT_PROXY, "localhost:9090");
        
        String templateResult = mTemplateInstance.render(WPAD_TEMPLATE, model);
        byte[] response = templateResult.getBytes(UTF_8);
        
        ByteBuf buffer = Unpooled.wrappedBuffer(response);
        
        DefaultFullHttpResponse newResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
        
        newResponse.headers().add("Content-Type", PROXY_MIME_TYPE);
        
        HttpHeaders.setContentLength(newResponse, response.length);
        
        return newResponse;
      }
    }
    
    return null;
  }

}
