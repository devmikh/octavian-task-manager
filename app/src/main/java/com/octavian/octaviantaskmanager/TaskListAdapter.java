package com.octavian.octaviantaskmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
        int type = getItemViewType(position);
        if(listItem == null){
            listItem = getLayoutForType(type, parent);
        }

        final TaskList currentTaskList = taskLists.get(position);


        TextView title = listItem.findViewById(R.id.textView_listTitle);
        title.setText(currentTaskList.getListTitle());

        if (type == 1){
            TextView deleteBtn =listItem.findViewById(R.id.deleteBtn);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete list")
                        .setMessage("All tasks associated with this list will be deleted\nAre you sure you want to proceed?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBHelper dbHelper = new DBHelper(getContext());

                                dbHelper.deleteTaskList(currentTaskList, true);

                                dbHelper.closeDB();

                                Intent updateListsIntent = new Intent("db.updateTaskLists");
                                Intent updateTasksIntent = new Intent("db.updateTasks");

                                getContext().getApplicationContext().sendBroadcast(updateListsIntent);
                                getContext().getApplicationContext().sendBroadcast(updateTasksIntent);

                                Toast.makeText(getContext(), "You successfully deleted the list",
                                        Toast.LENGTH_SHORT).show();

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
        }

        return listItem;
    }

    public int getItemViewType(int position){
        if (getItem(position).getListTitle().equals("Default")){
            return 0;
        }else {
            return 1;
        }
    }

    public int getViewTypeCount(){
        return 2;
    }

    public View getLayoutForType(int type, ViewGroup parent){
        if (type == 0){
            return LayoutInflater.from(mContext).inflate(R.layout.tasklist_list_item_alltasks, parent, false);
        }else{
            return LayoutInflater.from(mContext).inflate(R.layout.tasklist_list_item, parent, false);
        }
    }

//    public View getViewByPosition(int pos, ListView listView) {
//        final int firstListItemPosition = listView.getFirstVisiblePosition();
//        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
//
//        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
//            return listView.getAdapter().getView(pos, null, listView);
//        } else {
//            final int childIndex = pos - firstListItemPosition;
//            return listView.getChildAt(childIndex);
//        }
//    }

}
