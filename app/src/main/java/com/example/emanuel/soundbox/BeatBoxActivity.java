package com.example.emanuel.soundbox;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by Emanuel on 20/08/2015.
 */
public class BeatBoxActivity extends SingleFragmentActivity {

    private static final String EXTRA_FOLDER_NAME =
            "com.example.emanuel.soundbox.folder_name";


    public static Intent newIntent(Context packageContext, String folderName) {
        Intent intent = new Intent(packageContext, BeatBoxActivity.class);
        intent.putExtra(EXTRA_FOLDER_NAME, folderName);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String folderName = getIntent().getStringExtra(EXTRA_FOLDER_NAME);
        return BeatBoxFragment.newInstance(folderName);
    }
}
