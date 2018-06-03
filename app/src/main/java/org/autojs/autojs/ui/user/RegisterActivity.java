package org.autojs.autojs.ui.user;

import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import org.autojs.autojs.R;
import org.autojs.autojs.network.NodeBB;
import org.autojs.autojs.network.UserService;
import org.autojs.autojs.ui.BaseActivity;
import com.stardust.theme.ThemeColorManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/10/26.
 */
@EActivity(R.layout.activity_register)
public class RegisterActivity extends BaseActivity {

    @ViewById(R.id.email)
    TextView mEmail;

    @ViewById(R.id.username)
    TextView mUserName;

    @ViewById(R.id.password)
    TextView mPassword;

    @ViewById(R.id.register)
    View mRegister;


    @AfterViews
    void setUpViews() {
        setToolbarAsBack(getString(R.string.text_register));
        ThemeColorManager.addViewBackground(mRegister);
    }

    @Click(R.id.register)
    void login() {
        String email = mEmail.getText().toString();
        String userName = mUserName.getText().toString();
        String password = mPassword.getText().toString();
        if (!validateInput(email, userName, password)) {
            return;
        }
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .content(R.string.text_registering)
                .cancelable(false)
                .show();
        UserService.getInstance().register(email, userName, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            dialog.dismiss();
                            onRegisterResponse(response.string());
                        }
                        , error -> {
                            dialog.dismiss();
                            mPassword.setError(NodeBB.getErrorMessage(error, RegisterActivity.this, R.string.text_register_fail));
                        });

    }

    private void onRegisterResponse(String res) {
        Toast.makeText(this, R.string.text_register_succeed, Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateInput(String email, String userName, String password) {
        if (email.isEmpty()) {
            mEmail.setError(getString(R.string.text_email_cannot_be_empty));
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError(getString(R.string.text_email_format_error));
            return false;
        }
        if (userName.isEmpty()) {
            mUserName.setError(getString(R.string.text_username_cannot_be_empty));
            return false;
        }
        if (password.isEmpty()) {
            mUserName.setError(getString(R.string.text_password_cannot_be_empty));
            return false;
        }
        if (password.length() < 6) {
            mPassword.setError(getString(R.string.nodebb_error_change_password_error_length));
            return false;
        }
        return true;
    }
}
