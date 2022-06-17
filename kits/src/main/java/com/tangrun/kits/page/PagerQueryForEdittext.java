package com.tangrun.kits.page;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

public class PagerQueryForEdittext implements TextWatcher, IPageQuery, TextView.OnEditorActionListener, Runnable {
    IPager pager;
    EditText editText;
    String query;
    long delayChangedTime = 1000;

    public PagerQueryForEdittext(IPager pager, EditText editText) {
        this.pager = pager;
        this.editText = editText;
        setEditText(editText);
    }

    /**
     * 没有设置singe line时, 输入变化延迟时间
     * @param delayChangedTime
     * @return
     */
    public PagerQueryForEdittext setDelayChangedTime(long delayChangedTime) {
        this.delayChangedTime = delayChangedTime;
        return this;
    }

    void setEditText(EditText editText) {
        this.editText = editText;
        int maxLines = editText.getMaxLines();
        if (maxLines == 1){
            editText.setOnEditorActionListener(this);
        }else {
            editText.addTextChangedListener(this);
        }
    }

    boolean startRefresh(){
        if (pager!=null){
            query = editText.getText().toString();
            pager.startRefresh();
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        editText.removeCallbacks(this);
        editText.postDelayed(this, delayChangedTime);
    }

    @Override
    public String getQueryContent() {
        return query;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return startRefresh();
    }

    @Override
    public void run() {
        startRefresh();
    }
}
