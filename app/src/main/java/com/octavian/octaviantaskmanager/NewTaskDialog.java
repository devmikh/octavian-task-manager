package com.octavian.octaviantaskmanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class NewTaskDialog extends Dialog{

    public Activity activity;
    DatePickerDialog datePicker;
    EditText taskField, dateField;
    Button saveBtn, cancelBtn;
    TextView errorTextTask, errorTextDate;
    Spinner listsSpinner;
    DBHelper dbHelper;
    ArrayAdapter spinnerAdapter;

    public NewTaskDialog(Activity a){
        super(a);
        this.activity = a;
    }

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_new_task);
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        taskField = findViewById(R.id.taskField);
        dateField = findViewById(R.id.dateField);
        errorTextTask = findViewById(R.id.errorTextTask);
        errorTextDate = findViewById(R.id.errorTextDate);
        listsSpinner = findViewById(R.id.lists_spinner);

        dbHelper = new DBHelper(getContext());
        ArrayList<TaskList> taskLists = dbHelper.getAllTaskLists();
        dbHelper.closeDB();
        ArrayList<String> taskListTitles = new ArrayList<>();
        for (TaskList taskList : taskLists){
            taskListTitles.add(taskList.getListTitle());
        }
        spinnerAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, taskListTitles);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listsSpinner.setAdapter(spinnerAdapter);


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = taskField.getText().toString().trim();
                String date = dateField.getText().toString().trim();
                String taskListTitle = listsSpinner.getSelectedItem().toString();
                if (taskName.equals("")){
                    errorTextTask.setVisibility(View.VISIBLE);
                }else if(date.equals("")){
                    errorTextDate.setVisibility(View.VISIBLE);
                }else{
                    DBHelper dbHelper = new DBHelper(getContext());
                    long taskListId = dbHelper.getTaskList(taskListTitle).getId();
                    Task task = new Task(taskName, date, 0);
                    dbHelper.createTask(task, new long[]{taskListId});
                    dbHelper.closeDB();

                    Intent updateIntent = new Intent("db.updateTasks");

                    getContext().getApplicationContext().sendBroadcast(updateIntent);

                    Toast.makeText(activity, "Task added successfully",
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

        taskField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorTextTask.setVisibility(View.GONE);
            }
        });

        dateField.setInputType(InputType.TYPE_NULL);
        dateField.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                errorTextDate.setVisibility(View.GONE);
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePicker = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateField.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
                datePicker.show();
            }
        });

    }



}
