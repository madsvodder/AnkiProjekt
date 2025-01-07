package org.example.ankiprojekt;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    @Getter @Setter
    String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
