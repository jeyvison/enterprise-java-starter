package org.eclipse.microprofile.starter.addon.microprofile.servers.feature.dependency;

import org.eclipse.microprofile.starter.addon.microprofile.servers.feature.AbstractFeature;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.Feature;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.core.model.MavenDependency;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class KafkaClientFeature extends AbstractFeature {

    @Override
    public String addonName() {
        return Feature.KAFKA.getCode();
    }

    @Override
    public void initAddonProperties(JessieModel model) {
        Map<String, String> features = (Map<String, String>) model.getVariables().get("jk_runtime_features");
        features.put("https://central.maven.org/maven2/org/apache/kafka/kafka-clients/1.0.2/kafka-clients-1.0.2.jar", "kafka-clients-1.0.2.jar");
        features.put("http://central.maven.org/maven2/org/lz4/lz4-java/1.4/lz4-java-1.4.jar", "lz4-java-1.4.jar");
        features.put("http://central.maven.org/maven2/org/xerial/snappy/snappy-java/1.1.7.1/snappy-java-1.1.7.1.jar", "snappy-java-1.1.7.1.jar");
        features.put("http://central.maven.org/maven2/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar", "slf4j-api-1.7.25.jar");

        List<MavenDependency> dependencies = (List<MavenDependency>) model.getVariables().get("jk_dependencies");
        dependencies.add(new MavenDependency("org.apache.kafka.kafka-clients", "kafka-clients", "1.0.2"));
    }

    @Override
    public void createFiles(JessieModel model) {
        // nothing to do
    }
}