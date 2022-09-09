package com.example.dailyselfie;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends BaseAdapter {

    //Dữ liệu liên kết bởi Adapter là một mảng các sản phẩm
    private ArrayList<DataList> listProduct;
    private Context context;

    Adapter(Context context,ArrayList<DataList> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
    }

    @Override
    public int getCount() {
        //Trả về tổng số phần tử, nó được gọi bởi ListView
        return listProduct.size();
    }

    @Override
    public Object getItem(int position) {
        //Trả về dữ liệu ở vị trí position của Adapter, tương ứng là phần tử
        //có chỉ số position trong listProduct
        return listProduct.get(position);
    }

    @Override
    public long getItemId(int position) {
        //Trả về một ID của phần
        return listProduct.get(position).productID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //convertView là View của phần tử ListView, nếu convertView != null nghĩa là
        //View này được sử dụng lại, chỉ việc cập nhật nội dung mới
        //Nếu null cần tạo mới

        View viewProduct;
        if (convertView == null) {
            viewProduct = View.inflate(parent.getContext(), R.layout.list_layout, null);
        } else viewProduct = convertView;

        //Bind sữ liệu phần tử vào View
        DataList product = (DataList) listProduct.get(position);
        ((TextView) viewProduct.findViewById(R.id.tvID)).setText(String.valueOf(product.productID));
        ((TextView) viewProduct.findViewById(R.id.tvImgname)).setText(String.valueOf(product.imgname));
        ((ImageView) viewProduct.findViewById(R.id.im_item)).setImageBitmap(product.img);

        return viewProduct;
    }
}
