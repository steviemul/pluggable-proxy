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
 * The Class JavascriptLibsOverrideRequestProcessor.
 */
public class JavascriptLibsOverrideRequestProcessor implements RequestProcessor {
  
  /** The m logger. */
  private static Log mLogger = LogFactory.getLog(JavascriptLibsOverrideRequestProcessor.class);
  
  public static final String OVERRIDE_MAIN = "overrideMain";
  public static final String OVERRIDE_MAIN_LOCATION = "overrideMainLocation";
  public static final String OVERRIDE_STORE_LIBS = "overrideStorelibs";
  public static final String OVERRIDE_STORE_LIBS_LOCATION = "overrideStoreLibsLocation";
  public static final String FORWARD_SLASH = "/";
  public static final String MAIN_JS = "main.js";
  public static final String STORE_LIBS_JS = "store-libs.js";
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.Processor#accepts(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public boolean accepts(ProxiedHttpRequest pRequest, ModuleSettings pSettings) {
    
    if (!pRequest.getMethod().equals(HttpMethod.GET)) {
      return false;
    }
    
    String uri = pRequest.getUri();
    
    boolean overrideMain = pSettings.getBooleanValue(OVERRIDE_MAIN);
    
    if (overrideMain) {
      if (uri.contains(FORWARD_SLASH + MAIN_JS)) {
        return true;
      }
    }
    
    boolean overrideStoreLibs = pSettings.getBooleanValue(OVERRIDE_STORE_LIBS);
    
    if (overrideStoreLibs) {
      if (uri.contains(FORWARD_SLASH + STORE_LIBS_JS)) {
        return true;
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
    
    if (uri.contains(FORWARD_SLASH + MAIN_JS)) {
      
      String mainJsLocation = pSettings.getStringValue(OVERRIDE_MAIN_LOCATION);
      
      if (!StringUtils.isEmpty(mainJsLocation)) {
        return getLocalFile(mainJsLocation);
      }
    }
    
    if (uri.contains(FORWARD_SLASH + STORE_LIBS_JS)) {
      
      String mainJsLocation = pSettings.getStringValue(OVERRIDE_STORE_LIBS_LOCATION);
      
      if (!StringUtils.isEmpty(mainJsLocation)) {
        return getLocalFile(mainJsLocation);
      }
    }

    return null;
  }
  
  /**
   * Gets the local file.
   *
   * @param pPath the path
   * @return the local file
   */
  private HttpResponse getLocalFile(String pPath) {
    
    File resource = new File(pPath);
    
    if (resource.exists() && resource.isFile()) {
      try (InputStream in = new FileInputStream(resource)) {
        if (in != null) {
          byte[] content = IOUtils.toByteArray(in);
          
          String mimeType = MimeUtils.getMimeTypeFromFileExtension(pPath);
          
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
