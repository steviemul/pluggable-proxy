package net.stevemul.proxy.template;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * The Class TemplateEngine.
 */
public class TemplateEngine {

  private static Log mLogger = LogFactory.getLog(TemplateEngine.class);
  
  private final VelocityEngine mVe;
  private final static TemplateEngine mInstance = new TemplateEngine();
  
  /**
   * Instantiates a new template engine.
   */
  private TemplateEngine() {
    mVe = new VelocityEngine();
   
    mVe.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
    mVe.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
    
    mVe.init();
  }
  
  /**
   * Gets the single instance of TemplateEngine.
   *
   * @return single instance of TemplateEngine
   */
  public static TemplateEngine getInstance() {
    return mInstance;
  }
  
  /**
   * Render.
   *
   * @param pTemplatePath the template path
   * @param pModel the model
   * @return the string
   */
  public String render(String pTemplatePath, Map<String, Object> pModel) {
    
    String output = "";
    
    try {
      Template template = mVe.getTemplate(pTemplatePath, UTF_8.name());
      
      VelocityContext context = new VelocityContext();
      
      for (String key : pModel.keySet()) {
        context.put(key, pModel.get(key));
      }
      
      StringWriter writer = new StringWriter();
      
      template.merge(context, writer);
      
      output = writer.toString();
    }
    catch (Exception e) {
      mLogger.error("Unable to render template", e);
    }
      
    return output;
  }
  
}
