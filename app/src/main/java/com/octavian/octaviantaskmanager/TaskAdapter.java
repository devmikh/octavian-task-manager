package com.octavian.octaviantaskmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    private Context mContext;
    private List<Task> tasks = new ArrayList<>();

    public TaskAdapter(Context context, ArrayList<Task> list){
        super(context,0,list);
        mContext = context;
        tasks = list;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View listItem = convertView;
        if(listItem == null){
            int type = getItemViewType(position);
            listItem = getLayoutForType(type, parent);
        }


        final Task currentTask = tasks.get(position);

        TextView title = listItem.findViewById(R.id.textView_taskTitle);
        title.setText(currentTask.getTask());



//        if (currentTask.getStatus() == 0){
//            title.setPaintFlags(title.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
//        }else{
//            title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//        }


        TextView date = listItem.findViewById(R.id.textView_taskDate);
        date.setText(currentTask.getDate());

        Button deleteBtn = listItem.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper = new DBHelper(getContext());

                dbHelper.deleteTask(currentTask.getId());

                dbHelper.closeDB();

                Intent updateIntent = new Intent("db.updateTasks");

                getContext().getApplicationContext().sendBroadcast(updateIntent);

//                Toast.makeText(getActivity(), "You successfully deleted the task",
//                        Toast.LENGTH_LONG).show();
            }
        });

        Log.e("TaskAdapter", "called getItem();");

        return listItem;

    }

    public int getItemViewType(int position){
        return getItem(position).getStatus();
    }

    public int getViewTypeCount(){
        return 2;
    }

    public View getLayoutForType(int type, ViewGroup parent){
        if (type == 0){
            return LayoutInflater.from(mContext).inflate(R.layout.task_list_item, parent, false);
        }else if (type == 1){
            return LayoutInflater.from(mContext).inflate(R.layout.task_list_item_done, parent, false);
        }else{
            return LayoutInflater.from(mContext).inflate(R.layout.task_list_item, parent, false);
        }
    }

}
