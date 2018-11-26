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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListsFragment extends Fragment {

    FloatingActionButton fab;
    ListView listView;
    TextView emptyView;
    DBHelper dbHelper;
    TaskListAdapter taskListAdapter;
    BroadcastReceiver mReceiver;

    public static ListsFragment newInstance(int position) {
        ListsFragment fragment = new ListsFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);

        return fragment;
    }

    public ListsFragment() {

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

        View view = inflater.inflate(R.layout.fragment_lists, container, false);
        fab = view.findViewById(R.id.fab_lists);
        listView = view.findViewById(R.id.lists_list_view);
        emptyView = view.findViewById(R.id.empty);

        // if there is no "Default" list in db, create it
        dbHelper = new DBHelper(getContext());
        if (dbHelper.getTaskList("Default") == null){
            TaskList list = new TaskList("Default");
            dbHelper.createList(list);
            dbHelper.closeDB();
        }
        refreshFragment();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewListDialog dialog = new NewListDialog(getActivity());
                dialog.show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TasksDialog tasksDialog = new TasksDialog(getActivity(), taskListAdapter.getItem(position).getListTitle());
                tasksDialog.show();
                Window window = tasksDialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                EditListDialog dialog = new EditListDialog(getActivity(), taskListAdapter.getItem(position).getListTitle());
                dialog.show();

                return true;

            }
        });

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().contentEquals("db.updateTaskLists")){
                    refreshFragment();
                }
            }
        };

        IntentFilter mDataUpdateFilter = new IntentFilter("db.updateTaskLists");

        getActivity().getApplicationContext().registerReceiver(mReceiver, mDataUpdateFilter);

        return view;
    }

    public void refreshFragment(){

        dbHelper = new DBHelper(getContext());

        final ArrayList<TaskList> taskLists = dbHelper.getAllTaskLists();

        dbHelper.closeDB();

        taskListAdapter = new TaskListAdapter(getContext(), taskLists);

        listView.setAdapter(taskListAdapter);

        listView.setEmptyView(emptyView);
    }

    public void onDestroy(){
        super.onDestroy();
        getActivity().getApplicationContext().unregisterReceiver(mReceiver);
    }
}


