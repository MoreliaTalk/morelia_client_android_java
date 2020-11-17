package ru.wtw.moreliatalkclient.ui.flowlist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import ru.wtw.moreliatalkclient.DBHelper;
import ru.wtw.moreliatalkclient.FlowActivity;
import ru.wtw.moreliatalkclient.MainActivity;
import ru.wtw.moreliatalkclient.R;
import ru.wtw.moreliatalkclient.UserSession;

public class FlowListFragment extends Fragment {

    public static final String RADIO_FLOW_CHANGED = "morelia.wtw.ru.RADIODATASETCHANGED";

    private FlowListViewModel flowListViewModel;

    private ListView FlowList;
    private DBHelper appDB;
    private ArrayAdapter arrayAdapter;
    private ArrayList array_list;

    private Radio radio;

    private class Radio extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RADIO_FLOW_CHANGED)) {
                array_list.clear();
                array_list.addAll(appDB.getAllFlow());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        radio = new FlowListFragment.Radio();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        flowListViewModel = new
                ViewModelProvider(this).get(FlowListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_flowlist, container, false);

        appDB = new DBHelper(getContext());

        array_list = appDB.getAllFlow();
        arrayAdapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1,
                array_list);

        FlowList = (ListView) root.findViewById(R.id.listViewFlows);
        FlowList.setAdapter(arrayAdapter);

        FlowList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String[] words = FlowList.getItemAtPosition(position).toString().split(" ");
                int flow_id = Integer.valueOf(words[1]);

                UserSession userSession=((MainActivity) getActivity()).userSession;

                if (userSession != null) {
                    if (userSession.isAuthed()) {
                        Intent intent;
                        intent = new Intent(((MainActivity) getActivity()), FlowActivity.class);
                        intent.putExtra("login", userSession.getLogin());
                        intent.putExtra("username", userSession.getName());
                        intent.putExtra("password", userSession.getPassword());
                        intent.putExtra("servername", "ws://" + userSession.getServer() + ":" + "8000" + "/ws");
                        intent.putExtra("flow_id", flow_id);
                        intent.putExtra("user_login", userSession.getLogin());
                        intent.putExtra("flow_name", FlowList.getItemAtPosition(position).toString());
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "Login to see chat", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Login to see chat", Toast.LENGTH_SHORT).show();
                }
                /*

                Toast.makeText(getContext(), flow_id, Toast.LENGTH_SHORT).show();
*/
            }
        });

        return root;

    }

    @Override
    public void onResume() {
        super.onResume();
        //using intent filter just in case you would like to listen for more transmissions
        IntentFilter filter = new IntentFilter();
        filter.addAction(RADIO_FLOW_CHANGED);
        getActivity().getApplicationContext().registerReceiver(radio, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            getActivity().getApplicationContext().unregisterReceiver(radio);
        }catch (Exception e){
            //Cannot unregister receiver
        }

    }


}