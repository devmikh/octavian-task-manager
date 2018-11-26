package com.octavian.octaviantaskmanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TasksDialog extends Dialog {

    Activity activity;
    ListView listView;
    TextView emptyView;
    DBHelper dbHelper;
    TaskAdapter taskAdapter;
    String listTitle;
    FloatingActionButton fab;
    BroadcastReceiver mReceiver;

    public TasksDialog(Activity a, String listTitle) {
        super(a);
        this.activity = a;
        this.listTitle = listTitle;
    }

    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_tasks);

        listView = findViewById(R.id.tasks_list_view);
        emptyView = findViewById(R.id.empty);
        fab = findViewById(R.id.fab_tasks);

        refresh();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTaskDialog dialog = new NewTaskDialog(activity, listTitle);
                dialog.show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dbHelper = new DBHelper(getContext());
                Task task = dbHelper.getTask(taskAdapter.getItem(position).getId());
                if (task.getStatus() == 0){
                    task.setStatus(1);
                }else{
                    task.setStatus(0);
                }
                dbHelper.updateTaskStatus(task);

                Intent updateIntent = new Intent("db.updateTasks");

                getContext().getApplicationContext().sendBroadcast(updateIntent);

                dbHelper.closeDB();
                refresh();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                EditTaskDialog dialog = new EditTaskDialog(activity, taskAdapter.getItem(position).getId());
                dialog.show();

                return true;
            }
        });

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().contentEquals("db.updateTasks")){
                    refresh();
                }
            }
        };

        IntentFilter mDataUpdateFilter = new IntentFilter("db.updateTasks");

        activity.getApplicationContext().registerReceiver(mReceiver, mDataUpdateFilter);
    }

    public void refresh(){
        if (activity != null){
            dbHelper = new DBHelper(getContext());

            final ArrayList<Task> tasks = dbHelper.getAllTasksByList(listTitle);

            dbHelper.closeDB();

            taskAdapter = new TaskAdapter(getContext(), tasks);

            listView.setAdapter(taskAdapter);

            listView.setEmptyView(emptyView);
        }
    }
}
