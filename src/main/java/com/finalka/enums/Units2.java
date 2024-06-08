package com.finalka.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Units2 {
    kilogram("кг"),
    litre("л"),
    Pieces("шт");

    private final String units2;
}
