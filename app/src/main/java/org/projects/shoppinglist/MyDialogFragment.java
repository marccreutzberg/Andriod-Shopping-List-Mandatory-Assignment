package org.projects.shoppinglist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
/**
 * Created by Marc Creutzberg
 */

public class MyDialogFragment extends DialogFragment {

    public MyDialogFragment()
    {}

    OnPositiveListener mCallback;

    public interface OnPositiveListener {
        void onPositiveClicked();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

            mCallback = (OnPositiveListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()  + " must implement OnPositiveListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alert = new AlertDialog.Builder(
                getActivity());
        alert.setTitle(R.string.confirmation);
        alert.setMessage(R.string.areYouSure);
        alert.setPositiveButton(R.string.yes, pListener);
        alert.setNegativeButton(R.string.no, nListener);

        return alert.create();
    }

    DialogInterface.OnClickListener pListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            positiveClick();
        }
    };


    DialogInterface.OnClickListener nListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            negativeClick();
        }
    };

    protected void positiveClick()
    {
        mCallback.onPositiveClicked();
    }

    protected void negativeClick()
    {

    }
}
