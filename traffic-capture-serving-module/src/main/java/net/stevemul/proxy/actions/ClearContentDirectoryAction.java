package net.stevemul.proxy.actions;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.stevemul.proxy.TrafficCaptureConstants;
import net.stevemul.proxy.data.ModuleSettings;
import net.stevemul.proxy.http.ProxiedHttpRequest;
import net.stevemul.proxy.modules.api.Action;

/**
 * The Class ClearContentDirectoryAction.
 */
public class ClearContentDirectoryAction implements Action {

  /** The m logger. */
  private static Log mLogger = LogFactory.getLog(ClearContentDirectoryAction.class);
  
  /* (non-Javadoc)
   * @see net.stevemul.proxy.modules.api.Action#execute(net.stevemul.proxy.http.ProxiedHttpRequest, net.stevemul.proxy.data.ModuleSettings)
   */
  @Override
  public void execute(ProxiedHttpRequest pRequest, ModuleSettings pSettings) {
    
    String contentDirectory = pSettings.getStringValue(TrafficCaptureConstants.CONTENT_DIRECTORY);
    
    if (contentDirectory != null) {
      File directory = new File(contentDirectory);
      
      if (directory.exists() && directory.isDirectory()) {
        for (File child : directory.listFiles()) {
          delete(child);
        }
      }
    }
    
    mLogger.info("Successfully cleared directory : " + contentDirectory);
  }

  /**
   * Delete.
   *
   * @param pFile the file
   */
  private void delete(File pFile) {
    
    if (pFile.isDirectory()) {
      for (File child : pFile.listFiles()) {
        delete(child);
      }
    }
    
    pFile.delete();
  }
}
