package com.cahtegal.jadis.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cahtegal.jadis.BuildConfig;
import com.cahtegal.jadis.R;
import com.cahtegal.jadis.adapter.SlideMenuAdapter;
import com.cahtegal.jadis.fragment.FragmentUtama;
import com.cahtegal.jadis.model.ItemSlideMenu;
import com.cahtegal.jadis.util.AlarmReceiver;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
    ImageView imgCalendar;
    Calendar myCalendar = Calendar.getInstance();
    public static String namaHari = "",dates = "";
    public static int Hari = 0,Bulan = 0, Tahun = 0;
    public static String tglMajelis = "";

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
        setAlarm();
        setAlarm2();
        setAlarm3();
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Tokenfirebase ", "Refreshed token: " + refreshedToken);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAlarm();
        setAlarm2();
        setAlarm3();
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
        imgCalendar = findViewById(R.id.imgCalendar);
        listMenu = new ArrayList<>();
        TextView teksVersi = findViewById(R.id.teksVersi);
        teksVersi.setText("Versi "+BuildConfig.VERSION_NAME);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int tahun, int bulan,
                                  int hari) {

                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, tahun);
                myCalendar.set(Calendar.MONTH, bulan);
                myCalendar.set(Calendar.DAY_OF_WEEK, hari);
                String namaBulan="";
                Calendar calendar = new GregorianCalendar(tahun, bulan, hari);

                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
                    namaHari = "Senin";
                } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY){
                    namaHari = "Selasa";
                } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY){
                    namaHari = "Rabu";
                } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY){
                    namaHari = "Kamis";
                } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
                    namaHari = "Jum'at";
                } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                    namaHari = "Sabtu";
                } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
                    namaHari = "Minggu";
                }

                if (bulan == 0) {
                    namaBulan = "Januari";
                } else if (bulan == 1) {
                    namaBulan = "Februari";
                } else if (bulan == 2) {
                    namaBulan = "Maret";
                } else if (bulan == 3) {
                    namaBulan = "April";
                } else if (bulan == 4) {
                    namaBulan = "Mei";
                } else if (bulan == 5) {
                    namaBulan = "Juni";
                } else if (bulan == 6) {
                    namaBulan = "Juli";
                } else if (bulan == 7) {
                    namaBulan = "Agustus";
                } else if (bulan == 8) {
                    namaBulan = "September";
                } else if (bulan == 9) {
                    namaBulan = "Oktober";
                } else if (bulan ==10) {
                    namaBulan = "November";
                } else if (bulan == 11) {
                    namaBulan = "Desember";
                }
                Hari = hari;
                Bulan = bulan;
                Tahun = tahun;
                tglMajelis = namaHari+", "+hari + " " + namaBulan + " "+ tahun;
                FragmentUtama.getDataByDate();
            }
        };

        imgCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog mDate = new DatePickerDialog(MenuUtama.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDate.show();
            }
        });

    }

    private void setAlarm() {
        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();

        calSet.set(Calendar.HOUR_OF_DAY, 7);
        calSet.set(Calendar.MINUTE, 0);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        if (calSet.compareTo(calNow) <= 0) {
            // Today Set time passed, count to tomorrow
            calSet.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(),
                    pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
    }

    private void setAlarm2() {
        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();

        calSet.set(Calendar.HOUR_OF_DAY, 14);
        calSet.set(Calendar.MINUTE, 0);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        if (calSet.compareTo(calNow) <= 0) {
            // Today Set time passed, count to tomorrow
            calSet.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(),
                    pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
    }

    private void setAlarm3() {
        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();

        calSet.set(Calendar.HOUR_OF_DAY, 20);
        calSet.set(Calendar.MINUTE, 0);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        if (calSet.compareTo(calNow) <= 0) {
            // Today Set time passed, count to tomorrow
            calSet.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(),
                    pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
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
