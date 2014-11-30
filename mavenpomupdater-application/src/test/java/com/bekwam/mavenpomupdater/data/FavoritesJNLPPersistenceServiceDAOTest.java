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
import java.net.URL;
import java.util.Properties;

import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

public class FavoritesJNLPPersistenceServiceDAOTest {

	private Mockery context = new Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};
	
	FavoritesJNLPPersistenceServiceDAO dao;
	BasicService bs;
	PersistenceService ps;
	IOUtils ioUtils;
	
	@Before
	public void init() {
		
		bs = context.mock(BasicService.class);
		ps = context.mock(PersistenceService.class);
		ioUtils = context.mock(IOUtils.class);
		
		dao = new FavoritesJNLPPersistenceServiceDAO();
		dao.bs = bs;
		dao.ps = ps;
		dao.ioUtils = ioUtils;
	}
	
	@Test
	public void addFavoriteToEmptyList() throws IOException {
		
		String favorite = "/Users/carlwalker/git/respos-1";
		final URL codebase = new URL("http://www.bekwam.com/mpu");
		final String[] names = { "http://www.bekwam.com/mpu" };
		final FileContents fc = context.mock(FileContents.class);
		final Properties properties = new Properties();

		context.checking(new Expectations() {{
			oneOf(bs).getCodeBase(); will(returnValue(codebase));
			oneOf(ps).getNames( codebase ); will(returnValue(names));
			oneOf(ps).get( codebase ); will(returnValue(fc));
			oneOf(ioUtils).getProperties(fc); will(returnValue(properties));
			oneOf(ioUtils).setProperties(fc, "favoritesCSV", favorite, "jnlp muffins from mpu app");
		}});
		
		dao.addFavoriteRootDir( favorite );
		
		context.assertIsSatisfied();
	}
}
