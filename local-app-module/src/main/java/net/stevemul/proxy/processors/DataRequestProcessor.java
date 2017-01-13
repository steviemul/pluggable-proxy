package net.stevemul.proxy.processors;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import net.stevemul.proxy.Constants;
import net.stevemul.proxy.Environment;
import net.stevemul.proxy.data.ModuleSettingData;
import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.events.EventType;
import net.stevemul.proxy.http.ProxiedHttpRequest;
import net.stevemul.proxy.modules.api.Module;
import net.stevemul.proxy.modules.api.ModuleSettingType;
import net.stevemul.proxy.services.ModuleService;
import net.stevemul.proxy.services.ServiceRegistry;
import net.stevemul.proxy.utils.ApplicationArguments;

/**
 * The Class DataRequestProcessor.
 * 
 * @author smulrenn
 */
public class DataRequestProcessor implements RequestProcessor {

  public static final String DATA_CONTEXT = LocalAppRequestProcessor.FORWARD_SLASH + "data";
  
  /** The logger. */
  private static Log mLogger = LogFactory.getLog(DataRequestProcessor.class);

  private ModuleService mModuleService = ServiceRegistry.getModuleService();
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.Processor#accepts(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public boolean accepts(ProxiedHttpRequest pRequest, ModuleSettings pSettings) {
    
    String uri = pRequest.getUri();
    
