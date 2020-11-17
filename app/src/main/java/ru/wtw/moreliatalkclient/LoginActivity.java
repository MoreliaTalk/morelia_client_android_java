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
import android.widget.Spinner;
import android.widget.Switch;


public class LoginActivity extends Activity implements View.OnClickListener {

        public static final String APP_PREFERENCES = "preferences";
        private SharedPreferences settings;

        @Override
        public void onClick(View v){
            EditText editServername = findViewById(R.id.editFlowName);
            EditText editUsername = findViewById(R.id.editUsername);
            EditText editLogin = findViewById(R.id.editFlowType);
            EditText editPassword = findViewById(R.id.editFlowInfo);
            EditText editEmail = findViewById(R.id.editEmail);
            Switch switchReconnect = findViewById(R.id.switchReconnect);
            Switch switchJSON = findViewById(R.id.switchOutJSON);
            Switch switchRawJSON = findViewById(R.id.switchRawJSON);
            Spinner spinnerThemeIndex = findViewById(R.id.themeIndex);
            String login = editLogin.getText().toString();
            String username = editUsername.getText().toString();
            String password = editPassword.getText().toString();
            String servername = editServername.getText().toString();
            String email = editEmail.getText().toString();
            int themeIndex = spinnerThemeIndex.getSelectedItemPosition();
            boolean reconnect = switchReconnect.isChecked();
            boolean outJSON = switchJSON.isChecked();
            boolean rawJSON = switchRawJSON.isChecked();
            if (servername.isEmpty() || login.isEmpty() || password.isEmpty()) {
                Log.e("SERVER", "Must be filled");
            } else {
                Intent intent;
                intent = new Intent(LoginActivity.this, FlowActivity.class);
                intent.putExtra("login", login);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                intent.putExtra("servername", servername);
                intent.putExtra("email", email);
                intent.putExtra("reconnect", reconnect);
                intent.putExtra("outjson", outJSON);
                intent.putExtra("rawjson", rawJSON);
                intent.putExtra("register", true);
                intent.putExtra("theme", themeIndex);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("servername", servername);
                editor.putString("username", username);
                editor.putString("login", login);
                editor.putString("password", password);
                editor.putString("email", email);
                editor.putBoolean("reconnect", reconnect);
                editor.putBoolean("outjson", outJSON);
                editor.putBoolean("rawjson", rawJSON);
                editor.putInt("theme", themeIndex);
                switch (v.getId()) {
                    case R.id.btnNewFlow: {
                        intent.putExtra("register", false);
                        editor.apply();
                        startActivity(intent);
                        break;
                    }
                    case R.id.btnRegister: {
                        intent.putExtra("register", true);
                        editor.apply();
                        startActivity(intent);
                        break;
                    }
                }
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

            Button btnLogin = findViewById(R.id.btnNewFlow);
            btnLogin.setOnClickListener(this);
            Button btnRegister = findViewById(R.id.btnRegister);
            btnRegister.setOnClickListener(this);

            if (settings.contains("servername")) {
                ((EditText)findViewById(R.id.editFlowName)).setText(settings.getString("servername", ""));
            }
            if (settings.contains("login")) {
                ((EditText)findViewById(R.id.editFlowType)).setText(settings.getString("login", ""));
            } else if (settings.contains("username")) {
                ((EditText)findViewById(R.id.editFlowType)).setText(settings.getString("username", ""));
            }
            if (settings.contains("username")) {
                ((EditText)findViewById(R.id.editUsername)).setText(settings.getString("username", ""));
            }
            if (settings.contains("password")) {
                ((EditText)findViewById(R.id.editFlowInfo)).setText(settings.getString("password", ""));
            }
            if (settings.contains("email")) {
                ((EditText)findViewById(R.id.editEmail)).setText(settings.getString("email", ""));
            }
            ((Spinner)findViewById(R.id.themeIndex)).setSelection(settings.getInt("theme", 0));
            ((Switch)findViewById(R.id.switchReconnect)).setChecked(settings.getBoolean("reconnect", false));
            ((Switch)findViewById(R.id.switchOutJSON)).setChecked(settings.getBoolean("outjson", false));
            ((Switch)findViewById(R.id.switchRawJSON)).setChecked(settings.getBoolean("rawjson", false));
        }
}
