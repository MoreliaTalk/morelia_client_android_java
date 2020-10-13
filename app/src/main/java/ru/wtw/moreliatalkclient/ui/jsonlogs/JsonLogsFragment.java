package ru.wtw.moreliatalkclient.ui.jsonlogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import ru.wtw.moreliatalkclient.R;

public class JsonLogsFragment extends Fragment {

    private JsonLogsViewModel jsonLogsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        jsonLogsViewModel =
                ViewModelProviders.of(this).get(JsonLogsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_jsonlogs, container, false);
        final TextView textView = root.findViewById(R.id.text_jsonlogs);
        jsonLogsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}