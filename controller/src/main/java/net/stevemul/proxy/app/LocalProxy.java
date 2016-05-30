package net.stevemul.proxy.app;

import io.netty.handler.codec.http.HttpRequest;

import java.net.InetSocketAddress;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.littleshoot.proxy.ChainedProxy;
import org.littleshoot.proxy.ChainedProxyAdapter;
import org.littleshoot.proxy.ChainedProxyManager;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.littleshoot.proxy.mitm.CertificateSniffingMitmManager;
import org.littleshoot.proxy.mitm.RootCertificateException;

/**
 * The Class LocalProxy.
 * 
 * @author smulrenn
 */
public class LocalProxy {

  private static Log mLogger;
  
  static {
    System.setProperty("log4j.configuration", "log4j.xml");
    mLogger = LogFactory.getLog(LocalProxy.class);
  }

  private HttpProxyServerBootstrap mServer;
  
  int mListenPort = 9090;
  boolean mMitmEnabled = false;
  String mDownstreamProxyHost = null;
  int mDownstreamProxyPort = 80;
  
  /**
   * Chained proxy manager.
   *
   * @return the chained proxy manager
   */
  private ChainedProxyManager chainedProxyManager() {
    
    return new ChainedProxyManager() {

      @Override
      public void lookupChainedProxies(HttpRequest pRequest, Queue<ChainedProxy> pChainedProxies) {
        if (pRequest.getUri().contains("localhost")) {
          pChainedProxies.add(ChainedProxyAdapter.FALLBACK_TO_DIRECT_CONNECTION);
        }
        else {
          pChainedProxies.add(new BaseChainedProxy());
        }
      }
    };
  }
  
  /**
   * The Class BaseChainedProxy.
   */
  private class BaseChainedProxy extends ChainedProxyAdapter {
    @Override
    public InetSocketAddress getChainedProxyAddress() {
      return new InetSocketAddress(mDownstreamProxyHost, mDownstreamProxyPort);
    }
  }
  
  /**
   * Start.
   * @throws RootCertificateException 
   */
  public void start() throws RootCertificateException {
    
    mLogger.info("Starting proxy on port " + mListenPort);
    
    mServer = DefaultHttpProxyServer.bootstrap().withPort(mListenPort).withFiltersSource(new DefaultProcessor());
    
    if (mDownstreamProxyHost != null) {
      mLogger.info("Chaining proxy to downstream proxy at " + mDownstreamProxyHost + ":" + mDownstreamProxyPort);
      mServer = mServer.withChainProxyManager(chainedProxyManager());
    }
    
    if (mMitmEnabled) {
      mLogger.info("Enabling MITM");
      mServer = mServer.withManInTheMiddle(new CertificateSniffingMitmManager());
    }
    
    mLogger.info("Proxy initialization successful");
    
    mServer.start();
  }
  
}
