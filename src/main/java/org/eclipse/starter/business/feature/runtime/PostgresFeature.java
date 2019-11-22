package org.eclipse.starter.business.feature.runtime;

import org.eclipse.starter.business.feature.AbstractFeature;
import org.eclipse.starter.business.model.entity.Feature;
import org.eclipse.starter.business.model.entity.JakartaRuntime;
import org.eclipse.starter.business.model.entity.JessieModel;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class PostgresFeature extends AbstractFeature {

    @Override
    public String addonName() {
        return Feature.POSTGRES.getCode();
    }

    @Override
    public void initProperties(JessieModel model) {
        Map<String, String> features = (Map<String, String>) model.getVariables().get("runtime_features");
        features.put("http://central.maven.org/maven2/org/postgresql/postgresql/9.4.1212/postgresql-9.4.1212.jar", "postgresql-9.4.1212.jar");
    }

    @Override
    public void createFiles(JessieModel model) {
        Map<String, Object> variables = model.getVariables();

        if (model.getRuntime() == JakartaRuntime.LIBERTY) {
            processTemplateFile(model.getDirectory() + "/src/main/liberty/config/defaults/", "postgres-driver.xml", variables);
        }
    }

}
