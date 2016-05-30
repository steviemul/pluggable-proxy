package net.stevemul.proxy.modules;

import java.util.Arrays;
import java.util.List;

import net.stevemul.proxy.modules.api.AbstractModule;
import net.stevemul.proxy.processors.DataRequestProcessor;
import net.stevemul.proxy.processors.FileSystemRequestProcessor;
import net.stevemul.proxy.processors.LocalAppRequestProcessor;
import net.stevemul.proxy.processors.RequestProcessor;

/**
 * The Class LocalApplicationModule.
 */
public class LocalApplicationModule extends AbstractModule {
  
  /** The Constant NAMESPACE. */
  public static final String NAMESPACE = "stevemul.net.proxy.application";
  
  /** The Constant NAME. */
  public static final String NAME = "Application";
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getNamespace()
   */
  @Override
  public String getNamespace() {
    return NAMESPACE;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getName()
   */
  @Override
  public String getName() {
    return NAME;
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.AbstractModule#getRequestProcessors()
   */
  @Override
  public List<? extends RequestProcessor> getRequestProcessors() {
    return Arrays.asList(
        new DataRequestProcessor(),
        new LocalAppRequestProcessor(),
        new FileSystemRequestProcessor());
  }

  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Module#getLoadingPriority()
   */
  @Override
  public int getLoadingPriority() {
    return 0;
  }

}
