package com.bekwam.mavenpomupdater;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class to filter files based on passed-in string
 * 
 * String list parameter can contain regular expressions
 * 
 * @author carl
 * @since 1.1.0
 */
public class CSVFilenameFilter implements FilenameFilter {

    private List<String> filterStrings = new ArrayList<String>();

    public CSVFilenameFilter(String csv) {
        if( csv != null && csv.length() > 0 ) {
            filterStrings.addAll(Arrays.asList(csv.split(",")));
        }
    }

    public boolean accept(File f, String name) {

        for( String fs : filterStrings ) {
            if( name.matches( fs ) ) {
                return false;
            }
        }
        return true;
    }
}
