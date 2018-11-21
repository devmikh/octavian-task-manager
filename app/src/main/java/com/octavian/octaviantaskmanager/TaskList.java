package com.octavian.octaviantaskmanager;

import java.util.ArrayList;
import java.util.List;

public class TaskList {

    int id;
    String listTitle;

    // Constructors

    public TaskList(){

    }

    public TaskList(String listTitle){
        this.listTitle = listTitle;
    }

    public TaskList(int id, String listTitle){
        this.id = id;
        this.listTitle = listTitle;
    }

    // setters

    public void setId(int id){
        this.id = id;
    }

    public void setListTitle(String listTitle){
        this.listTitle = listTitle;
    }

    // getters

    public int getId(){
        return this.id;
    }

    public String getListTitle(){
        return this.listTitle;
    }
}
