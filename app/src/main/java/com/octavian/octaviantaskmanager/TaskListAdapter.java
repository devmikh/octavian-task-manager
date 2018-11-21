package com.octavian.octaviantaskmanager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TaskListAdapter extends ArrayAdapter<TaskList> {

    private Context mContext;
    private List<TaskList> taskLists = new ArrayList<>();

    public TaskListAdapter(Context context, ArrayList<TaskList> list){
        super(context, 0, list);
        mContext = context;
        taskLists = list;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View listItem = convertView;
        if(listItem == null){
            listItem = LayoutInflater.from(mContext).inflate(R.layout.tasklist_list_item, parent, false);;
        }

        final TaskList currentTaskList = taskLists.get(position);


        TextView title = listItem.findViewById(R.id.textView_listTitle);
        title.setText(currentTaskList.getListTitle());


            Button deleteBtn =listItem.findViewById(R.id.deleteBtn);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBHelper dbHelper = new DBHelper(getContext());

                    dbHelper.deleteTaskList(currentTaskList, true);

                    dbHelper.closeDB();

                    Intent updateListsIntent = new Intent("db.updateTaskLists");
                    Intent updateTasksIntent = new Intent("db.updateTasks");

                    getContext().getApplicationContext().sendBroadcast(updateListsIntent);
                    getContext().getApplicationContext().sendBroadcast(updateTasksIntent);

//                                Toast.makeText(getActivity(), "You successfully deleted the list",
//                                        Toast.LENGTH_LONG).show();
                }
            });











        return listItem;
    }



}
