package net.stevemul.proxy.http;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class LocalHttpResponse.
 */
public class LocalHttpResponse implements Response {

  /** The m content. */
  private byte[] mContent;
  
  /** The m headers. */
  private Map<String, String> mHeaders = new HashMap<>();
  
  /** The m status code. */
  private int mStatusCode;
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.http.Response#getContent()
   */
  @Override
  public byte[] getContent() {
    return mContent;
  }

  /**
   * Sets the content.
   *
   * @param pContent the new content
   */
  public void setContent(byte[] pContent) {
    mContent = pContent;
  }
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.http.Response#getHeaders()
   */
  @Override
  public Map<String, String> getHeaders() {
    return mHeaders;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.http.Response#getHeader(java.lang.String)
   */
  @Override
  public String getHeader(String pName) {
    return mHeaders.get(pName);
  }

  /**
   * Sets the header.
   *
   * @param pName the name
   * @param pValue the value
   */
  public void setHeader(String pName, String pValue) {
    mHeaders.put(pName, pValue);
  }
  
  /**
   * Gets the status code.
   *
   * @return the status code
   */
  public int getStatusCode() {
    return mStatusCode;
  }
  
  /**
   * Sets the status code.
   *
   * @param pStatusCode the new status code
   */
  public void setStatusCode(int pStatusCode) {
    mStatusCode = pStatusCode;
  }
}
