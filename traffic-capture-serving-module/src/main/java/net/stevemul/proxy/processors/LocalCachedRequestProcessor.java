package net.stevemul.proxy.processors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

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
import net.stevemul.proxy.expression.Evaluator;
import net.stevemul.proxy.expression.IOProcessor;
import net.stevemul.proxy.http.ProxiedHttpRequest;
import net.stevemul.proxy.modules.TrafficCaptureServingModule;
import net.stevemul.proxy.utils.AppUtils;

/**
 * The Class LocalCachedRequestProcessor.
 */
public class LocalCachedRequestProcessor implements RequestProcessor  {

  private static Log mLogger = LogFactory.getLog(LocalCachedRequestProcessor.class);
 
  public static final String FORWARD_SLASH = "/";
  public static final String ADMIN_CONTEXT = FORWARD_SLASH + ".proxy";
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.Processor#accepts(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public boolean accepts(ProxiedHttpRequest pRequest, ModuleSettings pSettings) {
   
    if (!pRequest.getMethod().equals(HttpMethod.GET)) {
      return false;
    }
    
    if (pRequest.getUri().startsWith(ADMIN_CONTEXT)) {
      return false;
    }
    
    boolean servingLocal = pSettings.getBooleanValue(TrafficCaptureServingModule.SERVING_LOCAL);
    
    return servingLocal;
  }

 
  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.spi.RequestProcessor#processRequest(net.stevemul.proxy.http.ProxiedHttpRequest, io.netty.handler.codec.http.HttpObject, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public HttpResponse processRequest(ProxiedHttpRequest pRequest, HttpObject pObject, ModuleSettings pSettings) {
   
    try {
      String contentLocationSetting = pSettings.getStringValue(TrafficCaptureServingModule.CONTENT_DIRECTORY);
      File contentLocation = new File(AppUtils.getContentOutputLocation(pRequest,contentLocationSetting));
      
      File headerLocation = new File(contentLocation.getPath() + ".hdrs");
      
      byte[] content = null;
      Map<String, String> headers = new HashMap<>();
      
      if (contentLocation.exists()) {
        content = readFile(contentLocation);
      }
      
      if (headerLocation.exists()) {
        headers = readHeaders(headerLocation);
      }
      
      if (content != null) {
        return buildResponse(content, headers);
      }
    }
    catch (Exception e) {
      mLogger.error("Could not locate local content", e);
    }
    
    return null;
  }
  
  /**
   * Read headers.
   *
   * @param pLocation the location
   * @return the map
   */
  @SuppressWarnings("unchecked")
  private Map<String, String> readHeaders(File pLocation) {
    
    Map<String, String> headers = new HashMap<>();
    
    try (InputStream in = new FileInputStream(pLocation)) {
      try (ObjectInput objIn = new ObjectInputStream(in)) {
        headers = (Map<String, String>) objIn.readObject();
      }
    }
    catch (IOException | ClassNotFoundException e) {
      mLogger.error("Unable to read headers", e);
    }
    
    return headers;
  }
  
  /**
   * Read file.
   *
   * @param pLocation the location
   * @return the byte[]
   * @throws IOException 
   * @throws FileNotFoundException 
   */
  private byte[] readFile(File pLocation) throws FileNotFoundException, IOException {
    byte[] content = null;
    
    try (InputStream in = new FileInputStream(pLocation)) {
      content = IOUtils.toByteArray(in);
    }
    
    return content;
  }
  
  /**
   * Builds the response.
   *
   * @param pContent the content
   * @param headers the headers
   * @return the http response
   */
  private HttpResponse buildResponse(byte[] pContent, Map<String, String> headers)  {
      
    String contentType = getContentType(headers);
    
    Evaluator evaluator = Evaluator.getInstance();
    
    evaluator.getContext().set("io", new IOProcessor());
    
    pContent = evaluator.process(pContent, contentType);
    
    HttpResponseStatus status = HttpResponseStatus.OK;
    
    ByteBuf buffer = Unpooled.wrappedBuffer(pContent);
    
    DefaultFullHttpResponse newResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buffer);
    
    for (String header : headers.keySet()) {
      newResponse.headers().add(header, headers.get(header));
    }
    
    newResponse.headers().add("X-CC_Response_Cached", "Reponse served from local proxy cache");
    
    HttpHeaders.setContentLength(newResponse, pContent.length);
    
    return newResponse;
  }
 
  /**
   * Gets the content type.
   *
   * @param pHeaders the headers
   * @return the content type
   */
  private String getContentType(Map<String, String> pHeaders) {
    
    String contentType = "";
    
    if (pHeaders.containsKey("Content-Type")) {
      contentType = pHeaders.get("Content-Type");
      
      if (contentType.contains(";")) {
        contentType = contentType.split(";")[0].trim();
      }
    }
    
    return contentType;
  }
}
