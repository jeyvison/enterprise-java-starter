package org.eclipse.starter.business.feature;

import org.eclipse.starter.business.addons.control.AbstractAddon;

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
