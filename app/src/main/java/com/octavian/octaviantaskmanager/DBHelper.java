package com.octavian.octaviantaskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DBHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "octavianDatabase";

    // Table Names
    static final String TABLE_TASK = "tasks";
    static final String TABLE_LIST = "lists";
    static final String TABLE_TASK_LIST = "task_list";

    // Common column names
    private static final String KEY_ID = "id";

    // TASKS table column names
    private static final String KEY_TASK = "task_title";
    private static final String KEY_DATE = "task_date";
    private static final String KEY_STATUS = "task_status";

    // LISTS table column names
    private static final String KEY_LIST = "list_title";

    // TASK_LIST table columns
    private static final String KEY_TASK_ID = "task_id";
    private static final String KEY_LIST_ID = "list_id";

    // Table Create Statements
    // Task table create statement
    private static final String CREATE_TABLE_TASK = "CREATE TABLE "
            + TABLE_TASK + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TASK
            + " TEXT," + KEY_STATUS + " INTEGER," + KEY_DATE + " DATETIME" + ")";

    private static final String CREATE_TABLE_LIST = "CREATE TABLE " + TABLE_LIST
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_LIST + " TEXT" + ")";

    private static final String CREATE_TABLE_TASK_LIST = "CREATE TABLE "
            + TABLE_TASK_LIST + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_TASK_ID + " INTEGER," + KEY_LIST_ID + " INTEGER" + ")";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        // creating tables
        db.execSQL(CREATE_TABLE_TASK);
        db.execSQL(CREATE_TABLE_LIST);
        db.execSQL(CREATE_TABLE_TASK_LIST);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK_LIST);

        // create new tables
        onCreate(db);
    }

    // Creating a task
    public long createTask(Task task, long[] list_ids){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASK, task.getTask());
        values.put(KEY_STATUS, task.getStatus());
        values.put(KEY_DATE, task.getDate());

        // insert row
        long task_id = db.insert(TABLE_TASK, null, values);


        for (long list_id : list_ids){
            assignTaskToList(task_id, list_id);
        }

        return task_id;
    }

    // get single task

    public Task getTask(long task_id){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_TASK + " WHERE "
                + KEY_ID + " = " + task_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Task task = new Task();
        task.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        task.setTask(c.getString(c.getColumnIndex(KEY_TASK)));
        task.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS) ));
        task.setDate(c.getString(c.getColumnIndex(KEY_DATE)));

        return task;
    }

    // get all tasks
    public ArrayList<Task> getAllTasks(){
        ArrayList<Task> tasks = new ArrayList<Task>();
        String selectQuery = "SELECT * FROM " + TABLE_TASK;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()){
            do{
                Task task = new Task();
                task.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                task.setTask(c.getString(c.getColumnIndex(KEY_TASK)));
                task.setDate(c.getString(c.getColumnIndex(KEY_DATE)));

                // adding to task list
                tasks.add(task);
            }while(c.moveToNext());
        }
        return tasks;
    }

    public ArrayList<Task> getAllTasksByList(String list_name){
        ArrayList<Task> tasks = new ArrayList<Task>();

        String selectQuery = "SELECT * FROM " + TABLE_TASK + ", "
                + TABLE_LIST + ", " + TABLE_TASK_LIST + " WHERE "
                + TABLE_LIST + "." + KEY_LIST + " = '" + list_name + "'"
                + " AND " + TABLE_LIST + "." + KEY_ID + " = " + TABLE_TASK_LIST
                + "." + KEY_LIST_ID + " AND " + TABLE_TASK + "." + KEY_ID
                + " = " + TABLE_TASK_LIST + "." + KEY_TASK_ID;

//        String query1 = "SELECT * FROM " + TABLE_TASK;
//        String query2 = "SELECT * FROM " + TABLE_LIST;
//        String query3 = "SELECT * FROM " + TABLE_TASK_LIST;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

//        Cursor c1 = db.rawQuery(query1, null);
//        Cursor c2 = db.rawQuery(query2, null);
//        Cursor c3 = db.rawQuery(query3, null);

//        Log.e(LOG, DatabaseUtils.dumpCursorToString(c));
//        Log.e(LOG, DatabaseUtils.dumpCursorToString(c1));
//        Log.e(LOG, DatabaseUtils.dumpCursorToString(c2));
//        Log.e(LOG, DatabaseUtils.dumpCursorToString(c3));
        // looping through all rows and adding to list
        if (c.moveToFirst()){
            do {
                Task task = new Task();
                task.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                task.setTask(c.getString(c.getColumnIndex(KEY_TASK)));
                task.setDate(c.getString(c.getColumnIndex(KEY_DATE)));

                // adding to task list
                tasks.add(task);
            }while (c.moveToNext());
        }
        return tasks;
    }

    // updating a task
    public int updateTask(Task task, long list_id){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASK, task.getTask());
        values.put(KEY_DATE, task.getDate());
        values.put(KEY_STATUS, task.getStatus());

        changeAssignedList(task.getId(), list_id);

        // updating row
        return db.update(TABLE_TASK, values, KEY_ID + " = ?",
                new String[] { String.valueOf(task.getId())});
    }

    // change status of a task
    public int updateTaskStatus(Task task){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, task.getStatus());

        String query1 = "SELECT * FROM " + TABLE_TASK;
        Cursor c1 = db.rawQuery(query1, null);
        Log.e(LOG, DatabaseUtils.dumpCursorToString(c1));

        return db.update(TABLE_TASK, values, KEY_ID + " = ?",
                new String[] { String.valueOf(task.getId())});
    }

    // delete a task
    public void deleteTask(long task_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASK, KEY_ID + " =?",
                new String[] {String.valueOf(task_id)});
    }

    // create a list
    public long createList(TaskList list){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LIST, list.getListTitle());

        long list_id = db.insert(TABLE_LIST, null, values);

        return list_id;
    }

    // fetch single list name
    public TaskList getTaskList(String taskListTitle){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_LIST + " WHERE "
                + KEY_LIST + " = " + "'" + taskListTitle + "'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            TaskList tl = new TaskList();
            tl.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            tl.setListTitle(c.getString(c.getColumnIndex(KEY_LIST)));
            return tl;
        }else
            return null;
    }

    public TaskList getTaskListById(long taskListId){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_LIST + " WHERE "
                + KEY_ID + " = " + taskListId;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        TaskList tl = new TaskList();
        tl.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        tl.setListTitle(c.getString(c.getColumnIndex(KEY_LIST)));

        return tl;
    }

    // fetch single list name of a particular task
    public long getListOfATask(long task_id){
        SQLiteDatabase db = this.getReadableDatabase();
        long list_id = -1;

        String selectQuery = "SELECT " + KEY_LIST_ID + " FROM "
                + TABLE_TASK_LIST + " WHERE " + KEY_TASK_ID + " = "
                + task_id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null){
            c.moveToFirst();
            list_id = c.getInt(c.getColumnIndex(KEY_LIST_ID));
        }
        return list_id;
    }

    // fetch all list names
    public ArrayList<TaskList> getAllTaskLists(){
        ArrayList<TaskList> lists = new ArrayList<TaskList>();
        String selectQuery = "SELECT * FROM " + TABLE_LIST;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()){
            do{
                TaskList tl = new TaskList();
                tl.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                tl.setListTitle(c.getString(c.getColumnIndex(KEY_LIST)));

                // adding to arraylist of task lists
                lists.add(tl);
            }while(c.moveToNext());
        }
        return lists;
    }

    // update a task list
    public int updateTaskList(TaskList list){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LIST, list.getListTitle());

        // updating row
        return db.update(TABLE_LIST, values, KEY_ID + " = ?",
                new String[] {String.valueOf(list.getId())});
    }

    // delete tasklist with tasks inside
    public void deleteTaskList(TaskList list, boolean delete_all_tasks_in_list){
        SQLiteDatabase db = this.getWritableDatabase();

        // before deleting list
        // check if tasks under this list should be deleted as well
        if (delete_all_tasks_in_list){
            List<Task> allListTasks = getAllTasksByList(list.getListTitle());

            // delete all tasks
            for (Task task : allListTasks){
                // delete task
                deleteTask(task.getId());
            }
        }

        // delete the task list
        db.delete(TABLE_LIST, KEY_ID + " = ?",
                new String[] {String.valueOf(list.getId())});
        db.delete(TABLE_TASK_LIST, KEY_LIST_ID + " = ?",
                new String[] {String.valueOf(list.getId())});
//        db.execSQL("DELETE FROM " + TABLE_TASK);
//        db.execSQL("DELETE FROM " + TABLE_LIST);
//        db.execSQL("DELETE FROM " + TABLE_TASK_LIST);
    }

    //assign a task to a task list
    public long assignTaskToList(long task_id, long list_id){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASK_ID, task_id);
        values.put(KEY_LIST_ID, list_id);

        long id = db.insert(TABLE_TASK_LIST, null, values);

        return id;
    }

    // change tasklist of a task
    public int changeAssignedList(long task_id, long list_id){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LIST_ID, list_id);

        // updating row
        return db.update(TABLE_TASK_LIST, values, KEY_TASK_ID + " =?",
                new String[] { String.valueOf(task_id)});
    }

    public void closeDB(){
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}
