package com.example.capston_project;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static com.example.capston_project.Loginloading.user;

public class AddFlight extends Activity {
    String day, flight;
    EditText airplain;
    int yy, mm, dd;
    int defense;
    Map<String, Set<TimeZone>> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addflight);
        Intent intent = getIntent();
        airplain = (EditText) findViewById(R.id.editText);
        yy = intent.getExtras().getInt("yy");
        mm = intent.getExtras().getInt("mm");
        dd = intent.getExtras().getInt("dd");
        MakeDay(yy, mm, dd);
        AddTripButton();
    }

    private void AddTripButton() {
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (airplain.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(AddFlight.this, "항공편을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                flight = airplain.getText().toString();
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();

            }
        });
    }

    private void MakeDay(int yy, int mm, int dd) {
        day = Integer.toString(yy);
        if (mm < 10)
            day = day + "0" + Integer.toString(mm);
        else
            day = day + Integer.toString(mm);
        if (dd < 10)
            day = day + "0" + Integer.toString(dd);
        else
            day = day + Integer.toString(dd);
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mDatabaseRef = database.getReference();
            Map<String, Object> childUpdates = new HashMap<>();
            Map<String, Object> surveyValue;
            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            String url = "https://www.flightview.com/TravelTools/FlightTrackerQueryResults.asp";
            try {
                Document doc = Jsoup.connect(url)
                        .header("Accept-Encoding", "gzip, deflate")
                        .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .header("Cache-Control", "max-age=0")
                        .header("Connection", "keep-alive")
                        .header("Content-Length", "123")
                        .header("Cookie", "__utmz=35767543.1590558578.1.1.utmccn=(referral)|utmcsr=google.com|utmcct=/|utmcmd=referral; __utmc=35767543; _hjid=8c5d7050-0ff9-4d1e-b552-de026304c519; _hjIncludedInSample=1; ApplicationGatewayAffinity=721656b12fed4f0e097cefb465f7dabd; ASPSESSIONIDQQRDBBRR=IBGHHKIDGJAPEPDLICGIPCDM; fcspersistslider1=1; __utma=35767543.1728073493.1590558578.1590634117.1590652387.7; __utmb=35767543; _hjAbsoluteSessionInProgress=1")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
                        .header("Host", "www.flightview.com")
                        .ignoreContentType(true)
                        .data("qtype", "sfi")
                        .data("sfw", "/FV/Home/Main")
                        .data("whenArrDep", "dep")
                        .data("namal", "Enter name or code")
                        .data("al", "")
                        .data("fn", flight)
                        .data("whenDate", day)
                        .data("input", "Track Flight")
                        .post();

                String ArrivalPort = doc.select("span[id=txt_arraptlnk]").toString();
                String DeparturePort = doc.select("span[id=txt_depaptlnk]").toString();
                String ArrivalTime = doc.select("table[id=tbl_arr]").toString();
                String DepartureTime = doc.select("table[id=tbl_dep]").toString();
                String ArrivalCountry;
                String DepartureCountry;
                String ArrivalCity;
                String DepartureCity;
                String ArrivalParallax;
                String DepartParallax;
                String day;
                int Parallax;


                day = ArrivalTime.substring(ArrivalTime.indexOf("new Array") + 11, ArrivalTime.indexOf(" );"));
                ArrivalCountry = ArrivalPort.substring(ArrivalPort.indexOf(",", ArrivalPort.indexOf(",", ArrivalPort.indexOf(",") + 1) + 1) + 2, ArrivalPort.lastIndexOf("\""));
                DepartureCountry = DeparturePort.substring(DeparturePort.indexOf(",", DeparturePort.indexOf(",", DeparturePort.indexOf(",") + 1) + 1) + 2, DeparturePort.lastIndexOf("\""));
                ArrivalPort = "(" + ArrivalPort.substring(ArrivalPort.indexOf("ftGetAirport(\"") + 14, ArrivalPort.lastIndexOf(",", ArrivalPort.lastIndexOf(",") - 1) - 1).replace("\",\"", ")");
                DeparturePort = "(" + DeparturePort.substring(DeparturePort.indexOf("ftGetAirport(\"") + 14, DeparturePort.lastIndexOf(",", DeparturePort.lastIndexOf(",") - 1) - 1).replace("\",\"", ")");
                ArrivalTime = ArrivalTime.substring(ArrivalTime.indexOf("&nbsp") - 9, ArrivalTime.lastIndexOf("<div style=\"display") - 5);
                ArrivalTime = ArrivalTime.replace("&nbsp;", " ");
                ArrivalTime = ArrivalTime.replace(">", "");
                DepartureTime = DepartureTime.substring(DepartureTime.indexOf("&nbsp") - 9, DepartureTime.lastIndexOf("<div style=\"display") - 5);
                DepartureTime = DepartureTime.replace("&nbsp;", " ");
                DepartureTime = DepartureTime.replace(">", " ");

                DepartureCity = DeparturePort.substring(DeparturePort.indexOf(")") + 1);
                DepartureCity = DepartureCity.replace(" ", "+");
                ArrivalCity = ArrivalPort.substring(ArrivalPort.indexOf(")") + 1);
                ArrivalCity = ArrivalCity.replace(" ", "+");


                url = "https://www.google.com/search?q=" + DepartureCity + "+timezone&oq=" + DepartureCity + "+timezone&sourceid=chrome&ie=UTF-8";
                doc = Jsoup.connect(url).get();
                DepartParallax = doc.select("div#search").toString();
                DepartParallax = DepartParallax.substring(DepartParallax.indexOf("GMT"));
                DepartParallax = DepartParallax.substring(3, DepartParallax.indexOf(")"));

                url = "https://www.google.com/search?q=" + ArrivalCity + "+timezone&oq=" + ArrivalCity + "+timezone&sourceid=chrome&ie=UTF-8";
                doc = Jsoup.connect(url).get();
                ArrivalParallax = doc.select("div#search").toString();
                ArrivalParallax = ArrivalParallax.substring(ArrivalParallax.indexOf("GMT"));
                ArrivalParallax = ArrivalParallax.substring(3, ArrivalParallax.indexOf(")"));


                SimpleDateFormat formatter_one = new SimpleDateFormat("hh:mm a, MMM dd", Locale.ENGLISH);

                ParsePosition pos = new ParsePosition(0);
                Date frmTime = formatter_one.parse(ArrivalTime, pos);

                Parallax = Integer.parseInt(ArrivalParallax) - Integer.parseInt(DepartParallax);
                int ss = Integer.parseInt(user.startsleep);
                int es = Integer.parseInt(user.endsleep);
                if (Parallax > 12 || (Parallax < 0 && Parallax > -12)) {//동쪽 30분
                    if (Math.abs(Parallax) > 3) {
                        ss -= 30;
                        es -= 30;
                        defense = 1;
                    } else {
                        ss -= Parallax * 10;
                        es -= Parallax * 10;
                        defense = 1;
                    }
                } else {//서쪽 1시간
                    if (Math.abs(Parallax) > 6) {
                        ss += 60;
                        es += 60;
                        defense = 2;
                    } else {
                        ss += Parallax * 10;
                        es += Parallax * 10;
                        defense = 2;
                    }
                }
                if (ss > 1440)
                    ss = ss - 1440;
                if (ss < 0)
                    ss = ss + 1440;
                if (es > 1440)
                    es = es - 1440;
                if (es < 0)
                    es = es + 1440;


                calendar.set(yy, frmTime.getMonth(), frmTime.getDate(), ss / 60, ss % 60, 0);
                new AlarmHATT(getApplicationContext()).SleepAlarm(calendar);
                calendar.set(yy, frmTime.getMonth(), frmTime.getDate(), es / 60, es % 60, 0);
                calendar.add(Calendar.DATE, 1);
                new AlarmHATT(getApplicationContext()).LookAlarm(calendar);
                calendar.set(yy, frmTime.getMonth(), frmTime.getDate(), ss / 60, ss % 60, 0);
                calendar.add(Calendar.DATE, 2);
                new AlarmHATT(getApplicationContext()).SleepAlarm(calendar);
                calendar.set(yy, frmTime.getMonth(), frmTime.getDate(), es / 60, es % 60, 0);
                calendar.add(Calendar.DATE, 2);
                new AlarmHATT(getApplicationContext()).LookAlarm(calendar);
                calendar.set(yy, frmTime.getMonth(), frmTime.getDate(), ss / 60, ss % 60, 0);
                calendar.add(Calendar.DATE, 3);
                new AlarmHATT(getApplicationContext()).SleepAlarm(calendar);
                calendar2.set(yy, frmTime.getMonth(), frmTime.getDate(), es / 60, es % 60, 0);
                calendar2.add(Calendar.DATE, 3);
                new AlarmHATT(getApplicationContext()).LookAlarm(calendar2);



                if (defense == 1) {

                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    String time1 = format.format(calendar.getTime());
                    String time2 = format.format(calendar2.getTime());
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddFlight.this, "출발지와 도착지의 시간차이는 " + Parallax + "시간입니다.\n" +
                                    "예약된 수면알람은 " + time1 + "입니다.\n" +
                                    "예약된 기상알람은 " + time2 + "입니다.\n" +
                                    "예약된 알람은 " +Math.abs(Parallax * 4) + "개 입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }, 0);

                } else {
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    String time1 = format.format(calendar.getTime());
                    String time2 = format.format(calendar2.getTime());
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddFlight.this, "출발지와 도착지의 시간차이는 " + Parallax + "시간입니다.\n" +
                                    "예약된 수면알람은 " + time1 + "입니다.\n" +
                                    "예약된 기상알람은 " + time2 + "입니다.\n" +
                                    "예약된 알람은 " + Math.abs(Parallax * 2) + "개 입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }, 0);

                }


                user.arrivalcountry = ArrivalCountry;
                user.arrivalport = ArrivalPort;
                user.arrivaltime = ArrivalTime;
                user.departurecountry = DepartureCountry;
                user.departureport = DeparturePort;
                user.departuretime = DepartureTime;
                surveyValue = user.towMap();
                childUpdates.put("User" + "/" + user.id, surveyValue);
                mDatabaseRef.updateChildren(childUpdates);


            } catch (IOException e) {
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddFlight.this, "항공편을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }, 0);
                e.printStackTrace();
            }
            return null;
        }
    }

    public class AlarmHATT {
        private Context context;

        public AlarmHATT(Context context) {
            this.context = context;
        }

        public void SleepAlarm(Calendar calendar) {
            AlarmManager am = (AlarmManager) AddFlight.this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(AddFlight.this, BroadcastD.class);
            PendingIntent sender = PendingIntent.getBroadcast(AddFlight.this, 0, intent, 0);
            //알람 예약
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }

        public void LookAlarm(Calendar calendar) {
            AlarmManager am = (AlarmManager) AddFlight.this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(AddFlight.this, BroadcastB.class);
            PendingIntent sender = PendingIntent.getBroadcast(AddFlight.this, 0, intent, 0);
            //알람 예약
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }

}
