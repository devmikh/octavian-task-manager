//package com.octavian.octaviantaskmanager;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.text.Layout;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//public class TasksFragment extends Fragment {
//
//    String listTitle;
//
//    public static TasksFragment newInstance(String listTitle){
//        TasksFragment f = new TasksFragment();
//        Bundle args = new Bundle();
//        args.putString("listTitle", listTitle);
//        f.setArguments(args);
//        return f;
//    }
//
//    ListView listView;
//    TextView emptyView;
//    DBHelper dbHelper;
//    TaskAdapter taskAdapter;
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
//
//        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
//        listView = view.findViewById(R.id.tasks_list_view);
//        emptyView = view.findViewById(R.id.empty);
//
//        Bundle args = getArguments();
//        listTitle = args.getString("listTitle", "Default");
//
//        refreshFragment();
//
//        return view;
//    }
//
//    public void refreshFragment(){
//        if (getActivity() != null){
//            dbHelper = new DBHelper(getContext());
//
//            final ArrayList<Task> tasks = dbHelper.getAllTasksByList(listTitle);
//
//            dbHelper.closeDB();
//
//            taskAdapter = new TaskAdapter(getContext(), tasks);
//
//            listView.setAdapter(taskAdapter);
//
//            listView.setEmptyView(emptyView);
//        }
//
//
//
//    }
//}
