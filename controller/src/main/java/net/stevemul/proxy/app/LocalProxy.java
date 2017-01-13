package net.stevemul.proxy.app;

import java.net.InetSocketAddress;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.littleshoot.proxy.ChainedProxy;
import org.littleshoot.proxy.ChainedProxyAdapter;
import org.littleshoot.proxy.ChainedProxyManager;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.littleshoot.proxy.mitm.RootCertificateException;

import io.netty.handler.codec.http.HttpRequest;
import net.stevemul.proxy.mitm.TrustingMitmManager;

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
  boolean mTrustAllCertificates = false;
  boolean mLocalOnlyConnections = true;
  
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

    /* (non-Javadoc)
     * @see org.littleshoot.proxy.ChainedProxyAdapter#getChainedProxyAddress()
     */
    @Override
    public InetSocketAddress getChainedProxyAddress() {
      return new InetSocketAddress(mDownstreamProxyHost, mDownstreamProxyPort);
    }

  }
  
  /**
   * Start.
   * @throws RootCertificateException 
   */
  public HttpProxyServer start() throws RootCertificateException {
    
    mLogger.info("Starting proxy on port " + mListenPort);
   
    mServer = DefaultHttpProxyServer.bootstrap().withAllowLocalOnly(mLocalOnlyConnections).withPort(mListenPort).withFiltersSource(new DefaultProcessor());
    
    if (mDownstreamProxyHost != null) {
      mLogger.info("Chaining proxy to downstream proxy at " + mDownstreamProxyHost + ":" + mDownstreamProxyPort);
      mServer = mServer.withChainProxyManager(chainedProxyManager());
    }
    
    if (mLocalOnlyConnections) {
      mLogger.info("Allowing local client connections only.");
    }
    else {
      mLogger.info("Allow external client connections.");
    }
    
    if (mMitmEnabled) {
      mLogger.info("Enabling MITM");
      mServer = mServer.withManInTheMiddle(new TrustingMitmManager(mTrustAllCertificates));
      
      if (mTrustAllCertificates) {
        mLogger.info("Trusting all backend certificates");
      }
      else {
        mLogger.info("Trusting valid backend certificates only");
      }
    }
   
    mLogger.info("Proxy initialization successful");
    
    return mServer.start();
  }
  
}
