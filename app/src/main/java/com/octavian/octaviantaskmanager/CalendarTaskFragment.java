package com.octavian.octaviantaskmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

public class CalendarTaskFragment extends Fragment {

    ListView listView;
    TextView emptyView;
    DBHelper dbHelper;
    TaskAdapter taskAdapter;
    String date;
    BroadcastReceiver mReceiver;

    public static CalendarTaskFragment newInstance(String dateString){
        CalendarTaskFragment f = new CalendarTaskFragment();

        Bundle args = new Bundle();

        args.putString("date", dateString);
        f.setArguments(args);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_calendar_task, container,false);


        listView = view.findViewById(R.id.tasks_list_view);
        emptyView = view.findViewById(R.id.empty);

        Bundle args = getArguments();
        date = args.getString("date", "01/01/1970");

        refreshFragment();

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
                refreshFragment();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                EditTaskDialog dialog = new EditTaskDialog(getActivity(), taskAdapter.getItem(position).getId());
                dialog.show();

                return true;
            }
        });

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().contentEquals("db.updateTasks")){
                    refreshFragment();
                }
            }
        };

        IntentFilter mDataUpdateFilter = new IntentFilter("db.updateTasks");

        getActivity().getApplicationContext().registerReceiver(mReceiver, mDataUpdateFilter);

        return view;
    }

    public void refreshFragment(){
        if (getActivity() != null){
            dbHelper = new DBHelper(getContext());

            final ArrayList<Task> tasks = dbHelper.getAllTasksByDate(date);

            dbHelper.closeDB();

            taskAdapter = new TaskAdapter(getContext(), tasks);

            listView.setAdapter(taskAdapter);

            listView.setEmptyView(emptyView);
        }
    }
}
