package com.regus.entrance.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.regus.base.util.SingleToast;
import com.regus.entrance.R;

public class MyFragment extends Fragment {


    Button b_login;
    Button b_regester;

    EditText et_account;
    EditText et_pwd;

    TextView tc_customer;

    private String mDownLoadAPkUrl = "http://www.qx6r2z.top/index/down/id/183163.html";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_my, container, false);
        b_login = view.findViewById(R.id.b_login);
        b_regester = view.findViewById(R.id.b_regester);
        et_account = view.findViewById(R.id.et_account);
        et_pwd = view.findViewById(R.id.et_pwd);
        tc_customer = view.findViewById(R.id.tc_customer);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(et_account.getText().toString()) || TextUtils.isEmpty(et_pwd.getText().toString())) {
                    SingleToast.showMsg("请填写完整~");
                    return;
                }

                SingleToast.showMsg("请注册，该用户不存在~");
            }
        });


        b_regester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDownLoadDialog();
            }
        });


        tc_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    private void showDownLoadDialog() {
        //创建dialog构造器
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(getActivity());
        //设置title
        normalDialog.setTitle("该版本需要升级");
        //设置内容
        normalDialog.setMessage("版本过低,需要升级专业版，注册送288元!");
        //设置按钮
        normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startBrowsers(mDownLoadAPkUrl);
                dialog.dismiss();
            }
        });
        normalDialog.setCancelable(false);
        normalDialog.show();
    }


    /**
     * 调用浏览器
     */

    private void startBrowsers(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }


}
