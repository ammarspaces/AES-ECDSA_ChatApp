package com.bruh.chatapp.ECDSA;

import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class ECDSA {
    private BigInteger dA; //private key A
    private BigInteger n; //urutan kurva
    private Curve curve; //kurva
    private Point G; //generator point
    private Point QA; //public key A

    public ECDSA(){
        dA = BigInteger.ZERO;
        curve = new Curve("P-192");
        n = curve.getN();
        G = curve.getG();
        QA = new Point();
    }

    public Curve getCurve() {
        return curve;
    }

    //Key generation
    public void setdA(BigInteger dA) {
        this.dA = dA; //Generate public key
        QA = G.multiplication(this.dA); //Generate private key
        Log.d("dA:", QA.toString());
    }

    public BigInteger getdA() {
        return dA;
    }

    public Point getQA() {
        return QA;
    }

    //Generate Public Key

    //Generate Random BigInt in range [1,n]
    //BigInteger random generator in closed set [1, n]
    private BigInteger randomBigInt(BigInteger n) {
        Random rnd = new Random();
        int maxNumBitLength = n.bitLength();
        BigInteger aRandomBigInt;
        do {
            aRandomBigInt = new BigInteger(maxNumBitLength, rnd);
            // compare random number lessthan ginven number
        } while (aRandomBigInt.compareTo(n) > 0);
        return aRandomBigInt;
    }

    /* Signature */

    //Generate Signature
    private Point SignGeneration(String message){
        BigInteger k = BigInteger.ZERO;
        BigInteger hash = BigInteger.ZERO;
        BigInteger r = BigInteger.ZERO;
        BigInteger s = BigInteger.ZERO;
        Random random = new Random(); //variabel for random k
        Point xy = new Point(); //variabel for kG
        String H = generateSha1String(message);
        hash = new BigInteger(H,16); //Compute hash
        //Proses
        do{
            k = randomBigInt(n.subtract(BigInteger.ONE)); //Generate random bigint with max range n-1
            Log.d("k", k.toString());
            xy = G.multiplication(k); //do k*G
            Log.d("xy", xy.toString());
            r = xy.getX().mod(n); //Compute r by get x_1 fro xy and mod it with n
            Log.d("r:",r.toString());
            if(!(r.compareTo(BigInteger.ZERO) == 0) ){
                if(k.gcd(n).compareTo(BigInteger.ONE) == 0){
                    BigInteger temp = k.modInverse(n);
                    s = temp.multiply(dA.multiply(r).add(hash)).mod(n); //Compute s
                    Log.d("s:", s.toString());
                }
            }
        }while((r.compareTo(BigInteger.ZERO) == 0) || (s.compareTo(BigInteger.ZERO) == 0));

        Point sign = new Point();

        Log.d("set x", r.toString());
        Log.d("set y", s.toString());
        sign.setX(r); //Set x value as r
        sign.setY(s); //Set y value as s

        return sign;
    }

//    private Point SignGeneration(String m){
//        BigInteger e, k, r, s = BigInteger.ZERO;
//        // e = HASH(m)
//        e = new BigInteger(generateSha1String(m), 16);
//        Point x1y1 = new Point();
//        Random rand = new Random();
//        do{
//            k = randomBigInt(n.subtract(BigInteger.ONE));
//            x1y1 = G.multiplication(k);
//            r = x1y1.getX().mod(n);
//            if (! (r.compareTo(BigInteger.ZERO) == 0)){
//                if (k.gcd(n).compareTo(BigInteger.ONE) == 0){
//                    BigInteger temp = k.modInverse(n);
//                    s = (temp.multiply((dA.multiply(r)).add(e))).mod(n);
//                }
//            }
//        } while ((r.compareTo(BigInteger.ZERO) == 0) || (s.compareTo(BigInteger.ZERO) == 0));
//        Point signature = new Point();
//        signature.setX(r);
//        signature.setY(s);
//        return signature;
//    }

    //Signature Verification
    private boolean SignVerification(String message, Point sign){
        //Verify r and s random integer in range max n-1
        BigInteger r = sign.getX();
        BigInteger s = sign.getY();
        BigInteger w,u1,u2 = BigInteger.ZERO;
        if(((r.compareTo(BigInteger.ONE) >= 0)
                && (r.compareTo(n.subtract(BigInteger.ONE)) <= 0))
                && ((s.compareTo(BigInteger.ONE) >= 0)
                && (s.compareTo(n.subtract(BigInteger.ONE)) <= 0))){
            String H = generateSha1String(message);
            BigInteger e = new BigInteger(H,16); //Compute e
            w = s.modInverse(n); //Compute w
            Log.d("w:", w.toString());
            u1 = (e.multiply(w)).mod(n); //Compute u1
            Log.d("u1", u1.toString());
            u2 = (r.multiply(w)).mod(n); //Compute u2
            Log.d("u2", u1.toString());
            Point X = new Point();
            X = (G.multiplication(u1)).addition(QA.multiplication(u2));//Compute X
            Log.d("X:", X.toString());
            Log.d("n", n.toString());
            if((X.getX().mod(n)).compareTo(r.mod(n)) == 0){
                return true;
            }else{
                System.out.println("x1 = " + X.getX().mod(n) + " | " + "r(mod n) = " + r.mod(n));
                return false;
            }

        }else{
            return false;
        }
    }

//    private boolean SignVerification(String m, Point signature){
//        BigInteger r = signature.getX();
//        BigInteger s = signature.getY();
//        BigInteger e, w, u1, u2;
//        if ((r.compareTo(BigInteger.ONE) >= 0) &&
//                (r.compareTo(n.subtract(BigInteger.ONE)) <= 0) &&
//                (s.compareTo(BigInteger.ONE) >= 0) &&
//                (s.compareTo(n.subtract(BigInteger.ONE)) <= 0)){
//            // e = HASH(m)
//            e = new BigInteger(generateSha1String(m), 16);
//            w = s.modInverse(n);
//            u1 = (e.multiply(w)).mod(n);
//            u2 = (r.multiply(w)).mod(n);
//            Point x1y1 = new Point();
//            x1y1 = (G.multiplication(u1)).addition(QA.multiplication(u2));
//            if ((x1y1.getX().mod(n)).compareTo(r.mod(n)) == 0){
//                return true;
//            } else {
//                System.out.println("x1 = " + x1y1.getX().mod(n) + " | " + "r(mod n) = " + r.mod(n));
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }

    //Signing Message
    public String SignMessage(String message){
        Point sign = SignGeneration(message);
        String signString = sign.toHexString();
        return signString;
    }

    //Verify Message Sign
    public boolean MessageVerify(String message, String sign){
        int length = sign.length();
        Point Signature = new Point();
        Signature.setX(new BigInteger(sign.substring(0, length/2), 16));
       Log.d("setX", new BigInteger(sign.substring(0, length/2), 16).toString());
        Signature.setY(new BigInteger(sign.substring(length/2), 16));
       Log.d("setY", new BigInteger(sign.substring(length/2), 16).toString());
        return SignVerification(message, Signature);
    }

    //Hash function
    public static String generateSha1String(String input) 
    { 
        try { 
            // getInstance() method is called with algorithm SHA-1 
            MessageDigest md = MessageDigest.getInstance("SHA-1");
  
            // digest() method is called 
            // to calculate message digest of the input string 
            // returned as array of byte 
            byte[] messageDigest = md.digest(input.getBytes()); 
  
            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest); 
  
            // Convert message digest into hex value 
            String hashtext = no.toString(16); 
  
            // Add preceding 0s to make it 32 bit 
            while (hashtext.length() < 32) { 
                hashtext = "0" + hashtext; 
            } 
  
            // return the HashText 
            return hashtext; 
		}

		// For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e); 
        } 
	}
}