package net.stevemul.proxy.generators;

/**
 * The Interface ContentTypeOutputGenerator.
 */
public interface ContentTypeOutputGenerator {

  /**
   * Output.
   *
   * @param pUri the uri
   * @param pContentType the content type
   * @param pContent the content
   * @param pWriter the writer
   */
  public void output (String pUri, String pContentType, byte[] pContent);
}
