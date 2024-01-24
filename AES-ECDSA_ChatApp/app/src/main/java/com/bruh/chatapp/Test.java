package com.bruh.chatapp;

import java.io.Serializable;

import androidx.annotation.Keep;

@Keep
public class Test implements Serializable {
    static String Nama = "";

    Test(){

    }

    public Test(String Nama){
        this.Nama = Nama;
    }

    public static String getNama() {
        return Nama;
    }

    public static void setNama(String nama) {
        Nama = nama;
    }
}
