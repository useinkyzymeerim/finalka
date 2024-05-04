package com.finalka.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Units {
    gram("г"),
    milliliter("мл"),
    litre("л"),
    teaspoon("ч.л"),
    spoon("с.л"),
    Pieces("шт");

    private final String units;

}
