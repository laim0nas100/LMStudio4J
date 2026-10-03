package com.github.laim0nas100.lmstudio4j;

import com.github.laim0nas100.cfg.TolerantConfig;
import com.github.laim0nas100.commonslb.io.ResourceFallbackLocator;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lemmin
 */
public class PropertyConfig {

    static Logger logger = LoggerFactory.getLogger(PropertyConfig.class);

    public final String PROPERTY;
    public final String PROPERTIES_FILE;
    public final String DEV_PROPERTIES_FILE;
    private final ResourceFallbackLocator locator;

    public PropertyConfig(String name) {
        PROPERTY = name;
        PROPERTIES_FILE = PROPERTY + ".properties";
        DEV_PROPERTIES_FILE = PROPERTY + "-dev" + ".properties";
        locator = new ResourceFallbackLocator()
                .withSystemPropertyPath(PROPERTY)
                .withSystemPropertyURL(PROPERTY)
                .withResource(PROPERTIES_FILE)
                .withResource(DEV_PROPERTIES_FILE);
    }

    public TolerantConfig loadConfig() {
        final ResourceFallbackLocator.Loader loader = locator.getFirstSuccessfulLoader();
        if (loader == null) {
            throw new IllegalStateException("failed to load " + PROPERTY + " settings");
        }
        return TolerantConfig.ofSuplierCached(() -> {
            logger.info("loaded " + PROPERTY + " settings from " + loader.getDescription());

            Properties prop = new Properties();
            InputStream stream = null;
            try {
                URL url = loader.get();
                stream = new BufferedInputStream(url.openStream());
                prop.load(stream);
                return prop;
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        });

    }
}
