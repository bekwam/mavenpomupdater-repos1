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
package com.bekwam.mavenpomupdater.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.jnlp.FileContents;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IOUtils {

	private Log log = LogFactory.getLog(IOUtils.class);
	
	public Properties getProperties(FileContents fc) throws IOException {
    	InputStream is = null;
		Properties properties = new Properties();
    	try {
    		is = fc.getInputStream(); 
    		properties.load( is );
    	} finally {
    		try {
    			if( is != null ) {
    				is.close();
    			}
    		} catch(IOException exc) {
    			log.warn("can't close input stream", exc);
    		}
    	}
    	return properties;
	}
	
	public void setProperty(FileContents fc, String key, String value, String comment)
			throws IOException {
		
        OutputStream os = null;
        Properties properties = new Properties();
        try {
        	os = fc.getOutputStream(true);  // true -> overwrite 
        	properties.put(key, value);
        	properties.store( os, comment );
        } finally {
    		try {
    			if( os != null ) {
    				os.close();
    			}
    		} catch(IOException exc) {
    			log.warn("can't close output stream", exc);
    		}
    	}
	}

    public void setProperties(FileContents fc, Properties properties, String comment)
            throws IOException {

        OutputStream os = null;
        try {
            os = fc.getOutputStream(true);  // true -> overwrite
            properties.store( os, comment );
        } finally {
            try {
                if( os != null ) {
                    os.close();
                }
            } catch(IOException exc) {
                log.warn("can't close output stream", exc);
            }
        }
    }
}
