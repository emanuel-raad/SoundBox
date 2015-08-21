package com.example.emanuel.soundbox;

/**
 * Created by Emanuel on 20/08/2015.
 */
public class AddAudioDialog extends EditTextDialog {
    @Override
    protected int title() {
        return R.string.new_audio;
    }

    @Override
    protected int validationString() {
        return R.string.audio_not_valid;
    }
}
