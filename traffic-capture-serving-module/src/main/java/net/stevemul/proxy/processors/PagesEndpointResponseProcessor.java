package net.stevemul.proxy.processors;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.http.LocalHttpResponse;
import net.stevemul.proxy.http.ProxiedHttpRequest;
import net.stevemul.proxy.http.Response;
import net.stevemul.proxy.processors.ResponseProcessor;

/**
 * The Class PagesEndpointResponseProcessor.
 * 
 * @author smulrenn
 */
public class PagesEndpointResponseProcessor implements ResponseProcessor {

  /** The Constant PAGES_ENDPOINT_URI. */
  public static final String PAGES_ENDPOINT_URI = "/ccstoreui/v1/pages";
  
 
  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.spi.ResponseProcessor#processResponse(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.http.Response, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public Response processResponse(ProxiedHttpRequest pRequest, Response pResponse, ModuleSettings pSettings) {
    
    String content = new String(pResponse.getContent(), UTF_8);
    
    content = filterContent(content);
    
    byte[] bytes = content.getBytes(UTF_8);
    
    LocalHttpResponse newResponse = new LocalHttpResponse();
    
    newResponse.setStatusCode(200);
    newResponse.setContent(bytes);
    
    Map<String, String> headers = pResponse.getHeaders();
    
    for (String header : headers.keySet()) {
      newResponse.setHeader(header, headers.get(header));
    }
      
    newResponse.setHeader("X-CC_Proxied", "Reponse modified by dev proxy");
    
    return newResponse;
  }

  /**
   * Filter content.
   *
   * @param pContent the content
   * @return the string
   */
  private String filterContent(String pContent) {
    
    String content = pContent;
    try {
      JSONObject json = new JSONObject(pContent);
      
      String title = json.getString("title") + " (Proxied)";
      
      json.put("title", title);
      
      content = json.toString();
    }
    catch(JSONException e) {
      e.printStackTrace();
    }
    
    return content;
  } 
 
  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.Processor#accepts(io.netty.handler.codec.http.HttpRequest, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public boolean accepts(ProxiedHttpRequest pRequest, ModuleSettings pSettings) {
    return pRequest.getUri().startsWith(PAGES_ENDPOINT_URI);
  }

}
