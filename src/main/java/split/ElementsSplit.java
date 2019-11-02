package split;

import com.smartupds.normalizer.exceptions.NormalizerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.extern.log4j.Log4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** This class is responsible for splitting elements from XML input to one or more elements 
 * w.r.t. various criteria
 * 
 * @author Yannis Marketakis (SmartUp Data Solutions)
 */
@Log4j
public class ElementsSplit {
    
    /** The method breaks elements into two or more based on the given list of separators.
     *  The given map contains as keys the names of the elements that needs to be split 
     *  and the corresponding list (for each key) contains the terms that should be considered as separators. 
     *  If the name of the key is '*' then split will be applied to all elements.
     * 
     * @param originalDoc the original XML document
     * @param elementsSeparatorsMap a map containing the elements to split and the corresponding list of separators.
     * @return the updated document with split contents */
    public static Document splitElements(Document originalDoc, Map<String,List<String>> elementsSeparatorsMap) throws NormalizerException{
        for(String elementName : elementsSeparatorsMap.keySet()){
            if(elementName.equals("*")){
                throw new NormalizerException("The method for splitting all elements is not supported yet");
            }
            NodeList elementsList=originalDoc.getElementsByTagName(elementName);
            log.debug("Number of elements with name "+elementName+": "+elementsList.getLength());
            for(int i=0;i<elementsList.getLength();i++){
                Node element=elementsList.item(i);
                for(String separator : elementsSeparatorsMap.get(elementName)){
                    if(element.getTextContent().contains(separator)){
                        List<String> splittedContents=new ArrayList<>();
                        for(String text : element.getTextContent().split(separator)){
                            splittedContents.add(text.trim());
                        }
                        for(Node newElement : createElements(originalDoc, element, splittedContents)){
                            element.getParentNode().appendChild(newElement);
                        }
                        element.getParentNode().removeChild(element);
                    }
                }
            }
        }
        return originalDoc;
    }
    
    private static List<Node> createElements(Document refDoc, Node origElement, List<String> contents){
        List<Node> retList=new ArrayList<>();
        for(String textElem : contents){
            Node newElement=origElement.cloneNode(true);
            newElement.setTextContent(textElem);
            retList.add(newElement);
        }
        return retList;
    }
    
    /** Creates a Document from a file containing XML contents. 
     * 
     * @param xmlFile a file with XML contents
     * @return an XML document (DOM) 
     * @throws NormalizerException for any error that might occur while parsing XML data */
    public static Document parseXmlDocument(File xmlFile) throws NormalizerException{
        try{
            Document retDocument=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            return retDocument;
        }catch(IOException | ParserConfigurationException | SAXException ex){
            log.error("An error occured while parsing XML file",ex);
            throw new NormalizerException("An error occured while parsing XML file",ex);
        }
    }
    
    /** Exports the contents of the XML Document in the given file
     * 
     * @param doc the XML document
     * @param file the file to export the contents of the XML document 
     * @throws NormalizerException for any error that might occur while exporting */ 
    public static void exportXmlDocument(Document doc, File file) throws NormalizerException{
        try{
            TransformerFactory transformerFactory=TransformerFactory.newInstance();
            Transformer transformer=transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source=new DOMSource(doc);
            StreamResult result=new StreamResult(file);
            transformer.transform(source, result);
        }catch(TransformerException | TransformerFactoryConfigurationError ex){
            log.error("An error occured while exporting data to XML",ex);
            throw new NormalizerException("An error occured while exporting data to XML",ex);
        }
    }
    
    public static void main(String[] args) throws NormalizerException{
        Map<String,List<String>> map=new HashMap<>();
        map.put("a5260", Arrays.asList("AND","&"));
        map.put("a5500", Arrays.asList("AND","&"));
        map.put("a8498", Arrays.asList("AND","&"));
        exportXmlDocument(
                splitElements(
                        parseXmlDocument(new File("example.xml"))
                        ,map), 
                new File("example_split.xml")
        );
        
    }
}
