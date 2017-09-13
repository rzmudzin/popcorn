package com.phoenixroberts.popcorn.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.phoenixroberts.popcorn.R;

import java.util.List;
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

    public interface ISelectionDialogItemData
    {
        String getTitle();
        Consumer<IDialogEventData> getOnSelected();
    }

    public interface ISelectionDialogData {
        Context getContext();
        String getTitle();
        List<ISelectionDialogItemData> getChoices();
    }

    public static class SelectionDialogItemData implements ISelectionDialogItemData {
        private String m_Title;
        private Consumer<IDialogEventData> m_OnSelected;
        public SelectionDialogItemData(String title, Consumer<IDialogEventData> onSelected) {
            m_Title=title;
            m_OnSelected=onSelected;
        }
        public String getTitle() {
            return m_Title;
        }
        public Consumer<IDialogEventData> getOnSelected() {
            return m_OnSelected;
        }
    }

    public static class SelectionDialogData implements ISelectionDialogData {
        private Context m_Context;
        private String m_Title;
        private String m_Text;
        private List<ISelectionDialogItemData> m_Choices;

        public SelectionDialogData(Context context, String title, List<ISelectionDialogItemData> choices) {
            m_Context=context;
            m_Title=title;
            m_Choices=choices;
        }
        public Context getContext() {
            return m_Context;
        }
        public String getTitle() {
            return m_Title;
        }
        public List<ISelectionDialogItemData> getChoices() {
            return m_Choices;
        }
    }

    public static class DroidSelectionDialogAdapter extends ArrayAdapter<ISelectionDialogItemData> implements View.OnClickListener {
        private Context m_Context;
        private Dialog m_Dialog;
        private List<ISelectionDialogItemData> m_Selections;
        public DroidSelectionDialogAdapter(Dialog dialog, Context context, List<ISelectionDialogItemData> selections) {
            super(context, R.layout.choice_selection_dialog_item,selections);
            m_Dialog=dialog;
            m_Context=context;
            m_Selections=selections;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            if(v == null) {
                v = LayoutInflater.from(m_Context).inflate(R.layout.choice_selection_dialog_item, parent, false);
                TextView title = (TextView) v.findViewById(R.id.title);
                v.setOnClickListener(this);
                v.setTag(new SelectableItemViewHolder(title));
            }
            SelectableItemViewHolder viewHolder = (SelectableItemViewHolder)v.getTag();
            viewHolder.getTitle().setText(m_Selections.get(position).getTitle());
            viewHolder.setAction((dlg)-> {
                dlg.dismiss();
                m_Selections.get(position).getOnSelected().accept(null);
            });
            return v;
        }

        @Override
        public void onClick(View view) {
            SelectableItemViewHolder viewHolder = (SelectableItemViewHolder)view.getTag();
            viewHolder.getAction().accept(m_Dialog);
        }
        class SelectableItemViewHolder {
            private TextView m_Title;
            private Consumer<Dialog> m_Action;
            public SelectableItemViewHolder(TextView title) {
                m_Title=title;
            }
            public TextView getTitle() { return m_Title; }
            public Consumer<Dialog> getAction() { return m_Action; }
            public void setAction(Consumer<Dialog> action) { m_Action=action; }
        }
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
