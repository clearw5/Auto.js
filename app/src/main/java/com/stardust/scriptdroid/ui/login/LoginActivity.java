package com.stardust.scriptdroid.ui.login;

import android.util.Log;
import android.widget.TextView;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.network.NodeBB;
import com.stardust.scriptdroid.network.api.UserApi;
import com.stardust.scriptdroid.network.entity.VerifyResponse;
import com.stardust.scriptdroid.tool.SimpleObserver;
import com.stardust.scriptdroid.ui.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
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

        NodeBB.getInstance().getRetrofit()
                .create(UserApi.class)
                .verify(userName, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<VerifyResponse>() {

                    @Override
                    public void onNext(@NonNull VerifyResponse verifyResponse) {
                        Log.d("Login", verifyResponse.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }
                });
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
