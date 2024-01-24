package com.bruh.chatapp.ECDSA;

import android.util.Log;

import java.math.BigInteger;

public class Point {
    private BigInteger x;
    private BigInteger y;
    private BigInteger a;
    private BigInteger p;
    private boolean inf;
    public static Point O = new Point(true);

    public Point(){
        this.x = BigInteger.ZERO;
        this.y = BigInteger.ZERO;
        this.inf = false;
    }

    public Point(boolean inf){
        this.x = BigInteger.ZERO;
        this.y = BigInteger.ZERO;
        this.inf = inf;
    }

    public Point(BigInteger x, BigInteger y, BigInteger a,BigInteger p){
        this.x = x;
        this.y = y;
        this.a = a;
        this.p = p;
        this.inf = false;
    }

     //Check whether this point is point O
     public boolean isInfinity(){
        return this.inf;
    }

    public BigInteger getA() {
        return a;
    }

    public BigInteger getX() {
        return x;
    }

    public BigInteger getY() {
        return y;
    }

    public BigInteger getP() {
        return p;
    }

    public void setA(BigInteger a) {
        this.a = a;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

//    public boolean isEqual(Point q){
//        if((this.x.compareTo(getX()) == 0) && (this.y.compareTo(getY()) == 0) && this.inf == q.isInfinity()){
//            return true;
//        }else{
//            return false;
//        }
//    }

    public Point copy(){
        Point r = new Point(this.x, this.y, this.a, this.p);
        return r;
    }

    //Mencari titik koordinat inverse
    public Point inverse(){
        Point invcoord = new Point(this.x, this.y.negate().mod(p), this.a, this.p);
        return invcoord;
    }

    //Mengecek point q adalah sama dengan koordinat this.x dan this.y
    public boolean isEqual(Point q){
        if ((this.x.compareTo(q.getX()) == 0) && (this.y.compareTo(q.getY()) == 0) && (this.inf == q.isInfinity())){
            return true;
        } else {
            return false;
        }
    }

    //Melakukan perhitungan koordinat
    public Point addition(Point q){
        if (q.isEqual(O)){ //Jika koordinat q adalah poin infinity
            return this.copy();
        } else if (this.isEqual(O)){ //Jika koordinat this adalah poin infinity
            return q;
        } else if (this.inverse().isEqual(q)){ //Jika koordinat inverse sama dengan koordinat q
            return O;
        } else if (this.isEqual(q)){ //Jika koordinat this sama dengan koordinat q
            return this.doubling();
        } else if (this.x.compareTo(q.getX()) == 0){ //Jika koordinat this x sama dengan koordinat q
            return O;
        } else { //Untuk lainnya
            BigInteger lambda = ((this.y.subtract(q.getY())).multiply((this.x.subtract(q.getX())).modInverse(p))).mod(p);  //Menghitung gradien
            BigInteger x1 = (lambda.pow(2).subtract(this.x)).subtract(q.getX()); //Menghitung koordinat x hasil penjumlahan
            BigInteger y1 = (lambda.multiply(this.x.subtract(x1))).subtract(this.y) ; //Menghitung koordinat y hasil penjumlahan
            Point r = new Point(x1.mod(p), y1.mod(p), this.a, this.p);
            Log.d("Hasil Penambahan Point:", r.toString());
            return r;
        }
    }

    //Melakukan pengurangan Point. P +(-Q)
    public Point subtraction(Point q){
        Point r = new Point();
        r = this.addition(q.inverse());
        Log.d("Hasil Pengurangan Point:", r.toString());
        return r;
    }

    //Melakukan penjumlahan dengan diri sendiri
    public Point doubling(){
        if (this.y.compareTo(BigInteger.ZERO) == 0){ //Jika this y sama dengan 0
            return O;
        } else { //Lainnya
            BigInteger lambda = ((this.x.pow(2).multiply(BigInteger.valueOf(3))).add(this.a)).multiply((this.y.multiply(BigInteger.valueOf(2))).modInverse(p));  //Menghitung gradien garis
            BigInteger x1 = (lambda.pow(2)).subtract(this.x.multiply(BigInteger.valueOf(2))) ;
            BigInteger y1 = (lambda.multiply(this.x.subtract(x1))).subtract(this.y) ;
            Point r = new Point(x1.mod(p), y1.mod(p), this.a, this.p);
            Log.d("Hasil Perkalian Point:", r.toString());
            return r;
        }
    }

    //Menghitung penjumlahan diri sendiri sebanyak k-1
    public Point iteration(BigInteger k){
        Point r = this.copy();
        for (BigInteger i=BigInteger.ONE; i.compareTo(k.subtract(BigInteger.ONE)) == -1; i = i.add(BigInteger.ONE)){
            r.addition(this);
        }
        Log.d("Hasil Iterasi Point:", r.toString());
        return r;
    }

    //Melakukan perkalian koordinat dengan konstanta k
    public Point multiplication(BigInteger k){
        Point r = new Point();
        if (k.compareTo(BigInteger.ZERO) == 0){ //Jika k = 0
            return O;
        }
        if (k.compareTo(BigInteger.ONE) == 0){ //Jika k = 1
            return this.copy();
        } else if (k.mod(BigInteger.valueOf(2)).compareTo(BigInteger.ONE) == 0) { //Jika sisa k dibagi 2 adalah 1
            r = this.addition(this.multiplication(k.subtract(BigInteger.ONE)));
            Log.d("Hasil Perkalian Point:", r.toString());
            return r;
        } else { //Jika melakukan perkalian dengan diri sendiri
            Point temp = this.doubling();
            r = temp.multiplication(k.divide(BigInteger.valueOf(2)));
            Log.d("Hasil Perkalian Point:", r.toString());
            return r;
        }
    }


    //Returns hex string representation of point
    public String toHexString(){
        String r = x.toString(16) + y.toString(16);
        return r;
    }

    @Override
    public String toString(){
        return "x adalah: " + getX() + ",y adalah: " + getY()+ ", a adalah: " + getA() + ", p adalah: " + getP();
    }

}