package com.phoenixroberts.popcorn.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Button;
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
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.notification_dialog);
        dialog.setCancelable(false);
        TextView alertTitle = (TextView)dialog.findViewById(R.id.title);
        alertTitle.setText(dialogData.getTitle());

        //alertTitle.setTypeface(new Typeface(Typeface.BOLD));

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
}
