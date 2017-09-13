package com.phoenixroberts.popcorn.dialogs;

import android.content.Context;

import java.util.function.Consumer;

/**
 * Created by rzmudzinski on 9/10/17.
 */

public class Dialogs {
    public interface IDialogEventData {}
    public interface IDialogTextChangedEventData extends IDialogEventData {
        String getText();
    }
    public static interface IDialogData {
        String getTitle();
        String getText();
        String getOkText();
        Consumer<IDialogEventData> getOkAction();
        Context getContext();
    }

    public interface ITextInputDialogData
    {
        Context getContext();
        String getTitle();
        String getText();
        String getOkText();
        Consumer<IDialogEventData> getOkAction();
        String getCancelText();
        Consumer<IDialogEventData> getCancelAction();
        Consumer<IDialogEventData> getTextChangedAction();
    }

    public static class TextInputDialogData implements ITextInputDialogData
    {
        private String m_Title;
        private String m_OkText;
        private String m_CancelText;
        private String m_Text;
        private Context m_Context;
        private Consumer<IDialogEventData> m_OkEventHandler;
        private Consumer<IDialogEventData> m_CancelEventHandler;
        private Consumer<IDialogEventData> m_TextChangedEventHandler;

        public TextInputDialogData(Context context, String title, String okText, String cancelText, String text,Consumer<IDialogEventData> okEventHandler,
                                   Consumer<IDialogEventData> cancelEventHandler, Consumer<IDialogEventData> textChangedEventHandler) {
            m_Context=context;
            m_Title=title;
            m_OkText=okText;
            m_CancelText=cancelText;
            m_Text=text;
            m_OkEventHandler=okEventHandler;
            m_CancelEventHandler=cancelEventHandler;
            m_TextChangedEventHandler=textChangedEventHandler;
        }

        public Context getContext() {
            return m_Context;
        }
        public String getTitle() {
            return m_Title;
        }
        public String getText() {
            return m_Text;
        }
        public String getOkText() {
            return m_OkText;
        }
        public String getCancelText() {
            return m_CancelText;
        }
        public Consumer<IDialogEventData> getOkAction() {
            return m_OkEventHandler;
        }
        public Consumer<IDialogEventData> getCancelAction() {
            return m_CancelEventHandler;
        }
        public Consumer<IDialogEventData> getTextChangedAction() {
            return m_TextChangedEventHandler;
        }
        public static class TextChangedEventArgs implements IDialogTextChangedEventData
        {
            String m_Text;
            public TextChangedEventArgs(String text) {
                m_Text=text;
            }
            public String getText() {
                return m_Text;
            }
        }
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
