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
import com.cahtegal.jadis.admin.VerifDetail;
import com.cahtegal.jadis.model.modMajlis;

import java.util.ArrayList;


public class JadwalNoVerifAdapter extends RecyclerView.Adapter {

    public static Activity activity;
    public static ArrayList<modMajlis> items;
    modMajlis mrt;

    private final int VIEW_ITEM = 1;
    private int lastPosition = -1;

    public JadwalNoVerifAdapter(Activity act, ArrayList<modMajlis> data) {
        activity = act;
        this.items = data;
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        return items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public static class BrandViewHolder extends RecyclerView.ViewHolder {

        TextView tTanggalTempat;
        ImageView imgMajelis;
        CardView cardView;

        public BrandViewHolder(View v) {
            super(v);

            tTanggalTempat = v.findViewById(R.id.teksInfo);
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

            vh = new JadwalNoVerifAdapter.BrandViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof JadwalNoVerifAdapter.BrandViewHolder) {

            mrt = items.get(position);
            int pos = position + 1;
            String uri = mrt.getImage();
            Glide.with(activity).load(uri).into(((BrandViewHolder) holder).imgMajelis);
            ((BrandViewHolder) holder).tTanggalTempat.setText(mrt.getTanggal()+"\n"+mrt.getTempat()+"");

            ((BrandViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mrt = items.get(position);
                    VerifDetail.kategori = mrt.getKategori();
                    VerifDetail.provinsi = mrt.getProvinsi();
                    VerifDetail.document = mrt.getDocument();
                    VerifDetail.linkMajlis = mrt.getImage();
                    VerifDetail.keterangan = mrt.getKeterangan();
                    VerifDetail.namaMajlis = mrt.getMajelis();
                    VerifDetail.penceramah = mrt.getPenceramah();
                    VerifDetail.pukul = mrt.getPukul();
                    VerifDetail.tema = mrt.getTema_acara();
                    VerifDetail.tempat = mrt.getTempat();
                    VerifDetail.tgl = mrt.getTanggal();
                    activity.startActivity(new Intent(activity, VerifDetail.class));
                }
            });

            setAnimation(((JadwalNoVerifAdapter.BrandViewHolder) holder).cardView, position);

        }
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
