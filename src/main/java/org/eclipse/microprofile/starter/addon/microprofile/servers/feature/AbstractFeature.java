package org.eclipse.microprofile.starter.addon.microprofile.servers.feature;

import org.eclipse.microprofile.starter.spi.AbstractAddon;

/**
 * A feature adds either an API dependency (e.g. Kafka client) or a runtime feature (e.g. Postgres driver)
 * to the generated project.
 */
public abstract class AbstractFeature extends AbstractAddon {

    @Override
    public int priority() {
        return 80;
    }

}
