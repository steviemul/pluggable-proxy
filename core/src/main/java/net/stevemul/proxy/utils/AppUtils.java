package net.stevemul.proxy.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import net.stevemul.proxy.http.ProxiedHttpRequest;
import static net.stevemul.proxy.Constants.HOST;
import static net.stevemul.proxy.Constants.FORWARD_SLASH;

public class AppUtils {
  
  public static final String RESOURCE_BUNDLE = "applicationResources";
  public static final String SHA_256 = "SHA-256";
  public static final String INDEX_HTML = "index.html";
  public static final int MAX_FILENAME_LENGTH = 255;
  
  private static Log mLogger = LogFactory.getLog(AppUtils.class);
  
  /**
   * Gets the bundle.
   *
   * @param pLocale the locale
   * @return the bundle
   */
  public static ResourceBundle getBundle(Locale pLocale) {
    return ResourceBundle.getBundle(RESOURCE_BUNDLE, pLocale);
  }
  
  /**
   * Gets the string.
   *
   * @param pKey the key
   * @return the string
   */
  public static String getString(String pKey) {
    return getString(pKey, new Object[0]);
  }
  
  /**
   * Gets the string.
   *
   * @param pKey the key
   * @param pArgs the args
   * @return the string
   */
  public static String getString(String pKey, Object... pArgs) {
    
    String value = getBundle(Locale.ENGLISH).getString(pKey);
    
    return String.format(value, pArgs);
  }
  
  /**
   * Creates the hash.
   *
   * @param pInput the input
   * @return the string
   */
  public static String createHash(String pInput) {

    String digestString = pInput;
    
    try {
      MessageDigest md = MessageDigest.getInstance(SHA_256);
      md.update(pInput.getBytes(UTF_8));
      
      byte[] digestBytes = md.digest();
      digestString = DatatypeConverter.printBase64Binary(digestBytes);
    }
    catch(NoSuchAlgorithmException e) {
      mLogger.error("Error creating hash", e);
    }
    
    return digestString;
  }
  
  /**
   * Gets the output location.
   *
   * @return the output location
   */
  public static String getContentOutputLocation(ProxiedHttpRequest pRequest, String pContentDirectory) {
    
    String location = null;
    
    String resource = FORWARD_SLASH.equals(pRequest.getUri()) ? INDEX_HTML : pRequest.getUri();
    
    String host = pRequest.getHeader(HOST);
      
    if (!StringUtils.isEmpty(host)) {
      File outputFile = new File(pContentDirectory + File.separator + host + File.separator + resource);
        
      if (outputFile.getParentFile() != null) {
        outputFile.getParentFile().mkdirs();
      }
      
      if (outputFile.getName().length() > MAX_FILENAME_LENGTH) {
        String hashedName = AppUtils.createHash(outputFile.getName()).replace("/", "#");
        
        outputFile = new File(outputFile.getParentFile() + File.separator + hashedName);
      }
      
      location = outputFile.getPath();
    }
    
    return location;
  }
  
  /**
   * Gets the query params.
   *
   * @param pUri the uri
   * @return the query params
   */
  public static Map<String, String> getQueryParams(String pUri) {
    
    Map<String, String> queryParamsMap = new HashMap<>();
    List<NameValuePair> queryParams = URLEncodedUtils.parse(pUri, UTF_8);
    
    for (NameValuePair pair : queryParams) {
      queryParamsMap.put(pair.getName(), pair.getValue());
    }
    return queryParamsMap;
  }
  
  /**
   * Gets the query string.
   *
   * @param pUri the uri
   * @return the query string
   */
  public static String getQueryString(String pUri) {
    int pos = pUri.indexOf("?");
    
    if (pos > -1) {
      return pUri.substring(pos + 1);
    }
    
    return pUri;
  }
  
  /**
   * Gets the last path.
   *
   * @param pUri the uri
   * @return the last path
   */
  public static String getLastPath(String pUri) {
    
    int pos = pUri.indexOf("?");
    
    if (pos > -1) {
      String path = pUri.substring(0, pos);
       
      pos = path.lastIndexOf("/");
      
      if (pos > -1) {
        return path.substring(pos + 1);
      }
    }
    
    return "";
  }
  
  private AppUtils(){}
  
}
