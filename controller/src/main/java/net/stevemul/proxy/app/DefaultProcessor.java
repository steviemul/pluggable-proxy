package net.stevemul.proxy.app;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import net.stevemul.proxy.data.ModuleDataHandler;
import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.http.LocalHttpResponse;
import net.stevemul.proxy.http.ProxiedHttpRequest;
import net.stevemul.proxy.http.ProxiedHttpResponse;
import net.stevemul.proxy.http.Response;
import net.stevemul.proxy.modules.ModuleServiceImpl;
import net.stevemul.proxy.modules.api.Action;
import net.stevemul.proxy.modules.api.Module;
import net.stevemul.proxy.processors.RequestProcessor;
import net.stevemul.proxy.processors.ResponseProcessor;
import net.stevemul.proxy.services.ServiceRegistry;

/**
 * The Class DefaultProcessor.
 * 
 * @author smulrenn
 */

public class DefaultProcessor extends HttpFiltersSourceAdapter {

  //-------------------------------------------------------------------------------
  // Constants
  //-------------------------------------------------------------------------------
  public static final int MAX_RESPONSE_BUFFER_SIZE = 10 * 1024 * 1024;
  public static final String ACTION_CONTEXT = "/.action";
  public static final String ACTION_NAME = "actionName";
  public static final String ACTION_NAMESPACE = "namespace";
  
  //-------------------------------------------------------------------------------
  // Members
  //-------------------------------------------------------------------------------
  private static Log mLogger = LogFactory.getLog(DefaultProcessor.class);
  private ModuleServiceImpl mModuleService;
  
  /**
   * Instantiates a new default processor.
   */
  public DefaultProcessor() {
    mModuleService = new ModuleServiceImpl();
    
    ServiceRegistry.registerModuleService(mModuleService);
    
    mModuleService.loadModules();
  }
  
   /* (non-Javadoc)
   * @see org.littleshoot.proxy.HttpFiltersSourceAdapter#getMaximumResponseBufferSizeInBytes()
   */
  @Override
  public int getMaximumResponseBufferSizeInBytes() {
    return MAX_RESPONSE_BUFFER_SIZE;
  }
  
  /* (non-Javadoc)
   * @see org.littleshoot.proxy.HttpFiltersSourceAdapter#getMaximumRequestBufferSizeInBytes()
   */
  @Override
  public int getMaximumRequestBufferSizeInBytes() {
    return 512 * 1024;
  }
  
  /* (non-Javadoc)
   * @see org.littleshoot.proxy.HttpFiltersSourceAdapter#filterRequest(io.netty.handler.codec.http.HttpRequest, io.netty.channel.ChannelHandlerContext)
   */
  @Override
  public HttpFilters filterRequest(final HttpRequest pOriginalRequest, ChannelHandlerContext pCtx) {
    
    return new HttpFiltersAdapter(pOriginalRequest) {
    
      /* (non-Javadoc)
       * @see org.littleshoot.proxy.HttpFiltersAdapter#clientToProxyRequest(io.netty.handler.codec.http.HttpObject)
       */
      @Override
      public HttpResponse clientToProxyRequest(HttpObject pHttpObject) {
        
        if (ACTION_CONTEXT.equals(pOriginalRequest.getUri()) && HttpMethod.POST.equals(pOriginalRequest.getMethod())) {
          return processAction(pOriginalRequest, pHttpObject);
        }
        else {
          return processRequest(pOriginalRequest, pHttpObject);
        }
      }
      
      /* (non-Javadoc)
       * @see org.littleshoot.proxy.HttpFiltersAdapter#serverToProxyResponse(io.netty.handler.codec.http.HttpObject)
       */
      @Override
      public HttpObject serverToProxyResponse(HttpObject pHttpObject) {
        return processResponse(pOriginalRequest, pHttpObject);
      }
    };
  }
  
  /**
   * Process request.
   *
   * @param pOriginalRequest the original request
   * @param pHttpObject the http object
   * @return the http response
   */
  private HttpResponse processRequest(final HttpRequest pOriginalRequest, HttpObject pHttpObject) {
  
    HttpResponse response = null;
    
    String uri = pOriginalRequest.getUri();
    
    for (Module module : mModuleService.getModules()) {
      if (isModuleEnabled(module)) {
        ModuleSettings settings = ModuleDataHandler.getInstance().getModuleData(module);
        
        ProxiedHttpRequest request = new ProxiedHttpRequest(pOriginalRequest);
        
        for (RequestProcessor processor : module.getRequestProcessors()) {
          if (processor.accepts(request, settings)) {
            
            mLogger.debug("Request uri : " + uri + " processed by " + processor);
            
            response = processor.processRequest(request, pHttpObject, settings);
            
            if (response != null) {
              return response;
            }
          }
        }
      }
    }
    
    return response;
  }
  
