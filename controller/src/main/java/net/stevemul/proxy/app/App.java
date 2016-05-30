package net.stevemul.proxy.app;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.littleshoot.proxy.mitm.RootCertificateException;

import net.stevemul.proxy.messenger.SocketIO;
import net.stevemul.proxy.services.ServiceRegistry;
import net.stevemul.proxy.utils.AppUtils;
import net.stevemul.proxy.utils.ApplicationArguments;

/**
 * The Class App.
 */
public class App {
  
  public static final String MSG_PROXY_STARTING = "msg.proxy.starting";
  public static final String MSG_SOCKET_IO_STARTING = "msg.socketio.starting";
  public static final String MSG_APP_STARTED = "msg.app.started";
  public static final String ERROR_INIT_APP = "error.app.init";
  
  private static Log mLogger = LogFactory.getLog(App.class);
  private static SocketIO mMessenger;
  
  /**
   * Main.
   */
  public static void main(String args[]){
    
    try {
      mLogger.info(AppUtils.getString(MSG_PROXY_STARTING));
      
      LocalProxy proxy = buildProxy(args);
      
      mLogger.info(AppUtils.getString(MSG_SOCKET_IO_STARTING));
      
      mMessenger = new SocketIO(9091);
      
      proxy.start();
      mMessenger.start();
      
      ServiceRegistry.registerMessenger(mMessenger);
      
      Runtime.getRuntime().addShutdownHook(new Thread() {

        @Override
        public void run() {
          mLogger.info("Stopping SocketIO Server");
          
          mMessenger.stop();
          
          mLogger.info("Proxy Exiting");
        }

      });
      
      mLogger.info(AppUtils.getString(MSG_APP_STARTED));
    }
    catch (Exception e) {
      mLogger.fatal(AppUtils.getString(ERROR_INIT_APP), e);
      System.exit(1);
    }
  }
  
  /**
   * Builds the proxy.
   *
   * @param args the args
   * @return the local proxy
   * @throws RootCertificateException the root certificate exception
   */
  private static LocalProxy buildProxy(String args[]) throws RootCertificateException {
    
    LocalProxyBuilder builder = LocalProxyBuilder.newInstance();
    
    Map<String, String> parsedArgs = ApplicationArguments.parseArgs(args);
    
    if (parsedArgs.containsKey(ApplicationArguments.MITM)) {
      builder.setMitmEnabled(true);
    }
    
    if (parsedArgs.containsKey(ApplicationArguments.LISTEN_PORT)) {
      builder.setListenPort(Integer.valueOf(parsedArgs.get(ApplicationArguments.LISTEN_PORT)));
    }
    
    if (parsedArgs.containsKey(ApplicationArguments.PROXY_HOST)) {
      builder.setDownstreamProxyHost(parsedArgs.get(ApplicationArguments.PROXY_HOST));
    }
    
    if (parsedArgs.containsKey(ApplicationArguments.PROXY_PORT)) {
      builder.setDownstreamProxyPort(Integer.valueOf(parsedArgs.get(ApplicationArguments.PROXY_PORT)));
    }
    
    return builder.build();
  }
  
  /**
   * Send message.
   *
   * @param pEventType the event type
   * @param pMessage the message
   */
  public static void sendMessage(String pEventType, String pMessage) {
    mMessenger.sendMessage(pEventType, pMessage);
  }
}
