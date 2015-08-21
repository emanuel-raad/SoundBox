package com.example.emanuel.soundbox;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class BeatBoxListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return BeatBoxListFragment.newInstance();
    }
}
