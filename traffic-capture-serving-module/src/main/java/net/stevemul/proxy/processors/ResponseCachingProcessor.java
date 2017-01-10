package net.stevemul.proxy.processors;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.netty.handler.codec.http.HttpMethod;
import net.stevemul.proxy.TrafficCaptureConstants;
import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.http.ProxiedHttpRequest;
import net.stevemul.proxy.http.ProxiedHttpResponse;
import net.stevemul.proxy.http.Response;
import net.stevemul.proxy.utils.AppUtils;

/**
 * The Class ResponseCachingProcessor.
 */
public class ResponseCachingProcessor extends AbstractResponseProcessor {

  private static Log mLogger = LogFactory.getLog(ResponseCachingProcessor.class);
  
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.Processor#accepts(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public boolean accepts(ProxiedHttpRequest pRequest, ModuleSettings pSettings) {
    
    if (!pRequest.getMethod().equals(HttpMethod.GET)) {
      return false;
    }
    
    boolean capturing = pSettings.getBooleanValue(TrafficCaptureConstants.CAPTURING);
    
    return capturing;
  }


  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.AbstractResponseProcessor#processServerToProxyResponse(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.http.Response, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public Response processServerToProxyResponse(ProxiedHttpRequest pRequest, Response pResponse, ModuleSettings pSettings) {
    
    try {
      String contentLocationSetting = pSettings.getStringValue(TrafficCaptureConstants.CONTENT_DIRECTORY);
      
      byte[] content = pResponse.getContent();
    
      if (content.length > 0) {
        String contentLocation = AppUtils.getContentOutputLocation(pRequest, contentLocationSetting);
        
        try (OutputStream out = new FileOutputStream(contentLocation)) {
          IOUtils.write(content, out);
          
          out.flush();
        }
          
        Map<String, String> headers = pResponse.getHeaders();
        
        saveHeaders(contentLocation, headers);
        
        ((ProxiedHttpResponse)pResponse).setHeader("Cache-Control", "max-age=0");
      } 
    }
    catch (Exception e) {
      mLogger.error("Unable to save response", e);
    }
 
    return pResponse;
  }

  /**
   * Save headers.
   *
   * @param pContentLocation the content location
   * @param pHeaders the headers
   */
  private void saveHeaders(String pContentLocation, Map<String, String> pHeaders) {
  
    String location = pContentLocation + ".hdrs";
    
    try (OutputStream out = new FileOutputStream(location)) {
      try (ObjectOutput objOut = new ObjectOutputStream(out)) {
        objOut.writeObject(pHeaders);
      }
    }
    catch (IOException e) {
      mLogger.error("Unable to save headers", e);
    }
  }
  
}
