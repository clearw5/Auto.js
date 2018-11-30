package org.autojs.autojs.ui.error;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import androidx.annotation.StringRes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.heinrichreimersoftware.androidissuereporter.R;
import com.heinrichreimersoftware.androidissuereporter.model.DeviceInfo;
import com.heinrichreimersoftware.androidissuereporter.model.Report;
import com.heinrichreimersoftware.androidissuereporter.model.github.ExtraInfo;
import com.heinrichreimersoftware.androidissuereporter.model.github.GithubLogin;
import com.heinrichreimersoftware.androidissuereporter.model.github.GithubTarget;
import com.heinrichreimersoftware.androidissuereporter.util.ColorUtils;
import com.heinrichreimersoftware.androidissuereporter.util.ThemeUtils;
import com.stardust.theme.ThemeColorManager;

import org.autojs.autojs.BuildConfig;
import org.autojs.autojs.ui.BaseActivity;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.IssueService;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

import static android.util.Patterns.EMAIL_ADDRESS;

/**
 * Created by Stardust on 2017/4/3.
 */

public abstract class AbstractIssueReporterActivity extends BaseActivity {

    private static final String TAG = AbstractIssueReporterActivity.class.getSimpleName();

    private static final int STATUS_BAD_CREDENTIALS = 401;
    private static final int STATUS_ISSUES_NOT_ENABLED = 410;
    private boolean mCrash = false;
    private boolean mReportFailed = true;

    @StringDef({RESULT_OK, RESULT_BAD_CREDENTIALS, RESULT_INVALID_TOKEN, RESULT_ISSUES_NOT_ENABLED,
            RESULT_UNKNOWN})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Result {
    }

    private static final String RESULT_OK = "RESULT_OK";
    private static final String RESULT_BAD_CREDENTIALS = "RESULT_BAD_CREDENTIALS";
    private static final String RESULT_INVALID_TOKEN = "RESULT_INVALID_TOKEN";
    private static final String RESULT_ISSUES_NOT_ENABLED = "RESULT_ISSUES_NOT_ENABLED";
    private static final String RESULT_UNKNOWN = "RESULT_UNKNOWN";
    private boolean emailRequired = false;
    private int bodyMinChar = 0;
    private Toolbar toolbar;
    private EditText inputTitle;
    private EditText inputDescription;
    private TextView textDeviceInfo;
    private ImageButton buttonDeviceInfo;
    private ExpandableRelativeLayout layoutDeviceInfo;
    private ExpandableRelativeLayout layoutAnonymous;
    private EditText inputUsername;
    private EditText inputPassword;
    private EditText inputEmail;
    private RadioButton optionUseAccount;
    private RadioButton optionAnonymous;
    private ExpandableRelativeLayout layoutLogin;
    private FloatingActionButton buttonSend;

    private Drawable optionUseAccountButtonDrawable = null;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (TextUtils.isEmpty(getTitle()))
            setTitle(R.string.air_title_report_issue);

        setContentView(org.autojs.autojs.R.layout.air_activity_issue_reporter);
        findViews();

        //noinspection deprecation
        token = getGuestToken();

        initViews();


        DeviceInfo deviceInfo = new DeviceInfo(this);
        textDeviceInfo.setText(deviceInfo.toString());

        handleIntent();

