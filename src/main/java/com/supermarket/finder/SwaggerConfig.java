package com.supermarket.finder;

import java.util.List;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    final Server server = new Server();
    server.setUrl("https://supermasbarato.es/superfinder");
    return new OpenAPI().servers(List.of(server));
  }
}
