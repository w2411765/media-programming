package com.speakeasy.model.alcohol;

import java.util.Objects;

public class AlcoholCard {
    private final AlcoholType type;

    public AlcoholCard(AlcoholType type) {
        this.type = Objects.requireNonNull(type);
    }

    public AlcoholType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "AlcoholCard{type=" + type + "}";
    }
}

