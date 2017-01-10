package net.stevemul.proxy.modules.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.stevemul.proxy.processors.RequestProcessor;
import net.stevemul.proxy.processors.ResponseProcessor;
import net.stevemul.proxy.xml.XMLProcessor;

/**
 * The Class XmlModuleDefinition.
 */
public class XmlModuleDefinition implements Module {
  
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
  
  /**
   * Instantiates a new xml module definition.
   *
   * @param pDefinition the definition
   * @throws Exception the exception
   */
  public XmlModuleDefinition(InputStream pDefinition) throws Exception {
    
    XMLProcessor processor = new XMLProcessor(pDefinition);
    
    Node module = processor.getNode("module");
    
    mName = processor.getNodeValue(module, "name");
    mNamespace = processor.getNodeValue(module, "namespace");
    mLoadingPriority = Integer.parseInt(processor.getNodeValue(module, "loading-priority"));
    
    NodeList settings = processor.getNodes("module/settings//setting");
    
    if (settings != null && settings.getLength() > 0) {
      for (int i=0;i<settings.getLength();i++) {
        Node setting = settings.item(i);
        
        String settingName = processor.getNodeValue(setting, "name");
        ModuleSettingType type = getSettingType(processor.getNodeValue(setting, "type"));
        int order = Integer.parseInt(processor.getNodeValue(setting, "order"));
        String label = processor.getNodeValue(setting, "label");
        String defaultValue = processor.getNodeValue(setting, "default");
        
        ModuleSetting moduleSetting = (StringUtils.isEmpty(defaultValue)) ? new ModuleSetting(settingName, type, order) : new ModuleSetting(settingName, type, defaultValue, order);
        
        mModuleSettings.add(moduleSetting);
        
        mLabels.put(settingName, label);
      }
    }
    
    NodeList requestProcessors = processor.getNodes("module/request-processors//processor");
    
    if (requestProcessors != null && requestProcessors.getLength() > 0) {
      for (int i=0;i<requestProcessors.getLength();i++) {
        Node requestProcessor = requestProcessors.item(i);
        
        String className = requestProcessor.getTextContent();
        
        RequestProcessor processorInstance = (RequestProcessor)Class.forName(className).newInstance();
        
        mRequestProcessors.add(processorInstance);
      }
    }
    
    NodeList responseProcessors = processor.getNodes("module/response-processors//processor");
    
    if (responseProcessors != null && responseProcessors.getLength() > 0) {
      for (int i=0;i<responseProcessors.getLength();i++) {
        Node responseProcessor = responseProcessors.item(i);
        
        String className = responseProcessor.getTextContent();
        
        ResponseProcessor processorInstance = (ResponseProcessor)Class.forName(className).newInstance();
        
        mResponseProcessors.add(processorInstance);
      }
    }
    
    NodeList actions = processor.getNodes("module/actions//action");
    
    if (actions != null && actions.getLength() > 0) {
      for (int i=0;i<actions.getLength();i++) {
        Node action = actions.item(i);
        
        String className = action.getTextContent();
        String actionKey = processor.getAttributeValue(action, "key");
        
        Action actionInstance = (Action)Class.forName(className).newInstance();
        
        mActions.put(actionKey, actionInstance);
      }
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
      case "CHECKBOX":
        return ModuleSettingType.CHECKBOX;
      case "CONSOLE":
        return ModuleSettingType.CONSOLE;
      case "ACTION":
        return ModuleSettingType.ACTION;
    }
    
    return null;
  }
}
