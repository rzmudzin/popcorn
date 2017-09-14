package com.phoenixroberts.popcorn.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.phoenixroberts.popcorn.R;

import java.util.function.Consumer;

/**
 * Created by rzmudzinski on 9/11/17.
 */

public class DialogService implements IDialogService {
    private static DialogService m_Instance = new DialogService();
    public static DialogService getInstance() {
        return m_Instance;
    }
    public void DisplayNotificationDialog(Dialogs.IDialogData dialogData) {
        if (dialogData == null)
        {
            return;
        }
        Dialog dialog = new Dialog(dialogData.getContext());
        Window dialogWindow = dialog.getWindow();
        if(dialogWindow!=null) {
            dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
            dialogWindow.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setContentView(R.layout.notification_dialog);
        dialog.setCancelable(false);
        TextView alertTitle = (TextView)dialog.findViewById(R.id.title);
        alertTitle.setText(dialogData.getTitle());
        alertTitle.setTypeface(null,Typeface.BOLD);
        TextView alertMessage = (TextView)dialog.findViewById(R.id.message);
        alertMessage.setText(dialogData.getText());

        Button ok = (Button)dialog.findViewById(R.id.okButton);
        ok.setText(dialogData.getOkText());
        ok.setOnClickListener((v) -> {
            dialog.dismiss();
            Consumer<Dialogs.IDialogEventData> okAction = dialogData.getOkAction();
            if(okAction!=null) {
                okAction.accept(null);
            }
        });
        dialog.show();
    }
    public void DisplayTextInputDialog(Dialogs.ITextInputDialogData dialogData) {

        Dialog dialog = new Dialog(dialogData.getContext());
        Window dialogWindow = dialog.getWindow();
        if(dialogWindow!=null) {
            dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
            dialogWindow.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setContentView(R.layout.text_input_dialog_data);
        dialog.setCancelable(false);
        TextView alertTitle = (TextView)dialog.findViewById(R.id.title);
        alertTitle.setText(dialogData.getTitle());
        alertTitle.setTypeface(null,Typeface.BOLD);
        TextView alertMessage = (TextView)dialog.findViewById(R.id.message);
        alertMessage.setText(dialogData.getText());
        EditText editText = (EditText)dialog.findViewById(R.id.inputValue);
        Button ok = (Button)dialog.findViewById(R.id.okButton);
        ok.setText(dialogData.getOkText());
        ok.setOnClickListener((v) -> {
            dialog.dismiss();
            Consumer<Dialogs.IDialogEventData> okAction = dialogData.getOkAction();
            if(okAction!=null) {
                String inputValue = editText.getText().toString();
                okAction.accept(new Dialogs.TextInputDialogData.TextChangedEventArgs(inputValue));
            }
        });

        Button cancel = (Button)dialog.findViewById(R.id.cancelButton);
        cancel.setText(dialogData.getCancelText());
        cancel.setOnClickListener((v) -> {
            dialog.dismiss();
            Consumer<Dialogs.IDialogEventData> cancelAction = dialogData.getCancelAction();
            if(cancelAction!=null) {
                cancelAction.accept(null);
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String inputValue = editText.getText().toString();
//                ok.setEnabled(inputValue.length()>3?true:false);
                Consumer<Dialogs.IDialogEventData> onTextChangedAction = dialogData.getTextChangedAction();
                if(onTextChangedAction!=null) {
                    onTextChangedAction.accept(new Dialogs.TextInputDialogData.TextChangedEventArgs(inputValue));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }
    public void DisplayChoiceSelectionDialog(Dialogs.ISelectionDialogData dialogData) {
        Dialog dialog = new Dialog(dialogData.getContext());
        Window dialogWindow = dialog.getWindow();
        if(dialogWindow!=null) {
            dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
            dialogWindow.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setContentView(R.layout.choice_selection_dialog);
        dialog.setCancelable(false);
        TextView alertTitle = (TextView)dialog.findViewById(R.id.title);
        alertTitle.setText(dialogData.getTitle());
        alertTitle.setTypeface(null,Typeface.BOLD);
        ListView selectionsList = (ListView)dialog.findViewById(R.id.itemsList);
        selectionsList.setAdapter(new Dialogs.DroidSelectionDialogAdapter(dialog,dialogData.getContext(),dialogData.getChoices()));
        Button cancel = (Button)dialog.findViewById(R.id.cancelButton);
        cancel.setText("Cancel");
        cancel.setOnClickListener((v) -> {
            dialog.dismiss();
        });
        dialog.show();
    }
}
