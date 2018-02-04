package com.cahtegal.jadis.admin;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cahtegal.jadis.R;
import com.cahtegal.jadis.activity.DetailJadwal;
import com.cahtegal.jadis.adapter.JadwalNoVerifAdapter;
import com.cahtegal.jadis.model.modMajlis;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

public class VerifDetail extends AppCompatActivity {

    ImageView imgMajlis;
    TextView teksNamaMjlis, teksPenceramah, teksPukul, teksTgl, teksTema, teksTempat, teksKeterangan, teksKategori, teksProvinsi;
    public static String linkMajlis = "", namaMajlis = "", pukul = "", tgl = "", tema = "", tempat = "", keterangan = "", penceramah = "",
            kategori = "", provinsi = "", document = "";
    Button btnNotVerif, btnVerif;
    ProgressDialog pLoading;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verif_detail);
        deklarasi();
        action();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Detail Verifikasi Majelis");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void deklarasi() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);
        btnNotVerif = findViewById(R.id.btnNotApprove);
        btnVerif = findViewById(R.id.btnSetujui);
        teksKategori = findViewById(R.id.teksKategori);
        teksProvinsi = findViewById(R.id.teksProvinsi);
        imgMajlis = findViewById(R.id.imgMajelis);
        teksKeterangan = findViewById(R.id.teksKeterangan);
        teksNamaMjlis = findViewById(R.id.teksNamaMajlis);
        teksPenceramah = findViewById(R.id.teksPenceramah);
        teksPukul = findViewById(R.id.teksPukul);
        teksTema = findViewById(R.id.teksTema);
        teksTempat = findViewById(R.id.teksTempat);
        teksTgl = findViewById(R.id.teksTgl);
    }

    private void action() {
        Glide.with(VerifDetail.this).load(linkMajlis).into(imgMajlis);
        teksTgl.setText(tgl);
        teksTempat.setText(tempat);
        teksTema.setText(tema);
        teksPukul.setText(pukul);
        teksPenceramah.setText(penceramah);
        teksNamaMjlis.setText(namaMajlis);
        teksKeterangan.setText(keterangan);
        teksProvinsi.setText(provinsi);
        teksKategori.setText(kategori);
        btnVerif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setujui();
            }
        });

        btnNotVerif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tidakdisetujui();
            }
        });
    }

    private void setujui() {
        pLoading = new ProgressDialog(this);
        pLoading.setTitle("Memuat data ...");
        pLoading.setCancelable(true);
        pLoading.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                db.collection("jadis").document(document).update("status", "on")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pLoading.dismiss();
                                Toast.makeText(VerifDetail.this,"Berhasil Terverifikasi",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pLoading.dismiss();
                                Toast.makeText(VerifDetail.this,"Gagal Terverifikasi",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }, 1000);
    }

    private void tidakdisetujui() {
        pLoading = new ProgressDialog(this);
        pLoading.setTitle("Memuat data ...");
        pLoading.setCancelable(true);
        pLoading.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                db.collection("jadis").document(document).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pLoading.dismiss();
                                Toast.makeText(VerifDetail.this,"Berhasil Hapus Data",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pLoading.dismiss();
                                Toast.makeText(VerifDetail.this,"Gagal Hapus Data",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }, 1000);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
