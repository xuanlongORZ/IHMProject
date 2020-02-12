package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AddContentActivity extends AppCompatActivity {
    public DBManager dbHelper;
    public String group_name;
    public String group_members;
    public String group_payers;
    public String group_money;
    public String group_debtors;
    public String group_purpose;


    public TextView title;
    public TextView membernames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_content);

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
                    group_purpose = group_cursor.getString(group_cursor.getColumnIndex("purpose"));
                    break;
                }
            }while(group_cursor.moveToNext());
        }

        // set group name
        title = this.findViewById(R.id.Group_name_inside);
        title.setText(group_name);

        // set members
        membernames = this.findViewById(R.id.MemberNames);
        String[] names = group_members.split("\n");
        String name_list = "";
//        for(int i=0;i<names.length;i++){
//            System.out.println(i+";;;;"+names[i]);
//        }
        for (int i=0;i<names.length;i++){
            if(i==0){
                name_list += names[i];
            }else if(i==1){
                continue;
            }else{
                name_list += "; "+names[i];
            }
        }
        membernames.setText(name_list);

        // set add new member
        TextView add_new_member = this.findViewById(R.id.AddNewMember);
        add_new_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddContentActivity.this);
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
                                name_list += "; "+names[i];
                            }
                        }
                        membernames.setText(name_list);

                        database.execSQL("UPDATE "+"Groups"+" SET members = "+"'"+group_members+"' "+ "WHERE id = "+"'"+group_id+"'");
                        Toast.makeText(AddContentActivity.this, new_member + " is added", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        // set add new record
        FloatingActionButton btn_addRecord = this.findViewById(R.id.AddRecord);
        btn_addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id", user_id);
                bundle.putString("group_id", group_id);
                Intent intent = new Intent(getApplicationContext(),AddNewRecord.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        // set record layout
        if(!group_payers.equals(" ")){
//            System.out.println("@#!@     "+group_payers);
            LinearLayout layout = findViewById(R.id.MyRecords);
            LinearLayout layout2 = new LinearLayout(this);
            layout2.setOrientation(LinearLayout.HORIZONTAL);
            layout2.setBackgroundColor(Color.DKGRAY);

            layout.setOrientation(LinearLayout.VERTICAL);
            System.out.println("Add content activity:");
            System.out.println(group_debtors);
            System.out.println(group_money);
            System.out.println(group_purpose);
            System.out.println(group_payers);

            String[] payer_names = group_payers.split("\n");
            String[] payer_money = group_money.split("\n");
            String[] debtor_names = group_debtors.split("\n");
            String[] purpose_names = group_purpose.split("\n");
            System.out.println("__________________");
            System.out.println(payer_names[0]+payer_money[0]+debtor_names[0]+purpose_names[0]);


            for (int i=0;i<payer_names.length;i++){
                TextView text_record1 = new TextView(this);
                TextView text_record2 = new TextView(this);
                text_record1.setText(debtor_names[i]+"\n"+purpose_names[i]);
                text_record1.setTextSize(25);
                text_record1.setBackgroundColor(Color.DKGRAY);
                text_record1.setTextColor(Color.WHITE);
                text_record2.setText(payer_names[i]+" paied "+payer_money[i]+" euros");
                text_record2.setTextSize(20);
                text_record2.setBackgroundColor(Color.DKGRAY);
                text_record2.setTextColor(Color.WHITE);
                layout2.addView(text_record1);
                layout2.addView(text_record2);
                layout.addView(layout2);
                Space sp = new Space(this);
                sp.setMinimumHeight(10);
                layout.addView(sp);
            }
        }

    }
}
