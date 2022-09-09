package com.example.dailyselfie;

import android.graphics.Bitmap;

// Các dữ liệu của 1 dòng trong ListView
public class DataList {
    public int productID; // Thứ tự phần tử
    public String imgname;// tên hình
    public Bitmap img;// Hình ảnh

    public DataList(int productID, String imgname, Bitmap img) {
        this.productID = productID;
        this.imgname = imgname;
        this.img = img;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getImgname() {
        return imgname;
    }

    public void setImgname(String imgname) {
        this.imgname = imgname;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }
}
