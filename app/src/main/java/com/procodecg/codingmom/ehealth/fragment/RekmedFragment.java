package com.procodecg.codingmom.ehealth.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.procodecg.codingmom.ehealth.R;
import com.procodecg.codingmom.ehealth.rekam_medis.RekmedDinamisFragment;
import com.procodecg.codingmom.ehealth.rekam_medis.RekmedStatisFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macbookpro on 8/29/17.
 */

public class RekmedFragment extends Fragment {

    public static RekmedFragment newInstance() {
        RekmedFragment fragment = new RekmedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rekmed,container, false);
        ((BottombarActivity) getActivity()).setTitleText("Rekam Medis");
        ((BottombarActivity) getActivity()).setSubTitleText();

        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.result_tabs);
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);


        return view;

    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new RekmedDinamisFragment(), "Dinamis");
        adapter.addFragment(new RekmedStatisFragment(), "Statis");
        viewPager.setAdapter(adapter);
    }



    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(RekmedDinamisFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        public void addFragment(RekmedStatisFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}