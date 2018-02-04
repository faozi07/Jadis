package com.cahtegal.jadis.fragment;

/*
 * Created by regopantes_apps on 31/01/18.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.cahtegal.jadis.R;
import com.cahtegal.jadis.adapter.JadwalAdapter;
import com.cahtegal.jadis.model.modMajlis;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;

public class FragmentUtama extends Fragment {

    public static RecyclerView rvMajlis;
    private SwipeRefreshLayout swipJadwal;
    private JadwalAdapter jadwalAdapter;
    private LinearLayoutManager llm;
    public static boolean isLoading = false, isLastPage = false;
    FirebaseFirestore db;
    ArrayList<modMajlis> arrayMajelis = new ArrayList<>();
    ProgressDialog pLoading;
    private AdView mAdView;
    FloatingActionButton fabFilter;
    String kategori = "";
    String provinsi = "";
    RelativeLayout layNoData;
    String[] arrayProvinsi = {"-- Pilih --", "Bali", "Banten", "Bengkulu", "D.I. Aceh", "D.I. Yogyakarta", "DKI Jakarta", "Gorontalo", "Jambi", "Jawa Barat", "Jawa Tengah",
            "Jawa Timur", "Kalimantan Barat", "Kalimantan Selatan", "Kalimantan Tengah", "Kalimantan Timur", "Kalimantan Utara", "Kepulauan Bangka Belitung",
            "Kepulauan Riau", "Lampung", "Maluku", "Maluku Utara", "Nusa Tenggara Barat", "Nusa Tenggara Timur", "Papua", "Papua Barat", "Riau", "Sulawesi Barat",
            "Sulawesi Selatan", "Sulawesi Tengah", "Sulawesi Tenggara", "Sulawesi Utara", "Sumatera Barat", "Sumatera Selatan", "Sumatera Utara"};
    String[] arrayKategori = {"-- Pilih --", "Harian", "Mingguan", "Bulanan", "Tidak Tentu"};

    ArrayAdapter<String> kategoriAdapter;
    ArrayAdapter<String> provinsiAdapter;
    AlertDialog theDialog = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        View view = inflater.inflate(R.layout.fragment_utama, null);
        deklarasi(view);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }
        });
        action();
        cekKoneksi();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    public void deklarasi(View view) {
        layNoData = view.findViewById(R.id.layNoData);
        layNoData.setVisibility(View.GONE);
        fabFilter = view.findViewById(R.id.fabFilter);
        mAdView = view.findViewById(R.id.adView);
        rvMajlis = view.findViewById(R.id.rvListMajelis);
        swipJadwal = view.findViewById(R.id.swipJadwal);
        if (getActivity() != null) {
            kategoriAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrayKategori);
            provinsiAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrayProvinsi);
        }
    }

    private void cekKoneksi() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Cek koneksi");
        progressDialog.show();
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (conMgr != null) {
            netInfo = conMgr.getActiveNetworkInfo();
        }
        if (netInfo == null) {
            progressDialog.dismiss();
            dialogNoKoneksi();
        } else {
            progressDialog.dismiss();
            getData();
        }
    }

    private void dialogNoKoneksi() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Tidak ada koneksi internet");
        alertDialogBuilder
                .setMessage("Pastikan Anda terhubung dengan koneksi internet untuk bisa mengirim jadwal majelis")
                .setCancelable(false)
                .setPositiveButton("Terhubung kembali", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cekKoneksi();
                    }
                })
                .setNegativeButton("Nanti", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void action() {
        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFilter();
            }
        });

        swipJadwal.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipJadwal.setRefreshing(false);
                if (!arrayMajelis.isEmpty() || arrayMajelis.size() > 0) {
                    arrayMajelis.clear();
                }
                cekKoneksi();
            }
        });

        llm = new LinearLayoutManager(getActivity());
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
        pLoading = new ProgressDialog(getActivity());
        pLoading.setTitle("Memuat data ...");
        pLoading.setMessage("Silahkan tunggu sejenak");
        pLoading.setCancelable(true);
        pLoading.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                db.collection("jadis").whereEqualTo("status", "on")
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
                                            jadwalAdapter = new JadwalAdapter(getActivity(), arrayMajelis);
                                            rvMajlis.setAdapter(jadwalAdapter);
                                            jadwalAdapter.notifyDataSetChanged();
                                            pLoading.dismiss();
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
    }

    private void getDataByProvinsi() {
        pLoading = new ProgressDialog(getActivity());
        pLoading.setTitle("Memuat data ...");
        pLoading.setMessage("Silahkan tunggu sejenak");
        pLoading.setCancelable(true);
        pLoading.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                db.collection("jadis").whereEqualTo("status", "on")
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
                                            if (String.valueOf(change.getDocument().get("provinsi")).equals(provinsi)) {
                                                modMajlis mm = new modMajlis();
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
                                                jadwalAdapter = new JadwalAdapter(getActivity(), arrayMajelis);
                                                rvMajlis.setAdapter(jadwalAdapter);
                                                jadwalAdapter.notifyDataSetChanged();
                                                layNoData.setVisibility(View.GONE);
                                                swipJadwal.setVisibility(View.VISIBLE);
                                            } else {
                                                if (arrayMajelis.size()>0) {
                                                    layNoData.setVisibility(View.GONE);
                                                    swipJadwal.setVisibility(View.VISIBLE);
                                                } else {
                                                    layNoData.setVisibility(View.VISIBLE);
                                                    swipJadwal.setVisibility(View.GONE);
                                                }
                                            }
                                            pLoading.dismiss();
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
    }

    private void getDataByKategori() {
        pLoading = new ProgressDialog(getActivity());
        pLoading.setTitle("Memuat data ...");
        pLoading.setMessage("Silahkan tunggu sejenak");
        pLoading.setCancelable(true);
        pLoading.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                db.collection("jadis").whereEqualTo("status", "on")
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
                                            if (String.valueOf(change.getDocument().getData().get("kategori")).equals(kategori)) {
                                                modMajlis mm = new modMajlis();
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
                                                jadwalAdapter = new JadwalAdapter(getActivity(), arrayMajelis);
                                                rvMajlis.setAdapter(jadwalAdapter);
                                                jadwalAdapter.notifyDataSetChanged();
                                                layNoData.setVisibility(View.GONE);
                                                swipJadwal.setVisibility(View.VISIBLE);
                                            } else {
                                                if (arrayMajelis.size() > 0) {
                                                    layNoData.setVisibility(View.GONE);
                                                    swipJadwal.setVisibility(View.VISIBLE);
                                                } else {
                                                    layNoData.setVisibility(View.VISIBLE);
                                                    swipJadwal.setVisibility(View.GONE);
                                                }
                                            }
                                            pLoading.dismiss();
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
    }

    private void getDataByKategoriProvinsi() {
        pLoading = new ProgressDialog(getActivity());
        pLoading.setTitle("Memuat data ...");
        pLoading.setMessage("Silahkan tunggu sejenak");
        pLoading.setCancelable(true);
        pLoading.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                db.collection("jadis").whereEqualTo("status", "on")
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
                                            if (String.valueOf(change.getDocument().getData().get("kategori")).equals(kategori) &&
                                                    change.getDocument().get("provinsi").toString().equals(provinsi)) {
                                                modMajlis mm = new modMajlis();
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
                                                jadwalAdapter = new JadwalAdapter(getActivity(), arrayMajelis);
                                                rvMajlis.setAdapter(jadwalAdapter);
                                                jadwalAdapter.notifyDataSetChanged();
                                                layNoData.setVisibility(View.GONE);
                                                swipJadwal.setVisibility(View.VISIBLE);
                                            } else {
                                                if (arrayMajelis.size() > 0) {
                                                    layNoData.setVisibility(View.GONE);
                                                    swipJadwal.setVisibility(View.VISIBLE);
                                                } else {
                                                    layNoData.setVisibility(View.VISIBLE);
                                                    swipJadwal.setVisibility(View.GONE);
                                                }
                                            }
                                            pLoading.dismiss();
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
    }

    private void dialogFilter() {
        LayoutInflater inflater;
        View dialog_layout;
        AlertDialog.Builder dialogFilter;

        inflater = LayoutInflater.from(getActivity());
        dialog_layout = inflater.inflate(R.layout.filter_jadwal, null);
        if (getActivity() != null) {
            dialogFilter = new AlertDialog.Builder(getActivity());
            dialogFilter.setView(dialog_layout);
            dialogFilter.setCancelable(true);
            theDialog = dialogFilter.create();
        }

        final SearchableSpinner spinnerKategori = dialog_layout.findViewById(R.id.spinner_kategori);
        final SearchableSpinner spinnerProvinsi = dialog_layout.findViewById(R.id.spinner_provinsi);
        spinnerKategori.setTitle("Pilih Kategori");
        spinnerKategori.setPositiveButton("TUTUP");
        spinnerKategori.setAdapter(kategoriAdapter);
        spinnerProvinsi.setTitle("Pilih Provinsi");
        spinnerProvinsi.setPositiveButton("TUTUP");
        spinnerProvinsi.setAdapter(provinsiAdapter);
        final Button btnCari = dialog_layout.findViewById(R.id.btnCari);
        btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kategori = spinnerKategori.getSelectedItem().toString();
                provinsi = spinnerProvinsi.getSelectedItem().toString();
                if (spinnerKategori.getSelectedItem().toString().equals("-- Pilih --") && spinnerProvinsi.getSelectedItem().toString().equals("-- Pilih --")) {
                    if (arrayMajelis.size() > 0 || !arrayMajelis.isEmpty()) {
                        arrayMajelis.clear();
                    }
                    cekKoneksi();
                    theDialog.dismiss();
                } else if (!spinnerKategori.getSelectedItem().toString().equals("-- Pilih --") && spinnerProvinsi.getSelectedItem().toString().equals("-- Pilih --")) {
                    if (arrayMajelis.size() > 0 || !arrayMajelis.isEmpty()) {
                        arrayMajelis.clear();
                    }
                    getDataByKategori();
                    theDialog.dismiss();
                } else if (spinnerKategori.getSelectedItem().toString().equals("-- Pilih --") && !spinnerProvinsi.getSelectedItem().toString().equals("-- Pilih --")) {
                    if (arrayMajelis.size() > 0 || !arrayMajelis.isEmpty()) {
                        arrayMajelis.clear();
                    }
                    getDataByProvinsi();
                    theDialog.dismiss();
                } else if (!spinnerKategori.getSelectedItem().toString().equals("-- Pilih --") && !spinnerProvinsi.getSelectedItem().toString().equals("-- Pilih --")) {
                    if (arrayMajelis.size() > 0 || !arrayMajelis.isEmpty()) {
                        arrayMajelis.clear();
                    }
                    getDataByKategoriProvinsi();
                    theDialog.dismiss();
                }
            }
        });

        if (!getActivity().isFinishing()) {
            theDialog.show();
        }
    }
}
