package com.pwj.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.pwj.helloya.R;


/**
 * Created by 13688 on 2018/4/2.
 */

public class DialogCalendar extends Dialog {
    private com.prolificinteractive.materialcalendarview.MaterialCalendarView calendar;

    private ICustomDialogEventListener mCustomDialogEventListener;
    private Context mContext;
    private View view;
    private int year;
    private int month;
    private int day;
    //增加一个回调函数,用以从外部接收返回值
    public interface ICustomDialogEventListener {
        public void customDialogEvent(String text);
    }


    public DialogCalendar(Context context,int year ,int month ,int day ,int theme,ICustomDialogEventListener listener) {
        super(context, theme);
        mContext = context;
        this.year=year;
        this.month=month;
        this.day=day;
        mCustomDialogEventListener = listener;
    }

    private void initView(View view) {
        calendar = view.findViewById(R.id.calendar);
        calendar.state().edit().setMinimumDate(CalendarDay.from(year,month,day)).commit();
        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
                mCustomDialogEventListener.customDialogEvent(String.valueOf(calendarDay.getDate()));
                dismiss();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.dialog_calendar, null);
        initView(view);
        this.setContentView(view);
    }

}
