package com.octavian.octaviantaskmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CalendarFragment extends Fragment {

    SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
    SimpleDateFormat dateFormatForTask  = new SimpleDateFormat("MM/dd/yyyy");
    ArrayList<Event> events;
    CompactCalendarView calendarView;
    TextView monthView;
    BroadcastReceiver mReceiver;
    FloatingActionButton fab;
    Date currentDate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarView = view.findViewById(R.id.calendar_view);
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        calendarView.shouldDrawIndicatorsBelowSelectedDays(true);

        monthView = view.findViewById(R.id.month_view);
        monthView.setText(dateFormatForMonth.format(calendarView.getFirstDayOfCurrentMonth()));
        fab = view.findViewById(R.id.fab_calendar);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTaskDialog dialog = new NewTaskDialog(getActivity(), currentDate);
                dialog.show();
            }
        });

        refreshFragment();


        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                String date = dateFormatForTask.format(dateClicked);
                currentDate = dateClicked;
                getChildFragmentManager().beginTransaction().replace(R.id.calendar_task_container,
                        CalendarTaskFragment.newInstance(date)).commit();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                monthView.setText(dateFormatForMonth.format(calendarView.getFirstDayOfCurrentMonth()));
                currentDate = calendarView.getFirstDayOfCurrentMonth();

            }
        });

        String today = dateFormatForTask.format(Calendar.getInstance().getTime());

        getChildFragmentManager().beginTransaction().replace(R.id.calendar_task_container,
                CalendarTaskFragment.newInstance(today)).commit();

        currentDate = Calendar.getInstance().getTime();

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

    public void getTasks(){
        DBHelper dbHelper = new DBHelper(getContext());
        ArrayList<Task> tasks = dbHelper.getAllTasks();
        events = new ArrayList<>();
        Event event;
        for (int i = 0; i < tasks.size(); i++){
            if (tasks.get(i).getStatus() == 0){
                event = new Event(Color.LTGRAY, convertToMs(tasks.get(i).getDate()), tasks.get(i).getTask());
            }else{
                event = new Event(Color.GREEN, convertToMs(tasks.get(i).getDate()), tasks.get(i).getTask());
            }

            events.add(event);
        }

    }

    public long convertToMs(String dateString){
        try {
            Date date = dateFormatForTask.parse(dateString);
            long timeInMs = date.getTime();
            return timeInMs;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void refreshFragment(){
        if (getActivity() != null) {
            calendarView.removeAllEvents();
            getTasks();
            for (int i = 0; i < events.size(); i++){
                calendarView.addEvent(events.get(i));
            }
        }

    }

}
