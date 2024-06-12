package com.finalka.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter

public enum Categories {
    BREAKFAST("ЗАВТРАКИ"),
    SOUPS("СУПЫ"),
    HOT_DISHES("ГОРЯЧИЕ БЛЮДА"),
    DESSERTS("ДЕСЕРТЫ"),
    SNACKS("ЗАКУСКИ"),
    DRINKS("НАПИТКИ"),
    SALADS("САЛАТЫ")
    ;

    private final String categories;

}
