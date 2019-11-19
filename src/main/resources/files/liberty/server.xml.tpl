<?xml version="1.0" encoding="UTF-8"?>
<server description="OpenLiberty Server">

    <featureManager>
        <feature>javaee-8.0</feature>
        <feature>microProfile-[# th:text="${mp_version}"/]</feature>
    </featureManager>

    <webApplication location="[# th:text="${maven_artifactid}"/].war" contextRoot="/"/>
    <mpMetrics authentication="false"/>

    <!-- This is the keystore that will be used by SSL and by JWT. -->
    <keyStore id="defaultKeyStore" location="public.jks" type="JKS" password="atbash" />

    <!-- The MP JWT configuration that injects the caller's JWT into a ResourceScoped bean for inspection. -->
    <mpJwt id="jwtUserConsumer" keyName="theKeyId" audiences="targetService" issuer="${jwt.issuer}"/>
</server>
