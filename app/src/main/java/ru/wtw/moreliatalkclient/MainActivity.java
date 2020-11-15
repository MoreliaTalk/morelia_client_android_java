package ru.wtw.moreliatalkclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ru.wtw.moreliatalkclient.ui.LoginFragment;
import ru.wtw.moreliatalkclient.ui.NewFlowFragment;
import ru.wtw.moreliatalkclient.ui.RegisterFragment;
import ru.wtw.moreliatalkclient.ui.flowlist.FlowListFragment;
import ru.wtw.moreliatalkclient.ui.jsonlogs.JsonLogsFragment;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginDialogListener,
        RegisterFragment.RegisterDialogListener, NewFlowFragment.NewFlowDialogListener {

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private ImageView connectStatus;
    private TextView userName;
    private TextView userStatus;

    private Network network;

    private DBHelper mydb;

    public UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i("SERVER", "setup onCreate()");
/*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Chat creation not implemented", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        connectStatus = navigationView.getHeaderView(0).findViewById(R.id.connectStatus);
        userName = navigationView.getHeaderView(0).findViewById(R.id.userName);
        userStatus = navigationView.getHeaderView(0).findViewById(R.id.userStatus);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_flowlist, R.id.nav_jsonlogs, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        if (id == R.id.nav_login) {

                            if (userSession != null) {
                                if (userSession.isAuthed()) {
/*
                                    ViewParent parent = navigationView.getParent();
                                    if (parent instanceof DrawerLayout) {
                                        ((DrawerLayout) parent).closeDrawer(navigationView);
                                    }
*/
                                    final Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            onLoggedOut();
                                        }
                                    }, 1000);
                                } else {
                                    DialogFragment dlgLogin = new LoginFragment();
                                    dlgLogin.show(getSupportFragmentManager(), dlgLogin.getClass().getName());
                                }
                            }  else {
                                DialogFragment dlgLogin = new LoginFragment();
                                dlgLogin.show(getSupportFragmentManager(), dlgLogin.getClass().getName());

                            }
/*
                            Intent intent;
                            intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
*/
                        }
                        if (id == R.id.nav_register) {
                            DialogFragment dlgRegister = new RegisterFragment();
                            dlgRegister.show(getSupportFragmentManager(), dlgRegister.getClass().getName());
                        }
                        if (id == R.id.nav_newflow) {
                            DialogFragment dlgNewFlow = new NewFlowFragment();
                            dlgNewFlow.show(getSupportFragmentManager(), dlgNewFlow.getClass().getName());
                        }
                        boolean handled = NavigationUI.onNavDestinationSelected(menuItem, navController);
                        if (handled) {
                            ViewParent parent = navigationView.getParent();
                            if (parent instanceof DrawerLayout) {
                                ((DrawerLayout) parent).closeDrawer(navigationView);
                            }
                        }
                        return handled;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            navController.navigate(R.id.nav_settings);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void onNewFlowDialog(String name, String type, String info) {
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawers();
        if (network != null) {
            network.createFlow(name, type, info);
        }
    }

    @Override
    public void onLoginDialog(String Server, String Login, String Password) {
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        userSession = new UserSession(Login, Password, "ws://", Server, "8000");
        connectStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_status_yellow));
        mDrawerLayout.closeDrawers();
        network = new Network(MainActivity.this);
        network.setLogin(Login);
        network.setPassword(Password);
        network.setServername("ws://" + Server + ":" + "8000" + "/ws");
        network.setShowJSON(true);
        network.setReconnect(true);
        network.setRegister(false);
        network.connect();
    }

    @Override
    public void onRegisterDialog(String Server, String Login, String Password, String Name, String Email) {
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        userSession = new UserSession(Login, Password, "ws://", Server, "8000", Name, Email);
        connectStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_status_yellow));
        mDrawerLayout.closeDrawers();
        network = new Network(MainActivity.this);
        network.setUsername(Name);
        network.setLogin(Login);
        network.setEmail(Email);
        network.setPassword(Password);
        network.setServername("ws://" + Server + ":" + "8000" + "/ws");
        network.setShowJSON(true);
        network.setReconnect(true);
        network.setRegister(true);
        network.connect();
    }

    public void onJson(String json) {
        Intent intent = new Intent(JsonLogsFragment.RADIO_JSON_CHANGED);
        this.getApplicationContext().sendBroadcast(intent);
    }

    public void onFlowsUpdate() {
        Intent intent = new Intent(FlowListFragment.RADIO_FLOW_CHANGED);
        this.getApplicationContext().sendBroadcast(intent);
    }

    public void onLoggedIn() {
        userSession.setAuthed();
        connectStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_status_green));
        userName.setText(userSession.getName());
        userStatus.setText(userSession.getLogin()+"@"+userSession.getServer());
        navigationView.getMenu().findItem(R.id.nav_register).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_newflow).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_login).setTitle(getResources().getString(R.string.menu_logout));
        navigationView.getMenu().findItem(R.id.nav_login).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_morelia_logout));
    }

    public void onLoggedOut() {
        userSession.setUnAuthed();
        connectStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_status_gray));
        userName.setText("");
        userStatus.setText("");
        navigationView.getMenu().findItem(R.id.nav_register).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_newflow).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_login).setTitle(getResources().getString(R.string.menu_login));
        navigationView.getMenu().findItem(R.id.nav_login).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_morelia_login));
        mydb.clearFlows();
        network = null;
        onFlowsUpdate();
    }

    public void onRegisterResponse(int code) {
        switch (code) {
            case 201: {
                onLoggedIn();
                network.sendRequestAllFlow();
                break;
            }
            case 409: {
                connectStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_status_red));
                userName.setText("");
                userStatus.setText("");
                break;
            }
            default: {
                connectStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_status_gray));
                break;
            }
        }
    }

    public void onUserInfoResponse(String login, String username, String email) {
        if (login.equals(userSession.getLogin())) {
            userName.setText(username);
            userSession.setName(username);
            userSession.setEmail(email);
        }
    }


    public void onAuthResponse(int code) {
        switch (code) {
            case 200: {
                onLoggedIn();
                network.sendRequestUserInfo(network.getUuid());
                network.sendRequestAllFlow();
                break;
            }
            case 403: {
                connectStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_status_red));
                userName.setText("");
                userStatus.setText("");
                break;
            }
            default: {
                connectStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_status_gray));
                break;
            }
        }
    }


    public void onMessage(String user, String text, String time) {
        int type = MessageAdapter.TYPE_SERVICE;
        if (!user.equals("")) {
            if (network.getLogin().equals(user)) {
                type = MessageAdapter.TYPE_MSG_IN;
            } else {
                type = MessageAdapter.TYPE_MSG_OUT;
            }
        } else {
            if (time.equals("*")) {
                type = MessageAdapter.TYPE_ERROR;
            }
        }
/*
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
*/
    }


//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

}