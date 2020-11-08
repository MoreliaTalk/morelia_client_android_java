package ru.wtw.moreliatalkclient.ui.jsonlogs;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ru.wtw.moreliatalkclient.DBHelper;
import ru.wtw.moreliatalkclient.R;

public class JsonLogsFragment extends Fragment {

    public static final String RADIO_JSON_CHANGED = "com.yourapp.app.RADIODATASETCHANGED";

    private JsonLogsViewModel jsonLogsViewModel;
    private ListView JsonList;
    private DBHelper mydb;
    private ArrayAdapter arrayAdapter;
    private ArrayList array_list;

    private Radio radio;

    private class Radio extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RADIO_JSON_CHANGED)) {
                array_list.clear();
                array_list.addAll(mydb.getAllJson());
                arrayAdapter.notifyDataSetChanged();
                //Notify dataset changed here
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        radio = new Radio();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        jsonLogsViewModel = new
                ViewModelProvider(this).get(JsonLogsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_jsonlogs, container, false);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mydb.clearJSON();
                array_list.clear();
                array_list.addAll(mydb.getAllJson());
                arrayAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Logs cleared", Toast.LENGTH_SHORT).show();
            }
        });



        //        final TextView textView = root.findViewById(R.id.text_jsonlogs);
        mydb = new DBHelper(getContext());
        array_list = mydb.getAllJson();
        arrayAdapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_list_item_1,
                array_list);

        JsonList = (ListView) root.findViewById(R.id.listView1);
        JsonList.setAdapter(arrayAdapter);
        JsonList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("json", JsonList.getItemAtPosition(position).toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });


/*
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("action_json_updated".equals(intent.getAction())) {
                    // update fragment here
                    Toast.makeText(getContext(), "UPDATE", Toast.LENGTH_SHORT).show();
                    arrayAdapter.notifyDataSetChanged();
                    arrayAdapter.
                }
            }
        };
        IntentFilter filter = new IntentFilter("action_json_updated");
        getContext().registerReceiver(mReceiver, filter);
*/

//        jsonLogsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        //using intent filter just in case you would like to listen for more transmissions
        IntentFilter filter = new IntentFilter();
        filter.addAction(RADIO_JSON_CHANGED);
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