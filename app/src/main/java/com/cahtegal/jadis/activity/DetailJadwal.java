package com.cahtegal.jadis.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cahtegal.jadis.R;

public class DetailJadwal extends AppCompatActivity {

    ImageView imgMajlis;
    TextView teksNamaMjlis,teksPenceramah,teksPukul,teksTgl,teksTema,teksTempat,teksKeterangan,teksKategori,teksProvinsi;
    public static String linkMajlis = "",namaMajlis = "", pukul = "", tgl = "",tema = "",tempat = "",keterangan = "", penceramah = "",
    kategori = "", provinsi = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_jadwal);
        deklarasi();
        action();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Detail Jadwal Majelis");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void deklarasi() {
        imgMajlis = findViewById(R.id.imgMajelis);
        teksKeterangan = findViewById(R.id.teksKeterangan);
        teksNamaMjlis = findViewById(R.id.teksNamaMajlis);
        teksPenceramah = findViewById(R.id.teksPenceramah);
        teksPukul = findViewById(R.id.teksPukul);
        teksTema = findViewById(R.id.teksTema);
        teksTempat = findViewById(R.id.teksTempat);
        teksTgl = findViewById(R.id.teksTgl);
        teksKategori = findViewById(R.id.teksKategori);
        teksProvinsi = findViewById(R.id.teksProvinsi);
    }

    private void action() {
        Glide.with(DetailJadwal.this).load(linkMajlis).into(imgMajlis);
        teksTgl.setText(tgl);
        teksTempat.setText(tempat);
        teksTema.setText(tema);
        teksPukul.setText(pukul);
        teksPenceramah.setText(penceramah);
        teksNamaMjlis.setText(namaMajlis);
        teksKeterangan.setText(keterangan);
        teksProvinsi.setText(provinsi);
        teksKategori.setText(kategori);
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
