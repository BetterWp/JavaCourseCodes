package io.github.kimmking.gateway.router;

import java.util.List;
import java.util.Random;

public class RandowRouter implements HttpEndpointRouter{

    private Random random;

    public RandowRouter(){
        random = new Random();
    }

    @Override
    public String route(List<String> endpoints) {
        if (endpoints == null || endpoints.size() == 0) {
            throw new IllegalArgumentException(String.format("Illegal argument endpoints:%s",endpoints));
        }
        return endpoints.get(random.nextInt(endpoints.size()));
    }
}
