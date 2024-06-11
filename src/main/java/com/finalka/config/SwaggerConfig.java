package com.finalka.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {
    @Bean
    public OpenAPI configure() {
        return new OpenAPI()
                .info(new Info()
                        .title("WeeklyMenu")
                        .description("У нас тут готовое меню на неделю")
                        .version("1.0.0")
                        .contact(new Contact().name("Aijan, Meerim, Elhan, Akim")
                                .email("aijan@mail")))
                .addTagsItem(new Tag().name("Admin API").description("Тут находятся все роуты для работы админа в магазине"))
                .addTagsItem(new Tag().name("Chef API").description("Тут находятся все роуты для поваров"))
                .addTagsItem(new Tag().name("Public API").description("Тут находятся все общие роуты для не авторизованных пользователей"))
                .addTagsItem(new Tag().name("User API").description("Тут находятся все роуты для пользователей"))
                .addTagsItem(new Tag().name("Authorized API").description("Тут находятся все общие роуты для авторизованных пользователей"));
    }
}
