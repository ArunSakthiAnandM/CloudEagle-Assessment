package cloud.eagle.assessment.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for WebClient with appropriate timeouts and connection settings.
 * Uses virtual threads for blocking operations as per Java 25 best practices.
 */
@Configuration
public class WebClientConfig {

    @Value("${webclient.connection-timeout:10000}")
    private int connectionTimeout;

    @Value("${webclient.response-timeout:30000}")
    private int responseTimeout;

    @Bean
    public WebClient.Builder webClientBuilder() {
        final HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
            .responseTimeout(Duration.ofMillis(responseTimeout))
            .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(responseTimeout, TimeUnit.MILLISECONDS))
                .addHandlerLast(new WriteTimeoutHandler(responseTimeout, TimeUnit.MILLISECONDS))
            );

        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}

