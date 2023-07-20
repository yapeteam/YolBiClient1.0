package dev.tenacity.utils.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReleaseType {

    PUBLIC("Public"),
    BETA("Beta"),
    DEV("Developer"),
    MACSPP("Dev-MacOs");

    private final String name;

}
