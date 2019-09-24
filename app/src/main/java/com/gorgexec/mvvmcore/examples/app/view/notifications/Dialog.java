package com.gorgexec.mvvmcore.examples.app.view.notifications;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.gorgexec.mvvmcore.examples.app.R;


public class Dialog {

    public interface ISingleResponseHandler {
        void onDialogResponse();
    }

    public interface IDoubleResponseHandler {
        void onDialogResponse(boolean positive);
    }

    public interface ISingleChoiceResponseHandler {
        void onDialogResponse(boolean positive, int position);
    }

    private String title;
    private int titleResId = -1;
    private String message;
    private int messageResId = -1;

    private String positiveButtonText = "";
    private int positiveButtonTextResId = -1;

    private String negativeButtonText = "";
    private int negativeButtonTextResId = -1;

    private boolean cancelable = true;

    private ISingleResponseHandler singleHandler;
    private IDoubleResponseHandler doubleHandler;
    private ISingleResponseHandler cancelHandler;

    private ISingleChoiceResponseHandler singleChoiceHandler;

    private ISingleChoiceResponseHandler itemSelectionHandler;



    private String[] items;

    private int checkedItemPosition = -1;

    private Dialog() {
    }

    public Dialog title(String title) {
        this.title = title;
        return this;
    }

    public Dialog title(int resId) {
        this.titleResId = resId;
        return this;
    }

    public Dialog message(String message) {
        this.message = message;
        return this;
    }

    public Dialog message(int resId) {
        this.messageResId = resId;
        return this;
    }

    public Dialog message(String[] items) {
        this.items = items;
        return this;
    }

    public Dialog checkedItem(int position) {
        this.checkedItemPosition = position;
        return this;
    }

    public Dialog positiveText(String text) {
        this.positiveButtonText = text;
        return this;
    }

    public Dialog positiveText(int resId) {
        this.positiveButtonTextResId = resId;
        return this;
    }

    public Dialog negativeText(String text) {
        this.negativeButtonText = text;
        return this;
    }

    public Dialog negativeText(int resId) {
        this.negativeButtonTextResId = resId;
        return this;
    }

    public Dialog setHandler(ISingleResponseHandler handler) {
        this.singleHandler = handler;
        return this;
    }

    public Dialog setHandler(IDoubleResponseHandler handler) {
        this.doubleHandler = handler;
        return this;
    }

    public Dialog setHandler(ISingleChoiceResponseHandler handler) {
        this.singleChoiceHandler = handler;
        return this;
    }

    public Dialog setItemSelectionHandler(ISingleChoiceResponseHandler handler) {
        this.itemSelectionHandler = handler;
        return this;
    }

    public Dialog setCancelHandler(ISingleResponseHandler handler) {
        this.cancelHandler = handler;
        return this;
    }

    public Dialog notCancelable(){
        this.cancelable = false;
        return this;
    }

    public void show(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(titleResId != -1 ? context.getString(titleResId) : title)
                .setMessage(messageResId != -1 ? context.getString(messageResId) : message);

        if (doubleHandler != null) {

            String positiveText = positiveButtonTextResId != -1
                    ? context.getString(positiveButtonTextResId)
                    : positiveButtonText.isEmpty()
                    ? context.getString(R.string.buttonDialogYes)
                    : positiveButtonText;

            builder.setPositiveButton(positiveText, this::onDialogPositiveFeedback);

            String negativeText = negativeButtonTextResId != -1
                    ? context.getString(negativeButtonTextResId)
                    : negativeButtonText.isEmpty()
                    ? context.getString(R.string.buttonDialogNo)
                    : negativeButtonText;

            builder.setNegativeButton(negativeText, this::onDialogNegativeFeedback);

        } else if (itemSelectionHandler == null) {
            String positiveText = positiveButtonText.isEmpty() ? context.getString(R.string.buttonDialogSubmit) : positiveButtonText;
            builder.setPositiveButton(positiveText, this::onDialogPositiveFeedback);
        }

        if (items != null) {
            if (singleChoiceHandler != null) {
                builder.setSingleChoiceItems(items, checkedItemPosition, this::onDialogSelectedChoiceFeedback);
            } else {
                builder.setItems(items, this::onDialogSelectedItemFeedback);
            }
        }

        if(cancelHandler !=null){
            builder.setOnCancelListener(dialog -> {
                dialog.dismiss();
                cancelHandler.onDialogResponse();
            });
        }

        AlertDialog dialog = builder.create();
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        dialog.show();
    }


    private void onDialogPositiveFeedback(DialogInterface dialog, int which) {
        dialog.dismiss();
        if (doubleHandler != null) {
            doubleHandler.onDialogResponse(true);
        } else if (singleHandler != null) {
            singleHandler.onDialogResponse();
        } else if (singleChoiceHandler != null) {
            singleChoiceHandler.onDialogResponse(true, -1);
        }

    }

    private void onDialogNegativeFeedback(DialogInterface dialog, int which) {
        dialog.dismiss();
        if (doubleHandler != null) {
            doubleHandler.onDialogResponse(false);
        } else if (singleChoiceHandler != null) {
            singleChoiceHandler.onDialogResponse(false, -1);
        }
    }

    private void onDialogSelectedChoiceFeedback(DialogInterface dialog, int position) {
        dialog.dismiss();
        if (singleChoiceHandler != null) {
            singleChoiceHandler.onDialogResponse(false, position);
        }
    }

    private void onDialogSelectedItemFeedback(DialogInterface dialog, int position) {
        dialog.dismiss();
        if (itemSelectionHandler != null) {
            itemSelectionHandler.onDialogResponse(false, position);
        }
    }

    public static Dialog create() {
        return new Dialog();
    }

}
