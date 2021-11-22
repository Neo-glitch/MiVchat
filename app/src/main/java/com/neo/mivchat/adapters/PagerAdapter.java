package com.neo.mivchat.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.neo.mivchat.ui.fragments.FindFriendsFragment;
import com.neo.mivchat.ui.fragments.FriendsFragment;
import com.neo.mivchat.ui.fragments.NotificationsFragment;


public class PagerAdapter extends FragmentStateAdapter {

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: {
                return new FriendsFragment();
            }
            case 1: {
                return new FindFriendsFragment();
            }
            case 2: {
                return new NotificationsFragment();
            }
            default:
                return null;
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
