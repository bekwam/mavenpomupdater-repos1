package com.bekwam.mavenpomupdater.data;

import java.util.List;
import java.util.Map;

/**
 * Created by carl_000 on 3/9/2015.
 */
public interface PreferencesDAO {

    void init();

    void savePreferences(Map<String, String> preferences);

    public Map<String, String> findAllPreferences();

}
