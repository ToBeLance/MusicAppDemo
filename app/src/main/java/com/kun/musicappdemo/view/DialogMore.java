package com.kun.musicappdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kun.musicappdemo.R;

//已废弃使用，使用BottomSheetDialog代替，要是需要生命周期监听，感觉可以使用这个
public class DialogMore extends BottomSheetDialogFragment {
    private int height = 0;

    public DialogMore newInstance() {
        return new DialogMore();
    }

    public DialogMore setDialogHeight(int height) {
        this.height = height;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentDialogStyle);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) getDialog();
        FrameLayout view = bottomSheetDialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(view);
        behavior.setPeekHeight(height);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_item_dialog_more, null);
        dialog.setContentView(view);
        view.findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return dialog;
    }
}
