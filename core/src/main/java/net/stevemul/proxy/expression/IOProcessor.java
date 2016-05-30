package net.stevemul.proxy.expression;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Class IOProcessor.
 */
public class IOProcessor {
  
  private static Log mLogger = LogFactory.getLog(IOProcessor.class);
  
  /**
   * Load.
   *
   * @param pFilename the filename
   * @return the string
   */
  public String load(String pFilename) {
    
    String output = "";
    
    try (InputStream in = new FileInputStream(pFilename)) {
      output = IOUtils.toString(in, UTF_8);
    }
    catch (IOException e) {
      mLogger.error("Unable to load file " + pFilename, e);
    }
    
    return output;
  }
}
