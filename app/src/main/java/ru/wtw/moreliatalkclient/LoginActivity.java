package ru.wtw.moreliatalkclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends Activity {

        public static final String APP_PREFERENCES = "preferences";
        private SharedPreferences settings;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

            Button btnLogin = findViewById(R.id.btnLogin);

            if (settings.contains("servername")) {
                ((EditText)findViewById(R.id.editServername)).setText(settings.getString("servername", ""));
            }
            if (settings.contains("username")) {
                ((EditText)findViewById(R.id.editUsername)).setText(settings.getString("username", ""));
            }
            if (settings.contains("password")) {
                ((EditText)findViewById(R.id.editPassword)).setText(settings.getString("password", ""));
            }

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText editServername = findViewById(R.id.editServername);
                    EditText editUsername = findViewById(R.id.editUsername);
                    EditText editPassword = findViewById(R.id.editPassword);
                    String username = editUsername.getText().toString();
                    String password = editPassword.getText().toString();
                    String servername = editServername.getText().toString();
                    if (servername.isEmpty() || username.isEmpty() || password.isEmpty()) {
                        Log.e("SERVER", "Must be filled");
                    } else {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("password", password);
                        intent.putExtra("servername", servername);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("servername", servername);
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.apply();
                        startActivity(intent);
                    }
                }
            });
        }
}