        optionAnonymous.post(() -> optionAnonymous.performClick());
    }

    private void handleIntent() {
        final String errorDetail = getIntent().getStringExtra("error");
        if (errorDetail != null) {
            inputDescription.setText(errorDetail);
            String title = getFirstLine(errorDetail);
            inputTitle.setText("[" + BuildConfig.VERSION_CODE + "]" + title);
            mCrash = true;
        }
    }

    private String getFirstLine(String str) {
        int i = str.indexOf('\n');
        if (i < 0)
            return str;
        return str.substring(0, i);
    }

    private void findViews() {
        toolbar = findViewById(R.id.air_toolbar);

        inputTitle = findViewById(R.id.air_inputTitle);
        inputDescription = findViewById(R.id.air_inputDescription);
        textDeviceInfo = findViewById(R.id.air_textDeviceInfo);
        buttonDeviceInfo = findViewById(R.id.air_buttonDeviceInfo);
        layoutDeviceInfo = findViewById(R.id.air_layoutDeviceInfo);

        inputUsername = findViewById(R.id.air_inputUsername);
        inputPassword = findViewById(R.id.air_inputPassword);
        inputEmail = findViewById(R.id.air_inputEmail);
        optionUseAccount = findViewById(R.id.air_optionUseAccount);
        optionAnonymous = findViewById(R.id.air_optionAnonymous);
        layoutLogin = findViewById(R.id.air_layoutLogin);
        layoutAnonymous = findViewById(R.id.air_layoutGuest);

        buttonSend = findViewById(R.id.air_buttonSend);
    }

    private void initViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ThemeColorManager.getColorPrimary());
        }
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(org.autojs.autojs.R.string.text_issue_report);
        }
        toolbar.setBackgroundColor(ThemeColorManager.getColorPrimary());


        toolbar.setNavigationOnClickListener(v -> finish());

        buttonDeviceInfo.setOnClickListener(v -> layoutDeviceInfo.toggle());


        inputPassword.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                reportIssue();
                return true;
            }
            return false;
        });

        updateGuestTokenViews();

        buttonSend.setImageResource(ColorUtils.isDark(ThemeUtils.getColorAccent(this)) ?
                R.drawable.air_ic_send_dark : R.drawable.air_ic_send_light);
        buttonSend.setOnClickListener(v -> {
            try {
                reportIssue();
            } catch (Exception e) {
                e.printStackTrace();
                mReportFailed = true;
                finish();
            }
        });

    }

    private void setOptionUseAccountMarginStart(int marginStart) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) optionUseAccount.getLayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(marginStart);
        } else {
            layoutParams.leftMargin = marginStart;
        }
        optionUseAccount.setLayoutParams(layoutParams);
    }

    private void updateGuestTokenViews() {
        if (TextUtils.isEmpty(token)) {
            int baseline = getResources().getDimensionPixelSize(R.dimen.air_baseline);
            int radioButtonPaddingStart = getResources().getDimensionPixelSize(R.dimen.air_radio_button_padding_start);
            setOptionUseAccountMarginStart(-2 * baseline - radioButtonPaddingStart);
            optionUseAccount.setEnabled(false);
            optionAnonymous.setVisibility(View.GONE);
        } else {
            setOptionUseAccountMarginStart(0);
            optionUseAccount.setEnabled(true);
            optionUseAccount.setOnClickListener(v -> {
                layoutLogin.expand();
                layoutAnonymous.collapse();
                inputUsername.setEnabled(true);
                inputPassword.setEnabled(true);
            });
            optionAnonymous.setVisibility(View.VISIBLE);
            optionAnonymous.setOnClickListener(v -> {
                layoutLogin.collapse();
                layoutAnonymous.expand();
                inputUsername.setEnabled(false);
                inputPassword.setEnabled(false);
            });
        }
    }

    private void reportIssue() {

        if (!validateInput()) return;

        if (optionUseAccount.isChecked()) {
            String username = inputUsername.getText().toString();
            String password = inputPassword.getText().toString();
            sendBugReport(new GithubLogin(username, password), null);
        } else {
            if (TextUtils.isEmpty(token))
                throw new IllegalStateException("You must provide a GitHub API Token.");

            String email = null;
            if (!TextUtils.isEmpty(inputEmail.getText()) &&
                    EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                email = inputEmail.getText().toString();
            }

            sendBugReport(new GithubLogin(token), email);
        }
    }

    private boolean validateInput() {
        boolean hasErrors = false;

        if (optionUseAccount.isChecked()) {
            if (TextUtils.isEmpty(inputUsername.getText())) {
                setError(inputUsername, R.string.air_error_no_username);
                hasErrors = true;
            } else {
                removeError(inputUsername);
            }

            if (TextUtils.isEmpty(inputPassword.getText())) {
                setError(inputPassword, R.string.air_error_no_password);
                hasErrors = true;
            } else {
                removeError(inputPassword);
            }
        } else {
            if (emailRequired) {
                if (TextUtils.isEmpty(inputEmail.getText()) ||
                        !EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                    setError(inputEmail, R.string.air_error_no_email);
                    hasErrors = true;
                } else {
                    removeError(inputEmail);
                }
            }
        }

        if (TextUtils.isEmpty(inputTitle.getText())) {
            setError(inputTitle, R.string.air_error_no_title);
            hasErrors = true;
        } else {
            removeError(inputTitle);
        }

        if (TextUtils.isEmpty(inputDescription.getText())) {
            setError(inputDescription, R.string.air_error_no_description);
            hasErrors = true;
        } else {
            if (bodyMinChar > 0) {
                if (inputDescription.getText().toString().length() < bodyMinChar) {
                    setError(inputDescription, getResources().getQuantityString(R.plurals.air_error_short_description, bodyMinChar, bodyMinChar));
                    hasErrors = true;
                } else {
                    removeError(inputDescription);
                }
            } else
                removeError(inputDescription);
        }
        return !hasErrors;
    }

    private void setError(EditText editText, @StringRes int errorRes) {
        try {
            View layout = (View) editText.getParent();
            while (!layout.getClass().getSimpleName().equals(TextInputLayout.class.getSimpleName()))
                layout = (View) layout.getParent();
            TextInputLayout realLayout = (TextInputLayout) layout;
            realLayout.setError(getString(errorRes));
        } catch (ClassCastException | NullPointerException e) {
            Log.e(TAG, "Issue while setting error UI.", e);
        }
    }

    private void setError(EditText editText, String error) {
        try {
            View layout = (View) editText.getParent();
            while (!layout.getClass().getSimpleName().equals(TextInputLayout.class.getSimpleName()))
                layout = (View) layout.getParent();
            TextInputLayout realLayout = (TextInputLayout) layout;
            realLayout.setError(error);
        } catch (ClassCastException | NullPointerException e) {
            Log.e(TAG, "Issue while setting error UI.", e);
        }
    }

    private void removeError(EditText editText) {
        try {
            View layout = (View) editText.getParent();
            while (!layout.getClass().getSimpleName().equals(TextInputLayout.class.getSimpleName()))
                layout = (View) layout.getParent();
            TextInputLayout realLayout = (TextInputLayout) layout;
            realLayout.setError(null);
        } catch (ClassCastException | NullPointerException e) {
            Log.e(TAG, "Issue while removing error UI.", e);
        }
    }

    private void sendBugReport(GithubLogin login, String email) {
        if (!validateInput()) return;

        String bugTitle = inputTitle.getText().toString();
        String bugDescription = inputDescription.getText().toString();

        DeviceInfo deviceInfo = new DeviceInfo(this);

        ExtraInfo extraInfo = new ExtraInfo();
        onSaveExtraInfo(extraInfo);

        Report report = new Report(bugTitle, bugDescription, deviceInfo, extraInfo, email);
        GithubTarget target = getTarget();

        report(this, report, target, login);
    }

    protected final void setGuestEmailRequired(boolean required) {
        this.emailRequired = required;
        if (required) {
            optionAnonymous.setText(R.string.air_label_use_email);
            ((TextInputLayout) findViewById(R.id.air_inputEmailParent)).setHint(getString(R.string.air_label_email));
        } else {
            optionAnonymous.setText(R.string.air_label_use_guest);
            ((TextInputLayout) findViewById(R.id.air_inputEmailParent)).setHint(getString(R.string.air_label_email_optional));
        }
    }

    protected final void setMinimumDescriptionLength(int length) {
        this.bodyMinChar = length;
    }

    protected void onSaveExtraInfo(ExtraInfo extraInfo) {
    }

    protected abstract GithubTarget getTarget();

    @Deprecated
    protected String getGuestToken() {
        return null;
    }

    protected final void setGuestToken(String token) {
        this.token = token;
        Log.d(TAG, "GuestToken: " + token);
        updateGuestTokenViews();
    }

    @Override
    public void finish() {
        if (mCrash) {
            if (!mReportFailed) {
                Toast.makeText(this, org.autojs.autojs.R.string.text_report_succeed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, org.autojs.autojs.R.string.text_report_fail, Toast.LENGTH_SHORT).show();
            }
            finishAffinity();
        } else {
            super.finish();
        }
    }

    private void report(Activity activity, Report report, GithubTarget target,
                        GithubLogin login) {
        new ReportIssueTask(activity, report, target, login).execute();
    }

    private class ReportIssueTask extends DialogAsyncTask<Void, Void, String> {
        private final Report report;
        private final GithubTarget target;
        private final GithubLogin login;

        private ReportIssueTask(Activity activity, Report report, GithubTarget target,
                                GithubLogin login) {
            super(activity);
            this.report = report;
            this.target = target;
            this.login = login;
        }


        @Override
        protected Dialog createDialog(@NonNull Context context) {
            return new MaterialDialog.Builder(context)
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .title(R.string.air_dialog_title_loading)
                    .show();
        }

        @Override
        @Result
        protected String doInBackground(Void... params) {
            GitHubClient client;
            if (login.shouldUseApiToken()) {
                client = new GitHubClient().setOAuth2Token(login.getApiToken());
            } else {
                client = new GitHubClient().setCredentials(login.getUsername(), login.getPassword());
            }

            Issue issue = new Issue().setTitle(report.getTitle()).setBody(report.getDescription());
            try {
                new IssueService(client).createIssue(target.getUsername(), target.getRepository(), issue);
                return RESULT_OK;
            } catch (RequestException e) {
                switch (e.getStatus()) {
                    case STATUS_BAD_CREDENTIALS:
                        if (login.shouldUseApiToken())
                            return RESULT_INVALID_TOKEN;
                        return RESULT_BAD_CREDENTIALS;
                    case STATUS_ISSUES_NOT_ENABLED:
                        return RESULT_ISSUES_NOT_ENABLED;
                    default:
                        e.printStackTrace();
                        return RESULT_UNKNOWN;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return RESULT_UNKNOWN;
            }
        }

        @Override
        protected void onPostExecute(@Result String result) {
            super.onPostExecute(result);

            Context context = getContext();
            if (context == null) return;

            switch (result) {
                case RESULT_OK:
                    mReportFailed = false;
                    tryToFinishActivity();
                    break;
                case RESULT_BAD_CREDENTIALS:
                    new MaterialDialog.Builder(context)
                            .title(R.string.air_dialog_title_failed)
                            .content(R.string.air_dialog_description_failed_wrong_credentials)
                            .positiveText(R.string.air_dialog_action_failed)
                            .show();
                    break;
                case RESULT_INVALID_TOKEN:
                    new MaterialDialog.Builder(context)
                            .title(R.string.air_dialog_title_failed)
                            .content(R.string.air_dialog_description_failed_invalid_token)
                            .positiveText(R.string.air_dialog_action_failed)
                            .show();
                    break;
                case RESULT_ISSUES_NOT_ENABLED:
                    new MaterialDialog.Builder(context)
                            .title(R.string.air_dialog_title_failed)
                            .content(R.string.air_dialog_description_failed_issues_not_available)
                            .positiveText(R.string.air_dialog_action_failed)
                            .show();
                    break;
                default:
                    new MaterialDialog.Builder(context)
                            .title(R.string.air_dialog_title_failed)
                            .content(R.string.air_dialog_description_failed_unknown)
                            .positiveText(R.string.air_dialog_action_failed)
                            .onPositive((dialog, which) -> tryToFinishActivity())
                            .cancelListener(dialog -> tryToFinishActivity())
                            .show();
                    break;
            }
        }

        private void tryToFinishActivity() {
            Context context = getContext();
            if (context instanceof Activity && !((Activity) context).isFinishing()) {
                ((Activity) context).finish();
            }
        }
    }

    private static abstract class DialogAsyncTask<Pa, Pr, Re> extends AsyncTask<Pa, Pr, Re> {
        private WeakReference<Context> contextWeakReference;
        private WeakReference<Dialog> dialogWeakReference;

        private boolean supposedToBeDismissed;

        private DialogAsyncTask(Context context) {
            contextWeakReference = new WeakReference<>(context);
            dialogWeakReference = new WeakReference<>(null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Context context = getContext();
            if (!supposedToBeDismissed && context != null) {
                Dialog dialog = createDialog(context);
                dialogWeakReference = new WeakReference<>(dialog);
                dialog.show();
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        protected final void onProgressUpdate(Pr... values) {
            super.onProgressUpdate(values);
            Dialog dialog = getDialog();
            if (dialog != null) {
                onProgressUpdate(dialog, values);
            }
        }

        @SuppressWarnings("unchecked")
        private void onProgressUpdate(@NonNull Dialog dialog, Pr... values) {
        }

        @Nullable
        Context getContext() {
            return contextWeakReference.get();
        }

        @Nullable
        Dialog getDialog() {
            return dialogWeakReference.get();
        }

        @Override
        protected void onCancelled(Re result) {
            super.onCancelled(result);
            tryToDismiss();
        }

        @Override
        protected void onPostExecute(Re result) {
            super.onPostExecute(result);
            tryToDismiss();
        }

        private void tryToDismiss() {
            supposedToBeDismissed = true;
            try {
                Dialog dialog = getDialog();
                if (dialog != null)
                    dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected abstract Dialog createDialog(@NonNull Context context);
    }
}
