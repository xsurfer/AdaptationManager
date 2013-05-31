/*
 * $Id: HelloWorld.java 739661 2009-02-01 00:06:00Z davenewton $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package eu.cloudtm.webif;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import eu.cloudtm.model.*;

/**
 * <code>Set welcome message.</code>
 */
public class MyCloud extends ExampleSupport {

	private static Log log = LogFactory.getLog(MyCloud.class);

	public String execute() throws Exception {

		Client client = Client.create();

		WebResource webResource = client
				.resource("http://localhost:9998/scale");

		ClientResponse response = webResource.accept("application/json").get(
				ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		String output = response.getEntity(String.class);

		log.info("Output from Server .... \n");
		/*
		 * {"small":0,"medium":0,"large":0,"tuning":{"type":"AUTO","method":
		 * "ANALYTICAL"}}
		 */
		log.info(output);

		//JSONObject jsonObject = JSONObject.fromObject(output);
		//ScaleInfo bean = (ScaleInfo) JSONObject.toBean(jsonObject, ScaleInfo.class);
		
		Gson gson = new Gson();
		ScaleInfo scaleInfoBean = gson.fromJson(output, ScaleInfo.class);   
		setScale(scaleInfoBean);
		

		log.info("MyCloud execute()");

		setMessage(getText(MESSAGE));
		return SUCCESS;
	}

	
	public ScaleInfo scale;
	public ScaleInfo getScale(){ return scale; }
	public void setScale(ScaleInfo value){ scale = value; }
	
	
	/**
	 * Provide default value for Message property.
	 */
	public static final String MESSAGE = "MyCloud.message";

	/**
	 * Field for Message property.
	 */
	private String message;

	/**
	 * Return Message property.
	 * 
	 * @return Message property
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set Message property.
	 * 
	 * @param message
	 *            Text to display on HelloWorld page.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
