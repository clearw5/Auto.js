package org.autojs.autojs.ui.user;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
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
import org.w3c.dom.Node;

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

    @ViewById(R.id.login)
    View mLogin;

    @AfterViews
    void setUpViews() {
        setToolbarAsBack(getString(R.string.text_login));
        ThemeColorManager.addViewBackground(mLogin);
    }

    @Click(R.id.login)
    void login() {
        String userName = mUserName.getText().toString();
        String password = mPassword.getText().toString();
        if (!checkNotEmpty(userName, password)) {
            return;
        }
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .content(R.string.text_logining)
                .cancelable(false)
                .show();
        UserService.getInstance().login(userName, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.text_login_succeed, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        , error -> {
                            dialog.dismiss();
                            mPassword.setError(NodeBB.getErrorMessage(error, LoginActivity.this, R.string.text_login_fail));
                        });

    }

    @Click(R.id.forgot_password)
    void forgotPassword() {
        WebActivity_.intent(this)
                .extra(WebActivity.EXTRA_URL, NodeBB.BASE_URL + "reset")
                .extra(Intent.EXTRA_TITLE, getString(R.string.text_reset_password))
                .start();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_register) {
            RegisterActivity_.intent(this).start();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
