package com.github.laim0nas100.lmstudio4j;

import com.github.laim0nas100.cfg.TolerantConfig;
import com.github.laim0nas100.uncheckedutils.LazySafe;
import org.apache.tika.io.CacheMemoryBudget;

/**
 *
 * @author Lemmin
 */
public abstract class Defaults {

    public static final String PROPERTIES_NAME = "lmstudio4j";

    public static final LazySafe<TolerantConfig> config = new LazySafe<>(() -> {
        return new PropertyConfig(PROPERTIES_NAME).loadConfig();
    });

    public static final LazySafe<CacheMemoryBudget> MEMORY_BUDGET = config.map(c -> {
        long bytes = 500 * 1024 * 1024; // 500MB
        if (c != null) {
            bytes = c.getLong("cache.memory.budged", bytes);
        }
        return new CacheMemoryBudget(bytes);
    });

}
