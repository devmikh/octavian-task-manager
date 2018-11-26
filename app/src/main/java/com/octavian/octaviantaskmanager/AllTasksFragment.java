package com.octavian.octaviantaskmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class AllTasksFragment extends Fragment {

    FloatingActionButton fab;
    ListView listView;
    TextView emptyView;
    DBHelper dbHelper;
    TaskAdapter taskAdapter;
    BroadcastReceiver mReceiver;

    public static AllTasksFragment newInstance(int position) {
        AllTasksFragment fragment = new AllTasksFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);

        return fragment;
    }

    public AllTasksFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_tasks, container, false);
        fab = view.findViewById(R.id.fab_alltasks);
        listView = view.findViewById(R.id.alltasks_list_view);
        emptyView = view.findViewById(R.id.empty);

        refreshFragment();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTaskDialog dialog = new NewTaskDialog(getActivity());
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

            final ArrayList<Task> tasks = dbHelper.getAllTasks();

            dbHelper.closeDB();

            taskAdapter = new TaskAdapter(getContext(), tasks);

            listView.setAdapter(taskAdapter);

            listView.setEmptyView(emptyView);
        }



    }



}
