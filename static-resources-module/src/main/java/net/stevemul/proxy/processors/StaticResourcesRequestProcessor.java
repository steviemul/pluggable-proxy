package net.stevemul.proxy.processors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import net.stevemul.proxy.utils.MimeUtils;

/**
 * The Class StaticResourcesRequestProcessor.
 */
public class StaticResourcesRequestProcessor implements RequestProcessor {
  
  private static Log mLogger = LogFactory.getLog(StaticResourcesRequestProcessor.class);
  
  public static final String ENABLED = "enabled";
  public static final String STATIC_RESOURCES_DIRECTORY = "staticResourcesDirectory";
  public static final String PATHS_TO_SERVE = "pathsToServe";
  
  /**
   * Accepts.
   *
   * @param pRequest the request
   * @param pSettings the settings
   * @return true, if successful
   */
  @Override
  public boolean accepts(ProxiedHttpRequest pRequest, ModuleSettings pSettings) {
    
    if (!pRequest.getMethod().equals(HttpMethod.GET)) {
      return false;
    }
    
    boolean enabled = pSettings.getBooleanValue(ENABLED);
    
    if (enabled) {
      
      String resourcesLocation = pSettings.getStringValue(STATIC_RESOURCES_DIRECTORY);
      
      if (!StringUtils.isEmpty(resourcesLocation)) {
        String[] paths = pSettings.getStringValue(PATHS_TO_SERVE).split(",");
        
        for (String path : paths) {
          if (pRequest.getResourcePath().startsWith(path)) {
            return true;
          }
        }
      }
    }
    
    return false;
  }

  /**
   * Process request.
   *
   * @param pRequest the request
   * @param pObject the object
   * @param pSettings the settings
   * @return the http response
   */
  @Override
  public HttpResponse processRequest(ProxiedHttpRequest pRequest, HttpObject pObject, ModuleSettings pSettings) {
    
    String resourcePath = pRequest.getResourcePath();
    String resourcesLocation = pSettings.getStringValue(STATIC_RESOURCES_DIRECTORY);
    
    String localPath = resourcesLocation + resourcePath;
    
    File resource = new File(localPath);
    
    if (resource.exists() && resource.isFile()) {
      try (InputStream in = new FileInputStream(resource)) {
        if (in != null) {
          byte[] content = IOUtils.toByteArray(in);
          
          String mimeType = MimeUtils.getMimeTypeFromFileExtension(resourcePath);
          
          ByteBuf buffer = Unpooled.wrappedBuffer(content);
          
          DefaultFullHttpResponse newResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
          
          HttpHeaders.setContentLength(newResponse, content.length);
          
          if (!StringUtils.isEmpty(mimeType)) {
            newResponse.headers().set("Content-Type", mimeType);
          }
          
          newResponse.headers().set("Connection","keep-alive");
          newResponse.headers().set("Accept-Ranges", "bytes");
          newResponse.headers().set("Vary", "Accept-Encoding");
          
          SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
          
          newResponse.headers().set("Last-Mofidied", df.format(new Date()));
          newResponse.headers().set("X-CC_Request_Served_Locally", true);
          
          mLogger.debug("Serving " + resourcePath + " from local directory.");
          
          return newResponse;
        }
      }
      catch(IOException e) {
        mLogger.error("Error processing request", e);
      }
    }
    
    return null;
  }

}
