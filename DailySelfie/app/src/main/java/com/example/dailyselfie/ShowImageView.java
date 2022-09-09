package com.example.dailyselfie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.FileInputStream;

public class ShowImageView extends AppCompatActivity {
    private PhotoView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image_view);

        img = (PhotoView) findViewById(R.id.imageshow);

        // Lấy hình ảnh bitmap từ file sau khi được đẩy dữ liệu qua đây
        Bitmap bmp = null;
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename); // Mở file lưu hình
            bmp = BitmapFactory.decodeStream(is); // Lấy dữ liệu hình
            img.setImageBitmap(bmp);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finishActivity(MainActivity.CODE_IMAGE);
//        finish();
    }
}
