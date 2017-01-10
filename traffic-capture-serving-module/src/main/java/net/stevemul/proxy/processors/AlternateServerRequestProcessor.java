package net.stevemul.proxy.processors;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import net.stevemul.proxy.TrafficCaptureConstants;
import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.http.ProxiedHttpRequest;

/**
 * The Class AlternateServerRequestProcessor.
 */
public class AlternateServerRequestProcessor implements RequestProcessor {
  
  private static Log mLogger = LogFactory.getLog(AlternateServerRequestProcessor.class);
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.Processor#accepts(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public boolean accepts(ProxiedHttpRequest pRequest, ModuleSettings pSettings) {
    
    boolean enabled = pSettings.getBooleanValue(TrafficCaptureConstants.ENABLE_ALTERNATE_SERVER_LOOKUPS);
    
    if (enabled) {
      String uri = pRequest.getUri();
      String resourcesPattern = pSettings.getStringValue(TrafficCaptureConstants.ALTERNATE_SERVER_RESOURCES);
      String alternateServer = pSettings.getStringValue(TrafficCaptureConstants.ALTERNATE_SERVER_URL);
      
      if (!StringUtils.isEmpty(resourcesPattern) && !StringUtils.isEmpty(alternateServer)) {
        Pattern pattern = Pattern.compile(resourcesPattern);
        
        Matcher matcher = pattern.matcher(uri);
        
        if (matcher.find()) {
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
    
    String uri = pRequest.getUri();
    String alternateServer = pSettings.getStringValue(TrafficCaptureConstants.ALTERNATE_SERVER_URL);
    
    return getUriFromServer(alternateServer, uri);
  }

  /**
   * Gets the uri from server.
   *
   * @param pServer the server
   * @param pUri the uri
   * @return the uri from server
   */
  private HttpResponse getUriFromServer(String pServer, String pUri) {
    
    try {
      HttpClient client = HttpClientBuilder.create().build();
      
      String url = pServer + pUri;
      
      HttpGet request = new HttpGet(url);
      
      org.apache.http.HttpResponse response = client.execute(request);
      
      HttpResponseStatus status = HttpResponseStatus.OK;
      
      byte[] content = null;
      
      try (InputStream in = response.getEntity().getContent()) {
        content = IOUtils.toByteArray(in);
      }
      
      ByteBuf buffer = Unpooled.wrappedBuffer(content);
      
      DefaultFullHttpResponse newResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buffer);
      
      Header[] headers = response.getAllHeaders();
      
      for (Header header : headers) {
        newResponse.headers().add(header.getName(), header.getValue());
      }
      
      newResponse.headers().add("X-CC_Response_Served", "Reponse served from " + pServer);
      
      HttpHeaders.setContentLength(newResponse, content.length);
      
      return newResponse;
    }
    catch (IOException e) {
      mLogger.error("Unable to retrieve " + pUri);
    }
    
    return null;
  }
}
