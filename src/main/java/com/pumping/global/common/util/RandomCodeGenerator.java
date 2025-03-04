package com.pumping.global.common.util;

import java.util.Random;

public abstract class RandomCodeGenerator {

    public static String generateCode() {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());

        int randomNumber = 10000 + random.nextInt(90000);
        return String.valueOf(randomNumber);
    }

}
