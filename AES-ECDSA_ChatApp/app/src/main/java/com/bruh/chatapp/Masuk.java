package com.bruh.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Masuk extends AppCompatActivity {
    TextView penggunaTerdaftar;
    EditText username, password;
    Button tombolMasuk;
    String user, pass,uid;
    FirebaseUser authUser;
    private FirebaseAuth auth;
    FirebaseDatabase getDatabase;
    DatabaseReference reference;
    DataSnapshot dataSnapshot;
    public String tempNama;
    Pesan Pesan;
    DatabaseHelper DBHelper;
    byte[] genkunci;
   //byte[] genkunci = BigInteger.ZERO;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.masuk);
        DBHelper = new DatabaseHelper(this);
        penggunaTerdaftar = findViewById(R.id.register);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        tombolMasuk = findViewById(R.id.loginButton);
        auth = FirebaseAuth.getInstance();
        getDatabase = FirebaseDatabase.getInstance();

        final Anggota anggota = new Anggota();

        penggunaTerdaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Masuk.this, Daftar.class));
            }
        });

        tombolMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = username.getText().toString();
                pass = password.getText().toString();
                uid = "";

                if(user.equals("")){
                    username.setError("Username tidak boleh kosong");
                }
                else if(pass.equals("")){
                    password.setError("Password tidak boleh kosong");
                }
                else if(!isValidEmail(user)){
                    username.setError("Hanya alamat email yang diperbolehkan");
                }else{
                    auth.signInWithEmailAndPassword(user,pass).addOnCompleteListener(Masuk.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                uid = auth.getCurrentUser().getUid();
                                reference = getDatabase.getReference();
                                DatabaseReference childReference = reference.child("Users").child(uid);
                                childReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Pesan = dataSnapshot.getValue(Pesan.class);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }


                                });
                                Toast.makeText(Masuk.this,"Berhasil masuk",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Masuk.this, Kontak.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(Masuk.this,"Gagal Masuk",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}
