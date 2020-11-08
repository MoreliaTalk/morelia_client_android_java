package ru.wtw.moreliatalkclient.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import ru.wtw.moreliatalkclient.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewFlowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewFlowFragment extends DialogFragment implements View.OnClickListener {

    private EditText editFlowType;
    private EditText editFlowName;
    private EditText editFlowInfo;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewFlowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewFlowFragment newInstance(String param1, String param2) {
        NewFlowFragment fragment = new NewFlowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public interface NewFlowDialogListener {
        void onNewFlowDialog(String Server, String Login, String Password);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_newflow, null);
        v.findViewById(R.id.btnNewFlow).setOnClickListener(this);
        editFlowType =v.findViewById(R.id.editFlowType);
        editFlowInfo =v.findViewById(R.id.editFlowInfo);
        editFlowName =v.findViewById(R.id.editFlowName);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNewFlow: {
                Integer EmptyFieldsCount=0;
                if (isEmpty(editFlowName)) {
                    editFlowName.setError(getString(R.string.cannot_be_empty));
                    EmptyFieldsCount++;
                }
                if (isEmpty(editFlowType)) {
                    editFlowType.setError(getString(R.string.cannot_be_empty));
                    EmptyFieldsCount++;
                }
                if (EmptyFieldsCount>0) {
                    Toast.makeText(getActivity(),
                            R.string.all_fields_must_be_filled, Toast.LENGTH_LONG).show();
                } else {
                    NewFlowDialogListener listener = (NewFlowDialogListener) getActivity();
                    listener.onNewFlowDialog(editFlowName.getText().toString(),
                            editFlowType.getText().toString(), editFlowInfo.getText().toString());
                    dismiss();
                }
            }
        }
    }
}