package com.dm.sam.listener;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class FormValidationWatcher implements TextWatcher {

    Button save_btn;

    public FormValidationWatcher(Button save_btn) {
        this.save_btn = save_btn;
    }


    /**
     * This method is triggered when a text field is not changed yet in
     * order to enable the save button for the update mode
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(!charSequence.toString().equals("") && charSequence.toString()!=null ){
            save_btn.setEnabled(true);
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    /**
     * This method is triggered after a text field is changed in
     * order to enable the save button for the add mode
     */
    @Override
    public void afterTextChanged(Editable editable) {
        if(!editable.toString().equals("") && editable.toString()!=null){
            save_btn.setEnabled(true);
        }else{
            save_btn.setEnabled(false);
        }
    }
}
