package com.phoenixroberts.popcorn.dialogs;

/**
 * Created by rzmudzinski on 9/11/17.
 */

public interface IDialogService {
    void DisplayNotificationDialog(Dialogs.IDialogData dialogData);
//    void DisplayConfirmationDialog(IConfirmationDialogData dialogData);
//    void DisplayChoiceSelectionDialog(ISelectionDialogData dialogData);
    void DisplayTextInputDialog(Dialogs.ITextInputDialogData data);
}
