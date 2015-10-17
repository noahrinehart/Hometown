package com.boilermakeproject.hometown;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;

public class AddContact extends AppCompatActivity {

    private SQLController dbcon;
    EditText nameText;
    EditText homeText;
    EditText numText;
    EditText noteText;
    Button saveButton;
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_contact);


        nameText = (EditText) findViewById(R.id.contact_name);
        homeText = (EditText)findViewById(R.id.hometown_add);
        numText = (EditText)findViewById(R.id.contact_phone);
        noteText = (EditText)findViewById(R.id.contact_note);

        saveButton = (Button)findViewById(R.id.save_contact);
        deleteButton = (Button)findViewById(R.id.delete_contact);


        dbcon = new SQLController(getApplicationContext());
        try{
            dbcon.open();

        }catch (SQLException e){
            e.printStackTrace();
        }
        if (this.getIntent().getExtras() != null) {
            Intent intent = getIntent();
            final String id = intent.getStringExtra("id");
            final String name = intent.getStringExtra("name");
            final String home = intent.getStringExtra("home");
            final String lat = intent.getStringExtra("lat");
            final String lon = intent.getStringExtra("long");
            final String num = intent.getStringExtra("num");
            final String note = intent.getStringExtra("note");
            nameText.setText(name);
            homeText.setText(home);
            numText.setText(num);
            noteText.setText(note);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbcon.update(Long.parseLong(id), nameText.getText().toString(), "0", "0", homeText.getText().toString(), null, numText.getText().toString(), noteText.getText().toString());
                    AddContact.this.finish();
                }
            });
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbcon.delete(Long.parseLong(id));
                    //insert dialog
                    //Toast toast = new Toast.makeText(getApplicationContext(), "deleted " + name, Toast.LENGTH_SHORT).show();
                    AddContact.this.finish();

                }
            });


        }
        if (this.getIntent().getExtras() == null){
            nameText.setHint("Name");
            homeText.setHint("Home");
            numText.setHint("Phone Number");
            noteText.setHint("Notes");
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbcon.insert(nameText.getText().toString(), "0", "0", homeText.getText().toString(), null, numText.getText().toString(), noteText.getText().toString());
                AddContact.this.finish();
            }
        });



    }
}
