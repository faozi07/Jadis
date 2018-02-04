package com.cahtegal.jadis.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cahtegal.jadis.R;
import com.cahtegal.jadis.util.ScalingUtilities;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TambahJadwal extends AppCompatActivity {

    RelativeLayout rlayImage,imgNoImage;
    TextView teksBtlImage,editTglMajlis;
    ImageView imgMajlis;
    EditText editNamaMajlis, editTemaAcara, editPenceramah, editPukul, editAlamat, editKeterangan;
    Button btnKirim;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef,imgRef;
    private final int AKSES_GALERI = 22131;
    String photoPath = "",strMyImagePath = "";
    GalleryPhoto galleryPhoto;
    Uri avatarUri;
    File avatarFile;
    int rotate = 0;
    Calendar myCalendar = Calendar.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog pLoading;
    private AdView mAdView;
    SearchableSpinner spinnerProvinsi,spinnerKategori;
    String[]arrayProvinsi = {"-- Pilih --","Bali","Banten","Bengkulu","D.I. Aceh","D.I. Yogyakarta","DKI Jakarta","Gorontalo","Jambi","Jawa Barat","Jawa Tengah",
            "Jawa Timur","Kalimantan Barat","Kalimantan Selatan","Kalimantan Tengah", "Kalimantan Timur","Kalimantan Utara","Kepulauan Bangka Belitung",
            "Kepulauan Riau","Lampung","Maluku","Maluku Utara","Nusa Tenggara Barat","Nusa Tenggara Timur","Papua","Papua Barat","Riau","Sulawesi Barat",
            "Sulawesi Selatan","Sulawesi Tengah","Sulawesi Tenggara","Sulawesi Utara","Sumatera Barat","Sumatera Selatan","Sumatera Utara"};
    String[]arrayKategori = {"-- Pilih --","Harian","Mingguan","Bulanan","Tidak Tentu"};
    AlertDialog alertDialog;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_jadwal);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tambah Jadwal Majelis");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        deklarasi();
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        strMyImagePath = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
        }
        strMyImagePath = "";
    }

    private void deklarasi() {
        mAdView = findViewById(R.id.adView);
        pLoading = new ProgressDialog(TambahJadwal.this);
        pLoading.setMessage("Upload data majelis");
        pLoading.setCancelable(false);
        storageRef = storage.getReference();
        imgRef = storageRef.child("images");
        rlayImage = findViewById(R.id.relImage);
        teksBtlImage = findViewById(R.id.cancelImage);
        imgMajlis = findViewById(R.id.imgMajelis);
        imgNoImage = findViewById(R.id.imgNoImage);
        editAlamat = findViewById(R.id.alamat);
        editNamaMajlis = findViewById(R.id.namaMajelis);
        editPenceramah = findViewById(R.id.namaPenceramah);
        editTemaAcara = findViewById(R.id.temaAcara);
        editPukul = findViewById(R.id.pukul);
        editTglMajlis = findViewById(R.id.tglMajlis);
        editKeterangan = findViewById(R.id.keterangan);
        btnKirim = findViewById(R.id.btnKirim);
        spinnerProvinsi = findViewById(R.id.spinner_provinsi);
        spinnerKategori = findViewById(R.id.spinner_kategori);
        spinnerProvinsi.setTitle("Pilih Provinsi");
        spinnerProvinsi.setPositiveButton("TUTUP");
        spinnerKategori.setTitle("Pilih Kategori");
        spinnerKategori.setPositiveButton("TUTUP");
        ArrayAdapter<String> provinsiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayProvinsi);
        spinnerProvinsi.setAdapter(provinsiAdapter);
        ArrayAdapter<String> kategoriAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayKategori);
        spinnerKategori.setAdapter(kategoriAdapter);
        galleryPhoto = new GalleryPhoto(getApplicationContext());
    }

    private void action() {
        imgNoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        startActivityForResult(galleryPhoto.openGalleryIntent(), AKSES_GALERI);
                    } else {
                        ActivityCompat.requestPermissions(TambahJadwal.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                } else {
                    startActivityForResult(galleryPhoto.openGalleryIntent(), AKSES_GALERI);
                }
            }
        });

        teksBtlImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlayImage.setVisibility(View.GONE);
                imgNoImage.setVisibility(View.VISIBLE);
                strMyImagePath = "";
            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editAlamat.getText().toString().equals("") || editKeterangan.getText().toString().equals("") || editNamaMajlis.getText().toString().equals("")
                        || editPenceramah.getText().toString().equals("") || editPukul.getText().toString().equals("") || editTemaAcara.getText().toString().equals("")
                        || editTglMajlis.getText().toString().equals("") || spinnerProvinsi.getSelectedItem().toString().equals("-- Pilih --")) {
                    Toast.makeText(TambahJadwal.this, "Isi semua data dengan lengkap", Toast.LENGTH_LONG).show();
                } else {
                    if (strMyImagePath.equals("")) {
                        Toast.makeText(TambahJadwal.this,"Anda belum menambahkan gambar",Toast.LENGTH_LONG).show();
                    } else {
                        cekKoneksi();
                    }
                }
            }
        });
        editTglMajlis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog mDate = new DatePickerDialog(TambahJadwal.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDate.show();
            }
        });
    }

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
            String namaHari = "";
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
            String tglmajlis = namaHari+", "+hari + " " + namaBulan + " "+ tahun;
            editTglMajlis.setText(tglmajlis);
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AKSES_GALERI) {
                if (data.getData() == null) {
                    avatarUri.toString();
                } else {
                    avatarUri = data.getData();
                }
                galleryPhoto.setPhotoUri(avatarUri);

                photoPath = galleryPhoto.getPath();
                getCameraPhotoOrientation(TambahJadwal.this, avatarUri, photoPath);
                decodeFile(photoPath, 1500, 1500);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        avatarFile = new File(strMyImagePath);
                        rlayImage.setVisibility(View.VISIBLE);
                        imgNoImage.setVisibility(View.GONE);
                        Glide.with(TambahJadwal.this).load(avatarUri).into(imgMajlis);
                        if (rotate == 90) {
                            imgMajlis.setRotation(90);
                        } else if (rotate == 180) {
                            imgMajlis.setRotation(180);
                        } else if (rotate == 270) {
                            imgMajlis.setRotation(270);
                        }
                    }
                },500);
            }
        }
    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    rotate = 0;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    rotate = 0;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    private String decodeFile(String path,int DESIREDWIDTH, int DESIREDHEIGHT) {
        Bitmap scaledBitmap = null;
        try {
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            Matrix matrix = new Matrix();
            if (rotate == 90) {
                matrix.setRotate(90);
            } else if (rotate == 270) {
                matrix.setRotate(270);
            } else if (rotate == 180) {
                matrix.setRotate(180);
            }
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);;

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/Jadis");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            } else {
                mFolder.delete();
                mFolder.mkdir();
            }

            String s = dateToString(new Date());

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            rotatedBitmap.recycle();
        } catch (Throwable e) {
            Log.e("LoadImage Error",String.valueOf(e));
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;
    }

    private void cekKoneksi() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Cek koneksi");
        progressDialog.show();
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (conMgr != null) {
            netInfo = conMgr.getActiveNetworkInfo();
        }
        if (netInfo == null) {
            progressDialog.dismiss();
            dialogNoKoneksi();
        } else {
            progressDialog.dismiss();
            getURLImage();
        }
    }

    private void dialogNoKoneksi() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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

    public String dateToString(Date date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        return df.format(date);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AKSES_GALERI: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(galleryPhoto.openGalleryIntent(), AKSES_GALERI);
                    Log.d("permission granted  ", "permission granted");
                } else {
                    Log.d("permission denied  ", "permission denied");
                }
            }
        }
    }


    private void getURLImage() {
        pLoading.show();
        try {
            Uri file = Uri.fromFile(new File(strMyImagePath));
            StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    pLoading.dismiss();
                    Toast.makeText(TambahJadwal.this,String.valueOf(exception),Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    kirimDataFirestore(String.valueOf(taskSnapshot.getDownloadUrl()));
                }
            });
        } catch (Exception ex) {
            Toast.makeText(TambahJadwal.this,"Gagal upload foto",Toast.LENGTH_LONG).show();
            pLoading.dismiss();
        }
    }

    private void kirimDataFirestore(String url) {
        Map<String, Object> datamajelis = new HashMap<>();
        datamajelis.put("status", "off");
        datamajelis.put("gambar", url);
        datamajelis.put("kategori", spinnerKategori.getSelectedItem().toString());
        datamajelis.put("provinsi", spinnerProvinsi.getSelectedItem().toString());
        datamajelis.put("keterangan", editKeterangan.getText().toString());
        datamajelis.put("majelis", editNamaMajlis.getText().toString());
        datamajelis.put("penceramah", editPenceramah.getText().toString());
        datamajelis.put("pukul", editPukul.getText().toString());
        datamajelis.put("tanggal", editTglMajlis.getText().toString());
        datamajelis.put("tema_acara", editTemaAcara.getText().toString());
        datamajelis.put("tempat", editAlamat.getText().toString());

        String tgl = "";
        for (int i=0;i<editTglMajlis.length();i++) {
            if (String.valueOf(editTglMajlis.getText().charAt(i)).equals(" ")) {
                tgl = tgl+"-";
            } else {
                tgl = tgl+editTglMajlis.getText().toString().charAt(i);
            }
        }
        String document = tgl+"-"+ UUID.randomUUID().toString();

        db.collection("jadis").document(document)
                .set(datamajelis)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialogBerhasil("Berhasil Terkirim","Informasi jadwal Anda akan di verifikasi oleh admin, silahkan tunggu");
                        pLoading.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialogBerhasil("Gagal Terkirim","Silahkan mencoba kembali mengirim informasi");
                        pLoading.dismiss();
                    }
                });
    }

    private void dialogBerhasil(String title,String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.dismiss();
                        finish();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
