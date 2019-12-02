package [# th:text="${java_package}"/];

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class HelloSystem {

    private final Client client;
    private final WebTarget baseTarget;

    public HelloSystem() {
        client = ClientBuilder.newClient();
        baseTarget = client.target(buildUri());
    }

    private URI buildUri() {
        String host = System.getProperty("hello.test.host", "localhost");
        String port = System.getProperty("hello.test.port", "[# th:utext="${jk_server_http_port}"/]");
        return UriBuilder.fromUri("http://{host}:{port}/data/hello/").build(host, port);
    }

    public String hello() {
        Response response = baseTarget.request().get();
        verifySuccess(response);
        return response.readEntity(String.class);
    }

    private void verifySuccess(Response response) {
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL)
            throw new AssertionError("Status was not successful: " + response.getStatus());
    }
}
