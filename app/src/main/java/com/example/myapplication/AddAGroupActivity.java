package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddAGroupActivity extends AppCompatActivity {

    public DBManager dbHelper;
    TextView firstMember;
    public String member_list;
    public String real_member_list;
    public Button btn_createGroup;
    public EditText groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_agroup);

        // get user name
        Bundle bundle = this.getIntent().getExtras();
        String user_id = bundle.getString("id");
        String user_name = bundle.getString("name");

        // set database
        dbHelper = new DBManager(this);
        dbHelper.openDatabase();

        final SQLiteDatabase database;
        database = dbHelper.getDatabase();

        // set 1st member
        member_list = "  " + user_name + " (group owner)";
        real_member_list = user_name + "\n";
        firstMember = this.findViewById(R.id.Myself);
        firstMember.setText(member_list);


        // set link_addMember
        TextView _link_addmember = this.findViewById(R.id.link_addmember);
        _link_addmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddAGroupActivity.this);
                builder.setTitle("New member");
                LayoutInflater Inflater = getLayoutInflater();
                v = Inflater.inflate(R.layout.add_member, null);
                builder.setView(v);
                EditText u = v.findViewById(R.id.NewMember);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String new_member = u.getText().toString();
                        member_list = member_list + "\n  " + new_member;
                        real_member_list = real_member_list + "\n" + new_member;
                        firstMember.setText(member_list);
                        Toast.makeText(AddAGroupActivity.this, new_member + " is added", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        // set group name
        groupName = this.findViewById(R.id.GroupName);

        // set finish button
        btn_createGroup = this.findViewById(R.id.CreateGroup);
        btn_createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group_name = groupName.getText().toString();
                System.out.println(group_name);
                if (group_name.length()<1) {
                    Toast.makeText(getBaseContext(), "Group Name Cannot Be Empty", Toast.LENGTH_LONG).show();
                } else {
                    validateAddition(database,group_name,user_id);
                }
            }
        });

    }

    public void validateAddition(SQLiteDatabase database, String group_name, String user_id) {
        Cursor cursor = database.query("Groups", null, null, null, null, null, null);
        int flag = 0;
        if (cursor.moveToFirst()) {
            do{
                System.out.println(cursor.getString(cursor.getColumnIndex("id")));
                System.out.println(cursor.getString(cursor.getColumnIndex("group_name")));
                if(group_name.equals(cursor.getString(cursor.getColumnIndex("group_name")))){
                    flag = 1;
                    break;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        if(flag==0){
            System.out.println(group_name);
            ContentValues values = new ContentValues();
            values.put("group_name",group_name);
            values.put("user_id",user_id);
            values.put("members",real_member_list);
            values.put("payers"," ");
            values.put("pay_money"," ");
            values.put("debtors"," ");
            values.put("purpose"," ");

            database.insert("Groups", null, values);
            Bundle bundle = new Bundle();
            bundle.putString("id", user_id);
            Intent intent = new Intent(getApplicationContext(),MainPageActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            database.close();
            finish();
        }else{
            onAddFailed();
        }
    }

    public void onAddFailed() {
        Toast.makeText(getBaseContext(), "Add failed -  Group name already exists", Toast.LENGTH_LONG).show();
        btn_createGroup.setEnabled(true);
    }
    
}