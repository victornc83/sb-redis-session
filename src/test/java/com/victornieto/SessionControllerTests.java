package com.victornieto;

import java.net.URI;

import org.junit.Test;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ServerPortInfoApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by victor.nieto.castan on 14/06/2017.
 */
public class SessionControllerTests {

    @Test
    public void sessionExpiry() throws Exception {

        String port = null;

        try {
            ConfigurableApplicationContext context = new SpringApplicationBuilder()
                    .sources(Application.class)
                    .properties("server.port:0")
                    .initializers(new ServerPortInfoApplicationContextInitializer())
                    .run();
            port = context.getEnvironment().getProperty("local.server.port");
        }
        catch (RuntimeException ex) {
            if (!redisServerRunning(ex)) {
                return;
            }
            throw ex;
        }

        URI uri = URI.create("http://localhost:" + port + "/");
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        String uuid1 = response.getBody();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", response.getHeaders().getFirst("Set-Cookie"));

        RequestEntity<Void> request = new RequestEntity<>(requestHeaders, HttpMethod.GET,
                uri);

        String uuid2 = restTemplate.exchange(request, String.class).getBody();
        assertThat(uuid1).isEqualTo(uuid2);

        Thread.sleep(5000);

        String uuid3 = restTemplate.exchange(request, String.class).getBody();
        assertThat(uuid2).isNotEqualTo(uuid3);
    }

    private boolean redisServerRunning(Throwable ex) {
        if (ex instanceof RedisConnectionFailureException) {
            return false;
        }
        return (ex.getCause() == null || redisServerRunning(ex.getCause()));
    }


}
