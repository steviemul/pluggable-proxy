package net.stevemul.proxy.http;

import java.util.Map;

/**
 * The Interface Response.
 */
public interface Response {

  /**
   * Gets the content.
   *
   * @return the content
   */
  public byte[] getContent();
  
  /**
   * Gets the headers.
   *
   * @return the headers
   */
  public Map<String, String> getHeaders();
  
  /**
   * Gets the header.
   *
   * @param pName the name
   * @return the header
   */
  public String getHeader(String pName);
}
