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
