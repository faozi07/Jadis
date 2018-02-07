package com.cahtegal.jadis.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cahtegal.jadis.BuildConfig;
import com.cahtegal.jadis.R;
import com.cahtegal.jadis.adapter.SlideMenuAdapter;
import com.cahtegal.jadis.fragment.FragmentUtama;
import com.cahtegal.jadis.model.ItemSlideMenu;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class MenuUtama extends AppCompatActivity {
    private List<ItemSlideMenu> listMenu;
    private ListView listViewSliding;
    private DrawerLayout drawerLayout;
    private RelativeLayout rlSlide;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    FrameLayout flContent;
    private Toolbar toolbar;
    String refreshedToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_utama);
        setComponent();
        addItemSlide();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupDrawerToggle();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        listViewSliding.setItemChecked(0,true);
        drawerLayout.closeDrawer(rlSlide);
        itemClickMenu();
        setFragment();
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Tokenfirebase ", "Refreshed token: " + refreshedToken);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @SuppressLint("SetTextI18n")
    private void setComponent(){
        rlSlide = findViewById(R.id.rlSlide);
        flContent = findViewById(R.id.flContent);
        listViewSliding = findViewById(R.id.list_sliding_menu);
        drawerLayout = findViewById(R.id.drawer_layout);
        listMenu = new ArrayList<>();
        TextView teksVersi = findViewById(R.id.teksVersi);
        teksVersi.setText("Versi "+BuildConfig.VERSION_NAME);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setFragment() {
        Fragment fragment = null;
        Class fragmentClass;
        fragmentClass = FragmentUtama.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            dialogKeluar();
        }
    }
    private void addItemSlide() {
        listMenu.add(new ItemSlideMenu(R.drawable.ic_home,"Beranda"));
        listMenu.add(new ItemSlideMenu(R.drawable.ic_tambah_majlis,"Tambah Jadwal Majelis"));
        listMenu.add(new ItemSlideMenu(R.drawable.ic_tentang_kami,"Tentang Kami"));
        listMenu.add(new ItemSlideMenu(R.drawable.ic_bagikan,"Bagikan"));
        listMenu.add(new ItemSlideMenu(R.drawable.ic_rate,"Beri Nilai"));
        listMenu.add(new ItemSlideMenu(R.drawable.ic_keluar,"Keluar"));

        SlideMenuAdapter adapter = new SlideMenuAdapter(this, listMenu);
        listViewSliding.setAdapter(adapter);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {

        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_closed);
    }
    private void itemClickMenu() {
        listViewSliding.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        startActivity(new Intent(MenuUtama.this,TambahJadwal.class));
                        break;
                    case 2:
                        startActivity(new Intent(MenuUtama.this,TentangKami.class));
                        break;
                    case 3:
                        Intent sendIntent;
                        sendIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Info jadwal majelis di seluruh wilayah Nusantara Indonesia, download di \n\nhttps://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID);
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, "Bagikan info jadwal majelis ini dengan "));
                        break;
                    case 4:
                        String appPackageName = BuildConfig.APPLICATION_ID; // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            finish();
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            finish();
                        }
                        break;
                    case 5:
                        dialogKeluar();
                        break;
                }
                listViewSliding.setItemChecked(position,true);
                drawerLayout.closeDrawer(rlSlide);
            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_closed) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    private void dialogKeluar() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder
                .setMessage("Anda ingin keluar dari Jadis ?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
