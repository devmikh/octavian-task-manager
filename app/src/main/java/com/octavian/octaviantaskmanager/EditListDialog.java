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

public class EditListDialog extends Dialog{

    public Activity activity;
    EditText listField;
    Button saveBtn, cancelBtn;
    TextView errorText;
    DBHelper dbHelper;
    String initialTitle;

    public EditListDialog(Activity a, String title){
        super(a);
        this.activity = a;
        this.initialTitle = title;
    }

    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_edit_list);

        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        listField = findViewById(R.id.listField);

        listField.setText(initialTitle);
        errorText = findViewById(R.id.errorText);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listName = listField.getText().toString().trim();
                if (listName.equals("")){
                    errorText.setVisibility(View.VISIBLE);
                }else{
                    dbHelper = new DBHelper(getContext());
                    TaskList list = new TaskList(listField.getText().toString());
                    list.setId(dbHelper.getTaskList(initialTitle).getId());

                    dbHelper.updateTaskList(list);

                    dbHelper.closeDB();

                    Intent updateIntent = new Intent("db.updateTaskLists");

                    getContext().getApplicationContext().sendBroadcast(updateIntent);

                    Toast.makeText(activity, "List name updated successfully",
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
            }
        });
    }
}
