package com.phoenixroberts.popcorn.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.phoenixroberts.popcorn.R;

/**
 * Created by robz on 9/7/17.
 */

public class StatusDialog {

    private ShowStatusRequest m_Request;
    private Dialog m_Dialog;


    public StatusDialog(ShowStatusRequest request)
    {
        m_Request = request;
    }
    public void showDialog() {
        showStatus(m_Request);
    }
    public void dismissDialog() {
        actionDismiss();
    }
    public void showStatus(ShowStatusRequest request)
    {
        showStatus(request.getContext(), request.getDisplaySpinner(), request.getStatusText(),
                request.getMaskType(), request.getIsCentered());
    }

    public void showStatus(Context context, boolean spinner, String status, MaskType maskType, boolean centered)
    {
        actionDismiss();
        setupDialog(status, context, maskType, spinner, true);
    }
    private int dpToPx(Context context, int dp)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = (int)Math.round((double)dp * ((double)displayMetrics.xdpi / (double) DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
    private void actionDismiss()
    {
        if (m_Dialog != null)
        {
            m_Dialog.hide();
            m_Dialog.dismiss();
            m_Dialog = null;
        }
    }
    private void setupDialog(String status, Context context, StatusDialog.MaskType maskType, boolean displaySpinner, boolean showCentered)
    {
        View view;
        m_Dialog = new Dialog(context);

        m_Dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        if (maskType != StatusDialog.MaskType.Black) {
            Window dialogWindow = m_Dialog.getWindow();
            if(dialogWindow!=null) {
                dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        }

        if (maskType == StatusDialog.MaskType.None) {
            m_Dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        }


        m_Dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));

        view = View.inflate(context, R.layout.loading, null);
        TextView statusTextView = (TextView)view.findViewById(R.id.textViewStatus);

        if (displaySpinner == false)
        {
            View progressBar = view.findViewById(R.id.loadingProgressBar);
            progressBar.setVisibility(View.GONE);
        }
        if (maskType != StatusDialog.MaskType.Black)
        {
            view.setBackgroundResource(R.drawable.roundedbgdark);
        }

        if (statusTextView != null)
        {
            statusTextView.setText(status==null?"":status);
            statusTextView.setVisibility(status==null||status.equals("")?View.GONE:View.VISIBLE);
        }
        if (!showCentered)
        {
            m_Dialog.getWindow().setGravity(Gravity.CENTER);

            WindowManager.LayoutParams p = m_Dialog.getWindow().getAttributes();

            p.y = dpToPx(context, 22);

            m_Dialog.getWindow().setAttributes(p);

        }

        m_Dialog.setContentView(view);
        m_Dialog.show();

    }
    public static class ShowStatusRequest
    {
        private Context m_Context;
        private boolean m_bDisplaySpinner;
        private String m_StatusText;
        private MaskType m_MaskType;
        private boolean m_bIsCentered;

        public ShowStatusRequest(Context context, boolean displaySpinner, String statusText, MaskType maskType, boolean isCentered) {
            m_Context=context;
            m_bDisplaySpinner=displaySpinner;
            m_StatusText=statusText;
            m_MaskType=maskType;
            m_bIsCentered=isCentered;
        }
        public Context getContext() {
            return m_Context;
        }
        public boolean getDisplaySpinner() {
            return m_bDisplaySpinner;
        }
        public String getStatusText() {
            return m_StatusText;
        }
        public MaskType getMaskType() {
            return m_MaskType;
        }
        public boolean getIsCentered() {
            return m_bIsCentered;
        }
    }
    public enum MaskType
    {
        None ,
        Clear,
        Black
    }
}
