package net.stevemul.proxy.services;

/**
 * The Interface Messenger.
 */
public interface Messenger {

  /**
   * Send message.
   *
   * @param pEventType the event type
   * @param pMessage the message
   */
  public void sendMessage(String pEventType, String pMessage);
}
