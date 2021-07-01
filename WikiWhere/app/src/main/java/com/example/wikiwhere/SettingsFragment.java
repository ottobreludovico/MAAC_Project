package com.example.wikiwhere;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;


public class SettingsFragment extends Fragment {

    private RadioGroup radioTheme;
    private RadioButton radioAuto, radioBattery;
    private SharedPreferences preferenceManager;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        preferenceManager = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferenceManager.edit();

        radioTheme = view.findViewById(R.id.radioTheme);
        radioAuto = view.findViewById(R.id.radioAuto);
        radioBattery = view.findViewById(R.id.radioBattery);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            radioTheme.check(preferenceManager.getInt("radioThemeId", R.id.radioAuto));
            radioBattery.setVisibility(View.GONE);
        }
        else {
            radioTheme.check(preferenceManager.getInt("radioThemeId", R.id.radioDay));
            radioAuto.setVisibility(View.GONE);
        }


        radioTheme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch (id) {
                    case R.id.radioDay:
                        editor.putInt("Theme", AppCompatDelegate.MODE_NIGHT_NO);
                        editor.apply();
                        editor.putInt("radioThemeId", R.id.radioDay);
                        editor.apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case R.id.radioNight:
                        editor.putInt("Theme", AppCompatDelegate.MODE_NIGHT_YES);
                        editor.apply();
                        editor.putInt("radioThemeId", R.id.radioNight);
                        editor.apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    case R.id.radioAuto:
                        editor.putInt("Theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        editor.apply();
                        editor.putInt("radioThemeId", R.id.radioAuto);
                        editor.apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;
                    case R.id.radioBattery:
                        editor.putInt("Theme", AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                        editor.apply();
                        editor.putInt("radioThemeId", R.id.radioBattery);
                        editor.apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                        break;
                }
            }
        });

    }

}
