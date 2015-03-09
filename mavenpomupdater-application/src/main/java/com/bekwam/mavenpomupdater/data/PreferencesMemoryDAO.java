package com.bekwam.mavenpomupdater.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by carl_000 on 3/9/2015.
 */
public class PreferencesMemoryDAO implements PreferencesDAO {

    private Log log = LogFactory.getLog(PreferencesMemoryDAO.class);

    private Map<String, String> preferences = new LinkedHashMap<String, String>();

    @Override
    public void init() {
        if( log.isDebugEnabled() ) {
            log.debug("[INIT]");
        }

        preferences.put( PreferencesConstants.KEY_FILTERS, PreferencesConstants.DEFAULT_FILTERS );
    }

    @Override
    public void savePreferences(Map<String, String> preferences) {

        if( log.isDebugEnabled() ) {
            log.debug("[SAVE]");
            preferences.entrySet().stream().forEach(e -> log.debug("[SAVE] " + e.getKey() + "/" + e.getValue()));
        }

        //
        // holding off on a clear() for now which means that the preferences set may be growing with obsolete
        // preferences
        //

        this.preferences.putAll( preferences );
    }

    @Override
    public Map<String, String> findAllPreferences() {
        return preferences;
    }
}
