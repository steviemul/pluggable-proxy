package net.stevemul.proxy.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Class MimeUtils.
 */
public class MimeUtils {

  /** The Constant MIME_TYPES_FILE. */
  public static final String MIME_TYPES_FILE = "/MimeTypes.properties";
  
  /** The Constant mMimeTypes. */
  private static final Properties mMimeTypes = new Properties();
  
  /** The m logger. */
  private static Log mLogger = LogFactory.getLog(MimeUtils.class);
  
  static {
    
    try (final InputStream stream = MimeUtils.class.getResourceAsStream(MIME_TYPES_FILE)) {
      mMimeTypes.load(stream);
    } 
    catch (IOException e) {
      mLogger.error("Unable to load mime types.", e);
    }
  }
  
  /**
   * Gets the mime type from file extension.
   *
   * @param pFilename the filename
   * @return the mime type from file extension
   */
  public static String getMimeTypeFromFileExtension(String pFilename) {
    
    int extPos = pFilename.lastIndexOf(".");
    
    if (extPos > -1) {
      String ext = pFilename.substring(extPos + 1); 
      return mMimeTypes.getProperty(ext);
    }
    
    return null;
  }
  
  /**
   * Instantiates a new mime utils.
   */
  private MimeUtils(){}
}
