package com.example.emanuel.soundbox;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * Created by emanuel on 8/18/15.
 */
public class BeatBoxFragment extends Fragment {

    private static final int REQUEST_SOUND = 0;
    private static final int REQUEST_FILE_NAME = 1;
    private static final String DIALOG_FILE = "file";

    private BeatBox mBeatBox;
    private String mFolderName;
    private Uri mUri;

    private FloatingActionButton mFloatingActionButton;

    private static final String EXTRA_FOLDER_NAME =
            "com.example.emanuel.soundbox.folder_name";

    private static final String TAG = "BeatBoxFragment";

    private SoundAdapter mAdapter;
    private RecyclerView mRecycleView;
    private MultiSelector mMultiSelector = new MultiSelector();
    private ModalMultiSelectorCallback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            super.onCreateActionMode(actionMode, menu);
            getActivity().getMenuInflater().inflate(R.menu.context_sounds, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.menu_item_delete_sound) {
                actionMode.finish();

                for (int i = mBeatBox.getSounds().size(); i >= 0; i--) {
                    if (mMultiSelector.isSelected(i, 0)) {
                        String file = mBeatBox.getSounds().get(i).getExternalPath();
                        DirectoryHelper.deleteFile(new File(file));
                        mBeatBox.getSounds().remove(i);
                        mRecycleView.getAdapter().notifyItemRemoved(i);
                    }
                }

                mMultiSelector.clearSelections();
                return true;
            }
            return false;
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        if (mMultiSelector != null) {
            Bundle bundle = savedInstanceState;
            if (bundle != null) {
                mMultiSelector.restoreSelectionStates(bundle.getBundle(TAG));
            }

            if (mMultiSelector.isSelectable()) {
                if (mDeleteMode != null) {
                    mDeleteMode.setClearOnPrepare(false);
                    ((AppCompatActivity) getActivity()).startSupportActionMode(mDeleteMode);
                }

            }
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle(TAG, mMultiSelector.saveSelectionStates());
        super.onSaveInstanceState(outState);
    }

    public static BeatBoxFragment newInstance(String folderName) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_FOLDER_NAME, folderName);

        BeatBoxFragment fragment = new BeatBoxFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mFolderName = (String) getArguments().getSerializable(EXTRA_FOLDER_NAME);

        mBeatBox = new BeatBox(mFolderName);
    }

    private void newSound() {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, REQUEST_SOUND);
    }

    private void newSoundName () {
        FragmentManager fm = getActivity()
                .getSupportFragmentManager();
        EditTextDialog dialog = new AddAudioDialog();
        dialog.setTargetFragment(BeatBoxFragment.this, REQUEST_FILE_NAME);
        dialog.show(fm, DIALOG_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_SOUND) {
            mUri = data.getData();
            newSoundName();
        } else if (requestCode == REQUEST_FILE_NAME) {
            String recordingName = (String) data
                    .getSerializableExtra(EditTextDialog.EXTRA_NAME);

            try {
                String filePath = getAudioFilePathFromUri(mUri);
                String extension = DirectoryHelper.getFileExtension(new File(filePath));
                String targetDestination = DirectoryHelper.getDirLocation(mBeatBox.getSoundsFolder())
                        + "/" + recordingName + "." + extension;
                copyFile(new File(filePath), new File(targetDestination));
                getActivity().getContentResolver().delete(mUri, null, null);
                (new File(filePath)).delete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                updateUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getAudioFilePathFromUri(Uri uri) {
        Cursor cursor = getActivity().getContentResolver()
                .query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
        return cursor.getString(index);
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        } else {
            Toast.makeText(getActivity(), R.string.file_already_exists, Toast.LENGTH_SHORT)
                    .show();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_beat_box, container, false);

        mRecycleView = (RecyclerView) view
                .findViewById(R.id.fragment_beat_box_recycler_view);
        mRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecycleView.setAdapter(new SoundAdapter(mBeatBox.getSounds()));

        if (NavUtils.getParentActivityName(getActivity()) != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
        }

        try {
            updateUI();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mFloatingActionButton = (FloatingActionButton) view
                .findViewById(R.id.floating_action_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newSound();
            }
        });

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mBeatBox.getSoundsFolder());

        return view;
    }

    private class SoundHolder extends SwappingHolder
            implements View.OnClickListener, View.OnLongClickListener {
        private Button mButton;
        private Sound mSound;

        public SoundHolder(View itemView) {
            super(itemView, mMultiSelector);

            mButton = (Button) itemView.findViewById(R.id.list_item_sound_button);

            mButton.setOnClickListener(this);
            mButton.setLongClickable(true);
            mButton.setOnLongClickListener(this);
        }

        public void bindSound(Sound sound) {
            mSound = sound;
            mButton.setText(mSound.getName());
        }

        @Override
        public void onClick(View v) {
            if (!mMultiSelector.tapSelection(this)){
                mBeatBox.play(mSound);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            ((AppCompatActivity) getActivity()).startSupportActionMode(mDeleteMode);
            mMultiSelector.setSelected(this, true);
            return true;
        }
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {

        private List<Sound> mSounds;

        public SoundAdapter(List<Sound> sounds) {
            mSounds = sounds;
        }

        @Override
        public SoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_sound, parent, false);
            return new SoundHolder(view);
        }

        @Override
        public void onBindViewHolder(SoundHolder soundHolder, int position) {
            Sound sound = mSounds.get(position);
            soundHolder.bindSound(sound);
        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }

        public void setSounds(List<Sound> Sounds) {
            mSounds = Sounds;
        }
    }

    private void updateUI() throws IOException{
        BeatBox beatBox = new BeatBox(mFolderName);

        if (mAdapter == null) {
            mAdapter = new SoundAdapter(beatBox.getSounds());
            mRecycleView.setAdapter(mAdapter);
        } else {
            mAdapter.setSounds(beatBox.getSounds());
            mAdapter.notifyDataSetChanged();
        }

        mBeatBox = beatBox;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecycleView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeatBox.release();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                    //getActivity().onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.context_sounds, menu);
    }

}
