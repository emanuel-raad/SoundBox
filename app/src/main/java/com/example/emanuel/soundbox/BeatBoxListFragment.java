package com.example.emanuel.soundbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;
import java.util.List;

/**
 * Created by Emanuel on 19/08/2015.
 */
public class BeatBoxListFragment extends Fragment {

    private List<String> mFoldersList;

    private static final String TAG = "BeatBoxListFragment";
    private static final String PARENT_FOLDER = "beatbox";

    private static final int REQUEST_NAME = 0;
    private static final String DIALOG_NAME = "name";

    private FolderAdapter mAdapter;
    private RecyclerView mRecycleView;

    private FloatingActionButton mFloatingActionButton;

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
            DirectoryHelper.createDirectory(PARENT_FOLDER, name);
            try {
                updateUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void createDefaultFolders() throws IOException {
        DirectoryHelper.createDirectory(PARENT_FOLDER, "sfayla");
        DirectoryHelper.createDirectory(PARENT_FOLDER, "sample_sounds");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_beat_box, container, false);

        mRecycleView = (RecyclerView) view
                .findViewById(R.id.fragment_beat_box_recycler_view);

        try {
            createDefaultFolders();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setAdapter(new FolderAdapter(mFoldersList));

        try {
            updateUI();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBeatBox();
            }
        });

        return view;
    }

    private class FolderHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Button mButton;
        private String mFolderName;

        public FolderHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.list_beatbox, container, false));

            mButton = (Button) itemView.findViewById(R.id.list_beatbox_button);
            mButton.setOnClickListener(this);
        }

        public void bindFile(String fileName) {
            mFolderName = fileName;
            mButton.setText(mFolderName.replace("_", " "));
        }

        @Override
        public void onClick(View v) {
            Intent intent = BeatBoxActivity.newIntent(getActivity(), mFolderName);
            startActivity(intent);
        }
    }

    private class FolderAdapter extends RecyclerView.Adapter<FolderHolder> {

        private List<String> mFolders;

        public FolderAdapter (List<String> folders) {
            mFolders = folders;
        }

        @Override
        public FolderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new FolderHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(FolderHolder holder, int position) {
            String fileName = (mFolders.get(position));
            holder.bindFile(fileName);
        }

        @Override
        public int getItemCount() {
            return mFolders.size();
        }

        public void setFolders(List<String> folders) {
            mFolders = folders;
        }
    }

    private void updateUI() throws IOException{
        List<String> folderList = DirectoryHelper.listDirsInFolder(PARENT_FOLDER);

        if (mAdapter == null) {
            mAdapter = new FolderAdapter(folderList);
            mRecycleView.setAdapter(mAdapter);
        } else {
            mAdapter.setFolders(folderList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            updateUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
