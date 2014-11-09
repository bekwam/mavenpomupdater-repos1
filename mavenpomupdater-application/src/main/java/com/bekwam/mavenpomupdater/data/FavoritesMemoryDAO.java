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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FavoritesMemoryDAO implements FavoritesDAO {

	private Log log = LogFactory.getLog(FavoritesMemoryDAO.class);

	private List<String> favorites = new ArrayList<String>();
	
	@Override
	public void init() {
		if( log.isDebugEnabled() ) {
			log.debug("[INIT]");
		}
	}

	@Override
	public void addFavoriteRootDir(String favorite) {
		if( log.isDebugEnabled() ) {
			log.debug("[ADD] favorite=" + favorite + "; in test mode, so not saving");
		}
		
		if( !favorites.contains(favorite) ) {
			favorites.add( favorite );
		}
	}

	@Override
	public List<String> findAllFavoriteRootDirs() {
		return favorites;
	}
}
