package com.bruh.chatapp;
import android.util.Log;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;

public class DHUtil{

    SecureRandom rnd = new SecureRandom(); //Secure random for generator
    private static BigInteger q; //Public Prime Modulus number
    private static BigInteger g; //Public Primitive Root
    private static BigInteger pubkey; //Public Key
    private static BigInteger privkey; //Private key
    private static BigInteger numReceive; //Received public key
    private static BigInteger sharedkey; //Shared key to encrypt data
    private int bitlength;

     private static ArrayList<BigInteger> factors = new ArrayList<>();
    private static ArrayList<Integer> primeFactors;
    public  DHUtil(){ //Inisialisasi

    }

    public DHUtil(BigInteger q, BigInteger g, BigInteger pubkey){
        this.q = q;
        this.g = g;
        this.pubkey = pubkey;
    }

    //Membuat bilangan acak untuk modulo q
    static boolean isPrime(int n)
    {
        // Corner cases
        if (n <= 1)
        {
            return false;
        }
        if (n <= 3)
        {
            return true;
        }

        // This is checked so that we can skip
        // middle five numbers in below loop
        if (n % 2 == 0 || n % 3 == 0)
        {
            return false;
        }

        for (int i = 5; i * i <= n; i = i + 6)
        {
            if (n % i == 0 || n % (i + 2) == 0)
            {
                return false;
            }
        }

        return true;
    }

    public static void setQ(){
        BigInteger random = BigInteger.probablePrime(1024,new SecureRandom());
        if(isPrime(random.intValue())){
            q = random;
        }else {
            setQ();
        }
    }

    public static void setQQ(String Qq) {
        q = new BigInteger(Qq);
    }


    //Ambil nilai q


    public static BigInteger getQ() {
        return q;
    }

    //Membuat prmitive root dari q


//    public static BigInteger sqrt(BigInteger x) {
//        BigInteger div = BigInteger.ZERO.setBit(x.bitLength()/2);
//        BigInteger div2 = div;
//        // Loop until we hit the same value twice in a row, or wind
//        // up alternating.
//        for(;;) {
//            BigInteger y = div.add(x.divide(div)).shiftRight(1);
//            if (y.equals(div) || y.equals(div2))
//                return y;
//            div2 = div;
//            div = y;
//        }
//    }
//
//
//
//    static BigInteger power(BigInteger x, BigInteger y, BigInteger p)
//    {
//        BigInteger res = BigInteger.ONE;     // Initialize result
//
//        x = x.mod(p); // Update x if it is more than or
//        // equal to p
//
//        while (y.compareTo(BigInteger.ZERO) > 0)
//        {
//            // If y is odd, multiply x with result
//            if (y.mod(BigInteger.valueOf(2)).compareTo(BigInteger.ONE) == 0 )
//            {
//                res = (res.multiply(x)).mod(p);
//            }
//
//            // y must be even now
//            y = y.shiftRight(1); // y = y/2
//            x = (x.multiply(x)).mod(p);
//        }
//        return res;
//    }
//
//    // Utility function to store prime factors of a number
//    static void findPrimefactors(HashSet<BigInteger> s, BigInteger n)
//    {
//        // Print the number of 2s that divide n
//        while (n.mod(BigInteger.valueOf(2)) == BigInteger.ZERO)
//        {
//            s.add(BigInteger.valueOf(2));
//            n = n.divide(BigInteger.valueOf(2));
//        }
//
//        // n must be odd at this point. So we can skip
//        // one element (Note i = i +2)
//        for (int i = 3; BigInteger.valueOf(i).compareTo(sqrt(n)) <= 0; i = i + 2)
//        {
//            // While i divides n, print i and divide n
//            while (n.mod(BigInteger.valueOf(i)) == BigInteger.ZERO)
//            {
//                s.add(BigInteger.valueOf(i));
//                n = n.divide(BigInteger.valueOf(i));
//            }
//        }
//
//        // This condition is to handle the case when
//        // n is a prime number greater than 2
//        if (n.compareTo(BigInteger.valueOf(2)) > 0)
//        {
//            s.add(n);
//        }
//    }
//
//    // Function to find smallest primitive root of n
//    static BigInteger findPrimitive(BigInteger q)
//    {
//        HashSet<BigInteger> s = new HashSet<BigInteger>();
//
//
//        // Find value of Euler Totient function of n
//        // Since n is a prime number, the value of Euler
//        // Totient function is n-1 as there are n-1
//        // relatively prime numbers.
//        BigInteger phi = q.subtract(BigInteger.ONE);
//
//        // Find prime factors of phi and store in a set
//        findPrimefactors(s, phi);
//
//        // Check for every number from 2 to phi
//        for (int r = 2; BigInteger.valueOf(r).compareTo(phi) <= 0; r++)
//        {
//            // Iterate through all prime factors of phi.
//            // and check if we found a power with value 1
//            boolean flag = false;
//            for (BigInteger a : s)
//            {
//
//                // Check if r^((phi)/primefactors) mod n
//                // is 1 or not
//                if (power(BigInteger.valueOf(r), phi.divide(a), q).compareTo(BigInteger.ONE) == 0)
//                {
//                    flag = true;
//                    break;
//                }
//            }
//
//            // If there was no power with value 1.
//            if (flag == false)
//            {
//                return BigInteger.valueOf(r);
//            }
//        }
//
//        // If no primitive root found
//        return BigInteger.valueOf(1);
//    }

