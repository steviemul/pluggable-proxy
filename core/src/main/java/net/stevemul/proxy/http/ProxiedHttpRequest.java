package net.stevemul.proxy.http;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.stevemul.proxy.Constants.Q_MARK;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

/**
 * The Class ProxiedHttpRequest.
 */
public class ProxiedHttpRequest {

  /** The m internal request. */
  private final DefaultFullHttpRequest mInternalRequest;
  
  /** The logger. */
  private static Log mLogger = LogFactory.getLog(ProxiedHttpRequest.class);
  
  /**
   * Instantiates a new proxied http request.
   *
   * @param pInternalRequest the internal request
   */
  public ProxiedHttpRequest(HttpRequest pInternalRequest) {
    mInternalRequest = (DefaultFullHttpRequest) pInternalRequest;
  }
  
  /**
   * Gets the uri.
   *
   * @return the uri
   */
  public String getUri() {
    return mInternalRequest.getUri();
  }
  
  /**
   * Gets the request body.
   *
   * @return the request body
   */
  public String getRequestBody() {
    return mInternalRequest.content().toString(UTF_8);
  }
  
  /**
   * Gets the method.
   *
   * @return the method
   */
  public HttpMethod getMethod() {
    return mInternalRequest.getMethod();
  }
  
  /**
   * Gets the query params.
   *
   * @return the query params
   */
  public Map<String, String> getQueryParams() {
    
    Map<String, String> params = new HashMap<>();
    
    try {
      String uri = getUri();
      
      int pos = uri.indexOf(Q_MARK);
      
      if (pos > -1) {
        String paramString = uri.substring(pos);
        
        paramString = URLDecoder.decode(paramString, UTF_8.name());
        
        List<NameValuePair> pairs = URLEncodedUtils.parse(paramString, UTF_8);
        
        for (NameValuePair pair : pairs) {
          params.put(pair.getName(), pair.getValue());
        }
      }
    }
    catch (Exception e) {
      mLogger.error("Unable to parse query string", e);
    }
      
    return params;
  }
  /**
   * Gets the headers.
   *
   * @return the headers
   */
  public Map<String, String> getHeaders() {
    Map<String, String> headers = new HashMap<>();
    
    Iterator<Entry<String, String>> headersItr = mInternalRequest.headers().iterator();
    
    while (headersItr.hasNext()) {
      Entry<String, String> entry = headersItr.next();
      
      headers.put(entry.getKey(), entry.getValue());
    }
    
    return headers;
  }
  
  /**
   * Gets the header.
   *
   * @param pName the name
   * @return the header
   */
  public String getHeader(String pName) {
    return getHeaders().get(pName);
  }
  
  /**
   * Gets the internal request.
   *
   * @return the internal request
   */
  public HttpRequest getInternalRequest() {
    return mInternalRequest;
  }
}
