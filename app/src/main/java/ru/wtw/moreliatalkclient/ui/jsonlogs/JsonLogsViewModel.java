package ru.wtw.moreliatalkclient.ui.jsonlogs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class JsonLogsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public JsonLogsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("JSON");
    }

    public LiveData<String> getText() {
        return mText;
    }
}