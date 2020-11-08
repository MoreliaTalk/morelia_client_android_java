package ru.wtw.moreliatalkclient.ui;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import ru.wtw.moreliatalkclient.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends DialogFragment implements View.OnClickListener {

    private EditText editUsername;
    private EditText editLogin;
    private EditText editServer;
    private EditText editPassword;
    private EditText editEmail;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
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
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public interface RegisterDialogListener {
        void onRegisterDialog(String Server, String Login, String Password, String Name, String Email);
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
        View v = inflater.inflate(R.layout.fragment_register, null);
        v.findViewById(R.id.btnRegister).setOnClickListener(this);
        editUsername=v.findViewById(R.id.editUsername);
        editLogin=v.findViewById(R.id.editFlowType);
        editPassword=v.findViewById(R.id.editFlowInfo);
        editServer=v.findViewById(R.id.editFlowName);
        editUsername=v.findViewById(R.id.editUsername);
        editEmail=v.findViewById(R.id.editEmail);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister: {
                Integer EmptyFieldsCount=0;
                if (isEmpty(editServer)) {
                    editServer.setError(getString(R.string.cannot_be_empty));
                    EmptyFieldsCount++;
                }
                if (isEmpty(editLogin)) {
                    editLogin.setError(getString(R.string.cannot_be_empty));
                    EmptyFieldsCount++;
                }
                if (isEmpty(editUsername)) {
                    editUsername.setError(getString(R.string.cannot_be_empty));
                    EmptyFieldsCount++;
                }
                if (isEmpty(editEmail)) {
                    editEmail.setError(getString(R.string.cannot_be_empty));
                    EmptyFieldsCount++;
                }
                if (isEmpty(editPassword)) {
                    editPassword.setError(getString(R.string.cannot_be_empty));
                    EmptyFieldsCount++;
                }
                if (EmptyFieldsCount>0) {
                    Toast.makeText(getActivity(),
                            R.string.all_fields_must_be_filled, Toast.LENGTH_LONG).show();
                } else {
                    RegisterDialogListener listener = (RegisterDialogListener) getActivity();
                    listener.onRegisterDialog(editServer.getText().toString(),
                            editLogin.getText().toString(),editPassword.getText().toString(),
                            editUsername.getText().toString(),editEmail.getText().toString());
                    dismiss();
                }
            }
        }
    }
}