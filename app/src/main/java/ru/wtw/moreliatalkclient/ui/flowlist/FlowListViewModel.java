package ru.wtw.moreliatalkclient.ui.flowlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FlowListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FlowListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Chat list");
    }

    public LiveData<String> getText() {
        return mText;
    }
}