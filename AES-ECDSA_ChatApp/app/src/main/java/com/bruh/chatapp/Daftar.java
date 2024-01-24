package com.bruh.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.google.firebase.FirebaseApp.getInstance;


public class Daftar extends AppCompatActivity {
    EditText nama, email, password; //tempat memasukkan nama, email,password
    Button tombolDaftar; //tombol daftar
    String user, pass, name; //isi nama,email, password
    TextView masuk; //kembali ke menu login
    FirebaseAuth auth; //Autentikas
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase; //Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar); //mengatur tampilan daftar
        firebaseDatabase = FirebaseDatabase.getInstance(); //ambil instance database
        if((!FirebaseApp.getApps(this).isEmpty()) && (firebaseDatabase == null) ) {
            firebaseDatabase.setPersistenceEnabled(true);
            firebaseDatabase.getReference("Users");
        }

        nama = (EditText)findViewById(R.id.nama); //mengambil tampilan EditText nama
        email = (EditText)findViewById(R.id.email); //mengambil tampilan EditText email
        password = (EditText)findViewById(R.id.password); //mengambil tampilan EditText password
        tombolDaftar = (Button)findViewById(R.id.registerButton); //mengambil tampilan tombol daftar
        masuk = (TextView)findViewById(R.id.login); //mengambil tampilan textview login

        auth = FirebaseAuth.getInstance(); //instansiasi autentikasi firebase

        masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //kembali ke menu masuk
                startActivity(new Intent(Daftar.this, Masuk.class));
            }
        });

        tombolDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //lakukan pendaftaran
                user = email.getText().toString();
                pass = password.getText().toString();
                name = nama.getText().toString();

                if(!isValidEmail(user)){
                    email.setError("Username hanya boleh berbentuk email");
                }
                else if(pass.equals("")){
                    password.setError("Password tidak boleh kosong");
                }
                else if(pass.length()<5){
                    password.setError("Panjang password tidak boleh lebih kecil dari 5 karakter");
                }else{
                   //create user
                    auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(Daftar.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(Daftar.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(Daftar.this, "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Anggota informasi = new Anggota(
                                       user,
                                        name
                                );

                                firebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(informasi)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                               Toast.makeText(Daftar.this,"Registrasi Berhasil",Toast.LENGTH_SHORT);
                                               startActivity(new Intent(getApplicationContext(),Daftar.class));
                                            }
                                        });

                            }
                        }
                    });

                }
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}
