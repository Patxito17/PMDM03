package com.gortmol.tupokedex;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.gortmol.tupokedex.data.FirestoreHelper;
import com.gortmol.tupokedex.databinding.ActivityMainBinding;
import com.gortmol.tupokedex.fragments.SettingsFragment;
import com.gortmol.tupokedex.ui.adapter.MyViewPagerAdapter;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirestoreHelper.getInstance()
                .getUserSetting(FirebaseAuth.getInstance().getCurrentUser(), SettingsFragment.PREF_LANGUAGE, language -> {
                    if (language != null) {
                        LocaleListCompat appLocales;
                        if (language.equals("es")) {
                            appLocales = LocaleListCompat.forLanguageTags("es");
                        } else {
                            appLocales = LocaleListCompat.forLanguageTags("en");
                        }
                        AppCompatDelegate.setApplicationLocales(appLocales);
                    }
                });

        binding.viewPager.setAdapter(new MyViewPagerAdapter(this));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(binding.tabLayout.getTabAt(position)).select();
            }
        });

    }
}