package com.changhong.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.changhong.faceattendance.R;


public class ConnectDialog {
    private static Dialog dialog;
    public static final String DIALOG_MSG = "数据加载中…";
    
    public static Dialog getDialog() {
        return dialog;
    }
    
    public static void showDialog(Context context, String msg) {
        if (TextUtils.isEmpty(msg)) {
            msg = DIALOG_MSG;
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = createLoadingDialog(context, msg);
        // sdk14以后，不设置为false，当显示对话框的时候，点击屏幕会被取消
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void showDialog(Context context) {
        showDialog(context, "");
    }


    public static void dismiss() {
        try {
            if (dialog == null) {
                return;
            }
            dialog.dismiss();
            dialog = null;
        } catch (Exception e) {
            e.printStackTrace();
            dialog = null;
        }
    }


    public static Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.lodaing_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

//        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));// 设置布局
        return loadingDialog;
    }

}