    //Generasi nilai g
    public static void setG(){
        BigInteger random = BigInteger.probablePrime(1024,new SecureRandom());
        if(isPrime(random.intValue()) && testPrimitiveRoot(random,q)){
            g = random;
        }else {
            setG();
        }

       // g = getPrimitiveRoot(q);
    }

    public static BigInteger getPrimitiveRoot(BigInteger p)
    {
        BigInteger a, q = p.subtract(BigInteger.ONE); // q= etf
        // mencari faktor-faktor dari p-1 aka etf
        factors.add(BigInteger.ONE);
        for(int i=0;i<primeFactors.size();++i) {
            BigInteger prime = BigInteger.valueOf(primeFactors.get(i));
            BigInteger temp = q, pembagi = prime;
            if (prime.multiply(prime).compareTo(temp) > 0) break; // jika p*p > q, berarti sudah tidak memenuhi
            while(temp.mod(prime).compareTo(BigInteger.ZERO) == 0) { // selama temp%p == 0
                temp = temp.divide(prime);
                factors.add(temp); // hasil bagi = faktornya
                factors.add(pembagi);
                pembagi = pembagi.multiply(pembagi);
            }
        }
        for(a=BigInteger.ONE;a.compareTo(p) < 0;a=a.add(BigInteger.ONE))
        {
            if(testPrimitiveRoot(a,p))
                break;
        }
        factors.clear();
        return a;
    }

        private static boolean testPrimitiveRoot(BigInteger a, BigInteger p)
    {
        BigInteger val;
        for(int i=0;i<factors.size();++i) {
            val = a.modPow(factors.get(i), p);
            if (val.compareTo(BigInteger.ONE) == 0) return false;
        }
        return true;
    }



    //Ambil nilai G
    public static BigInteger getG(){
        return g;
    }

    //Generate public key user
    public static BigInteger pubkey(BigInteger privkey, BigInteger g, BigInteger q){
        Log.d("primr00t", g.toString());
        Log.d("priv key", privkey.toString());
        Log.d("q", q.toString());
        pubkey = g.modPow(privkey,q);
        return  pubkey;
    }

    //Generate private key user
    public static BigInteger chooseNumber(){ //Generate random p
        SecureRandom rand = new SecureRandom();
        do{
            privkey = new BigInteger(256,rand);
        }while((privkey.compareTo(q) >=0));

        return privkey;
    }

    //Retrieve public key from receiver
    public static void SideKey(BigInteger sidekey){ //Get shared public key
        numReceive = sidekey;
    }

     public static BigInteger finalKey(BigInteger numReceive, BigInteger privkey, BigInteger q){ //Generate shared key
          sharedkey = numReceive.modPow(privkey,q);
          return sharedkey;
      }


    /**
     * Mengisi primeFactors dengan bilangan prima <= 1000000
     */
    public static void getPrimeFactors() { // sieve of eratosthenes
        primeFactors = new ArrayList<>();
        boolean flag[] = new boolean[1000001];
        for(int i=2;i<=1000000;++i)
            if (flag[i] == false) {
                primeFactors.add(i);
                for(int j=i+i;j<=1000000;j+=i)
                    flag[j] = true;
            }
    }

    //    private BigInteger p; //private key
//    private BigInteger g; //primitive root of p
//    private int bitlength; //bit length
//    private BigInteger chosen;
//    private BigInteger calc;
//    private BigInteger numReceive;
//    private BigInteger finalkey;
//
//    public DHUtil(){ //Initialization
//
//    }
//

//
//    public void chooseNumber(){ //Generate random p
//        SecureRandom rand = new SecureRandom();
//        do{
//            chosen = new BigInteger(bitlength,rand);
//        }while(chosen.compareTo(p) >=0);
//    }
//
//    public void valuetosend(){ //Generate public key sender
//        calc = g.modPow(chosen, p);
//    }
//
//    public void SideKey(BigInteger sidekey){ //Get shared public key
//        numReceive = sidekey;
//    }
//
//    public void finalKey(){ //Generate shared key
//        finalkey = numReceive.modPow(chosen,p);
//    }
//
//    public void setP(BigInteger p) {
//        this.p = p;
//    }
//
//    public BigInteger getP() {
//        return p;
//    }
//
//    public void setG(BigInteger g) {
//        this.g = g;
//    }
//
//    public BigInteger getG() {
//        return g;
//    }
//
//    public void setBitlength(int bitlength) {
//        this.bitlength = bitlength;
//    }
//
//    public int getBitlength() {
//        return bitlength;
//    }
//
//    public BigInteger getChosen() {
//        return chosen;
//    }
//
//    public void setChosen(BigInteger chosen) {
//        this.chosen = chosen;
//    }
//
//    public BigInteger getCalc() {
//        return calc;
//    }
//
//    public void setCalc(BigInteger calc) {
//        this.calc = calc;
//    }
//
//    public BigInteger getNumReceive() {
//        return numReceive;
//    }
//
//    public void setNumReceive(BigInteger numReceive) {
//        this.numReceive = numReceive;
//    }
//
//    public BigInteger getFinalkey() {
//        return finalkey;
//    }
//
//    public void setFinalkey(BigInteger finalkey) {
//        this.finalkey = finalkey;
//    }






}