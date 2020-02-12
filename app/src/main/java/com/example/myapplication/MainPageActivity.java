package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity {
    public DBManager dbHelper;
    public String user_name = null;
    public String user_email = null;
    public ArrayList<String> group_ids;
    public ArrayList<String> group_names;
    public ArrayList<String> group_members;
    public ArrayList<String> group_payers;
    public ArrayList<String> group_money;
    public ArrayList<String> group_debtors;
    public ArrayList<String> group_purposes;
    public ArrayList<Button> btn_mygroups;



    public float user_money = 0;
    TextView title;
    TextView title_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        group_names = new ArrayList<String>();
        group_members = new ArrayList<String>();
        group_payers = new ArrayList<String>();
        group_money = new ArrayList<String>();
        group_debtors = new ArrayList<String>();
        btn_mygroups = new ArrayList<Button>();
        group_ids = new ArrayList<String>();
        group_purposes = new ArrayList<String>();

        // get user id
        Bundle bundle = this.getIntent().getExtras();
        String user_id = bundle.getString("id");

        // set database
        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        final SQLiteDatabase database;
        database = dbHelper.getDatabase();

        Cursor cursor = database.query("User", null, null, null, null, null, null);
        Cursor group_cursor = database.query("Groups", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do{
                if(user_id.equals(cursor.getString(cursor.getColumnIndex("id")))){
                    user_email = cursor.getString(cursor.getColumnIndex("mail"));
                    user_name = cursor.getString(cursor.getColumnIndex("name"));
                    user_money = Float.parseFloat(cursor.getString(cursor.getColumnIndex("money")));
                    if(group_cursor.moveToFirst()){
                        do{
                            if(user_id.equals(group_cursor.getString(group_cursor.getColumnIndex("user_id")))){
                                group_ids.add(group_cursor.getString(group_cursor.getColumnIndex("id")));
                                group_names.add(group_cursor.getString(group_cursor.getColumnIndex("group_name")));
                                group_members.add(group_cursor.getString(group_cursor.getColumnIndex("members")));
                                group_payers.add(group_cursor.getString(group_cursor.getColumnIndex("payers")));
                                group_money.add(group_cursor.getString(group_cursor.getColumnIndex("pay_money")));
                                group_debtors.add(group_cursor.getString(group_cursor.getColumnIndex("debtors")));
                                group_purposes.add(group_cursor.getString(group_cursor.getColumnIndex("purpose")));
                            }
                        }while(group_cursor.moveToNext());
                    }
                    break;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();

        // set title
        title = this.findViewById(R.id.UserName);
        title.setText(user_name);

        // set info button
        Button btn_info = this.findViewById(R.id.UserButton);

        btn_info.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                builder.setMessage("PERSONAL INFO:"+"\n\n Your Email:\n"+" "+user_email+" "+"\n\n Your Name:\n"+" "+user_name+"\n");
                builder.show();
            }
        });

        // set add group button
        FloatingActionButton btn_group = this.findViewById(R.id.AddGroup);

        btn_group.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
//                Animation animation = AnimationUtils.loadAnimation(MainPageActivity.this, R.anim.rotate);
//                btn_group.setAnimation(animation);
                Bundle bundle = new Bundle();
                bundle.putString("id", user_id);
                bundle.putString("name", user_name);
                Intent intent = new Intent(getApplicationContext(), AddAGroupActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


        // set money
        title_money = this.findViewById(R.id.UserMoney);
        if(user_money>0){
            title_money.setText("Others owe me: "+Float.toString(user_money)+" Euros");
        }else if(user_money<0){
            title_money.setText("I owe other people: "+Float.toString(-1*user_money)+" Euros");
        }else{
            title_money.setText("No debt, No claims: 0 Euro");
        }

        // set MyGroups
        if(group_names!=null){
            LinearLayout layout = findViewById(R.id.MyGroups);
            layout.setOrientation(LinearLayout.VERTICAL);
            for (int i=0;i<group_names.size();i++){
                Button btn_mygroup = new Button(this);
                btn_mygroup.setTextSize(20);
                int currColor = (int) -(Math.random() * (16777216 - 1) + 1);
                btn_mygroup.setBackgroundColor(currColor);
                btn_mygroup.setText(group_names.get(i));
                System.out.println("\n"+"\tMembers: "+group_members.get(i));
                btn_mygroups.add(btn_mygroup);
                layout.addView(btn_mygroup);
                Space sp = new Space(this);
                sp.setMinimumHeight(10);
                layout.addView(sp);
            }
        }


        // set New buttons
        if(group_names!=null){
            for (int i=0;i<btn_mygroups.size();i++){
                int finalI = i;
                btn_mygroups.get(i).setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        Bundle bundle = new Bundle();
                        bundle.putString("id", user_id);
                        bundle.putString("group_id", group_ids.get(finalI));
                        Intent intent = new Intent(getApplicationContext(), AddContentActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });
            }
        }
    }
}
