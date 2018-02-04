package com.cahtegal.jadis.admin;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.cahtegal.jadis.R;
import com.cahtegal.jadis.adapter.JadwalAdapter;
import com.cahtegal.jadis.adapter.JadwalNoVerifAdapter;
import com.cahtegal.jadis.model.modMajlis;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class JadwalNoVerif extends AppCompatActivity {

    public static RecyclerView rvMajlis;
    private SwipeRefreshLayout swipJadwal;
    private JadwalNoVerifAdapter jadwalAdapter;
    private LinearLayoutManager llm;
    public static boolean isLoading = false, isLastPage = false;
    FirebaseFirestore db;
    ArrayList<modMajlis> arrayMajelis = new ArrayList<>();
    ProgressDialog pLoading;
    RelativeLayout layNoData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jadwal_no_verif);
        deklarasi();
        action();
        getData();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Verifikasi Jadis");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

    public void deklarasi() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        layNoData = findViewById(R.id.layNoData);
        layNoData.setVisibility(View.GONE);
        rvMajlis = findViewById(R.id.rvListMajelis);
        swipJadwal = findViewById(R.id.swipJadwal);
    }

    public void action() {
        swipJadwal.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipJadwal.setRefreshing(false);
                if (!arrayMajelis.isEmpty() || arrayMajelis.size() > 0) {
                    arrayMajelis.clear();
                }
                getData();
            }
        });

        llm = new LinearLayoutManager(this);
        rvMajlis.setLayoutManager(llm);
        rvMajlis.setHasFixedSize(true);
        rvMajlis.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = llm.getChildCount();
                int totalItemCount = llm.getItemCount();
                int firstVisibleItemPosition = llm.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if (dy > 0) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0 && totalItemCount >= 10) {
                            isLastPage = true;
                            /*if (offset < totalDeposit) {
                                tampilDeposit();
                            }*/
                        }
                    }
                }
            }
        });
    }
    private void getData() {
        pLoading = new ProgressDialog(this);
        pLoading.setTitle("Memuat data ...");
        pLoading.setCancelable(true);
        pLoading.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                db.collection("jadis").whereEqualTo("status", "off")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    return;
                                }

                                if (querySnapshot != null) {
                                    for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                                        if (change.getType() == DocumentChange.Type.ADDED) {
                                            modMajlis mm = new modMajlis();
                                            mm.setDocument(String.valueOf(change.getDocument().getId()));
                                            mm.setKategori(String.valueOf(change.getDocument().get("kategori")));
                                            mm.setProvinsi(String.valueOf(change.getDocument().get("provinsi")));
                                            mm.setImage(String.valueOf(change.getDocument().getData().get("gambar")));
                                            mm.setKeterangan(String.valueOf(change.getDocument().getData().get("keterangan")));
                                            mm.setMajelis(String.valueOf(change.getDocument().getData().get("majelis")));
                                            mm.setPenceramah(String.valueOf(change.getDocument().getData().get("penceramah")));
                                            mm.setPukul(String.valueOf(change.getDocument().getData().get("pukul")));
                                            mm.setTanggal(String.valueOf(change.getDocument().getData().get("tanggal")));
                                            mm.setTema_acara(String.valueOf(change.getDocument().getData().get("tema_acara")));
                                            mm.setTempat(String.valueOf(change.getDocument().getData().get("tempat")));
                                            arrayMajelis.add(mm);
                                            jadwalAdapter = new JadwalNoVerifAdapter(JadwalNoVerif.this, arrayMajelis);
                                            rvMajlis.setAdapter(jadwalAdapter);
                                            jadwalAdapter.notifyDataSetChanged();
                                            pLoading.dismiss();
                                            layNoData.setVisibility(View.GONE);
                                            swipJadwal.setVisibility(View.VISIBLE);
                                        }

                                        String source = querySnapshot.getMetadata().isFromCache() ?
                                                "local cache" : "server";
                                        Log.d("Tag ", "Data fetched from " + source);
                                    }
                                }

                            }
                        });
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pLoading.isShowing()) {
                    pLoading.dismiss();
                    layNoData.setVisibility(View.VISIBLE);
                    swipJadwal.setVisibility(View.GONE);
                }
            }
        },5000);
    }
}
