package com.finalka.controller.util;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {
    @Bean
    public OpenAPI configure() {
        return new OpenAPI()
                .info(new Info()
                        .title("WeeklyMenu")
                        .description("У нас тут готовое меню на неделю")
                        .version("1.0.0")
                        .contact(new Contact().name("Aijan, Meerim, Elhan")
                                .email("aijan@mail"))
                );
    }
}
