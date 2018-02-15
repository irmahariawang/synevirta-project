package com.procodecg.codingmom.ehealth.main;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.data.EhealthContract.KartuEntry;
import com.procodecg.codingmom.ehealth.data.EhealthDbHelper;
import com.procodecg.codingmom.ehealth.fragment.BottombarActivity;
import com.procodecg.codingmom.ehealth.hpcpdc_card.PDCDataActivity;

/**
 * Created by macbookpro on 7/27/17.
 */

public class PasiensyncActivity extends AppCompatActivity {

    Typeface font;
    Typeface fontbold;
    PDCDataActivity pdc;

    public static final int SELECTED_PICTURE =1;
    //rivate static String currentNamaDokter;

    ImageView iv;
    private static String currentHPCNumber;
    public static String currentNamaDokter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pasiensync);

        font = Typeface.createFromAsset(getAssets(), "font1.ttf");
        fontbold = Typeface.createFromAsset(getAssets(), "font1bold.ttf");
        TextView tv1 = (TextView) findViewById(R.id.textView1);
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        TextView tv3 = (TextView) findViewById(R.id.textNamaDokter);
        tv1.setTypeface(fontbold);
        tv2.setTypeface(font);
        tv3.setTypeface(fontbold);

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayNamaDokter();
    }

    public void goToProfil(View v){
        pdc = new PDCDataActivity(getApplicationContext());

        Intent activity = new Intent(this, BottombarActivity.class);
        startActivity(activity);
        finish();
    }

    //utk UPLOAD PHOTO
    public void imgClick (View v) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECTED_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SELECTED_PICTURE :
                if(resultCode==RESULT_OK){
                    Uri uri=data.getData();
                    String[]projection={MediaStore.Images.Media.DATA};

                    Cursor cursor=getContentResolver().query(uri,projection,null,null,null);
                    cursor.moveToFirst();

                    int columnIndex=cursor.getColumnIndex(projection[0]);
                    String filePath=cursor.getString(columnIndex);
                    cursor.close();

                    ImageView iv=(ImageView) findViewById(R.id.imageView);

                    Bitmap yourSelectedImage= BitmapFactory.decodeFile(filePath);
                    Drawable d= new BitmapDrawable(yourSelectedImage);
                    iv.setBackground(d);
                }
        }
    };

    //utk DISPLAY NAMA DOKTER
    private void displayNamaDokter() {
        // Create and/or open a database to read from it
        EhealthDbHelper mDbHelper = new EhealthDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        final TextView textNamaDoktertv = (TextView) findViewById(R.id.textNamaDokter);

        String[] projection = {
                KartuEntry.COLUMN_HPCNUMBER,
                KartuEntry.COLUMN_DOKTER,
        //KartuEntry.COLUMN_PIN_HPC,
        };

        Cursor cursor = db.query(KartuEntry.TABLE_NAME,projection,null,null,null,null,null);
        // TextView displayView = (TextView) findViewById(R.id.text_view_kartu);
        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        // Cursor cursor = db.rawQuery("SELECT * FROM " + KartuEntry.TABLE_NAME, null);

        try {

            // Figure out the index of each column
            // int idColumnIndex = cursor.getColumnIndex(KartuEntry._ID);
            int HPCnumberColumnIndex = cursor.getColumnIndex(KartuEntry.COLUMN_HPCNUMBER);
            int namaDokterColumnIndex = cursor.getColumnIndex(KartuEntry.COLUMN_DOKTER);
            //int pinHPCColumnIndex = cursor.getColumnIndex(KartuEntry.COLUMN_PIN_HPC);
            //int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            // Iterate through all the returned rows in the cursor

            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                // int currentID = cursor.getInt(idColumnIndex);
                currentHPCNumber = cursor.getString(HPCnumberColumnIndex);
                currentNamaDokter = cursor.getString(namaDokterColumnIndex);
                //String currentPinHPC = cursor.getString(pinHPCColumnIndex);
//              Menampilkan nama dokter
                textNamaDoktertv.setText(currentNamaDokter);
                textNamaDoktertv.setVisibility(View.VISIBLE);
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    public static String getNamaDokter(){
            return currentNamaDokter;
    }

}

//    //SINKRONISASI TIDAK BERHASIL
//    Button mShowDialog = (Button) findViewById(R.id.btnShowDialog);
//        mShowDialog.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            AlertDialog.Builder mBuilder = new AlertDialog.Builder(PasiensyncActivity.this);
//            mBuilder.setIcon(R.drawable.logo2);
//            mBuilder.setTitle("Kartu pasien tidak dapat diakses");
//            mBuilder.setMessage("Silahkan coba lagi");
//            mBuilder.setCancelable(false);
//            mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialogInterface, int i) {
//            dialogInterface.dismiss();
//            }
//            });
//            AlertDialog alertDialog = mBuilder.create();
//            alertDialog.show();
//        }
//        });
//        }
//        }