package net.stevemul.proxy.processors;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.http.ProxiedHttpRequest;
import net.stevemul.proxy.processors.RequestProcessor;

/**
 * The Class LocalAppRequestProcessor.
 * 
 * @author smulrenn
 */
public class LocalAppRequestProcessor implements RequestProcessor {

  public static final String FORWARD_SLASH = "/";
  public static final String ADMIN_CONTEXT = FORWARD_SLASH + ".proxy";
  public static final String INDEX_HTML = "index.html";
  public static final String WEB_FOLDER = FORWARD_SLASH + "web";
  public static final String DATA_URL = "data";
  
  private static Log mLogger = LogFactory.getLog(LocalAppRequestProcessor.class);

  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.Processor#accepts(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public boolean accepts(ProxiedHttpRequest pRequest, ModuleSettings pSettings) {
    String uri = pRequest.getUri();
    
    return uri.startsWith(ADMIN_CONTEXT) && !(uri.startsWith(ADMIN_CONTEXT + FORWARD_SLASH + DATA_URL));
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.spi.RequestProcessor#processRequest(net.stevemul.proxy.http.ProxiedHttpRequest, io.netty.handler.codec.http.HttpObject, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public HttpResponse processRequest(ProxiedHttpRequest pRequest, HttpObject pObject, ModuleSettings pSettings) {
    
    String uri = pRequest.getUri();
    
    if (pRequest.getMethod().equals(HttpMethod.GET)) {
      return processDirectRequest(uri);
    }
    
    return null;
  }

/**
 * Process direct request.
 *
 * @param uri the uri
 * @return the http response
 */
  private HttpResponse processDirectRequest(String uri)  {
    
    byte[] responseContent = new byte[0];
    HttpResponseStatus status = HttpResponseStatus.OK;
    
    String resource = getResourcePath(uri);
    
    try (InputStream in = getInputStreamForResource(resource)) {
      if (in == null) {
        status = HttpResponseStatus.NOT_FOUND;
      }
      else {
        responseContent = IOUtils.toByteArray(in);
      }
    }
    catch(IOException e) {
      mLogger.error("Error processing request", e);
      status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    }
    
    ByteBuf buffer = Unpooled.wrappedBuffer(responseContent);
    
    DefaultFullHttpResponse newResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buffer);
    
    HttpHeaders.setContentLength(newResponse, responseContent.length);
    
    return newResponse;
  }
  
  /**
   * Gets the input stream for resource.
   *
   * @param pResource the resource
   * @return the input stream for resource
   */
  private InputStream getInputStreamForResource(String pResource)  {
    return getClass().getResourceAsStream(pResource);
  }
  
  /**
   * Gets the resource path.
   *
   * @param uri the uri
   * @return the resource path
   */
  private String getResourcePath(String uri) {
    
    String resource = uri.replace(ADMIN_CONTEXT, "");
    
    if (resource.length() == 0 || resource.equals(FORWARD_SLASH)) {
      resource = FORWARD_SLASH + INDEX_HTML;
    }
    
    return WEB_FOLDER + resource;
  }
}
