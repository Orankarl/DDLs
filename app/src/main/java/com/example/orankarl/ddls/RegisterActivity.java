package com.example.orankarl.ddls;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    RegisterActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = this;

        Button registerButton = findViewById(R.id.register_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText username = findViewById(R.id.register_username);
                final EditText password = findViewById(R.id.register_password);
                final EditText stuNumber = findViewById(R.id.student_number);
                if (username.getText().toString().equals("")) {
                    Toast.makeText(context, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (stuNumber.getText().toString().equals("")) {
                    Toast.makeText(context, "Student number cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (stuNumber.getText().toString().equals("")) {
                    Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String retStr = Net.register(username.getText().toString(), stuNumber.getText().toString(), password.getText().toString());
                            if (retStr.equals("")) {
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Register successfully", Toast.LENGTH_SHORT).show();
                                        context.onBackPressed();
                                    }
                                });
                            } else {
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, retStr, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }

            }
        });

        Toolbar toolbar = findViewById(R.id.register_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back_arrow);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
