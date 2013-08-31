package eu.cloudtm.autonomicManager;/*
 * INESC-ID, Instituto de Engenharia de Sistemas e Computadores Investigação e Desevolvimento em Lisboa
 * Copyright 2013 INESC-ID and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import eu.cloudtm.autonomicManager.commons.EvaluatedParam;
import eu.cloudtm.autonomicManager.commons.ForecastParam;
import eu.cloudtm.autonomicManager.commons.Param;
import eu.cloudtm.autonomicManager.oracles.InputOracleWPM;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Map;


/**
 * @author Diego Didona, didona@gsd.inesc-id.pt
 *         Date: 31/08/13
 */
public class WPMInputOracleDumper {

   private InputOracleWPM input;

   public WPMInputOracleDumper(InputOracleWPM input) {
      this.input = input;
   }

   public void dump(String toFile) throws ParserConfigurationException {

      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

      // root elements
      Document doc = docBuilder.newDocument();
      Element rootElement = doc.createElement("FileInputOracle");
      doc.appendChild(rootElement);

      Element forecast = doc.createElement("ForecastParams");
      Map<ForecastParam, Object> forecastM = this.input.getForecastParamMap();
      for (Map.Entry<ForecastParam, Object> entry : forecastM.entrySet()) {
         Element fp = doc.createElement("ForecastParam");
         rootElement.appendChild(fp);
         Attr name = doc.createAttribute("name");
         name.setValue(entry.getKey().getKey());
         Attr type = doc.createAttribute("type");
         type.setValue(entry.getKey().getClazz().toString());
         Attr value = doc.createAttribute("value");
         value.setValue(entry.getValue().toString());
         forecast.appendChild(fp);
      }
      rootElement.appendChild(forecast);

      Map<EvaluatedParam, Object> evaluatedM = input.getEvaluatedParamMap();
      Element evaluated = doc.createElement("EvaluatedParams");
      for (Map.Entry<EvaluatedParam, Object> entry : evaluatedM.entrySet()) {
         Element fp = doc.createElement("EvaluatedParam");
         rootElement.appendChild(fp);
         Attr name = doc.createAttribute("name");
         name.setValue(entry.getKey().getKey());
         Attr type = doc.createAttribute("type");
         type.setValue(Double.class.toString());
         Attr value = doc.createAttribute("value");
         value.setValue(entry.getValue().toString());
         forecast.appendChild(fp);
      }
      rootElement.appendChild(evaluated);

      Map<String, Object> paramM = input.getParamMap();
      Element aggregated = doc.createElement("AggregatedParams");
      for (Map.Entry<String, Object> entry : paramM.entrySet()) {
         Element fp = doc.createElement("AggregatedParam");
         rootElement.appendChild(fp);
         Attr name = doc.createAttribute("name");
         String nameK = entry.getKey();
         name.setValue(nameK);
         Attr type = doc.createAttribute("type");
         Class clazz = Param.getByName(nameK).getClazz();
         type.setValue(clazz.toString());
         Attr value = doc.createAttribute("value");
         value.setValue(entry.getValue().toString());
         forecast.appendChild(fp);
      }
      rootElement.appendChild(aggregated);


      doc.

   }

}
