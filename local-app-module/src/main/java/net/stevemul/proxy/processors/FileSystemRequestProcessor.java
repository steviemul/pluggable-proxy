package net.stevemul.proxy.processors;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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

/**
 * The Class FileSystemRequestProcessor.
 */
public class FileSystemRequestProcessor implements RequestProcessor {

  public static final String FILE_API_CONTEXT = "/.api/file";
  public static final String UL_OPENER = "<ul class=\"jqueryFileTree\" style=\"display: none;\">";
  public static final String UL_CLOSER = "</ul>";
  public static final String DIR_TAG = "<li class=\"directory collapsed\"><a href=\"#\" rel=\"%s/\">%s</a></li>";
  public static final String FILE_TAG = "<li class=\"file ext_%s\"><a href=\"#\" rel=\"%s\">%s</a></li>";
  public static final String FORWARD_SLASH = "/";
  
  private static Log mLogger = LogFactory.getLog(FileSystemRequestProcessor.class);
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.Processor#accepts(io.netty.handler.codec.http.HttpRequest, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public boolean accepts(ProxiedHttpRequest pRequest, ModuleSettings pSettings) {
    return pRequest.getUri().startsWith(FILE_API_CONTEXT);
  }

  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.spi.RequestProcessor#processRequest(io.netty.handler.codec.http.HttpRequest, io.netty.handler.codec.http.HttpObject, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public HttpResponse processRequest(ProxiedHttpRequest pRequest, HttpObject pObject, ModuleSettings pSettings) {
    
    HttpResponseStatus status = HttpResponseStatus.OK;
    
    String dir = FORWARD_SLASH;
    
    if (pRequest.getMethod().equals(HttpMethod.POST)) {
        dir = getDir(pRequest.getRequestBody());
    }
    
    if (FORWARD_SLASH.equals(dir)) {
      dir = System.getProperty("user.home") + File.separator;
    }
    
    Map<String, String> queryParams = getQueryParams(pRequest.getUri());
    boolean showFiles = true;
    
    if (queryParams.containsKey("dirs")) {
      showFiles = false;
    }
    
    String responseContent = buildFileTreeResponse(new File(dir), showFiles);
    
    byte[] bytes = responseContent.getBytes(UTF_8);
    
    ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
    
    DefaultFullHttpResponse newResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buffer);
    
    HttpHeaders.setContentLength(newResponse, bytes.length);
    
    return newResponse; 
  }

  /**
   * Gets the dir.
   *
   * @param pRequestBody the request body
   * @return the dir
   */
  private String getDir(String pRequestBody) {
    
    try {
      pRequestBody = URLDecoder.decode(pRequestBody, UTF_8.name());
      
      String[] parts = pRequestBody.split("=");
      
      if (parts.length == 2 && "dir".equals(parts[0])) {
        return parts[1];
      }
    }
    catch (Exception e) {
      mLogger.error("Error reading file", e);
    }
    
    return "/";
  }
  
  /**
   * Gets the query params.
   *
   * @param pUri the uri
   * @return the query params
   */
  private Map<String, String> getQueryParams(String pUri) {
    
    Map<String, String> params = new HashMap<>();
    
    try {
      if (pUri.contains("?")) {
        int pos = pUri.indexOf("?");
        
        String query = URLDecoder.decode(pUri.substring(pos + 1), UTF_8.name());
        
        String[] parts = query.split("&");
        
        for (String part : parts) {
          String[] values = part.split("=");
          params.put(values[0], values[1]);
        }
      }
    }
    catch (Exception e) {
      mLogger.error("Unable to decode query params", e);
    }
    
    return params;
  }
  
  /**
   * Builds the file tree response.
   *
   * @param pFile the file
   * @return the string
   */
  private String buildFileTreeResponse(File pFile, boolean pShowFiles) {
    
    StringBuilder response = new StringBuilder();
    
    response.append(UL_OPENER);
    
    if (pFile.exists() && pFile.isDirectory()) {
      File[] files = pFile.listFiles();
      
      Arrays.sort(files, new Comparator<File>(){

        @Override
        public int compare(File file1, File file2) {
          
          return file1.getName().compareToIgnoreCase(file2.getName());
        }
        
      });
      
      for (File child : files) {
        if (!child.isHidden()) {
          if (child.isDirectory()) {
            response.append(String.format(DIR_TAG, child.getPath(), child.getName()));
          }
          else if (pShowFiles && child.isFile()) {
            String ext = "js";
            
            int extPos = child.getName().lastIndexOf(".");
            
            if (extPos > -1) {
              ext = child.getName().substring(extPos + 1);
            }
            
            response.append(String.format(FILE_TAG, ext, child.getPath(), child.getName()));
          }
        }
      }
    }
    
    response.append(UL_CLOSER);
    
    return response.toString();
  }
  
}
