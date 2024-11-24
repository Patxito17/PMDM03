package com.gortmol.tupokedex.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gortmol.tupokedex.fragments.CapturedPokemonFragment;
import com.gortmol.tupokedex.fragments.PokedexFragment;
import com.gortmol.tupokedex.fragments.SettingsFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {

    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 1:
                return new PokedexFragment();
            case 2:
                return new SettingsFragment();
            default:
                return new CapturedPokemonFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
