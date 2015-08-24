package com.example.emanuel.soundbox;

/**
 * Created by Emanuel on 24/08/2015.
 */
public class SoundPack {

    private String mFullDirectory;
    private String mDirectory;

    public SoundPack(String fullDirectory) {
        mFullDirectory = fullDirectory;
        mDirectory = DirectoryHelper.cleanFolderName(mFullDirectory);
    }

    public String getFullDirectory() {
        return mFullDirectory;
    }

    public void setFullDirectory(String directory) {
        this.mFullDirectory = directory;
    }

    public String getDirectory() {
        return mDirectory;
    }

    public void setDirectory(String directory) {
        mDirectory = directory;
    }
}
