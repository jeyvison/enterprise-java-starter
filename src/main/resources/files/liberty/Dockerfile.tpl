FROM open-liberty:javaee8-java12

[# th:each="entry : ${runtime_features}"]
ADD [# th:utext="${entry.key}"/] /lib/[# th:utext="${entry.value}"/]
[/]

COPY src/main/liberty/config/defaults/* /config/defaults/
COPY src/main/liberty/config/server.xml /config/

COPY target/[# th:utext="${maven_artifactid}"/].war /config/dropins/
