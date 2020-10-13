package ru.wtw.moreliatalkclient.ui.flowlist;

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

public class FlowListFragment extends Fragment {

    private FlowListViewModel flowListViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        flowListViewModel =
                ViewModelProviders.of(this).get(FlowListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_flowlist, container, false);
        final TextView textView = root.findViewById(R.id.text_flowlist);
        flowListViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}