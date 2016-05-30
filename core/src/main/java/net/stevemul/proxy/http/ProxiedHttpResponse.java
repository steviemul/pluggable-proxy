package net.stevemul.proxy.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;

/**
 * The Class ProxiedHttpResponse.
 */
public class ProxiedHttpResponse implements Response {

  /** The m internal response. */
  private HttpResponse mInternalResponse;
  
  /** The m internal message. */
  private HttpMessage mInternalMessage;
  
  /** The m internal content. */
  private HttpContent mInternalContent;
  
  /**
   * Instantiates a new proxied http response.
   *
   * @param pHttpResponse the http response
   */
  public ProxiedHttpResponse(HttpObject pHttpObject) {
    mInternalResponse = (HttpResponse) pHttpObject;
    mInternalMessage = (HttpMessage) pHttpObject;
    mInternalContent = (HttpContent) pHttpObject;
  }
  
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.http.Response#getContent()
   */
  @Override
  public byte[] getContent() {
    ByteBuf buf = mInternalContent.content().copy();
    
    byte[] content = new byte[buf.readableBytes()];
    buf.readBytes(content);
    
    return content;
  }
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.http.Response#getHeaders()
   */
  @Override
  public Map<String, String> getHeaders() {
    Map<String, String> headers = new HashMap<>();
    
    Iterator<Entry<String, String>> itr = mInternalMessage.headers().iterator();
    
    while (itr.hasNext()) {
      Entry<String, String> header = itr.next();
      
      headers.put(header.getKey(), header.getValue());
    }
    
    return headers;
  }
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.http.Response#getHeader(java.lang.String)
   */
  @Override
  public String getHeader(String pName) {
    return getHeaders().get(pName);
  }
  
  /**
   * Sets the header.
   *
   * @param pName the name
   * @param pValue the value
   */
  public void setHeader(String pName, String pValue) {
    HttpHeaders.setHeader(mInternalMessage, pName, pValue);
  }
  
  /**
   * Gets the internal response.
   *
   * @return the internal response
   */
  public HttpObject getInternalObject() {
    return mInternalResponse;
  }
}
