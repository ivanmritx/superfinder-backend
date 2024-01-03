package com.supermarket.finder;

import java.util.List;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(title = "supermasbarato.es - API", version = "v1", description = "API para la búsqueda de productos en supermercados"))
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    final Server server = new Server();
    server.setUrl("https://supermasbarato.es/superfinder/");
    return new OpenAPI().servers(List.of(server));
  }
}
