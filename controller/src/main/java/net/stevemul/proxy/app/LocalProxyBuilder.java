package net.stevemul.proxy.app;

import org.littleshoot.proxy.mitm.RootCertificateException;

/**
 * The Class LocalProxyBuilder.
 */
public class LocalProxyBuilder {
  
  private LocalProxy proxy;
  
  private LocalProxyBuilder() throws RootCertificateException{
    proxy = new LocalProxy();
  }
  
  /**
   * New instance.
   *
   * @return the local proxy builder
   * @throws RootCertificateException the root certificate exception
   */
  public static LocalProxyBuilder newInstance() throws RootCertificateException{
    return new LocalProxyBuilder();
  }
  
  /**
   * Sets the listen port.
   *
   * @param pListenPort the listen port
   * @return the local proxy builder
   */
  public LocalProxyBuilder setListenPort(int pListenPort) {
    proxy.mListenPort = pListenPort;
    
    return this;
  }
  
  /**
   * Sets the mitm enabled.
   *
   * @param pMitmEnabled the mitm enabled
   * @return the local proxy builder
   */
  public LocalProxyBuilder setMitmEnabled(boolean pMitmEnabled) {
    proxy.mMitmEnabled = pMitmEnabled;
    
    return this;
  }
  
  /**
   * Sets the downstream proxy host.
   *
   * @param pHost the host
   * @return the local proxy builder
   */
  public LocalProxyBuilder setDownstreamProxyHost(String pHost) {
    proxy.mDownstreamProxyHost = pHost;
    
    return this;
  }
  
  /**
   * Sets the downstream proxy port.
   *
   * @param pPort the port
   * @return the local proxy builder
   */
  public LocalProxyBuilder setDownstreamProxyPort(int pPort) {
    proxy.mDownstreamProxyPort = pPort;
    
    return this;
  }
  
  /**
   * Sets the blind trust.
   *
   * @param pBlindTrust the blind trust
   * @return the local proxy builder
   */
  public LocalProxyBuilder setBlindTrust(boolean pBlindTrust) {
    proxy.mTrustAllCertificates = pBlindTrust;
    
    return this;
  }
  
  /**
   * Builds the.
   *
   * @return the local proxy
   */
  public LocalProxy build() {
    return proxy;
  }
}
