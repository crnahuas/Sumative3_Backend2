package com.minimarket.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI minimarketOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MINIMARKET PLUS API")
                        .version("1.0.0")
                        .description("API REST para gestionar productos, carritos, inventario, ventas y usuarios. "
                                + "Las respuestas incluyen enlaces HATEOAS para facilitar la navegación.")
                        .contact(new Contact().name("Equipo MINIMARKET PLUS"))
                        .license(new License().name("Uso académico")))
                .components(new Components()
                        .addSecuritySchemes("cookieAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("JSESSIONID")
                                .description("Sesión creada por Spring Security después de iniciar sesión.")));
    }
}
