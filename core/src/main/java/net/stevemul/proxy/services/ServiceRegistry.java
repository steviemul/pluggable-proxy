package net.stevemul.proxy.services;

import org.littleshoot.proxy.HttpProxyServer;

/**
 * The Class ServiceRegistry.
 */
public class ServiceRegistry {

  /** The m module service. */
  private static ModuleService mModuleService;
  
  private static Messenger mMessenger;
  
  private static HttpProxyServer mProxyServer;
  
  private static EventService mEventService;
  
  /**
   * Gets the module service.
   *
   * @return the module service
   */
  public static final ModuleService getModuleService() {
    return mModuleService;
  }
  
  /**
   * Register module service.
   *
   * @param pModuleService the module service
   */
  public static final void registerModuleService(ModuleService pModuleService) {
    mModuleService = pModuleService;
  }
  
  /**
   * Gets the messenger.
   *
   * @return the messenger
   */
  public static final Messenger getMessenger() {
    return mMessenger;
  }
  
  /**
   * Register messenger.
   *
   * @param pMessenger the messenger
   */
  public static void registerMessenger(Messenger pMessenger) {
    mMessenger = pMessenger;
  }
  
  /**
   * Gets the proxy server.
   *
   * @return the proxy server
   */
  public static HttpProxyServer getProxyServer() {
    return mProxyServer;
  }
  
  /**
   * Register proxy server.
   *
   * @param pProxyServer the proxy server
   */
  public static void registerProxyServer(HttpProxyServer pProxyServer) {
    mProxyServer = pProxyServer;
  }
  
  /**
   * Gets the event service.
   *
   * @return the event service
   */
  public static EventService getEventService() {
    return mEventService;
  }
  
  /**
   * Register event service.
   *
   * @param pEventService the event service
   */
  public static void registerEventService(EventService pEventService) {
    mEventService = pEventService;
  }
  
}
