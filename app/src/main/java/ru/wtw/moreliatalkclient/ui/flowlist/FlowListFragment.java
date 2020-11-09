package ru.wtw.moreliatalkclient.ui.flowlist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import ru.wtw.moreliatalkclient.DBHelper;
import ru.wtw.moreliatalkclient.R;
import ru.wtw.moreliatalkclient.ui.jsonlogs.JsonLogsFragment;

public class FlowListFragment extends Fragment {

    public static final String RADIO_FLOW_CHANGED = "com.yourapp.app.RADIODATASETCHANGED";

    private FlowListViewModel flowListViewModel;

    private ListView FlowList;
    private DBHelper mydb;
    private ArrayAdapter arrayAdapter;
    private ArrayList array_list;

    private Radio radio;

    private class Radio extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RADIO_FLOW_CHANGED)) {
                array_list.clear();
                array_list.addAll(mydb.getAllFlow());
                arrayAdapter.notifyDataSetChanged();
                //Notify dataset changed here
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

        mydb = new DBHelper(getContext());

        array_list = mydb.getAllFlow();
        arrayAdapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1,
                array_list);

        FlowList = (ListView) root.findViewById(R.id.listViewFlows);
        FlowList.setAdapter(arrayAdapter);

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