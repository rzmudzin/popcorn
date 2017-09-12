package com.phoenixroberts.popcorn.dialogs;

import android.content.Context;

import java.util.function.Consumer;

/**
 * Created by rzmudzinski on 9/10/17.
 */

public class Dialogs {
    public interface IDialogEventData {}
    public static interface IDialogData {
        String getTitle();
//        void setTitle(String dialogTitle);
        String getText();
//        String setText(String dialogText);
        String getOkText();
//        void setOkText(String okButtonText);
        Consumer<IDialogEventData> getOkAction();
//        void setOkAction(Consumer<IDialogEventData> eventHandler);
        Context getContext();
    }

    public static class DialogData implements IDialogData {
        private String m_Title;
        private String m_Text;
        private String m_OkText;
        private Context m_Context;
        private Consumer<IDialogEventData> m_EventHandler;

        public DialogData(Context context, String title, String text, String okText, Consumer<IDialogEventData> eventHandler) {
            m_Context=context;
            m_Title=title;
            m_Text=text;
            m_OkText=okText;
            m_EventHandler=eventHandler;
        }

        public Context getContext() {
            return m_Context;
        }

        public String getTitle() {
            return m_Title;
        }
        public void setTitle(String dialogTitle) {
            m_Title = dialogTitle;
        }
        public String getText() {
            return m_Text;
        }

        public String getOkText() {
            return m_OkText;
        }

        public Consumer<IDialogEventData> getOkAction() {
            return m_EventHandler;
        }

    }
}
