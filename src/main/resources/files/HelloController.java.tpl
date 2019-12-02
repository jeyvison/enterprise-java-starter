package [# th:text="${java_package}"/];

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 */
@Path("/hello")
@ApplicationScoped
public class HelloController {

    @GET
    public String hello() {
        return "Hello World";
    }
}
