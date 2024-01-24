package com.bruh.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Kontak extends AppCompatActivity {
    ListView daftarKontak;
    TextView nullKontak;
    FirebaseUser User;
    ArrayList<String> AL = new ArrayList<>();
    int totKontak = 0;
    ProgressDialog pd;
    DatabaseReference getDatabase;
//    VolleyError volleyError;
    DatabaseHelper DBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kontak); //Mengambil tambilan kontak
        DBHelper = new DatabaseHelper(this);
        daftarKontak = (ListView) findViewById(R.id.usersList); //Mengambil listview untuk daftar kontak
        nullKontak = (TextView)findViewById(R.id.noUsersText); //mengambil textview tidak ada pengguna
        pd = new ProgressDialog(Kontak.this);
        pd.setMessage("Memuat kontak");
        pd.show();
        User = FirebaseAuth.getInstance().getCurrentUser(); //Ambil pengguna yang masuk
        DBHelper = new DatabaseHelper(this);
        final String loggedInUid = User.getUid();



        getDatabase = FirebaseDatabase.getInstance().getReference(); //Ambil referensi database

        getDatabase.child("Users").addValueEventListener(new ValueEventListener() { //Tampilkan semua pengguna
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> snapshotIterable = dataSnapshot.getChildren();
                Iterator<DataSnapshot> i = snapshotIterable.iterator();
                String key = "";
                String currentUser = dataSnapshot.child(loggedInUid).child("Nama").getValue(String.class);
                Log.d("currentUser", currentUser);
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    key = i.next().toString();
                    String tempNama = data.child("Nama").getValue(String.class);

                    if(!tempNama.equals(currentUser)){
                        AL.add(tempNama);
                        totKontak++;
                    }

                }

                if(totKontak <=1){
                    nullKontak.setVisibility(View.VISIBLE);
                    daftarKontak.setVisibility(View.GONE);
                }else{
                    nullKontak.setVisibility(View.GONE);
                    daftarKontak.setVisibility(View.VISIBLE);
                    ArrayAdapter adapter = new ArrayAdapter(Kontak.this, android.R.layout.simple_list_item_1,AL);
                    daftarKontak.setAdapter(adapter);
                }

                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        daftarKontak.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { //Ketika kontak ditekan, lakukan aktivitas obrol
                Pesan.chatWith = AL.get(i);
                startActivity(new Intent(Kontak.this, Obrol.class));
            }
        });
   }

}
