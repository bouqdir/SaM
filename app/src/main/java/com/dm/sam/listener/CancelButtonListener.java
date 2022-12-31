package com.dm.sam.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import com.dm.sam.activity.CarteActivity;


public class CancelButtonListener implements View.OnClickListener{
    Activity current, next;

    public CancelButtonListener(Activity current, Activity next) {
        this.current = current;
        this.next = next;
    }

    @Override
    public void onClick(View view) {
        current.startActivity(new Intent(current, next.getClass()));
    }
}
