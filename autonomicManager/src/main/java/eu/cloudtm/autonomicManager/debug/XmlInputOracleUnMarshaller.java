package eu.cloudtm.autonomicManager.debug;

import eu.cloudtm.autonomicManager.commons.EvaluatedParam;
import eu.cloudtm.autonomicManager.commons.ForecastParam;
import eu.cloudtm.autonomicManager.commons.Param;
import eu.cloudtm.autonomicManager.commons.ReplicationProtocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public class XmlInputOracleUnMarshaller {

   private String filePath;
   private final static Log log = LogFactory.getLog(XmlInputOracleUnMarshaller.class);
   private final boolean trace;

   public XmlInputOracleUnMarshaller(String filePath) {
      this.filePath = filePath;
      trace = log.isTraceEnabled();
   }

   public UnMarshalledInputOracle unMarshal() {
      File sampleF = new File(this.filePath);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = null;

      try {
         dBuilder = dbFactory.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
         e.printStackTrace();
         throw new IllegalArgumentException(e.getMessage());
      }
      Document doc = null;
      try {
         doc = dBuilder.parse(sampleF);
         NodeList params = doc.getElementsByTagName("AggregatedParam");
         trace(params.toString());
         NodeList evals = doc.getElementsByTagName("EvaluatedParam");
         trace(evals.toString());
         NodeList forecasts = doc.getElementsByTagName("ForecastParam");
         trace(evals.toString());
         HashMap<Param, Object> paramM = this.unmarshalParams(params);
         HashMap<EvaluatedParam, Object> evalM = this.unmarshalEvals(evals);
         HashMap<ForecastParam, Object> forecastM = this.unmarshalForecasts(forecasts);
         doc.getDocumentElement().normalize();
         return new UnMarshalledInputOracle(paramM, evalM, forecastM);
      } catch (SAXException e) {
         e.printStackTrace();
         throw new IllegalArgumentException(e.getMessage());
      } catch (IOException e) {
         e.printStackTrace();
         throw new IllegalArgumentException(e.getMessage());
      }

   }


   private HashMap<Param, Object> unmarshalParams(NodeList paramList) {
      HashMap<Param, Object> retMap = new HashMap<Param, Object>();
      for (int temp = 0; temp < paramList.getLength(); temp++) {
         Node nNode = paramList.item(temp);
         if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNode;
            trace("Parsing " + eElement.getNodeName());
            Param param = Param.getByName(eElement.getAttribute("name"));
            Class clazz;
            try {
               clazz = classFromString(eElement.getAttribute("type"));
            } catch (ClassNotFoundException e) {
               throw new IllegalArgumentException(e.getMessage());
            }
            String value = eElement.getAttribute("value");
            retMap.put(param, customUnmarshal(clazz, value));
         }
      }
      return retMap;
   }

   private HashMap<EvaluatedParam, Object> unmarshalEvals(NodeList paramList) {
      HashMap<EvaluatedParam, Object> retMap = new HashMap<EvaluatedParam, Object>();
      for (int temp = 0; temp < paramList.getLength(); temp++) {
         Node nNode = paramList.item(temp);
         if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNode;
            trace("Parsing " + eElement.getNodeName());
            EvaluatedParam param = EvaluatedParam.getByName(eElement.getAttribute("name"));
            Class clazz;
            try {
               clazz = classFromString(eElement.getAttribute("type"));
            } catch (ClassNotFoundException e) {
               throw new IllegalArgumentException(e.getMessage());
            }
            String value = eElement.getAttribute("value");
            retMap.put(param, customUnmarshal(clazz, value));
         }
      }
      return retMap;
   }

   private HashMap<ForecastParam, Object> unmarshalForecasts(NodeList paramList) {
      HashMap<ForecastParam, Object> retMap = new HashMap<ForecastParam, Object>();
      for (int temp = 0; temp < paramList.getLength(); temp++) {
         Node nNode = paramList.item(temp);
         if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNode;
            trace("Parsing " + eElement.getNodeName());
            ForecastParam param = ForecastParam.getByName(eElement.getAttribute("name"));
            Class clazz;
            try {
               clazz = classFromString(eElement.getAttribute("type"));
            } catch (ClassNotFoundException e) {
               throw new IllegalArgumentException(e.getMessage());
            }
            String value = eElement.getAttribute("value");
            retMap.put(param, customUnmarshal(clazz, value));
         }
      }
      return retMap;
   }

   /**
    * @param clazz
    * @param value
    * @return
    */
   private Object customUnmarshal(Class clazz, String value) {
      if (clazz.equals(String.class))
         return value;
      if (clazz.equals(Integer.class))
         return Integer.parseInt(value);
      if (clazz.equals(Long.class))
         return Long.parseLong(value);
      if (clazz.equals(Double.class))
         return Double.parseDouble(value);
      if (clazz.equals(eu.cloudtm.autonomicManager.commons.ReplicationProtocol.class))
         return  ReplicationProtocol.valueOf(value);
      throw new IllegalArgumentException(value + " is not a String or a int,long or double");
   }


   private Class classFromString(String s) throws ClassNotFoundException {
      trace("Type " + s + " split " + Arrays.toString(s.split(" ")));
      return Class.forName(s.split(" ")[1]);
   }

   private void trace(String s) {
      if (trace) System.out.println(s);
      //log.trace(s);
   }
}
