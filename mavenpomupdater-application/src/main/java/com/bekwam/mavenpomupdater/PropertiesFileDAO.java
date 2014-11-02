/*
 * Copyright 2014 Bekwam, Inc 
 * 
 * Licensed under the Apache License, Version 2.0 (the “License”); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at: 
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an “AS IS” BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 */
package com.bekwam.mavenpomupdater;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Retrieves properties from file
 * 
 * Ex,
 *   version=1.1.0-SNAPSHOT
 *   supportURL=http://www.bekwam.com/mpu
 *   licenseURL=http://www.apache.org/licenses/LICENSE-2.0.txt
 * 
 * @author carlwalker
 * @since 1.1.0
 */
public class PropertiesFileDAO {

	private Log log = LogFactory.getLog(PropertiesFileDAO.class);
	
	private final static String FILE_NAME = "app.properties";
	
	public Properties getProperties() {
		
		InputStream is = this.getClass().getResourceAsStream(FILE_NAME);
		Properties p = new Properties();
		try {
			p.load( is );
		} catch(Exception exc) {
			
			if( log.isWarnEnabled() ) {
				log.warn("cannot retrieve properties from " + FILE_NAME, exc);
			}
			
		} finally {
			if( is != null ) {
				try {
					is.close();
				} catch(IOException exc) {
					if( log.isWarnEnabled() ) {
						log.warn("error closing input stream", exc);
					}
				}
			}
		}
		return p;
	}
}
