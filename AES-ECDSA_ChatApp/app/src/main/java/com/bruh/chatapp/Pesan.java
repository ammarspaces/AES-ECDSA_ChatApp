package com.bruh.chatapp;

import java.io.Serializable;
import java.math.BigInteger;

import androidx.annotation.Keep;

@Keep
public class Pesan implements Serializable {
    static String Nama = "";
    static String Pesan = "";
    static String chatWith = "";
    static String Signature = "";
    static String q = "";
    static String g = "";
    static String publickey = "";
    Pesan(){

    }

    public Pesan(String Nama, String Pesan, String chatWith,String signature,String q,String g, String publickey){
        this.Nama = Nama;
        this.Pesan = Pesan;
        this.chatWith = chatWith;
        this.Signature = signature;
        this.q = q;
        this.g = g;
        this.publickey = publickey;
    }

    public String getPesan() {
        return Pesan;
    }

    public String getChatWith() {
        return chatWith;
    }

    public String getNama() {
        return Nama;
    }

    public String getSignature() { return Signature; }

    public static String getQ() {
        return q;
    }

    public static String getG() {
        return g;
    }

    public static String getPublickey() {
        return publickey;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public void setPesan(String pesan) {
        Pesan = pesan;
    }

    public void setChatWith(String chatWith) {
        this.chatWith = chatWith;
    }

    public void setSignature(String signature) { Signature = signature; }

    public void setQ(String q) {
        this.q = q;
    }

    public static void setG(String g) {
        g = g;
    }

    //    public static void setG(String G) {
//        Pesag = g;
//    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }
}

