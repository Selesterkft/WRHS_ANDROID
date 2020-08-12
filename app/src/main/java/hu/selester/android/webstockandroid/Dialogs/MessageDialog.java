package hu.selester.android.webstockandroid.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import hu.selester.android.webstockandroid.Objects.MessageBoxSettingsObject;
import hu.selester.android.webstockandroid.R;

public class MessageDialog extends DialogFragment {

    private String headerText;
    private String contentText;
    private DialogInterface.OnClickListener okBtnListener;
    private MessageBoxSettingsObject settings;
    private int iconPic;

    public static final int NONE = 0;
    public static final int DONE = R.drawable.dialog_done;
    public static final int ERROR = R.drawable.dialog_error;
    public static final int WARNING = R.drawable.dialog_warning;
    public static final int DELETE = R.drawable.dialog_delete;


    public MessageDialog(int iconPic, String headerText, String contentText, MessageBoxSettingsObject settings, DialogInterface.OnClickListener okBtnListener){
        this.iconPic        = iconPic;
        this.headerText     = headerText;
        this.contentText    = contentText;
        this.okBtnListener  = okBtnListener;
        this.settings       = settings;
    }

    public MessageDialog(int iconPic, String headerText, String contentText, MessageBoxSettingsObject settings){
        this.iconPic        = iconPic;
        this.headerText     = headerText;
        this.contentText    = contentText;
        this.settings       = settings;
    }

    public MessageDialog(int iconPic, String headerText, String contentText){
        this.iconPic        = iconPic;
        this.headerText     = headerText;
        this.contentText    = contentText;
        settings = new MessageBoxSettingsObject();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(headerText);
        builder.setMessage(contentText);
        builder.setIcon(iconPic);
        String btn1Text;
        if(settings.getBtn1Label()!=null){ btn1Text = settings.getBtn1Label(); }else{ btn1Text = "OK"; }
        String btn2Text;
        if(settings.getBtn2Label()!=null){ btn2Text = settings.getBtn2Label(); }else{ btn2Text = "Mégsem"; }

        if(okBtnListener!=null){
            builder.setPositiveButton(btn1Text, okBtnListener);
        }

        builder.setNegativeButton(btn2Text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getDialog().dismiss();
            }
            // Create the AlertDialog object and return it
        });
         return builder.create();

    }
}
