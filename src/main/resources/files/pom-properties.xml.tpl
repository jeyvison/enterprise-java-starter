    <maven.compiler.target>[# th:utext="${java_version}"/]</maven.compiler.target>
    <maven.compiler.source>[# th:utext="${java_version}"/]</maven.compiler.source>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <failOnMissingWebXml>false</failOnMissingWebXml>
[# th:if="${jk_servername} == 'liberty'"]    <openliberty.maven.version>3.1</openliberty.maven.version>
    <openliberty.version>[19.0.0.9,)</openliberty.version>[/]
