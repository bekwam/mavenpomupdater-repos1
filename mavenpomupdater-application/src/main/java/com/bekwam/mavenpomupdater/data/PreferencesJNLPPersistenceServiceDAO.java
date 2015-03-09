package com.bekwam.mavenpomupdater.data;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.jnlp.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.prefs.Preferences;

/**
 * Created by carl_000 on 3/9/2015.
 */
public class PreferencesJNLPPersistenceServiceDAO implements PreferencesDAO {

    private Log log = LogFactory.getLog(PreferencesJNLPPersistenceServiceDAO.class);

    private final int FC_MAX_SIZE = 100000;

    PersistenceService ps;
    BasicService bs;

    @Inject
    IOUtils ioUtils;

    @Override
    public void init() {
        if( log.isDebugEnabled() ) {
            log.debug("[INIT]");
        }

        try {
            ps = (PersistenceService) ServiceManager.lookup("javax.jnlp.PersistenceService");
            bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
        } catch (UnavailableServiceException e) {
            log.error( "cannot init persistence and basic services", e );
        }
    }

    @Override
    public void savePreferences(Map<String, String> preferences) {

        if( log.isDebugEnabled() ) {
            log.debug("[SAVE]");
            preferences.entrySet().stream().forEach(e -> log.debug("[SAVE] " + e.getKey() + "/" + e.getValue()));
        }

        if (ps != null && bs != null) {

            if( log.isDebugEnabled() ) {
                log.debug("[SAVE] persistence and basic service have been initialized");
            }

            try {

                URL codebase = bs.getCodeBase();

                //
                // Could return subdirectories as distinct names for sharing
                // muffins among apps
                //
                // Ex, getNames(codebase) -> { codebase, codebase + "/app2" };
                //
                String [] muffins = ps.getNames(codebase);

                if( muffins == null || muffins.length == 0 ) {

                    if( log.isDebugEnabled() ) {
                        log.debug("[SAVE] creating new datastore for codebase=" + codebase);
                    }

                    ps.create(codebase, FC_MAX_SIZE);

                    //
                    // Can either be CACHED (default), DIRTY, or TEMPORARY
                    //
                    // Since there's no server-side component, using TEMPORARY
                    //

                    ps.setTag(codebase, PersistenceService.TEMPORARY);
                }

                FileContents fc = ps.get(codebase);

                Properties properties = ioUtils.getProperties(fc);

                properties.putAll(preferences); // overwrite what's there

                ioUtils.setProperties(fc, properties, "jnlp muffins from mpu app");

            } catch (Exception exc) {
                log.error( "error writing muffin", exc);
            }
        }

    }

    @Override
    public Map<String, String> findAllPreferences() {

        Map<String, String> preferences = new LinkedHashMap<>();

        if (ps != null && bs != null) {

            if( log.isDebugEnabled() ) {
                log.debug("[FIND ALL] persistence and basic service have been initialized");
            }

            try {

                URL codebase = bs.getCodeBase();

                String[] muffins_a = ps.getNames(codebase);

                if( log.isDebugEnabled() ) {
                    for( String s : muffins_a ) {
                        log.debug("[FIND ALL] m_a=" + s);
                    }
                }

                List<String> muffins = Arrays.asList(ps.getNames(codebase));

                if( log.isDebugEnabled() ) {
                    log.debug("[FIND ALL] codebase=" + codebase + ", muffins empty?=" + (CollectionUtils.isEmpty(muffins)));
                }

                List<URL> muffinURLs = new ArrayList<URL>();

                if( log.isDebugEnabled() ) {
                    for( String s : muffins ) {
                        log.debug("[FIND ALL] s=" + s);
                    }
                }

                muffins.forEach(m -> {
                    try {
                        if( log.isDebugEnabled() ) {
                            log.debug("[FIND ALL] adding m=" + m);
                        }
                        muffinURLs.add( new URL(codebase + m) );
                    } catch(MalformedURLException exc) {
                        if( log.isInfoEnabled() ) {
                            log.info("[FIND ALL] can't create URL from " + m );
                        }
                    }
                });

                //
                // Only expecting 1 muffin URL for now which is codebase
                //

                if( CollectionUtils.isNotEmpty(muffinURLs) ) {

                    if (log.isDebugEnabled()) {
                        log.debug("[FIND ALL] there are muffins to consult");
                    }

                    FileContents fc = ps.get(muffinURLs.get(0));

                    Properties properties = ioUtils.getProperties(fc);

                    properties.forEach((k, v) -> {
                        if( StringUtils.startsWith( (String)k, PreferencesConstants.PREFIX_PREFERENCES ) ) {
                            preferences.put( (String)k, (String)v );
                        }
                    });

                    if( log.isDebugEnabled()  ) {
                        log.debug("[FIND ALL] retrieved " + preferences.size() + " preferences");
                    }
                }

            } catch (Exception exc ) {
                log.error( "error retrieving muffins", exc );
            }
        }

        return preferences;
    }
}
