package net.stevemul.proxy.processors;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.stevemul.proxy.TrafficCaptureConstants;
import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.http.LocalHttpResponse;
import net.stevemul.proxy.http.ProxiedHttpRequest;
import net.stevemul.proxy.http.Response;

/**
 * The Class PagesEndpointResponseProcessor.
 * 
 * @author smulrenn
 */
public class PagesEndpointResponseProcessor extends AbstractResponseProcessor {

  /** The Constant PAGES_ENDPOINT_URI. */
  public static final String PAGES_ENDPOINT_URI = "/ccstoreui/v1/pages";
  
  /** The Constant WIDGETS. */
  public static final String WIDGETS = "widgets";
  
  /** The Constant REGIONS. */
  public static final String REGIONS = "regions";
  
  /** The Constant ID. */
  public static final String ID = "id";
  
  /** The Constant TEMPLATE_SRC. */
  public static final String TEMPLATE_SRC = "templateSrc";
  public static final String ELEMENTS_SRC = "elementsSrc";
  
  /** The m logger. */
  private static Log mLogger = LogFactory.getLog(PagesEndpointResponseProcessor.class);
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.AbstractResponseProcessor#processProxyToClientResponse(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.http.Response, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public Response processProxyToClientResponse(ProxiedHttpRequest pRequest, Response pResponse, ModuleSettings pSettings) {
    
    LocalHttpResponse newResponse = null;
        
    String content = new String(pResponse.getContent(), UTF_8);
    
    if (!StringUtils.isEmpty(content)) {
      content = filterContent(content, pSettings);
      
      byte[] bytes = content.getBytes(UTF_8);
      
      newResponse = new LocalHttpResponse();
      
      newResponse.setStatusCode(200);
      newResponse.setContent(bytes);
      
      Map<String, String> headers = pResponse.getHeaders();
      
      for (String header : headers.keySet()) {
        newResponse.setHeader(header, headers.get(header));
      }
        
      newResponse.setHeader("X-CC_Proxied", "Reponse modified by dev proxy");
    }
    
    return newResponse;
  }

  /**
   * Filter content.
   *
   * @param pContent the content
   * @param pSettings the settings
   * @return the string
   */
  private String filterContent(String pContent, ModuleSettings pSettings) {
    
    String content = pContent;
    try {
      JSONObject json = new JSONObject(pContent);
      
      Date now = new Date();
      DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);
      
      if (json.has("title")) {
        String title = json.getString("title") + " (Proxied at " + df.format(now) + ")";
        
        json.put("title", title);
        
        processPagesEndpoint(json, pSettings);
        
        content = json.toString();
      }
      
    }
    catch(JSONException e) {
      mLogger.error("Error parsing page response.", e);
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

  /**
   * Process pages endpoint.
   *
   * @param pInput the input
   * @param pSettings the settings
   */
  private void processPagesEndpoint(JSONObject pInput, ModuleSettings pSettings) {
  
    boolean allowTemplateOverrides = pSettings.getBooleanValue(TrafficCaptureConstants.ALLOW_TEMPLATE_OVERRIDES);
    boolean dumpTemplates = pSettings.getBooleanValue(TrafficCaptureConstants.DUMP_TEMPLATES);
    
    if (allowTemplateOverrides || dumpTemplates) {
      JSONArray regions = pInput.optJSONArray(REGIONS);
      
      if (regions != null && regions.length() > 0) {
        for (int i=0;i<regions.length();i++) {
          JSONObject region = regions.getJSONObject(i);
        
          JSONArray widgets = region.optJSONArray(WIDGETS);
          
          if (widgets != null && widgets.length() > 0) {
            for (int j=0;j<widgets.length();j++) {
              JSONObject widget = widgets.getJSONObject(j);
              
              String id = widget.optString(ID);
              
              try {
                
                String templateDir = pSettings.getStringValue(TrafficCaptureConstants.TEMPLATE_DIRECTORY);
                
                String templateSrcLocation = templateDir + File.separator + id + ".html";
                String elementSrcLocation = templateDir + File.separator + id + ".elements.html";
                
                overrideWidgetPropertyFromFileIfApplicable(widget, TEMPLATE_SRC, templateSrcLocation, pSettings);
                overrideWidgetPropertyFromFileIfApplicable(widget, ELEMENTS_SRC, elementSrcLocation, pSettings);
                
              }
              catch (IOException e) {
                mLogger.error("Error getting template.", e);
              }
            }
          }
        }
      }
    }
  }
  
  /**
   * Override widget property from file if applicable.
   *
   * @param pWidget the widget
   * @param pProperty the property
   * @param pFileLocation the file location
   * @param pSettings the settings
   * @throws FileNotFoundException the file not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void overrideWidgetPropertyFromFileIfApplicable(JSONObject pWidget, String pProperty, String pFileLocation, ModuleSettings pSettings) throws FileNotFoundException, IOException {
    
    boolean allowTemplateOverrides = pSettings.getBooleanValue(TrafficCaptureConstants.ALLOW_TEMPLATE_OVERRIDES);
    boolean dumpTemplates = pSettings.getBooleanValue(TrafficCaptureConstants.DUMP_TEMPLATES);
    
    String prop = pWidget.optString(pProperty, "");
    
    File templateFile = new File(pFileLocation);
    
    if (allowTemplateOverrides) {
      
      if (templateFile.exists()) {
        try (InputStream in = new FileInputStream(templateFile)) {
          if (in != null) {
            String overrideContents = IOUtils.toString(in, UTF_8);
          
            pWidget.put(pProperty, overrideContents);
          
            prop = overrideContents;
          }
        }
      }
    }
    
    if (dumpTemplates) {
      try (OutputStream out = new FileOutputStream(templateFile)) {
        if (out != null) {
          IOUtils.write(prop, out);
        }
      }
    }
  }
}
