package com.example.emanuel.soundbox;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Emanuel on 20/08/2015.
 */
public abstract class EditTextDialog extends DialogFragment {

    protected abstract int title();
    protected abstract int validationString();

    public static final String EXTRA_NAME =
            "com.example.emanuel.soundbox.name";

    private EditText mNameEditText;

    private void sendResult (int resultCode, String name) {
        if (getTargetFragment() == null)
            return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_NAME, name);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private boolean validate (String text) {
        if (!(text.isEmpty() && (text != null))) {
            text = text.trim();
            String regex = "^[a-zA-Z0-9_ ]+$";
            return text.matches(regex);
        } else return false;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_name, null);

        mNameEditText = (EditText) view.findViewById(R.id.new_sound_editText);

        final AlertDialog d = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(title())
                .setPositiveButton(android.R.string.ok, null)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (validate(mNameEditText.getText().toString())) {
                            d.dismiss();
                            sendResult(Activity.RESULT_OK, (mNameEditText.getText()).toString());
                        } else {
                            Toast.makeText(getActivity(), validationString(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            }
        });

        return d;
    }
}
