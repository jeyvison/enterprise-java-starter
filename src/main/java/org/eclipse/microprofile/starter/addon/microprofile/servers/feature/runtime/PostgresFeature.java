package org.eclipse.microprofile.starter.addon.microprofile.servers.feature.runtime;

import org.eclipse.microprofile.starter.addon.microprofile.servers.feature.AbstractFeature;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.Feature;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.model.JessieModel;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class PostgresFeature extends AbstractFeature {

    @Override
    public String addonName() {
        return Feature.POSTGRES.getCode();
    }

    @Override
    public void initAddonProperties(JessieModel model) {
        Map<String, String> features = (Map<String, String>) model.getVariables().get("jk_runtime_features");
        features.put("http://central.maven.org/maven2/org/postgresql/postgresql/9.4.1212/postgresql-9.4.1212.jar", "postgresql-9.4.1212.jar");
    }

    @Override
    public void createFiles(JessieModel model) {
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, Object> variables = model.getVariables();

        String serverName = model.getOptions().get("jk.server").getSingleValue();
        SupportedServer supportedServer = SupportedServer.valueFor(serverName);

        if (supportedServer == SupportedServer.LIBERTY) {
            processTemplateFile(model.getDirectory() + "/src/main/liberty/config/defaults/", "postgres-driver.xml", alternatives, variables);
        }
    }

}
