package com.example.myapplicationk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;


public class SecondFloor extends Fragment {
ImageView imageViewF2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_second_floor, container, false);
        imageViewF2 = rootView.findViewById(R.id.f2image);
        return rootView;
    }
}