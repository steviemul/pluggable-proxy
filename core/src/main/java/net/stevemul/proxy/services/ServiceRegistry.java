package net.stevemul.proxy.services;

/**
 * The Class ServiceRegistry.
 */
public class ServiceRegistry {

  /** The m module service. */
  private static ModuleService mModuleService;
  
  private static Messenger mMessenger;
  
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
}
