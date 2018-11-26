package com.octavian.octaviantaskmanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewListDialog extends Dialog {

    public Activity activity;
    EditText listField;
    Button saveBtn, cancelBtn;
    TextView errorText, errorText2;
    DBHelper dbHelper;

    public NewListDialog(Activity a){
        super(a);
        this.activity = a;
    }

    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_new_list);

        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        listField = findViewById(R.id.listField);
        errorText = findViewById(R.id.errorText);
        errorText2 = findViewById(R.id.errorText2);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorText.setVisibility(View.GONE);
                errorText2.setVisibility(View.GONE);
                dbHelper = new DBHelper(getContext());
                String listName = listField.getText().toString().trim();
                if (listName.equals("")){
                    errorText.setVisibility(View.VISIBLE);
                }else if(dbHelper.getTaskList(listName) != null) {
                    errorText2.setVisibility(View.VISIBLE);
                }else{

                    TaskList list = new TaskList(listField.getText().toString());
                    dbHelper.createList(list);
                    dbHelper.closeDB();

                    Intent updateIntent = new Intent("db.updateTaskLists");

                    getContext().getApplicationContext().sendBroadcast(updateIntent);

                    Toast.makeText(activity, "List created successfully",
                            Toast.LENGTH_LONG).show();
                    dismiss();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        listField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorText.setVisibility(View.GONE);
                errorText2.setVisibility(View.GONE);
            }
        });
    }

}
