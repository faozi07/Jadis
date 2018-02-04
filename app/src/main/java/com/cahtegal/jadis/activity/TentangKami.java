package com.cahtegal.jadis.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cahtegal.jadis.R;
import com.cahtegal.jadis.admin.JadwalNoVerif;

public class TentangKami extends AppCompatActivity {

    int klik = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tentang_kami);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tentang Kami");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ImageView imgJadis = findViewById(R.id.imgJadis);
        imgJadis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                klik = klik + 1;
                if (klik == 15) {
                    showDialogPass();
                    klik = 0;
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        klik = 0;
    }

    private void showDialogPass() {
        LayoutInflater inflater;
        View dialog_layout;
        AlertDialog.Builder dialogFilter;
        final AlertDialog theDialog;

        inflater = LayoutInflater.from(TentangKami.this);
        dialog_layout = inflater.inflate(R.layout.dialog_password, null);
        dialogFilter = new AlertDialog.Builder(TentangKami.this);
        dialogFilter.setView(dialog_layout);
        dialogFilter.setCancelable(true);
        theDialog = dialogFilter.create();

        final EditText editPass = dialog_layout.findViewById(R.id.editPass);
        final Button btnOk = dialog_layout.findViewById(R.id.btnPass);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editPass.getText().toString().equals("110199")) {
                    startActivity(new Intent(TentangKami.this, JadwalNoVerif.class));
                    theDialog.dismiss();
                }
            }
        });

        if (!TentangKami.this.isFinishing()) {
            theDialog.show();
        }
    }
}
