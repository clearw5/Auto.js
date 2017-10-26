package com.stardust.scriptdroid.ui.login;

import android.widget.TextView;
import android.widget.Toast;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.network.UserService;
import com.stardust.scriptdroid.ui.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/9/20.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @ViewById(R.id.username)
    TextView mUserName;

    @ViewById(R.id.password)
    TextView mPassword;

    @AfterViews
    void setUpViews() {
        setToolbarAsBack(getString(R.string.text_login));
    }

    @Click(R.id.login)
    void login() {
        String userName = mUserName.getText().toString();
        String password = mPassword.getText().toString();
        if (!checkNotEmpty(userName, password)) {
            return;
        }
        UserService.getInstance().login(userName, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            Toast.makeText(getApplicationContext(), R.string.text_login_succeed, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        , error ->
                                Toast.makeText(getApplicationContext(), R.string.text_login_fail, Toast.LENGTH_SHORT).show());

    }

    private boolean checkNotEmpty(String userName, String password) {
        if (userName.isEmpty()) {
            mUserName.setError(getString(R.string.text_username_cannot_be_empty));
            return false;
        }
        if (password.isEmpty()) {
            mUserName.setError(getString(R.string.text_password_cannot_be_empty));
            return false;
        }
        return true;
    }
}
