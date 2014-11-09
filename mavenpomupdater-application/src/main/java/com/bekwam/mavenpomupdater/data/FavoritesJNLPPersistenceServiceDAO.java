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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FavoritesJNLPPersistenceServiceDAO implements FavoritesDAO {

	private Log log = LogFactory.getLog( FavoritesJNLPPersistenceServiceDAO.class );
	
	private final static String KEY_FAVORITES_CSV = "favoritesCSV";
	
	PersistenceService ps; 
	BasicService bs; 
	IOUtils ioUtils;
	
	public FavoritesJNLPPersistenceServiceDAO() {
		ioUtils = new IOUtils();
	}
	
	public void init() {
		if( log.isDebugEnabled() ) {
			log.debug("[INIT]");
		}
		try { 
	        ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService"); 
	        bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService"); 
	    } catch (UnavailableServiceException e) { 
	    	log.error( "cannot init persistence and basic services", e );
	    } 
	}

	public void addFavoriteRootDir(String favorite) {
		
		if( log.isDebugEnabled() ) {
			log.debug("[ADD FAV] f=" + favorite);
		}
		
	    if (ps != null && bs != null) { 

	    	if( log.isDebugEnabled() ) {
	    		log.debug("[ADD FAV] persistence and basic service have been initialized");
	    	}
	    	
	        try { 

	        	URL codebase = bs.getCodeBase(); 

		    	if( log.isDebugEnabled() ) {
		    		log.debug("[ADD FAV] codebase=" + codebase);
		    	}

		    	//
		    	// Could return subdirectories as distinct names for sharing
		    	// muffins among apps
		    	//
		    	// Ex, getNames(codebase) -> { codebase, codebase + "/app2" };
		    	//
	        	String [] muffins = ps.getNames(codebase); 

        		if( muffins == null || muffins.length == 0 ) {
        			
        			if( log.isDebugEnabled() ) {
        				log.debug("[ADD FAV] creating new datastore for codebase=" + codebase);
        			}
        			
	        		ps.create(codebase, 100000); 
	        		
	        		//
	        		// Can either be CACHED (default), DIRTY, or TEMPORARY
	        		//
	        		// Since there's no server-side component, using TEMPORARY
	        		//
	        		
	        		//ps.setTag(codebase, PersistenceService.TEMPORARY);
	        	} 
	        	
            	FileContents fc = ps.get(codebase); 

            	Properties properties = ioUtils.getProperties(fc);
            	
            	String favoritesCSV = properties.getProperty(KEY_FAVORITES_CSV);

            	if( StringUtils.isNotEmpty(favoritesCSV) ) {
            		
            		String[] favs = StringUtils.split(favoritesCSV, ",");
            		if( !ArrayUtils.contains( favs, favorite ) ) {
            			if( log.isDebugEnabled() ) {
            				log.debug("[ADD FAV] appending favorite");
            			}
            			favoritesCSV += ",";
            			favoritesCSV += favorite;
            		} else {
            			if( log.isDebugEnabled() ) {
            				log.debug("[ADD FAV] favorite already exists; skipping output writing");
            			}
            			return;
            		}
            		
            	} else {
            		favoritesCSV = favorite;
            	}

        		if( log.isDebugEnabled() ) {
        			log.debug("[ADD FAV] saving csv=" + favoritesCSV);
        		}
        		
        		ioUtils.setProperties(fc, KEY_FAVORITES_CSV, favoritesCSV, "jnlp muffins from mpu app");

	        } catch (Exception exc) { 
	        	log.error( "error writing muffin", exc);
	        } 
	    } 
		
	}

	public List<String> findAllFavoriteRootDirs() {
		
		if( log.isDebugEnabled() ) {
			log.debug("[FIND ALL]");
		}
		
		List<String> favorites = new ArrayList<String>();
		
	    if (ps != null && bs != null) { 

	    	if( log.isDebugEnabled() ) {
	    		log.debug("[FIND ALL] persistence and basic service have been initialized");
	    	}
	    	
	        try { 
	        	
	            URL codebase = bs.getCodeBase(); 
	           
	            String [] muffins = ps.getNames(codebase); 

	            if( log.isDebugEnabled() ) {
	            	log.debug("[FIND ALL] codebase=" + codebase + ", muffins empty?=" + (muffins==null||muffins.length==0));
	            }
	            
	            // get the attributes (tags) for each of these muffins. 
	            // update the server's copy of the data if any muffins 
	            // are dirty 
	            int [] tags = new int[muffins.length]; 
	            URL [] muffinURLs = new URL[muffins.length]; 
	            for (int i = 0; i < muffins.length; i++) { 
	            	
	                muffinURLs[i] = new URL(codebase.toString() + muffins[i]); 
	                
	                tags[i] = ps.getTag(muffinURLs[i]); 
	                if( log.isDebugEnabled() ) {
	                	log.debug("[FIND ALL] m=" + muffinURLs[i] + ", tag=" + tags[i]);
	                }
	                
	                // update the server if anything is tagged DIRTY 
	                if (tags[i] == PersistenceService.DIRTY) { 
	                    doUpdateServer(muffinURLs[i]); 
	                } 
	            } 

	            //
	            // Only expecting 1 muffin URL for now
	            //
	            
	            if( muffinURLs != null && muffinURLs.length > 0 ) {
	            	
	            	if( log.isDebugEnabled() ) {
	            		log.debug("[FIND ALL] there are muffins to consult");
	            	}
	            	
	            	FileContents fc = ps.get(muffinURLs[0]); 
	            	
	            	InputStream is = fc.getInputStream(); 
	            	Properties properties = new Properties();
	            	properties.load( is );
	            	
	            	String favoritesCSV = properties.getProperty(KEY_FAVORITES_CSV);
	            	
	            	if( StringUtils.isNotEmpty(favoritesCSV) ) {
	            		if( log.isDebugEnabled() ) {
	            			log.debug("[FIND ALL] there is a favoritesCSV item=" + favoritesCSV);
	            		}
	            		favorites = Arrays.asList( StringUtils.split(favoritesCSV, ","));
	            	}
	            	
	            	is.close();
	            }

	        } catch (Exception exc ) { 
	        	log.error( "error retrieving muffins", exc );
	        } 
	    } 
	    
		return favorites;
	}
	
	private void doUpdateServer(URL url) throws MalformedURLException, IOException { 
		ps.setTag(url, PersistenceService.CACHED); 
	}
}
