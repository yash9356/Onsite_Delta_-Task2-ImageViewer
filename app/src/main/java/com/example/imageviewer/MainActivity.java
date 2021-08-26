package com.example.imageviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    public RequestQueue mRequestQueue;
    public ImageView image_View;
    public ImageButton button;
    public  int CurrentImg_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        SharedPreferences Img= getSharedPreferences("Img_Id",Context.MODE_PRIVATE);
        CurrentImg_Id=Img.getInt("IMG_ID",0)+1;

        mRequestQueue = Volley.newRequestQueue(this);
        button = findViewById(R.id.imgchange);
        image_View = findViewById(R.id.imageView);


//        File sdCard = Environment.getExternalStorageDirectory();
//        File directory = new File (sdCard.getAbsolutePath() + "/Pictures/TestFolder/");
//        File file = new File(directory, "Image(1).jpg"); //or any other format supported
//        FileInputStream streamIn = null;
//        try {
//            streamIn = new FileInputStream(file);
//        } catch (FileNotFoundException e) {
//            Toast.makeText(this,"Error301"+e,Toast.LENGTH_SHORT).show();
//        }
//        Bitmap bitmap = BitmapFactory.decodeStream(streamIn); //This gets the image

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 8;
//        File photoPath=new File(Environment.DIRECTORY_PICTURES+ File.separator+"TestFolder"+File.separator+"SavedImages2.jpg");
//        final Bitmap b = BitmapFactory.decodeFile(String.valueOf(photoPath), options);
//        image_View.setImageBitmap(b);


        //Picasso.with(this).load("/storage/emulated/0/Pictures/TestFolder/Image.jpg(1)").into(image_View);

        parseJson();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJson();
                BitmapDrawable bitmapDrawable=(BitmapDrawable) image_View.getDrawable();
                Bitmap bitmap=bitmapDrawable.getBitmap();
                saveImageToGallary(bitmap,CurrentImg_Id);
                SaveImgId(CurrentImg_Id,Img);
                CurrentImg_Id++;
            }
        });


    }

    private void SaveImgId(int n,SharedPreferences Img) {
        SharedPreferences.Editor editor=Img.edit();
        editor.putInt("IMG_ID",n);
        editor.commit();
    }

    private void saveImageToGallary(Bitmap bitmap,int n) {
        OutputStream fos;
        try {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                ContentResolver resolver=getContentResolver();
                ContentValues contentValues=new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"Saved_Img"+Integer.toString(n)+".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+ File.separator+"TestFolder");
                Uri imageUri= resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                fos =  resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                Objects.requireNonNull(fos);
                Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "E"+e,Toast.LENGTH_SHORT).show();
        }
    }

    public void parseJson() {
        String url="https://random.imagecdn.app/v1/image?width=500&height=150&category=buildings&format=json";

        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String imgUrl=response.getString("url");
                    //String imgUrl1="https://images.unsplash.com/photo-1628214460107-38469e9a117e?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=150&ixid=MnwxfDB8MXxyYW5kb218MHx8fHx8fHx8MTYyOTkwNjA5Ng&ixlib=rb-1.2.1&q=80&w=500";
                    Picasso.with(MainActivity.this).load(imgUrl).fit().into(image_View);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"Error in jasonArray",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

//    private void askStoragePermission() {
//        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//                && ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//        }
//    }
}