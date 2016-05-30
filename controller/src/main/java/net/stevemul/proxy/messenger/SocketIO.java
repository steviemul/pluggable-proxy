package net.stevemul.proxy.messenger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

import net.stevemul.proxy.services.Messenger;
import net.stevemul.proxy.utils.AppUtils;

/**
 * The Class SocketIO.
 */
public class SocketIO implements Messenger {

  private SocketIOServer mServer;
  private static Log mLogger = LogFactory.getLog(SocketIO.class);
  private static final String WARN_SOCKETIO_SERVER_START = "warn.socketIo.start";
  
  /**
   * Instantiates a new socket io.
   *
   * @param pPort the port
   */
  public SocketIO(int pPort) {
    
    Configuration config = new Configuration();
    config.setHostname("localhost");
    config.setPort(pPort);

    mServer = new SocketIOServer(config);
  }
  
  /**
   * Send message.
   *
   * @param pMessage the message
   */
  public void sendMessage(String pEventType, String pMessage) {
    
    mServer.getBroadcastOperations().sendEvent(pEventType, pMessage);
  }
  
  /**
   * Start.
   */
  public void start() {
    try {
      mServer.start();
    }
    catch (Exception e) {
      mLogger.warn(AppUtils.getString(WARN_SOCKETIO_SERVER_START), e);
    }
  }

  /**
   * Stop.
   */
  public void stop() {
    mServer.stop();
  }
  
  
}
