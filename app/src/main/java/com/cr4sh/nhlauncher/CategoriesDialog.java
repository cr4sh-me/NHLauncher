package com.cr4sh.nhlauncher;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CategoriesDialog extends DialogFragment {

    MainActivity myActivity;

    public CategoriesDialog(MainActivity activity) {
        this.myActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.categories_dialog, container, false);

//        MainUtils mainUtils = new MainUtils((MainActivity) requireActivity());
        MyPreferences myPreferences = new MyPreferences(requireActivity());


        TextView title = view.findViewById(R.id.dialog_title);
        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        ListView recyclerCat = view.findViewById(R.id.recyclerViewCategories);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        List<String> valuesList = Arrays.asList(
                myActivity.getResources().getString(R.string.category_ft),
                myActivity.getResources().getString(R.string.category_01),
                myActivity.getResources().getString(R.string.category_02),
                myActivity.getResources().getString(R.string.category_03),
                myActivity.getResources().getString(R.string.category_04),
                myActivity.getResources().getString(R.string.category_05),
                myActivity.getResources().getString(R.string.category_06),
                myActivity.getResources().getString(R.string.category_07),
                myActivity.getResources().getString(R.string.category_08),
                myActivity.getResources().getString(R.string.category_09),
                myActivity.getResources().getString(R.string.category_10),
                myActivity.getResources().getString(R.string.category_11),
                myActivity.getResources().getString(R.string.category_12),
                myActivity.getResources().getString(R.string.category_13)
        );
        List<Integer> imageList = Arrays.asList(
                R.drawable.nhl_favourite_trans,
                R.drawable.kali_info_gathering_trans,
                R.drawable.kali_vuln_assessment_trans,
                R.drawable.kali_web_application_trans,
                R.drawable.kali_database_assessment_trans,
                R.drawable.kali_password_attacks_trans,
                R.drawable.kali_wireless_attacks_trans,
                R.drawable.kali_reverse_engineering_trans,
                R.drawable.kali_exploitation_tools_trans,
                R.drawable.kali_sniffing_spoofing_trans,
                R.drawable.kali_maintaining_access_trans,
                R.drawable.kali_forensics_trans,
                R.drawable.kali_reporting_tools_trans,
                R.drawable.kali_social_engineering_trans
        );


        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        title.setTextColor(Color.parseColor(myPreferences.color80()));

        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(myActivity, valuesList, imageList, myPreferences.color20(), myPreferences.color80());
        recyclerCat.setAdapter(adapter);



//        MyAdapter adapter = new MyAdapter(this);
//        recyclerCat.setAdapter(adapter);

//        statsSpinner.setAdapter(adapter);

//        statsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

        cancelButton.setOnClickListener(view1 -> Objects.requireNonNull(getDialog()).cancel());

        return view;
    }

}