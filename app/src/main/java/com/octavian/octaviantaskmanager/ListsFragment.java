package com.octavian.octaviantaskmanager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

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

        // if there is no "All Tasks" list in db, create it
        dbHelper = new DBHelper(getContext());
        if (dbHelper.getTaskList("All Tasks") == null){
            TaskList list = new TaskList("All Tasks");
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
                dbHelper = new DBHelper(getContext());

                ArrayList<Task> tasks = dbHelper.getAllTasksByList(taskListAdapter.getItem(position).getListTitle());

                String message = "";

                for (Task task : tasks){
                    message += task.getTask() + ", ";
                }

                dbHelper.closeDB();



                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Show tasks")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                EditListDialog dialog = new EditListDialog(getActivity(), taskListAdapter.getItem(position).getListTitle());
                dialog.show();

//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setTitle("Delete task")
//                        .setMessage("Are you sure you want to delete this task?")
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                dbHelper = new DBHelper(getContext());
//
//                                TaskList list_to_delete = taskListAdapter.getItem(position);
//
//                                dbHelper.deleteTaskList(list_to_delete, true);
//
//                                dbHelper.closeDB();
//
//                                Intent updateListsIntent = new Intent("db.updateTaskLists");
//                                Intent updateTasksIntent = new Intent("db.updateTasks");
//
//                                getContext().getApplicationContext().sendBroadcast(updateListsIntent);
//                                getContext().getApplicationContext().sendBroadcast(updateTasksIntent);
//
//                                Toast.makeText(getActivity(), "You successfully deleted the list",
//                                        Toast.LENGTH_LONG).show();
//                            }
//                        })
//                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .show();

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


