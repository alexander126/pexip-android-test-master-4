package com.gearedapp.ajentapexip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class Login extends AppCompatActivity {

    private EditText roomext;
    private EditText username;
    public String roomextension;
    public String name;
    private Button connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        roomext = findViewById(R.id.room_extension);
        username = findViewById(R.id.username);
        connect = findViewById(R.id.connect);

        connect.setOnClickListener(onConnect);

    }
    private View.OnClickListener onConnect = ((View v) -> {
        roomextension = roomext.getText().toString();
        name = username.getText().toString();
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.putExtra("ext", roomextension);
        intent.putExtra("usr", name);
        startActivity(intent);
    });
}
