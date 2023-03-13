package id.my.rizkiyuwanda.utility;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
public class LSWebClient {

    @Bean
    public WebClient getWebClient(){
        WebClient webClient = WebClient.builder().baseUrl("https://api.luwid.cloud")
                .defaultHeaders(httpHeaders -> httpHeaders.setBasicAuth("admin@gmail.com", "admin"))
                .build();
        return webClient;
    }
}
