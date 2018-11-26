package com.octavian.octaviantaskmanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NewTaskDialog extends Dialog{

    SimpleDateFormat dateFormatForTask  = new SimpleDateFormat("MM/dd/yyyy");

    public Activity activity;
    DatePickerDialog datePicker;
    EditText taskField, dateField;
    Button saveBtn, cancelBtn;
    TextView errorTextTask, errorTextDate;
    Spinner listsSpinner;
    DBHelper dbHelper;
    ArrayAdapter spinnerAdapter;
    String listName = null;
    Date date = null;

    public NewTaskDialog(Activity a){
        super(a);
        this.activity = a;
    }

    public NewTaskDialog(Activity a, String listTitle){
        super(a);
        this.activity = a;
        this.listName = listTitle;
    }

    public NewTaskDialog(Activity a, Date date){
        super(a);
        this.activity = a;
        this.date = date;
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
        dateField.setFocusable(false);

        taskField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});

        dbHelper = new DBHelper(getContext());
        ArrayList<TaskList> taskLists = dbHelper.getAllTaskLists();
        dbHelper.closeDB();
        ArrayList<String> taskListTitles = new ArrayList<>();
        for (TaskList taskList : taskLists){
            taskListTitles.add(taskList.getListTitle());
        }
        spinnerAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item, taskListTitles);
        listsSpinner.setAdapter(spinnerAdapter);
        if (listName != null){
            for (int i=0; i < taskListTitles.size(); i++){
                if (taskListTitles.get(i).equals(listName)){
                    listsSpinner.setSelection(i);
                }
            }
        }

        if (date != null){
            String dateString =  dateFormatForTask.format(date);
            dateField.setText(dateString);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorTextTask.setVisibility(View.GONE);
                errorTextDate.setVisibility(View.GONE);
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
                final int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePicker = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        monthOfYear = monthOfYear + 1;
                        String monthString = Integer.toString(monthOfYear);
                        String dayString = Integer.toString(dayOfMonth);


                        if (monthString.length() == 1){
                            monthString = "0" + monthString;
                        }
                        if (dayString.length() == 1){
                            dayString = "0" + dayString;
                        }
                        dateField.setText(monthString + "/" + dayString + "/" + year);
                    }
                }, year, month, day);
                datePicker.show();
            }
        });

    }



}
