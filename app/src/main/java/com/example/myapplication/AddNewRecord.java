package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.StreamSupport;

public class AddNewRecord extends AppCompatActivity {

    public DBManager dbHelper;
    public String group_name;
    public String group_members;
    public String group_payers;
    public String group_money;
    public String group_debtors;
    public String group_purposes;

    public Spinner spinnerWho;
    public Button btn_newrecord;
    public TextView et_date;

    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_record);
        final Calendar calendar = Calendar.getInstance();


        // get user id, group id
        Bundle bundle = this.getIntent().getExtras();
        String user_id = bundle.getString("id");
        String group_id = bundle.getString("group_id");

        // set database
        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        final SQLiteDatabase database;
        database = dbHelper.getDatabase();

        Cursor group_cursor = database.query("Groups", null, null, null, null, null, null);
        if(group_cursor.moveToFirst()){
            do{
                if(group_id.equals(group_cursor.getString(group_cursor.getColumnIndex("id")))){
                    group_name = group_cursor.getString(group_cursor.getColumnIndex("group_name"));
                    group_members = group_cursor.getString(group_cursor.getColumnIndex("members"));
                    group_payers = group_cursor.getString(group_cursor.getColumnIndex("payers"));
                    group_money = group_cursor.getString(group_cursor.getColumnIndex("pay_money"));
                    group_debtors = group_cursor.getString(group_cursor.getColumnIndex("debtors"));
                    group_purposes = group_cursor.getString(group_cursor.getColumnIndex("purpose"));
                    break;
                }
            }while(group_cursor.moveToNext());
        }

        // set group name
        TextView GroupNameRecord = this.findViewById(R.id.GroupNameRecord);
        GroupNameRecord.setText(group_name);

        // set myself
        String[] names = group_members.split("\n");
        String name_list = "";
        for (int i=0;i<names.length;i++){
            if(i==0){
                name_list += names[i];
            }else if(i==1){
                continue;
            }else{
                name_list += "\n"+names[i];
            }
        }
        TextView myself = this.findViewById(R.id.MyselfRecord);
        myself.setText(name_list);

        // set link member
        TextView _link_addmember = this.findViewById(R.id.link_addmemberRecord);
        _link_addmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddNewRecord.this);
                builder.setTitle("New member");
                LayoutInflater Inflater = getLayoutInflater();
                v = Inflater.inflate(R.layout.add_member, null);
                builder.setView(v);
                EditText u = v.findViewById(R.id.NewMember);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String new_member = u.getText().toString();
                        group_members += "\n"+new_member;
                        String[] names = group_members.split("\n");
                        String name_list = "";
                        for (int i=0;i<names.length;i++){
                            if(i==0){
                                name_list += names[i];
                            }else if(i==1){
                                continue;
                            }else{
                                name_list += "\n"+names[i];
                            }
                        }
                        myself.setText(name_list);

                        database.execSQL("UPDATE "+"Groups"+" SET members = "+"'"+group_members+"' "+ "WHERE id = "+"'"+group_id+"'");

                        ArrayList<String> list = new ArrayList<String>();
                        list.add(" ");
                        for(int i=0;i<names.length;i++){
                            if(i!=1){
                                list.add(names[i]);
                            }
                        }
                        final ArrayAdapter<String> adapter0=new ArrayAdapter<String>(AddNewRecord.this,android.R.layout.simple_list_item_single_choice,list);
                        adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerWho.setAdapter(adapter0);

                        Toast.makeText(AddNewRecord.this, new_member + " is added", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        //set records who
        spinnerWho = this.findViewById(R.id.who);
        spinnerWho.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ((TextView) spinnerWho.getSelectedView()).setTextColor(Color.WHITE);
            }
        });
        ArrayList<String> list = new ArrayList<String>();
        list.add(" ");
        for(int i=0;i<names.length;i++){
            if(i!=1){
                list.add(names[i]);
            }
        }
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWho.setAdapter(adapter);


        // get who
        spinnerWho.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        // set date
        et_date = this.findViewById(R.id.date);

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddNewRecord.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int month, int day) {
                                // TODO Auto-generated method stub
                                mYear = year;
                                mMonth = month;
                                mDay = day;
                                et_date.setText(new StringBuilder()
                                        .append((mDay < 10) ? "0" + mDay : mDay)
                                        .append("/")
                                        .append((mMonth + 1) < 10 ? "0"
                                                + (mMonth + 1) : (mMonth + 1))
                                        .append("/")
                                        .append(mYear)
                                );
                            }
                        }, calendar.get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        // set botton
        btn_newrecord = this.findViewById(R.id.NewRecord);
        btn_newrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(group_payers.equals(" ")){
                    group_payers = "";
                }
                group_payers += spinnerWho.getSelectedItem().toString()+"\n";


                String date = et_date.getText().toString();
                if(group_debtors.equals(" ")){
                    group_debtors = "";
                }
                group_debtors += date+"\n";

                EditText pays = AddNewRecord.this.findViewById(R.id.pays);
                String moneys = pays.getText().toString();
                if(group_money.equals(" ")){
                    group_money = "";
                }
                group_money += moneys+"\n";

                EditText purposes = AddNewRecord.this.findViewById(R.id.what);
                String purposes_what = purposes.getText().toString();
                String[] purposes_what2 = purposes_what.split("\n");
                if(group_purposes.equals(" ")){
                    group_purposes = "";
                }
                group_purposes += purposes_what2[0]+"\n";

                System.out.println("add new record:");
                System.out.println(group_debtors);
                System.out.println(group_money);
                System.out.println(group_purposes);
                System.out.println(group_payers);


                database.execSQL("UPDATE "+"Groups"+" SET members = "+"'"+group_members+"' "
                        + ", payers = "+"'"+group_payers+"'"
                        + ", pay_money = "+"'"+group_money+"'"
                        + ", debtors = "+"'"+group_debtors+"'"
                        + ", purpose = "+"'"+group_purposes+"'"
                        + "WHERE id = "+"'"+group_id+"'");

                Bundle bundle = new Bundle();
                bundle.putString("id", user_id);
                bundle.putString("group_id", group_id);
                Intent intent = new Intent(getApplicationContext(),AddContentActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                database.close();
                finish();
            }
        });
    }
}
