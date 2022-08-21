package com.example.travel_uk.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.travel_uk.Adapter.CustomAdapter;
import com.example.travel_uk.CreateJourney;
import com.example.travel_uk.Model.CustomPlaceModel;
import com.example.travel_uk.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeJourney extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ListView listView;
    List<CustomPlaceModel> customPlaceModelList = new ArrayList<>();
    CustomPlaceModel customPlaceModel;
    private ArrayList<String> keysList = new ArrayList<>();
    private CustomAdapter customAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_journeylist, container, false);
        listView = view.findViewById(R.id.journeylist);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("PlacesInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        customPlaceModelList.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Map<String,String> map = (Map<String, String>) snapshot.getValue();
                customPlaceModel = new CustomPlaceModel();
                customPlaceModel.setDate(map.get("date"));
                customPlaceModel.setCity(map.get("city"));
                customPlaceModel.setStreet(map.get("street"));
                customPlaceModel.setImage(map.get("image"));

                customPlaceModelList.add(customPlaceModel);
                keysList.add(snapshot.getKey());

                CustomAdapter myAdapter = new CustomAdapter(getActivity(),customPlaceModelList);
                listView.setAdapter(myAdapter);



                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                        final int item = position;
                        new AlertDialog.Builder(requireContext())
                                .setIcon(R.drawable.ic_delete)
                                .setTitle("Are You Sure?")
                                .setMessage("Do you want delete this item")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        customPlaceModelList.remove(item);
                                        myAdapter.notifyDataSetChanged();
                                        String key = keysList.get(item);
                                        databaseReference.child(key).removeValue();


                                    }
                                })
                                .setNegativeButton("No",null)
                                .show();

                        return true;
                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                keysList.remove(snapshot.getKey());

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


        return view;
    }

    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.createjourney);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CreateJourney.class);
                startActivity(i);
            }
        });
    }

    public class CustomAdapter extends BaseAdapter{

        Context context;
        List<CustomPlaceModel> stringList;
        TextView textstreet,txtcity,textdate;
        ImageView imageplace;

        public CustomAdapter(Context context, List<CustomPlaceModel> stringList) {
            this.context = context;
            this.stringList = stringList;
        }

        @Override
        public int getCount() {
            return stringList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.journey_item_layout,viewGroup,false);
            textstreet= view.findViewById(R.id.txt_street);
            txtcity = view.findViewById(R.id.txtcity);
            textdate = view.findViewById(R.id.txt_date);
            imageplace = view.findViewById(R.id.imgplace);

            txtcity.setText(stringList.get(i).getCity());
            textstreet.setText(stringList.get(i).getStreet());
            textdate.setText(stringList.get(i).getDate());

            Glide.with(context).load(stringList.get(i).getImage()).into(imageplace);

            return view;
        }
    }




}
