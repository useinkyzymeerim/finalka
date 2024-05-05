package com.finalka.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Units {
    gram("г"),
    milliliter("мл"),
    litre("л"),
    teaspoon("ч.л"),
    spoon("с.л"),
    Pieces("шт")
    ;

    private final String units;

}
