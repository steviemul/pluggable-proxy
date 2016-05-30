package net.stevemul.proxy.processors;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.http.ProxiedHttpRequest;
import net.stevemul.proxy.processors.Processor;

/**
 * The Interface RequestProcessor.
 * 
 * @author smulrenn
 */
public interface RequestProcessor extends Processor {

  /**
   * Process request.
   *
   * @param pRequest the request
   * @param pObject the object
   * @param pSettings the settings
   * @return the http response
   */
  public HttpResponse processRequest(ProxiedHttpRequest pRequest, HttpObject pObject, ModuleSettings pSettings);
}
