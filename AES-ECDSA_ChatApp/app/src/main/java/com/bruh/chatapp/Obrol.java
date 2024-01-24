package com.bruh.chatapp;

import android.app.ActionBar;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bruh.chatapp.ECDSA.ECDSA;
import com.bruh.chatapp.ECDSA.Point;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import static com.bruh.chatapp.Pesan.g;
import static com.bruh.chatapp.Pesan.publickey;

public class Obrol extends AppCompatActivity {
    String pengirim;
    String penerima;
    LinearLayout layout;
    RelativeLayout layout2;
    ImageButton kirim;
    EditText ketikPesan;
    ScrollView scrollView;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    aesContoh aesContoh;
    ECDSA ecdsa;
    DatabaseHelper DBHelper;
    FirebaseUser User;
    String Sign = new String();
    byte[] genkunci;
    DHUtil DiffHellman;
    BigInteger privatekey;
    BigInteger G;
    String primeroot;
    String prime_root;
    BigInteger kuncipublik;
    BigInteger Gg;
    String pengguna;
    byte[] pesan;
    int status_pertama;
    BigInteger kunci_privat;
    byte[] kunciEnkripsiblmdipotong;
    byte[] kunciEnkripsisdhdipotong;

    final String FILE_NAME = new String(Pesan.Nama + "_" + Pesan.chatWith);

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obrol); //Ambil tampilan obrolan

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layout = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        kirim = findViewById(R.id.sendButton);
        scrollView = findViewById(R.id.scrollView);
        ketikPesan = findViewById(R.id.messageArea);


        firebaseDatabase = FirebaseDatabase.getInstance();
        DBHelper = new DatabaseHelper(this);
        final Anggota anggota = new Anggota();
        ecdsa = new ECDSA();
        SecureRandom rnd = new SecureRandom();

        User = FirebaseAuth.getInstance().getCurrentUser(); //Ambil pengguna yang masuk
        final String loggedInUid = User.getUid();


        final DatabaseReference reference1 = firebaseDatabase.getReference("Message/"+ Pesan.Nama+"_"+Pesan.chatWith); //Inisiasi referensi database untuk menampung obrolan
        final DatabaseReference reference2 = firebaseDatabase.getReference("Message/"+ Pesan.chatWith+"_"+Pesan.Nama); //Inisiasi referensi database untuk menampung obrolan




        //Ambil referensi dari database
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Cek apakah random prime dan prime root ada
                if((!dataSnapshot.child("Random prime").exists()) && (!dataSnapshot.child("Prime root").exists())  && (!dataSnapshot.child("Publik Key 1").exists())){
                    Map<String,String> messageTextBody = new HashMap<String,String>();
                    DHUtil.setQ(); //Buat Random Prime
                    BigInteger randomprime;
                    randomprime = DHUtil.getQ(); //Ambil Random Prime
                    Log.d("random prime:", randomprime.toString());
                    DHUtil.setG(); //Buat Prime Root
                    G = DHUtil.getG(); //Ambil Prime root
                    Log.d("primitive root:", G.toString());

                    //Jika loggedinUid tidak ada di database, buat kunci privat
                    if(!DBHelper.checkUid(loggedInUid)) {
                        kunci_privat = DHUtil.chooseNumber(); //generasi kunci User
                        DBHelper.insertDatalogin(loggedInUid,kunci_privat.toString());

                    }
                    String decodedprivatekey = DBHelper.getUidKey(loggedInUid);//Ambil kunci privat dari database lokal
                    privatekey = new BigInteger(decodedprivatekey);
                    Log.d("kunci privat 1: ", privatekey.toString());
                        //Buat kunci publik
                        publickey = DHUtil.pubkey(privatekey,G,randomprime).toString();
                        Log.d("kunci publik user 1:", publickey.toString());

                    //Masukkan kunci publik user 1, random prime dan primitive root ke database
                    messageTextBody.put("Publik Key 1", publickey);
                    messageTextBody.put("Random Prime", randomprime.toString());
                    messageTextBody.put("Prime root", G.toString());
                    status_pertama = 1; //Beri status user pertama
                    reference1.setValue(messageTextBody);
                    reference2.setValue(messageTextBody);
                }else if(!dataSnapshot.child("Publik Key 2").exists()){ //Cek jika di database tidak ada kunci publik user kedua
                    Map<String,String> messageTextBody = (Map<String,String>) dataSnapshot.getValue();

                    BigInteger randomprime;
                    String prime = messageTextBody.get("Random Prime").toString(); //Ambil Random Prime dari database
                   DHUtil.setQQ(prime); //Buat random prime
                    String prime_root = messageTextBody.get("Prime root").toString(); //Ambil Prime root dari database
                    G = new BigInteger(prime_root); // Konversi string primitive root menjadi database
                    if(!DBHelper.checkUid(loggedInUid)) { //Jika loggedinUid tidak ada di database, buat kunci privat
                        kunci_privat = DHUtil.chooseNumber(); //generasi kunci User
                        DBHelper.insertDatalogin(loggedInUid,kunci_privat.toString());
                    }
                    String decodedprivatekey = DBHelper.getUidKey(loggedInUid);//Ambil kunci privat dari database lokal
                    privatekey = new BigInteger(decodedprivatekey);
                    Log.d("kunci privat user 2", privatekey.toString());
                    randomprime = new BigInteger(prime); //Ambil nilai Random Prime
                    publickey = DHUtil.pubkey(privatekey,G,randomprime).toString(); //Generasi Publik Key
                    Log.d("kunci publik user 2", privatekey.toString());
                    messageTextBody.put("Publik Key 2", publickey); //Masukkan ke database Publik Key user 2
                    status_pertama = 2; //Beri status user 2
                    reference1.setValue(messageTextBody);
                    reference2.setValue(messageTextBody);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Inisiasi kunci private ECDSA
        final BigInteger dA = BigInteger.probablePrime(192,new Random());
        ecdsa.setdA(dA);


        //Ketika mengirim pesan lakukan (Enkripsi Pesan)
        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String,String> map = (Map<String,String>) dataSnapshot.getValue();
                            if(status_pertama == 1){ //Jika status sebagai user pertama
                                publickey = map.get("Publik Key 2").toString(); //Ambil Publik Key User 2
                            }else if(status_pertama == 2){ //Jika status sebagai user kedua
                                publickey = map.get("Publik Key 1").toString(); //Ambil Publik Key User 1
                            }
                            BigInteger randomprime;
                            String prime = map.get("Random Prime").toString();
                            randomprime = new BigInteger(prime);
                            kuncipublik = new BigInteger(publickey); //Ubah Public Key menjadi BigInteger
                            String decodedprivatekey = DBHelper.getUidKey(loggedInUid);//Ambil kunci privat dari database lokal
                            privatekey = new BigInteger(decodedprivatekey);
                            genkunci = DHUtil.finalKey(kuncipublik,privatekey,randomprime).toByteArray(); //Generasi kunci untuk enkripsi pesan


                            try{
                               kunciEnkripsiblmdipotong = getSHA(new String(genkunci));

                            }catch (NoSuchAlgorithmException e){
                                System.out.println("Exception thrown for incorrect algorithm: " + e);
                            }

                            kunciEnkripsisdhdipotong = Arrays.copyOfRange(kunciEnkripsiblmdipotong,0,15);


                            String isiPesan = ketikPesan.getText().toString(); //Ambil isi pesan
                            Log.d("pesan yang akan dienkripsi", isiPesan);
                            byte[]  pesan = isiPesan.getBytes(); //Ubah isi pesan menjadi bit
                            Log.d("Kunci Enkripsi:", Arrays.toString(kunciEnkripsisdhdipotong));
                            byte[] encrypt = Base64.getEncoder().encode(aesContoh.Encrypt(pesan, kunciEnkripsisdhdipotong)); //Lakukan enkripsi dengan AES
                            final String enkripsi = new String(encrypt);
                            Log.d("hasil enkripsi", enkripsi);

                            Sign = ecdsa.SignMessage(isiPesan); //Beri tanda tangan pada pesan yang dikirim
                            Log.d("Penanda pesan", Sign);

                            //Jika isi pesan tidak kosong dan pengecekan tanda tangan benar
                            if((!isiPesan.equals("")) && (ecdsa.MessageVerify(isiPesan,Sign))){
                                Map<String,String> messageTextBody = new HashMap<String,String>();
                                //Masukkan isi pesan, pengirim, dan signature ke database
                                messageTextBody.put("Pesan",enkripsi);
                                messageTextBody.put("Pengguna", Pesan.Nama);
                                messageTextBody.put("Signature", Sign);
                                reference1.push().setValue(messageTextBody);
                                reference2.push().setValue(messageTextBody);
                                ketikPesan.setText("");


                            }else{ //Jika tidak, munculkan toast anda bukan pengirim
                                Toast.makeText(getApplicationContext(),"You are not the sender",Toast.LENGTH_SHORT).show();
                            }
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        //Ambil referensi dari database (Ambil Pesan dan Lakukan Proses Dekripsi
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { //Tiap ada data baru

                //Jika data pesan terdeteksi
                if(dataSnapshot.child("Pesan").exists()){
                    Map<String,String> map = (Map<String,String>) dataSnapshot.getValue();
                    String message = map.get("Pesan").toString();
                    pesan = Base64.getDecoder().decode(message); //Ambil data pesan dari database
                    pengguna = map.get("Pengguna").toString(); //Ambil data pengguna dari database
                    Point QA = ecdsa.getQA();

                    //Decrypt AES
                    reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Map<String,String> map = (Map<String,String>) dataSnapshot.getValue();
                            if(status_pertama == 1){ //Jika status sebagai user pertama
                                publickey = map.get("Publik Key 2").toString(); //Ambil Publik Key User 2
                            }else if(status_pertama == 2){ //Jika status sebagai user kedua
                                publickey = map.get("Publik Key 1").toString(); //Ambil Publik Key User 1
                            }

                            kuncipublik = new BigInteger(publickey.trim().replaceAll("\"",""));
                            Log.d("kuncipublik decrypt:", kuncipublik.toString());
                            BigInteger randomprime;
                            String prime = map.get("Random Prime").toString();
                            randomprime = new BigInteger(prime);
                            Log.d("random primitive decrypt:", randomprime.toString());

                            String decodedprivatekey = DBHelper.getUidKey(loggedInUid);//Ambil kunci privat dari database lokal
                            privatekey = new BigInteger(decodedprivatekey);
                            Log.d("privatekey decrypt:", privatekey.toString());
                            genkunci = DHUtil.finalKey(kuncipublik,privatekey,randomprime).toByteArray(); //Generasi kunci


                            try{
                                kunciEnkripsiblmdipotong = getSHA(new String(genkunci));

                            }catch (NoSuchAlgorithmException e){
                                System.out.println("Exception thrown for incorrect algorithm: " + e);
                            }

                            kunciEnkripsisdhdipotong = Arrays.copyOfRange(kunciEnkripsiblmdipotong,0,15);


                            Log.d("Kunci Decrypt:", Arrays.toString(kunciEnkripsisdhdipotong));
                            byte[] decrypt = aesContoh.Decrypt(pesan, kunciEnkripsisdhdipotong); //Lakukan dekripsi
                            String dekripsi = new String(decrypt);
                            Log.d("Hasil Dekripsi", new String(decrypt));
                            //Tampilkan pesan sebagai pengirim
                            if(pengguna.equals(Pesan.Nama)){
                                String out;
                                saveMessage(dekripsi);
                                out = dekripsi;
                                addMessageBox(out,1);
                            }else{ //Tampilkan pesan sebagai penerima
                                String out;
                                saveMessage(dekripsi);
                                out = dekripsi;
                                addMessageBox(out,2);
                            }

                            }

                    //    }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //Tampilkan pesan
    public void addMessageBox(String pesan, int type){
        TextView textView = new TextView(Obrol.this);
        textView.setText(pesan);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 7.0f;

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public void saveMessage(String pesan){
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(pesan.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public String loadMessage(){
        FileInputStream fis = null;
        String out = new String();
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while((text = br.readLine()) != null){
                sb.append(text).append("\n");
            }
            out = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return out;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }


}

