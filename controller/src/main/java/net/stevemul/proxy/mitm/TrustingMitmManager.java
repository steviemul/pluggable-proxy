package net.stevemul.proxy.mitm;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.littleshoot.proxy.mitm.CertificateSniffingMitmManager;
import org.littleshoot.proxy.mitm.RootCertificateException;

/**
 * The Class TrustingMitmManager.
 */
public class TrustingMitmManager extends CertificateSniffingMitmManager {
  
  /** The m logger. */
  private Log mLogger = LogFactory.getLog(TrustingMitmManager.class);
  
  private boolean mTrustAllCertificates = false;
  
  /**
   * Instantiates a new trusting mitm manager.
   *
   * @throws RootCertificateException the root certificate exception
   */
  public TrustingMitmManager(boolean pTrustAllCertificates) throws RootCertificateException {
    super();
    
    mTrustAllCertificates = pTrustAllCertificates;
  }

  /* (non-Javadoc)
   * @see org.littleshoot.proxy.mitm.CertificateSniffingMitmManager#serverSslEngine(java.lang.String, int)
   */
  @Override
  public SSLEngine serverSslEngine(String peerHost, int peerPort) {
    
    try {
      if (mTrustAllCertificates) {
        return getBlindTrustingSSLEngine();
      }
    } 
    catch (NoSuchAlgorithmException | KeyManagementException e) {
      mLogger.error("Unable to create Server SSL Context, falling back to default.", e);
    } 
    
    return super.serverSslEngine(peerHost, peerPort);
  }
  
  /**
   * Gets the blind trusting SSL engine.
   *
   * @return the blind trusting SSL engine
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws KeyManagementException the key management exception
   */
  private SSLEngine getBlindTrustingSSLEngine() throws NoSuchAlgorithmException, KeyManagementException {
    
    SSLContext sslContext = SSLContext.getInstance("TLS");
    
    TrustManager tm = new X509TrustManager() {

      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        
      }

      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        
      }

      @Override
      public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
      }
    };
    
    TrustManager[] tms = new TrustManager[] { tm };

    sslContext.init(null, tms, null);

    return sslContext.createSSLEngine();
  }
}
