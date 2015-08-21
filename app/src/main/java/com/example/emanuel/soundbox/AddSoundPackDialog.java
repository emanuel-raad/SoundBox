package com.example.emanuel.soundbox;

/**
 * Created by Emanuel on 20/08/2015.
 */
public class AddSoundPackDialog extends EditTextDialog {
    @Override
    protected int title() {
        return R.string.new_sound_pack;
    }

    @Override
    protected int validationString() {
        return R.string.sound_pack_not_valid;
    }
}