    return uri.startsWith(LocalAppRequestProcessor.ADMIN_CONTEXT + DATA_CONTEXT);
  }

  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.processors.spi.RequestProcessor#processRequest(net.stevemul.proxy.http.ProxiedHttpRequest, io.netty.handler.codec.http.HttpObject, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public HttpResponse processRequest(ProxiedHttpRequest pRequest, HttpObject pObject, ModuleSettings pSettings) {
    
    if (pRequest.getMethod().equals(HttpMethod.GET)) {
      String data = getJsonData();
      
      return buildResponse(data);
    }
    else if (pRequest.getMethod().equals(HttpMethod.PUT)) {
      String body = pRequest.getRequestBody();
      
      saveData(body);
      
      return getSuccess();
    }
    
    return null;
  }
  
  /**
   * Builds the response.
   *
   * @param pData the data
   * @return the http response
   */
  private HttpResponse buildResponse(String pData)  {
    
    HttpResponseStatus status = HttpResponseStatus.OK;
    
    byte[] bytes = pData.getBytes(UTF_8);
    
    ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
    
    DefaultFullHttpResponse newResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buffer);
    
    HttpHeaders.setContentLength(newResponse, bytes.length);
    newResponse.headers().add("Content-Type", "application/json; charset=UTF-8");
    newResponse.headers().add("Vary", "Accept-Encoding");
    
    newResponse.headers().add("Connection", "close");
    
    return newResponse;
  }
  
  /**
   * Gets the success.
   *
   * @return the success
   */
  private HttpResponse getSuccess() {
    JSONObject jsonResponse = new JSONObject();
    
    jsonResponse.put("success", true);
    
    byte[] responseContent = jsonResponse.toString().getBytes(UTF_8);
    
    ByteBuf buffer = Unpooled.wrappedBuffer(responseContent);
    
    DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
    
    HttpHeaders.setHeader(response, "Content-Type", "application/json");
    HttpHeaders.setContentLength(response, responseContent.length);
    
    return response;
  }
  
  /**
   * Gets the modules json data.
   *
   * @return the modules json data
   */
  private String getJsonData() {
    
    List<Module> modules = mModuleService.getModules();
    
    JSONObject data = new JSONObject();
    JSONArray jsonModules = new JSONArray();
    
    for (Module module : modules) {
      List<ModuleSettingData> settings = mModuleService.getModuleData(module);
      
      if (settings.size() > 0) {
        Map<String, Object> moduleData = new HashMap<>();
        
        moduleData.put("title", module.getName());
        moduleData.put("settings", getSettingData(module, settings));
        moduleData.put("namespace", module.getNamespace());
        moduleData.put("id", module.getNamespace().replace(".", "-"));
        
        jsonModules.put(moduleData);
      }
    }
    
    data.put("modules", jsonModules);
    data.put("application", getAppSettings());
    
    return data.toString();
  }
  
  /**
   * Gets the app settings.
   *
   * @return the app settings
   */
  private JSONObject getAppSettings() {
    JSONObject appSettings = new JSONObject();
    
    int listenPort = Constants.DEFAULT_SOCKET_IO_PORT;
    String hostname = Constants.DEFAULT_HOSTNAME;
    
    if (Environment.getEnvironmentArgument(ApplicationArguments.SOCKET_IO_PORT) != null) {
      listenPort = Integer.valueOf(Environment.getEnvironmentArgument(ApplicationArguments.SOCKET_IO_PORT));
    }
    
    if (Environment.getEnvironmentArgument(ApplicationArguments.OVERRIDE_HOSTNAME) != null) {
      hostname = Environment.getEnvironmentArgument(ApplicationArguments.OVERRIDE_HOSTNAME);
    }
    
    appSettings.put("SocketIOPort", listenPort);
    appSettings.put("Hostname", hostname);
    
    return appSettings;
  }
  
  /**
   * Gets the setting data.
   *
   * @param pModule the module
   * @param pSetting the setting
   * @return the setting data
   */
  private List<Map<String, Object>> getSettingData(Module pModule, List<ModuleSettingData> pSettings) {
    
    List<Map<String, Object>> settings = new ArrayList<>();
    
    for (ModuleSettingData setting : pSettings) {
      Map<String, Object> data = new HashMap<>();
      
      data.put("key", setting.getKey());
      
      if (ModuleSettingType.CHECKBOX.getName().equals(setting.getType())) {
        data.put("value", Boolean.valueOf(setting.getValue()));
      }
      else {
        data.put("value", setting.getValue());
      }
      
      data.put("type", setting.getType());
      data.put("label", pModule.getSettingLabel(setting.getKey()));
      
      if (ModuleSettingType.CONSOLE.getName().equals(setting.getType())) {
        data.put("messages", Collections.<String>emptyList());
      }
      
      if (ModuleSettingType.OPTIONS.getName().equals(setting.getType())) {
        data.put("options", buildOptions(pModule.getSettingOptions(setting.getKey())));
      }
      
      settings.add(data);
    }
    
    return settings;
  }
  
  /**
   * Builds the options.
   *
   * @param pOptions the options
   * @return the list
   */
  private List<Map<String, String>> buildOptions(Map<String, String> pOptions) {
    List<Map<String, String>> options = new ArrayList<>();
    
    for (String key : pOptions.keySet()) {
      Map<String, String> option = new HashMap<>();
      
      option.put("key", key);
      option.put("value", pOptions.get(key));
      
      options.add(option);
    }
    
    return options;
  }
  
  /**
   * Save data.
   *
   * @param pBody the body
   */
  private void saveData(String pBody) {
    
    try {
      JSONArray json = new JSONArray(pBody);
      
      for (int i=0;i<json.length();i++) {
        JSONObject moduleInput = json.getJSONObject(i);
        
        String namespace = moduleInput.optString("namespace");
        
        Module module = mModuleService.getModule(namespace);
        
        if (module != null) {
          List<ModuleSettingData> settingsData = new ArrayList<>();
          
          JSONArray settings = moduleInput.optJSONArray("settings");
          
          if (settings != null && settings.length() > 0) {
            for (int j=0;j<settings.length();j++) {
              JSONObject setting = settings.getJSONObject(j);
              
              String key = setting.getString("key");
              String value = setting.optString("value");
              
              if (value != null) {
                settingsData.add(ModuleSettingData.buildSettingData(key, value, "", 0));
              }
            }
            
            mModuleService.saveModuleData(module, settingsData);
            
            if (module.getProxyEventListener() != null) {
              ServiceRegistry.getEventService().dispatchEvent(module.getProxyEventListener(), EventType.SETTINGS_SAVED, module, settingsData);
            }
          }
        }
      }
    
    }
    catch (JSONException e) {
      mLogger.error("Unable to save data", e);
    }
  }
}
