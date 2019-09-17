package com.smartupds.normalizer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Yannis Marketakis 
 */
public class Normalizer {
    private Set<Entry> collection;
    
    public Normalizer(String entryName, String measurementName){
        this.collection=new HashSet<>();
        Entry.entryName=entryName;
        Entry.measurementName=measurementName;
    }
    
    public void importColletion(File file) throws IOException{
        Document document=Jsoup.parse(file, "UTF-8");
        Elements elements=document.getElementsByTag(Entry.entryName);
        for(Element element : elements){
            this.collection.add(new Entry(element.getElementsByTag("image1").text(),
                                          element.getElementsByTag("image2").text(),
                                          element.getElementsByTag(Entry.measurementName).text()));
        }
        System.out.println(this.collection.size());
        System.out.println(this.collection);
        
    }  
    
    public void exportColletion(File file) throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException{
        StringBuilder sb=new StringBuilder();
        sb.append(Resources.XML_HEADER)
          .append("\n\n")
          .append(Resources.DATAROOT_OPEN_ELEMENT)
          .append("\n");
        for(Entry entry : this.collection){
            sb.append(entry.toXML());
            sb.append("\n");
        }
        sb.append(Resources.DATAROOT_CLOSE_ELEMENT);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc=builder.parse(new InputSource(new ByteArrayInputStream(sb.toString().getBytes("UTF-8"))));
        this.exportToXML(doc, file);
    }  
    
    /*Export the contents of the given DOM document into XML format with the given name */
    private void exportToXML(org.w3c.dom.Document doc, File xmlFile){
        try{
            TransformerFactory transformerFactory=TransformerFactory.newInstance();
            Transformer transformer=transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source=new DOMSource(doc);
            StreamResult result=new StreamResult(xmlFile);
            transformer.transform(source, result);
        }catch(TransformerException | TransformerFactoryConfigurationError ex){
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException, ParserConfigurationException, UnsupportedEncodingException, SAXException{
        Normalizer normalizer=new Normalizer("clarifai", "calrifai");
//        Normalizer normalizer=new Normalizer("pastec_similar", "pastec");
        normalizer.importColletion(new File("test.xml"));
        normalizer.exportColletion(new File("test_clean.xml"));
    }
}
