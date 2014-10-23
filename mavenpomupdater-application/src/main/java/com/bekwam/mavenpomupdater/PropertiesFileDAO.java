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
