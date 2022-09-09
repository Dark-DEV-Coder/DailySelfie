package com.example.dailyselfie;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static final int CODE_IMAGE = 1 ;
    private static final long ONE_MINUTES = 100L;
    private static int DEM=1;
    ArrayList<DataList> listProduct;
    Adapter productListViewAdapter;
    ListView listViewProduct;
    String currentPhotoname,currentSelfiename;
    File myFile;
    private String filepath = "DataImageName";
    private String filename = "ImageName.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cài đặt thanh app Bar (ActionBar)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);// Cài đặt logo
        actionBar.setDisplayUseLogoEnabled(true);// Hiện Logo

        CreateAlarm(); // Tạo thông báo cho ứng dụng

        listViewProduct = (ListView) findViewById(R.id.lvImage);
        listProduct = new ArrayList<DataList>();

        // Mở file đã lưu các tên hình
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File direc = contextWrapper.getDir(filepath,Context.MODE_PRIVATE);
        myFile = new File(direc,filename);

        // Đọc các file ảnh đã được lưu
        try{
            FileInputStream fis = new FileInputStream(myFile);// Mở file

            // Lấy dữ liệu từ file
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            // Đọc từng dòng trong file
            String line =null;
            while((line=br.readLine()) != null){
                Bitmap bmp = LoadImage(MainActivity.this,line,".png");// Lấy hình ảnh bitmap từ file
                //Đưa dữ liệu vào danh sách ArrayList
                DataList product = new DataList(DEM,line,bmp);
                DEM++;
                listProduct.add(product);
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        productListViewAdapter = new Adapter(MainActivity.this,listProduct);
        listViewProduct.setAdapter(productListViewAdapter);

        // Phóng to hình khi click vào
        listViewProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Đẩy dữ liệu qua giao diện khác để hiện hình ảnh lớn
                // Do dữ liệu ảnh có kích thước quá lớn nên lưu xuống file
                // Đẩy file dữ liệu qua
                Bitmap bmp = listProduct.get(position).getImg();
                try {
                    String filename = "bitmap.png"; // Tên file lưu ảnh
                    Context context = MainActivity.this;
                    // Đặt chế độ bảo mật để không truy cập được từ nơi khác
                    FileOutputStream stream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);// Lưu ảnh bitmap xuống file có dạng .png
                    stream.close(); // Đóng file

                    // Mở giao diện khác và đẩy dữ liệu qua giao diện đó
                    Intent in1 = new Intent(MainActivity.this, ShowImageView.class);
                    in1.putExtra("image", filename);
                    startActivityForResult(in1,CODE_IMAGE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        // Đặt tên file ảnh
                        File photoFile = new File(currentPhotoname);
                        File selfieFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), currentSelfiename+".png");
                        photoFile.renameTo(selfieFile);

                        // Đặt tên hình có dạng thời gian
                        String s = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        // Lấy dữ liệu ảnh
                        Bitmap imgaebitmap = setImageFilePath(Uri.fromFile(selfieFile).getPath());

                        // Thêm dữ liệu vào danh sách ArrayList
                        DataList product = new DataList(DEM,s,imgaebitmap);
                        listProduct.add(product);

                        // Lưu hình xuống file
                        SaveImage(MainActivity.this,imgaebitmap, product.getImgname(),".png");

                        // Lưu tên hình vào file .txt
                        try{
                            String a="\n";
                            FileOutputStream op = new FileOutputStream(myFile);
                            for (DataList d : listProduct){
                                op.write(d.getImgname().getBytes()); // Ghi tên hình xuống file
                                op.write(a.getBytes());
                            }
                            op.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        DEM++;
                        productListViewAdapter = new Adapter(MainActivity.this,listProduct);
                        listViewProduct.setAdapter(productListViewAdapter);
                    }
                }
            });

    // Hàm mở camera
    private void OpenCameraActivityForResult(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null){ // Kiểm tra có tồn tại camera điện thoại hay không
            File photoFile =null;
            try {
                photoFile = createImageFile(); // Tạo file ảnh
            }catch (IOException ex){
                Toast.makeText(MainActivity.this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null){
                // Lấy Uri của hình ảnh từ file
                Uri photoUri = FileProvider.getUriForFile(MainActivity.this,
                        getApplicationContext().getPackageName()+".fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri); // Đẩy dữ liệu
                cameraActivityResultLauncher.launch(takePictureIntent);// Mở Camera
            }
//        }
    }

    // Cài đặt size cho ảnh bitmap không bị bể hình
    public static Bitmap setImageFilePath(String imagePath, int targetW, int targetH){
        BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
        bmpOptions.inJustDecodeBounds=true; // đọc thông tin ảnh nhưng không đọc dữ liệu
        Bitmap bmp = BitmapFactory.decodeFile(imagePath,bmpOptions); // Đọc thông tin ảnh
        int photoW = bmpOptions.outWidth;
        int photoH = bmpOptions.outHeight;

        int scaleFactor = Math.max( photoW / targetW , photoH / targetH);

        bmpOptions.inJustDecodeBounds=false;
        bmpOptions.inSampleSize=scaleFactor; // Giảm kích thước ảnh

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath,bmpOptions);
        return bitmap;
    }

    public static Bitmap setImageFilePath(String imagePath) {
        return setImageFilePath(imagePath, 1920, 1080);
    }

    // Hàm tạo file ảnh
    private File createImageFile() throws IOException{
        currentSelfiename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPG_" + currentSelfiename + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".png",
                storageDir
        );
        currentPhotoname = image.getAbsolutePath();
        return image;
    }

    // Hàm tạo các icon menu trên thanh app Bar (AcitonBar)
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Sự kiện click cho các icon trên thanh app Bar (ActionBar)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu1:
                //code xử lý khi bấm vào icon máy ảnh
                Toast.makeText(this, "Camera on", Toast.LENGTH_SHORT).show();
                OpenCameraActivityForResult();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Hàm để lưu hình ảnh dạng bitmap xuống file
    public void SaveImage(Context context, Bitmap bitmap, String name, String extension){
        name = name + "." + extension; // Tên hình + định dạng hình như .png, .jpg
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = context.openFileOutput(name, Context.MODE_PRIVATE); // Thiết lập bảo mật để không bị truy cập từ nơi khác
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream); // Lưu hình vào file
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Tải hình ảnh từ file đã lưu lên máy dưới dạng bitmap
    public Bitmap LoadImage(Context context,String name,String extension){
        name = name + "." + extension; // Tên hình + định dạng hình như .png, .jpg
        FileInputStream fileInputStream;
        Bitmap bitmap = null;
        try{
            fileInputStream = context.openFileInput(name); // Mở file lưu hình
            bitmap = BitmapFactory.decodeStream(fileInputStream); // Lấy hình được lưu từ file kiểu bitmap
            fileInputStream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // Tạo thông báo theo thời gian (1 phút báo 1 lần) tùy vào mình chỉnh ở biến ONE_MINUTES
    private void CreateAlarm(){
        try{
            // Chuyển Activity qua class khác để cài đặt các nội dung thông báo
            Intent intent = new Intent(MainActivity.this,Alarm.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_IMMUTABLE);

            // Đặt thời gian thông báo
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime()+ ONE_MINUTES,
                    ONE_MINUTES,pendingIntent);

        }catch (Exception e){
            Log.d("ALARM",e.getMessage().toString());
        }
    }
}