package com.example.emanuel.soundbox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by emanuel on 8/1/15.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity{
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //The layout file that contains the fragments
        setContentView(R.layout.activity_fragment);
        //Lets us interact with fragments
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    /*
                        id          Tells the fragment where in the layout the it should
                                    appear. Can refer to createFragment() by this id
                        fragment    The fragment to place
                     */
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }
}
