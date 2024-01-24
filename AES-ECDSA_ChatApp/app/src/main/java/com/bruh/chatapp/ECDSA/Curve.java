package com.bruh.chatapp.ECDSA;

import java.math.BigInteger;
import java.util.ArrayList;

public class Curve {
    private BigInteger a; //parameter
    private BigInteger b; //parameter
    private BigInteger p; //curve prime
    private BigInteger n; //Banyak point
    private BigInteger Fp; //banyak koordinat pada kurva elliptika
    private Point G; //Generator point
    private String standard; //Standard yang dipakai
    public ArrayList<Point> coord; //Berisi semua koordinat kurva elliptika


    public Curve(String standard){
        //Standar P-192
        this.standard = standard;
        switch (standard){
            case "P-192":
                a = BigInteger.valueOf(-3);
                b = new BigInteger("64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1", 16);
                p = new BigInteger("6277101735386680763835789423207666416083908700390324961279");
                G = new Point(new BigInteger("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012", 16), new BigInteger("07192b95ffc8da78631011ed6b24cdd573f977a11e794811", 16),a, p);
                n = new BigInteger("6277101735386680763835789423176059013767194773182842284081");
                break;
            case "P-256":
                a = BigInteger.valueOf(-3);
                b = new BigInteger("5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b", 16);
                p = new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951");
                G = new Point(new BigInteger("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296", 16),
                        new BigInteger("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5", 16),
                        a, p);
                n = new BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369");
                break;

        }

    }
    
    public BigInteger getA() {
        return a;
    }

    public BigInteger getB() {
        return b;
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getN() {
        return n;
    }

    public Point getG() {
        return G;
    }

    public void setA(BigInteger a) {
        this.a = a;
    }

    public void setB(BigInteger b) {
        this.b = b;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }

    public void setG(Point g) {
        G = g;
    }

    public void GenElliptic(){ //Generate set of points of Elliptic Group
        coord = new ArrayList<>();
        BigInteger x,y;
        x = BigInteger.ZERO;
        n = BigInteger.ZERO;
        while(x.compareTo(this.p.subtract(BigInteger.ONE)) <= 0){
            BigInteger y_2 = (x.pow(3)).add(this.a.multiply(x)).add(this.b);
            BigInteger y2 = y_2.mod(this.p);
            BigInteger i;
            for(i=BigInteger.ONE;i.compareTo(this.p.subtract(BigInteger.ONE)) <= 0;i.add(BigInteger.ONE)){
                BigInteger y3 = this.p.multiply(i).add(y2);
                if(perfectSquare(y3)){
                    y = sqrt(y3);
                    Point add = new Point(x,y,a,p);
                    if(checkPoint(add) == false){
                        coord.add(add);
                        n = n.add(BigInteger.ONE);
                    }

                    Point addinv = new Point(x,this.p.subtract(y),a,p);
                    if(checkPoint(addinv) == false){
                        coord.add(addinv);
                        n = n.add(BigInteger.ONE);
                    }
                }
                x = x.add(BigInteger.ONE);
            }
        }
    }
 
    //Cek koordinat apakah ada atau tidak pada kurva
    private boolean checkPoint(Point prime){
        boolean status = false;
        int  i = 0;
        while((!status) && i < coord.size()){
            if(prime.isEqual(coord.get(i))){
                status = true;
            }
            i++;
        }

        return status;
    }

    //Check n ewether have root or not
    private boolean perfectSquare(BigInteger n){
        BigInteger root = sqrt(n);
        return isSqrt(n, root);
    }

    //Mencari akar kuadrat dengan menggunakan Metode Heron
    public static BigInteger sqrt(BigInteger n){
        if (n.signum() >= 0){
            final int bitLength = n.bitLength();
            BigInteger root = BigInteger.ONE.shiftLeft(bitLength / 2);

            while (isSqrt(n, root) == false){
                root = root.add(n.divide(root)).divide(BigInteger.valueOf(2));
            }
            return root;
        }else{
            throw new ArithmeticException("square root of negative number");
        }
    }

    //Mengecek square root 
    private static boolean isSqrt(BigInteger n, BigInteger root) {
        final BigInteger lowerBound = root.pow(2);
        final BigInteger upperBound = root.add(BigInteger.ONE).pow(2);
        return lowerBound.compareTo(n) <= 0
            && n.compareTo(upperBound) < 0;
    }
}