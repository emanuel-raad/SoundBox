package com.example.emanuel.soundbox;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emanuel on 20/08/2015.
 */
public class DirectoryHelper {

    private static final String TAG = "DirectoryHelper";

    public static List<String> listDirsInFolder(String parentDir) throws IOException {

        List<String> folders = new ArrayList<>();

        File voiceRecordingDirectory = new File(Environment.getExternalStorageDirectory()
                + "/" + parentDir + "/");

        File[] possibleFolders = voiceRecordingDirectory.listFiles();

        for (File file : possibleFolders) {
            if (file.isDirectory() && file.exists() && file.canWrite()) {
                folders.add(cleanFolderName(file.toString()));
                Log.d(TAG, file.toString() + " is a directory");
            }
        }

        return folders;
    }

    public static String[] listFilesInDir(String parentDir) throws IOException {

        File voiceRecordingDirectory = new File(Environment.getExternalStorageDirectory()
                + "/beatbox/" + parentDir);

        String[] possibleFiles = voiceRecordingDirectory.list();

        return possibleFiles;
    }

    public static String getDirLocation(String dir) {
        return Environment.getExternalStorageDirectory() + "/beatbox/" + dir;
    }


    public static boolean createDirectory (String parentName, String dirName) {
        File voiceRecordingDirectory = new File(Environment.getExternalStorageDirectory()
                + "/" + parentName + "/" + dirName + "/");

        if (!voiceRecordingDirectory.exists()){
            Log.d(TAG, "aight making dir");
            voiceRecordingDirectory.mkdirs();
            return true;
        } else {
            return false;
        }
    }

    public static String dirNameToText (String dirName) {
        return dirName.replace("_", " ");
    }

    public static String removeExtension (String file) {
        return file.substring(0, file.lastIndexOf('.'));
    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    public static String cleanFolderName (String folderName) {
        String[] components = folderName.split("/");
        String filename = components[components.length - 1];
        return filename;
    }
}
