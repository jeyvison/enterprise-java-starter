<?xml version="1.0" encoding="UTF-8"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <modelVersion>4.0.0</modelVersion>
  <groupId>[# th:utext="${maven_groupid}"/]</groupId>
  <artifactId>[# th:utext="${maven_artifactid}"/]</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>war</packaging>

  <dependencies>
    <dependency>
      <groupId>jakarta.platform</groupId>
      <artifactId>jakarta.jakartaee-api</artifactId>
      <version>8.0.0</version>
      <scope>provided</scope>
    </dependency>
    <dependency>
      <groupId>org.eclipse.microprofile</groupId>
      <artifactId>microprofile</artifactId>
      <version>[# th:utext="${mp_mvn_version}"/]</version>
      <type>pom</type>
      <scope>provided</scope>
    </dependency>
[# th:each="dependency : ${dependencies}"]
    <dependency>
      <groupId>[# th:utext="${dependency.groupId}"/]</groupId>
      <artifactId>[# th:utext="${dependency.artifactId}"/]</artifactId>
      <version>[# th:utext="${dependency.version}"/]</version>
      <scope>[# th:utext="${dependency.scope}"/]</scope>
    </dependency>
[/][# th:each="dependency : ${test_dependencies}"]
    <dependency>
      <groupId>[# th:utext="${dependency.groupId}"/]</groupId>
      <artifactId>[# th:utext="${dependency.artifactId}"/]</artifactId>
      <version>[# th:utext="${dependency.version}"/]</version>
      <scope>[# th:utext="${dependency.scope}"/]</scope>
    </dependency>
[/]  </dependencies>

  <build>
    <finalName>[# th:utext="${maven_artifactid}"/]</finalName>
[# th:insert="files/pom-maven-plugins.xml.tpl"/]  </build>

  <properties>
[# th:insert="files/pom-properties.xml.tpl"/]  </properties>

</project>
