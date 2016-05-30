package net.stevemul.proxy.expression;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Class Evaluator.
 */
public class Evaluator {

  private final static Evaluator mInstance = new Evaluator();
  private final static String EL_REGEX = "\\$\\{.+\\}";
  
  private final JexlEngine mJexl;
  private final JexlContext mContext;
  private static Log mLogger = LogFactory.getLog(Evaluator.class);
  
  private final Pattern pattern = Pattern.compile(EL_REGEX);
  /**
   * Instantiates a new evaluator.
   */
  private Evaluator() {
    mJexl = new JexlBuilder().create();
    mContext = new MapContext();
  }
  
  /**
   * Gets the single instance of Evaluator.
   *
   * @return single instance of Evaluator
   */
  public static Evaluator getInstance() {
    return mInstance;
  }
  
  /**
   * Gets the context.
   *
   * @return the context
   */
  public JexlContext getContext() {
    return mContext;
  }
  
  /**
   * Process.
   *
   * @param pInput the input
   * @return the byte[]
   */
  public byte[] process(byte[] pInput, String pContentType) {
    
    try {
      if (canProcess(pContentType)) {
        String input = new String(pInput, UTF_8);
        
        Matcher matcher = pattern.matcher(input);
        boolean processed = false;
        
        StringBuffer result = new StringBuffer();

        while(matcher.find()) {
          String el = matcher.group();
          
          if (el.startsWith("${")) el = el.substring(2);
          if (el.endsWith("}")) el = el.substring(0, el.length() - 1);
          
          JexlExpression e = mJexl.createExpression( el );

          String value = (String) e.evaluate(mContext);
          
          matcher.appendReplacement(result, value);
          
          processed = true;
        }
        
        if (processed) {
          matcher.appendTail(result);
          
          return result.toString().getBytes(UTF_8);
        }
      }
    }
    catch (Exception e) {
      mLogger.warn("Unable to evaluate content", e);
    }
    
    return pInput;
  }
 
  private boolean canProcess(String pContentType) {
    return "application/json".equals(pContentType);
  }
}
