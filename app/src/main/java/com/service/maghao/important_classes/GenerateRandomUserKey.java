package com.service.maghao.important_classes;

import java.util.Random;

public class GenerateRandomUserKey {

    private String chars = "qw2er4ty1ui6op8as2df9gh0jk4l3zx5cvb2nm691283645";

    private StringBuilder builder = new StringBuilder();

    private Random random = new Random();

    private int randomNumber;

    public String generateKey(){


        if (builder != null){
            builder.setLength(0);
        }

        for(int i=0;i<10;i++){

            randomNumber = random.nextInt(chars.length());

            builder.append(chars.charAt(randomNumber));
        }

        return builder.toString();

    }

}
