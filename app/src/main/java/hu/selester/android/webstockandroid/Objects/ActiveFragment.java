package hu.selester.android.webstockandroid.Objects;

import android.support.v4.app.Fragment;

public class ActiveFragment {

    private static Fragment fragment;

    public static void setFragment(Fragment frg){
        fragment = frg;
    }

    public static Fragment getFragment(){
        return fragment;
    }

}
