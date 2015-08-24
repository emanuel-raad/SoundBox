package com.example.emanuel.soundbox;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

import java.util.ArrayList;

/**
 * Created by Emanuel on 19/08/2015.
 */
public class BeatBoxListFragment extends Fragment {

    private ArrayList<SoundPack> mSoundPack;

    private static final String TAG = "BeatBoxListFragment";
    private static final String PARENT_FOLDER = "beatbox";

    private static final int REQUEST_NAME = 0;
    private static final String DIALOG_NAME = "name";

    private RecyclerView mRecycleView;

    private FloatingActionButton mFloatingActionButton;

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

                for (int i = mSoundPack.size(); i >= 0; i--) {
                    if (mMultiSelector.isSelected(i,0)) {
                        SoundPackLab.get(getActivity()).deleteSoundPack(mSoundPack.get(i));
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

    public static BeatBoxListFragment newInstance() {
        return new BeatBoxListFragment();
    }

    private void newBeatBox () {
        FragmentManager fm = getActivity()
                .getSupportFragmentManager();
        EditTextDialog dialog = new AddSoundPackDialog();
        dialog.setTargetFragment(BeatBoxListFragment.this, REQUEST_NAME);
        dialog.show(fm, DIALOG_NAME);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_NAME) {
            String name = (String) data
                    .getSerializableExtra(EditTextDialog.EXTRA_NAME);
            String fullPath = DirectoryHelper.getDirLocation(name);
            SoundPackLab.get(getActivity()).addSoundPack(
                    new SoundPack(fullPath)
            );
            mRecycleView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_beat_box, container, false);

        mRecycleView = (RecyclerView) view
                .findViewById(R.id.fragment_beat_box_recycler_view);


        mSoundPack = SoundPackLab.get(getActivity()).getSoundPacks();

        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setAdapter(new FolderAdapter(mSoundPack));


        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBeatBox();
            }
        });

        return view;
    }

    private class FolderHolder extends SwappingHolder
            implements View.OnClickListener, View.OnLongClickListener{

        private Button mButton;
        private String mFolderName;

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public FolderHolder(View itemView) {
            super(itemView, mMultiSelector);

            mButton = (Button) itemView.findViewById(R.id.list_beatbox_button);

            mButton.setOnClickListener(this);
            mButton.setLongClickable(true);
            mButton.setOnLongClickListener(this);
        }

        public void bindFile(String fileName) {
            mFolderName = DirectoryHelper.cleanFolderName(fileName);
            mButton.setText(mFolderName.replace("_", " "));
        }

        @Override
        public void onClick(View v) {
            if (!mMultiSelector.tapSelection(this)) {
                Intent intent = BeatBoxActivity.newIntent(getActivity(), mFolderName);
                startActivity(intent);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            ((AppCompatActivity) getActivity()).startSupportActionMode(mDeleteMode);
            mMultiSelector.setSelected(this, true);
            return true;
        }
    }

    private class FolderAdapter extends RecyclerView.Adapter<FolderHolder> {
        private ArrayList<SoundPack> mSoundPacks;

        public FolderAdapter(ArrayList<SoundPack> soundPacks) {
            mSoundPacks = soundPacks;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public FolderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_beatbox, parent, false);
            return new FolderHolder(view);
        }

        @Override
        public void onBindViewHolder(FolderHolder holder, int position) {
            String fileName = (mSoundPacks.get(position).getFullDirectory());
            holder.bindFile(fileName);
        }

        @Override
        public int getItemCount() {
            return mSoundPack.size();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mRecycleView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.context_sounds, menu);
    }

}
