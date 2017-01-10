package net.stevemul.proxy.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The Class XMLProcessor.
 */
public class XMLProcessor {

  /** The document. */
  private Document document;

  /** The x path. */
  private XPath xPath;

  /**
   * Instantiates a new xML processor.
   * 
   * @param sourceXml
   *          the source xml
   * @throws Exception
   *           the exception
   */
  public XMLProcessor(InputStream sourceXml) throws Exception {

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    
    DocumentBuilder builder = factory.newDocumentBuilder();
    
    builder.setEntityResolver(new EntityResolver() {

      @Override
      public InputSource resolveEntity(String publicId, String systemId)
              throws SAXException, IOException {
          return new InputSource(new StringReader(""));
      }
    });
    
    document = builder.parse(sourceXml);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    xPath = xPathfactory.newXPath();
  }

  /**
   * Gets the nodes.
   * 
   * @param query
   *          the query
   * @return the nodes
   * @throws Exception
   *           the exception
   */
  public NodeList getNodes(String query) throws Exception {
    XPathExpression expr = xPath.compile(query);
    NodeList nl = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

    return nl;
  }
  
  /**
   * Gets the nodes.
   *
   * @param parent the parent
   * @param query the query
   * @return the nodes
   * @throws Exception the exception
   */
  public NodeList getNodes(Node parent, String query) throws Exception {
    XPathExpression expr = xPath.compile(query);
    NodeList nl = (NodeList) expr.evaluate(parent, XPathConstants.NODESET);

    return nl;
  }
  
  /**
   * Gets the node.
   *
   * @param query the query
   * @return the node
   * @throws Exception the exception
   */
  public Node getNode(String query) throws Exception {
    XPathExpression expr = xPath.compile(query);
    
    Node node = (Node)expr.evaluate(document, XPathConstants.NODE);
    
    return node;
  }
  
  /**
   * Gets the node.
   *
   * @param parent the parent
   * @param query the query
   * @return the node
   * @throws Exception the exception
   */
  public Node getNode(Node parent, String query) throws Exception {
    XPathExpression expr = xPath.compile(query);
    
    Node node = (Node)expr.evaluate(parent, XPathConstants.NODE);
    
    return node;
  }
  
  
  /**
   * Gets the node value.
   *
   * @param node the node
   * @param query the query
   * @return the node value
   */
  public String getNodeValue(Node node, String query)
  {
      String value = "";
      
      try
      {
        Node childNode = getNode(node, query);
        
        value = childNode.getTextContent();
      }
      catch (Exception e)
      {
          
      }
      
      return value;    
  }

  /**
   * Sets the node value.
   *
   * @param node the node
   * @param value the value
   */
  public void setNodeValue(Node node, String value) {
    
    node.setTextContent(value);
  }
  
  /**
   * Removes the node.
   *
   * @param query the query
   * @throws Exception 
   */
  public void removeNode(String query) throws Exception {
    
    Node childNode = getNode(query);
    
    removeNode(childNode);
  }
  
  /**
   * Removes the nodes.
   *
   * @param query the query
   * @throws Exception the exception
   */
  public void removeNodes(String query) throws Exception {
    
    NodeList nodes = getNodes(query);
    
    if (nodes != null) {
      for (int i=0;i<nodes.getLength();i++) {
        Node target = nodes.item(i);
        removeNode(target);
      }
    }
  }
  
  /**
   * Removes the node.
   *
   * @param node the node
   */
  public void removeNode(Node node) {
    
    node.getParentNode().removeChild(node);
  }
  
  /**
   * Gets the attribute value.
   * 
   * @param node
   *          the node
   * @param name
   *          the name
   * @return the attribute value
   */
  public String getAttributeValue(Node node, String name) {
    String value = null;

    try {
      value = node.getAttributes().getNamedItem(name).getTextContent();
    } 
    catch (Exception e) {

    }

    return value;
  }
  
  /**
   * Sets the attribute value.
   *
   * @param node the node
   * @param name the name
   * @param value the value
   */
  public void setAttributeValue(Node node, String name, String value) {
    try {
      node.getAttributes().getNamedItem(name).setTextContent(value);
    }
    catch(Exception e) {
      
    }
  }
  
  /**
   * Gets the document.
   *
   * @return the document
   */
  public Document getDocument() {
    return this.document;
  }
  
  /**
   * Output doc.
   *
   * @param location the location
   * @throws Exception the exception
   */
  public void outputDoc(String location) throws Exception {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    transformerFactory.setAttribute("indent-number", new Integer(2));
    Transformer transformer = transformerFactory.newTransformer();
    
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    
    final DocumentType doctype = document.getDoctype();
    if(doctype != null) {
      String systemId = doctype.getSystemId();
      String publicId = doctype.getPublicId();
      
      if(systemId != null) {
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemId);
      }
      
      if(publicId != null) {
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicId);
      }
    }
    
    File target = new File(location);
    
    if(target.exists()) {
      target.delete();
    }
    
    DOMSource source = new DOMSource(document);
    
    StreamResult result = new StreamResult(new File(location));
    
    transformer.transform(source, result);
  }
}