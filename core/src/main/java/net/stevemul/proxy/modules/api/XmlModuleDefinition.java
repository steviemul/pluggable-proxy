package net.stevemul.proxy.modules.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.stevemul.proxy.events.ProxyEventListener;
import net.stevemul.proxy.processors.RequestProcessor;
import net.stevemul.proxy.processors.ResponseProcessor;
import net.stevemul.proxy.xml.XMLProcessor;

/**
 * The Class XmlModuleDefinition.
 */
public class XmlModuleDefinition implements Module {
  
  public static final String MODULE = "module";
  public static final String NAME = "name";
  public static final String NAMESPACE = "namespace";
  public static final String LOADING_PRIORITY = "loading-priority";
  public static final String ORDER = "order";
  public static final String TYPE = "type";
  public static final String LABEL = "label";
  public static final String KEY = "key";
  public static final String DEFAULT = "default";
  
  public static final String SETTINGS_XPATH = "module/settings//setting";
  public static final String REQUEST_PROCESSORS_XPATH = "module/request-processors//processor";
  public static final String RESPONCE_PROCESSORS_XPATH = "module/response-processors//processor";
  public static final String ACTIONS_XPATH = "module/actions//action";
  public static final String OPTIONS_XPATH = "options//option";
  public static final String EVENT_LISTENER_XPATH = "module/event-listener";
  
  /** The m name. */
  private String mName;
  
  /** The m namespace. */
  private String mNamespace;
  
  /** The m loading priority. */
  private int mLoadingPriority;
  
  /** The m request processors. */
  private List<RequestProcessor> mRequestProcessors = new ArrayList<>();
  
  /** The m response processors. */
  private List<ResponseProcessor> mResponseProcessors = new ArrayList<>();
  
  /** The m actions. */
  private Map<String, Action> mActions = new HashMap<>();
  
  private List<ModuleSetting> mModuleSettings = new ArrayList<>();
  
  private Map<String, String> mLabels = new HashMap<>();
  
  private ProxyEventListener mEventListener;
  
