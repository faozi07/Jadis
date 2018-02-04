package com.cahtegal.jadis.adapter;

/*
 * Created by faozi on 01/02/18.
 */

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cahtegal.jadis.R;
import com.cahtegal.jadis.activity.DetailJadwal;
import com.cahtegal.jadis.model.modMajlis;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;


public class JadwalAdapter extends RecyclerView.Adapter {

    public static Activity activity;
    public static ArrayList<modMajlis> items;
    modMajlis mrt;

    private final int VIEW_ITEM = 1;
    private int lastPosition = -1;
    private InterstitialAd mInterstitialAd;

    public JadwalAdapter(Activity act, ArrayList<modMajlis> data) {
        activity = act;
        this.items = data;
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        return items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public static class BrandViewHolder extends RecyclerView.ViewHolder {

        TextView tTanggal,teksTempat;
        ImageView imgMajelis;
        CardView cardView;

        public BrandViewHolder(View v) {
            super(v);

            tTanggal = v.findViewById(R.id.teksInfo);
            teksTempat = v.findViewById(R.id.teksTempat);
            imgMajelis = v.findViewById(R.id.imgMajelis);
            cardView = v.findViewById(R.id.card_view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_jadwal_majelis, parent, false);

            vh = new JadwalAdapter.BrandViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof JadwalAdapter.BrandViewHolder) {

            mrt = items.get(position);
            String uri = mrt.getImage();
            Glide.with(activity).load(uri).into(((BrandViewHolder) holder).imgMajelis);
            ((BrandViewHolder) holder).tTanggal.setText(mrt.getTanggal());
            ((BrandViewHolder) holder).teksTempat.setText(mrt.getTempat());

            ((BrandViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iklan(activity);
                    mrt = items.get(position);
                    DetailJadwal.kategori = mrt.getKategori();
                    DetailJadwal.provinsi = mrt.getProvinsi();
                    DetailJadwal.linkMajlis = mrt.getImage();
                    DetailJadwal.keterangan = mrt.getKeterangan();
                    DetailJadwal.namaMajlis = mrt.getMajelis();
                    DetailJadwal.penceramah = mrt.getPenceramah();
                    DetailJadwal.pukul = mrt.getPukul();
                    DetailJadwal.tema = mrt.getTema_acara();
                    DetailJadwal.tempat = mrt.getTempat();
                    DetailJadwal.tgl = mrt.getTanggal();
                    activity.startActivity(new Intent(activity, DetailJadwal.class));
                }
            });

            setAnimation(((JadwalAdapter.BrandViewHolder) holder).cardView, position);

        }
    }

    private void iklan(Activity activity) {
        mInterstitialAd = new InterstitialAd(activity);
        mInterstitialAd.setAdUnitId("ca-app-pub-5730449577374867/1054200602");
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if(mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
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
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.blink2);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