  /**
   * Process action.
   *
   * @param pOriginalRequest the original request
   * @param pHttpObject the http object
   * @return the http response
   */
  private HttpResponse processAction(final HttpRequest pOriginalRequest, HttpObject pHttpObject) {
    
    HttpResponse response = null;
    
    ProxiedHttpRequest request = new ProxiedHttpRequest(pOriginalRequest);
   
    String body = request.getRequestBody();
    JSONObject input = new JSONObject(body);
    
    String name = input.optString(ACTION_NAME);
    String namespace = input.optString(ACTION_NAMESPACE, "");
    
    if (name != null) {
      for (Module module : mModuleService.getModules()) {
        if (isModuleEnabled(module) && namespace.equals(module.getNamespace())) {
          Action action = module.getActions().get(name);
          
          if (action != null) {
            ModuleSettings settings = ModuleDataHandler.getInstance().getModuleData(module);
            action.execute(request, settings);
          }
          
          JSONObject jsonResponse = new JSONObject();
          
          jsonResponse.put("success", true);
          
          byte[] responseContent = jsonResponse.toString().getBytes(UTF_8);
          
          ByteBuf buffer = Unpooled.wrappedBuffer(responseContent);
          
          DefaultFullHttpResponse newResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
          
          HttpHeaders.setHeader(newResponse, "Content-Type", "application/json");
          HttpHeaders.setContentLength(newResponse, responseContent.length);
          
          response = newResponse;
        }
      }
    }
    
    return response;
  }
  
  /**
   * Process response.
   *
   * @param pOriginalRequest the original request
   * @param pHttpObject the http object
   * @return the http object
   */
  private HttpObject processResponse(final HttpRequest pOriginalRequest, HttpObject pHttpObject) {
    
    String uri = pOriginalRequest.getUri();
    
    for (Module module : mModuleService.getModules()) {
      if (isModuleEnabled(module)) {
        ModuleSettings settings = ModuleDataHandler.getInstance().getModuleData(module);
        
        ProxiedHttpRequest request = new ProxiedHttpRequest(pOriginalRequest);
        
        for (ResponseProcessor processor : module.getResponseProcessors()) {
          if (processor.accepts(request, settings)) {
            
            ProxiedHttpResponse proxiedResponse = new ProxiedHttpResponse(pHttpObject);
            
            mLogger.debug("Response uri : " + uri + " processed by " + processor);
            
            Response processedResponse = processor.processResponse(request, proxiedResponse, settings);
            
            if (processedResponse != null) {
              pHttpObject = toHttpObject(processedResponse);
            }
          }
        }
      }
    }
    
    return pHttpObject;
  }
  
  /**
   * To http object.
   *
   * @param pProcessedResponse the processed response
   * @return the http object
   */
  private HttpObject toHttpObject(Response pProcessedResponse) {
    
    HttpObject response = null;
    
    if (pProcessedResponse instanceof ProxiedHttpResponse) {
      response = ((ProxiedHttpResponse)pProcessedResponse).getInternalObject();
    }
    else if (pProcessedResponse instanceof LocalHttpResponse) {
      LocalHttpResponse localResponse = (LocalHttpResponse) pProcessedResponse;
      
      ByteBuf buffer = Unpooled.wrappedBuffer(localResponse.getContent());
      
      DefaultFullHttpResponse newResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(localResponse.getStatusCode()), buffer);
      
      Map<String, String> headers = localResponse.getHeaders();
      
      for (String header : headers.keySet()) {
        newResponse.headers().add(header, headers.get(header));
      }
      
      HttpHeaders.setContentLength(newResponse, localResponse.getContent().length);
      
      response = newResponse;
    }
    
    return response;
  }
  
  /**
   * Checks if is module enabled.
   *
   * @param pModule the module
   * @return true, if is module enabled
   */
  private boolean isModuleEnabled(Module pModule) {
    return true;
  }
}