  /**
   * Instantiates a new xml module definition.
   *
   * @param pDefinition the definition
   * @throws Exception the exception
   */
  public XmlModuleDefinition(InputStream pDefinition) throws Exception {
    
    XMLProcessor processor = new XMLProcessor(pDefinition);
    
    Node module = processor.getNode(MODULE);
    
    mName = processor.getNodeValue(module, NAME);
    mNamespace = processor.getNodeValue(module, NAMESPACE);
    mLoadingPriority = Integer.parseInt(processor.getNodeValue(module, LOADING_PRIORITY));
    
    NodeList settings = processor.getNodes(SETTINGS_XPATH);
    
    if (settings != null && settings.getLength() > 0) {
      for (int i=0;i<settings.getLength();i++) {
        Node setting = settings.item(i);
        
        String settingName = processor.getNodeValue(setting, NAME);
        ModuleSettingType type = getSettingType(processor.getNodeValue(setting, TYPE));
        int order = Integer.parseInt(processor.getNodeValue(setting, ORDER));
        String label = processor.getNodeValue(setting, LABEL);
        String defaultValue = processor.getNodeValue(setting, DEFAULT);
        
        ModuleSetting moduleSetting = (StringUtils.isEmpty(defaultValue)) ? new ModuleSetting(settingName, type, order) : new ModuleSetting(settingName, type, defaultValue, order);
        
        if (ModuleSettingType.OPTIONS == type) {
          NodeList options = processor.getNodes(setting, OPTIONS_XPATH);
          
          if (options != null && options.getLength() > 0) {
            for (int j=0;j<options.getLength();j++) {
              Node option = options.item(j);
              
              String optionKey = processor.getAttributeValue(option, "key");
              String optionValue = option.getTextContent();
              
              moduleSetting.addOption(optionKey, optionValue);
            }
          }
        }
        
        mModuleSettings.add(moduleSetting);
        
        mLabels.put(settingName, label);
      }
    }
    
    NodeList requestProcessors = processor.getNodes(REQUEST_PROCESSORS_XPATH);
    
    if (requestProcessors != null && requestProcessors.getLength() > 0) {
      for (int i=0;i<requestProcessors.getLength();i++) {
        Node requestProcessor = requestProcessors.item(i);
        
        String className = requestProcessor.getTextContent();
        
        RequestProcessor processorInstance = (RequestProcessor)Class.forName(className).newInstance();
        
        mRequestProcessors.add(processorInstance);
      }
    }
    
    NodeList responseProcessors = processor.getNodes(RESPONCE_PROCESSORS_XPATH);
    
    if (responseProcessors != null && responseProcessors.getLength() > 0) {
      for (int i=0;i<responseProcessors.getLength();i++) {
        Node responseProcessor = responseProcessors.item(i);
        
        String className = responseProcessor.getTextContent();
        
        ResponseProcessor processorInstance = (ResponseProcessor)Class.forName(className).newInstance();
        
        mResponseProcessors.add(processorInstance);
      }
    }
    
    NodeList actions = processor.getNodes(ACTIONS_XPATH);
    
    if (actions != null && actions.getLength() > 0) {
      for (int i=0;i<actions.getLength();i++) {
        Node action = actions.item(i);
        
        String className = action.getTextContent();
        String actionKey = processor.getAttributeValue(action, KEY);
        
        Action actionInstance = (Action)Class.forName(className).newInstance();
        
        mActions.put(actionKey, actionInstance);
      }
    } 
    
    Node eventListener = processor.getNode(EVENT_LISTENER_XPATH);
    
    if (eventListener != null) {
      String className = eventListener.getTextContent();
      
      ProxyEventListener eventListenerInstance = (ProxyEventListener)Class.forName(className).newInstance();
      
      mEventListener = eventListenerInstance;
    }
  }
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getRequestProcessors()
   */
  @Override
  public List<RequestProcessor> getRequestProcessors() {
    return mRequestProcessors;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getResponseProcessors()
   */
  @Override
  public List<ResponseProcessor> getResponseProcessors() {
    return mResponseProcessors;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getActions()
   */
  @Override
  public Map<String, Action> getActions() {
    return mActions;
  }

  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getProxyEventListener()
   */
  @Override
  public ProxyEventListener getProxyEventListener() {
    return mEventListener;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getNamespace()
   */
  @Override
  public String getNamespace() {
    return mNamespace;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getName()
   */
  @Override
  public String getName() {
    return mName;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getSettings()
   */
  @Override
  public List<ModuleSetting> getSettings() {
    return mModuleSettings;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getSettingLabel(java.lang.String)
   */
  @Override
  public String getSettingLabel(String pSettingKey) {
    return mLabels.get(pSettingKey);
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getLoadingPriority()
   */
  @Override
  public int getLoadingPriority() {
    return mLoadingPriority;
  }
  
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getSettingOptions(java.lang.String)
   */
  @Override
  public Map<String, String> getSettingOptions(String pSettingKey) {
    
    for (ModuleSetting setting : mModuleSettings) {
      if (setting.getType() == ModuleSettingType.OPTIONS && pSettingKey.equals(setting.getName())) {
        return setting.getOptions();
      }
    }
    
    return null;
  }

  /**
   * Gets the setting type.
   *
   * @param pType the type
   * @return the setting type
   */
  private ModuleSettingType getSettingType(String pType) {
    
    switch (pType) {
      case "TEXT_BOX":
        return ModuleSettingType.TEXT_BOX;
      case "FILE":
        return ModuleSettingType.FILE;
      case "FOLDER":
        return ModuleSettingType.FOLDER;
      case "CHECKBOX":
        return ModuleSettingType.CHECKBOX;
      case "CONSOLE":
        return ModuleSettingType.CONSOLE;
      case "ACTION":
        return ModuleSettingType.ACTION;
      case "OPTIONS":
        return ModuleSettingType.OPTIONS;
    }
    
    return null;
  }
}
