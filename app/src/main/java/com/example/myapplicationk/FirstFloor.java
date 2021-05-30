package com.example.myapplicationk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;


public class FirstFloor extends Fragment {
    ImageView imageViewF1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_first_floor, container, false);
        imageViewF1 = rootView.findViewById(R.id.f1image);


        return rootView;
    }
}