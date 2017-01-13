package net.stevemul.proxy.app;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.mitm.RootCertificateException;

import net.stevemul.proxy.Constants;
import net.stevemul.proxy.Environment;
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
      
      Map<String, String> parsedArgs = ApplicationArguments.parseArgs(args);
      
      LocalProxy proxy = buildProxy(parsedArgs);
      
      mLogger.info(AppUtils.getString(MSG_SOCKET_IO_STARTING));
      
      mMessenger = buildMessenger(parsedArgs);
      
      HttpProxyServer proxyServer = proxy.start();
      
      mMessenger.start();
      
      ServiceRegistry.registerMessenger(mMessenger);
      ServiceRegistry.registerProxyServer(proxyServer);
      
      Runtime.getRuntime().addShutdownHook(new Thread() {

        @Override
        public void run() {
          mLogger.info("Stopping SocketIO Server");
          
          mMessenger.stop();
          
          mLogger.info("Proxy Exiting");
        }

      });
      
      mLogger.info(AppUtils.getString(MSG_APP_STARTED));
      
      Environment.setEnvironmentArguments(parsedArgs);
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
  private static LocalProxy buildProxy(Map<String, String> pParsedArgs) throws RootCertificateException {
    
    LocalProxyBuilder builder = LocalProxyBuilder.newInstance();
    
    if (pParsedArgs.containsKey(ApplicationArguments.MITM)) {
      builder.setMitmEnabled(true);
    }
    
    if (pParsedArgs.containsKey(ApplicationArguments.BLIND_TRUST)) {
      builder.setBlindTrust(true);
    }
    
    if (pParsedArgs.containsKey(ApplicationArguments.LISTEN_PORT)) {
      builder.setListenPort(Integer.valueOf(pParsedArgs.get(ApplicationArguments.LISTEN_PORT)));
    }
    
    if (pParsedArgs.containsKey(ApplicationArguments.PROXY_HOST)) {
      builder.setDownstreamProxyHost(pParsedArgs.get(ApplicationArguments.PROXY_HOST));
    }
    
    if (pParsedArgs.containsKey(ApplicationArguments.PROXY_PORT)) {
      builder.setDownstreamProxyPort(Integer.valueOf(pParsedArgs.get(ApplicationArguments.PROXY_PORT)));
    }
    
    if (pParsedArgs.containsKey(ApplicationArguments.ALLOW_EXTERNAL_CONNECTIONS)) {
      builder.setAllowExternalConnections(true);
    }
    
    return builder.build();
  }
  
  /**
   * Builds the messenger.
   *
   * @param pParsedArgs the parsed args
   * @return the socket IO
   */
  private static SocketIO buildMessenger(Map<String, String> pParsedArgs) {
    
    int listenPort = Constants.DEFAULT_SOCKET_IO_PORT;
    
    if (pParsedArgs.containsKey(ApplicationArguments.SOCKET_IO_PORT)) {
      listenPort = Integer.valueOf(pParsedArgs.get(ApplicationArguments.SOCKET_IO_PORT));
    }
    
    return new SocketIO(listenPort);
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
