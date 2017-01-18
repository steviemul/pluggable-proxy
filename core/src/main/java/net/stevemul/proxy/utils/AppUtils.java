package net.stevemul.proxy.utils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.stevemul.proxy.Constants.FORWARD_SLASH;
import static net.stevemul.proxy.Constants.HOST;
import static net.stevemul.proxy.Constants.HASH;
import static net.stevemul.proxy.Constants.Q_MARK;
  
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
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
      File outputFile = new File(pContentDirectory + File.separator + host + File.separator + getOSPath(resource));
        
      if (outputFile.getParentFile() != null) {
        outputFile.getParentFile().mkdirs();
      }
      
      if (outputFile.getName().length() > MAX_FILENAME_LENGTH) {
        String hashedName = AppUtils.createHash(outputFile.getName()).replace(FORWARD_SLASH, HASH);
        
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
    int pos = pUri.indexOf(Q_MARK);
    
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
    
    int pos = pUri.indexOf(Q_MARK);
    
    if (pos > -1) {
      String path = pUri.substring(0, pos);
       
      pos = path.lastIndexOf(FORWARD_SLASH);
      
      if (pos > -1) {
        return path.substring(pos + 1);
      }
    }
    
    return "";
  }
  
  /**
   * Load resources.
   *
   * @param name the name
   * @param classLoader the class loader
   * @return the list
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static List<InputStream> loadResources(final String name, final ClassLoader classLoader) throws IOException {
    final List<InputStream> list = new ArrayList<InputStream>();
    final Enumeration<URL> systemResources = (classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader).getResources(name);
    
    while (systemResources.hasMoreElements()) {
      list.add(systemResources.nextElement().openStream());
    }
  
    return list;
  }
  
  /**
   * Gets the OS path.
   *
   * @param pPath the path
   * @return the OS path
   */
  public static String getOSPath(String pPath) {
    
    return pPath.replaceAll(FORWARD_SLASH, File.separator);
  }
  
  private AppUtils(){}
  
}
