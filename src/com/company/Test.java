package com.company;

import com.company.collections.changeAPI.Change;

public class Test {
    public static void main(String[] args) {

        final var change =
                Change.of(Integer.class)
                        .addAll(20, 5, 2)
                        .optimise();

        System.out.println(change);
        System.out.println(change.multiplyAll());

    }
}
