package com.bekwam.mavenpomupdater;

import java.util.Map;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bekwam.mavenpomupdater.data.PreferencesConstants;
import com.bekwam.mavenpomupdater.data.PreferencesDAO;
import com.google.inject.Inject;

/**
 * Created by carl_000 on 3/9/2015.
 */
public class PreferencesDelegate {

    private Log log = LogFactory.getLog(PreferencesDelegate.class);

    TabPane tabPane;
    Tab preferencesTab;
    TextField tfFilters;

    @Inject
    PreferencesDAO preferencesDAO;

    public void init() {

        if(log.isDebugEnabled()) {
            log.debug("[INIT]");
        }

        preferencesTab.setOnCloseRequest(evt -> {

            if (log.isDebugEnabled()) {
                log.debug("[PREFS CLOSED]");
            }

            if (tabPane.getTabs().contains(preferencesTab)) {
               tabPane.getTabs().remove(preferencesTab);
            }

            evt.consume();
        });

        tabPane.getTabs().remove(preferencesTab); // start w/o it showing

        refresh();  // make prefs available to other components; can defer if this is costly
    }

    public void show() {
        if( log.isDebugEnabled() ) {
            log.debug("[SHOW]");
        }
        refresh();
    }

    private void refresh() {

        Map<String, String> preferences = preferencesDAO.findAllPreferences();
        String filters = preferences.get(PreferencesConstants.KEY_FILTERS );
        tfFilters.setText( filters );
    }

    public void savePreferences() {

        if( log.isDebugEnabled() ) {
            log.debug("[SAVE]");
        }

        Map<String, String> preferences = preferencesDAO.findAllPreferences();

        String filters = tfFilters.getText();

        if( StringUtils.isNotEmpty(filters) ) {
            preferences.put( PreferencesConstants.KEY_FILTERS, filters );
        } else {
            if( preferences.containsKey(PreferencesConstants.KEY_FILTERS) ) {
                preferences.remove( PreferencesConstants.KEY_FILTERS );
            }
        }
    }

    public String getProperty(String key) {
        return getProperty(key, false);
    }

    public String getProperty(String key, boolean refresh) {

        String value = null;

        if( refresh ) refresh();

        if( StringUtils.equalsIgnoreCase(key, PreferencesConstants.KEY_FILTERS ) ) {
            value = tfFilters.getText();
        } else {
            throw new IllegalArgumentException("key '" + key + "' not found in preferences");
        }

        return value;

    }
}
