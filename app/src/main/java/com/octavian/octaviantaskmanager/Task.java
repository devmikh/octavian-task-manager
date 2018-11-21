package com.octavian.octaviantaskmanager;

public class Task {

    int id;
    String task;
    int status;
    String date;


    // constructors
    public Task(){

    }

    public Task(String task, String date, int status){
        this.task = task;
        this.date = date;
        this.status = status;
    }

    public Task(int id, String task, String date, int status){
        this.id = id;
        this.task = task;
        this.date = date;
        this.status = status;
    }

    // setters

    public void setId(int id){
        this.id = id;
    }

    public void setTask(String task){
        this.task = task;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public void setDate(String date){
        this.date = date;
    }

    // getters

    public long getId(){
        return this.id;
    }

    public String getTask(){
        return this.task;
    }

    public int getStatus(){
        return this.status;
    }

    public String getDate(){
        return this.date;
    }

}
