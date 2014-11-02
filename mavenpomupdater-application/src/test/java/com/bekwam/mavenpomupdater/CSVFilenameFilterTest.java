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

import java.io.File;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class CSVFilenameFilterTest {

	@Test
	public void filterTargets() {
		
		File rootPath = new File("/Users/carlwalker/Desktop/git/repos1/css-impact/css-impact-application");
		String pomFileName = "pom.xml";
		String targetDirName = "target";

		CSVFilenameFilter ff = new CSVFilenameFilter( "target" );
		
		assertTrue( ff.accept( rootPath, pomFileName ) );
		assertFalse( ff.accept( rootPath, targetDirName ) );
	}

}
