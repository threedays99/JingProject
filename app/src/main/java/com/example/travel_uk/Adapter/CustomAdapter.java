package com.example.travel_uk.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.travel_uk.Model.CustomPlaceModel;
import com.example.travel_uk.R;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<CustomPlaceModel> {
    Context context;
    List<CustomPlaceModel> customPlaceModelList;
    TextView textstreet,textcity,textdate;
    ImageView imageplace;

    public CustomAdapter(@NonNull Context context, int resource, Context context1, List<CustomPlaceModel> customPlaceModelList) {
        super(context, resource);
        this.context = context1;
        this.customPlaceModelList = customPlaceModelList;
    }
    
    public void addElement(CustomPlaceModel element) {

        customPlaceModelList.add(element);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.journey_item_layout,viewGroup,false);

        textstreet = view.findViewById(R.id.txt_street);
        textcity = view.findViewById(R.id.txtcity);
        textdate = view.findViewById(R.id.txt_date);
        imageplace = view.findViewById(R.id.imgplace);

        textcity.setText( customPlaceModelList.get(i).getCity());
        textstreet.setText(customPlaceModelList.get(i).getStreet());
        textdate.setText(customPlaceModelList.get(i).getDate());

        Glide.with(context).load(customPlaceModelList.get(i).getImage()).into(imageplace);
        return view;
    }

}
