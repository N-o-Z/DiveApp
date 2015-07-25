package com.example.nozery.diveapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends Activity {

    private final String TAG = getClass().getSimpleName();

    private EditText mUsernameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordValView;
    private Button mSubmitView;
    private TextView mNewUserLoginView;
    private RelativeLayout mProcessing;

    private static boolean mNewUser = false;
    private static char[] allowed_chars = {'.','-','_'};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_form);

        mUsernameView = (EditText) findViewById(R.id.login_username);
        mEmailView = (EditText) findViewById(R.id.login_email);
        mPasswordView = (EditText) findViewById(R.id.login_password);
        mPasswordValView = (EditText) findViewById(R.id.login_validate_password);
        mSubmitView = (Button) findViewById(R.id.login_submit);
        mNewUserLoginView = (TextView) findViewById(R.id.sign_login);
        mProcessing = (RelativeLayout) findViewById(R.id.log_in_panel);

        mUsernameView.addTextChangedListener(new TextWatcher() {

            boolean revert = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(1 > s.length()) {
                    return;
                }
                if(2 > s.length()){
                    for(char sChar : allowed_chars) {
                        if(sChar == s.charAt(0)) {
                            showMessage("Username must start with a letter");
                            revert = true;
                            break;
                        }
                    }
                }
                else {
                    for (char firstChar : allowed_chars) {
                        if (firstChar == s.charAt(s.length() - 2)) {
                            for(char secChar : allowed_chars)
                               if(secChar == s.charAt(s.length() - 1)) {
                            showMessage("\'"+secChar+"\'"+"must precede a letter");
                            revert = true;
                            break;
                        }
                    }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(revert) {
                    revert = false;
                    if(2 > s.length()) {
                        s.clear();
                    }
                    else {
                        s.delete(s.length()-2,s.length()-1);
                    }
                }
            }
        });

        mNewUserLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setForm();
            }
        });


        findViewById(R.id.login_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    if (mNewUser)
                        createAccount();
                    else {
                        tryLogin();
                    }
                }
            }
        });
    }

    private void createAccount() {

        //TODO: Validation!
        showProcessing(true);
        new AsyncTask<String, Void, Intent>() {

            String username = ((EditText) findViewById(R.id.login_username))
                    .getText().toString().trim();
            String accountPassword = ((EditText) findViewById(R.id.login_password))
                    .getText().toString().trim();
            String email = ((EditText) findViewById(R.id.login_email))
                    .getText().toString().trim();
            protected Intent doInBackground(String... params) {

                Log.d(getResources().getString(R.string.app_name), TAG + "> Attempt Sign Up");

                String authToken;
                Bundle data = new Bundle();
                try {
                    authToken = MainActivity.mAppDbHelper.userSignUp(
                            username, email, accountPassword
                            , ParseDbHelper.AUTHTOKEN_TYPE_FULL_ACCESS);


                } catch (Exception e) {
                    data.putString("Error", e.getLocalizedMessage());
                }
                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                setResult(RESULT_CANCELED, intent);
                if (intent.hasExtra("Error")) {
                    showMessage(intent.getStringExtra("Error"));
                    showProcessing(false);
                }
                else {

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }.execute();
    }

    private void tryLogin() {

        //TODO: Validation!
        showProcessing(true);
        new AsyncTask<String, Void, Intent>() {

            String username = ((EditText) findViewById(R.id.login_username))
                    .getText().toString().trim();
            String accountPassword = ((EditText) findViewById(R.id.login_password))
                    .getText().toString().trim();
            protected Intent doInBackground(String... params) {

                Log.d(getResources().getString(R.string.app_name), TAG + "> Attempt Log In");

                String authToken;
                Bundle data = new Bundle();
                try {
                    authToken = MainActivity.mAppDbHelper.userSignIn(username, accountPassword
                            , ParseDbHelper.AUTHTOKEN_TYPE_FULL_ACCESS);


                } catch (Exception e) {
                    data.putString("Error", e.getLocalizedMessage());
                }
                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                setResult(RESULT_CANCELED, intent);
                mPasswordView.setText("");
                if (intent.hasExtra("Error")) {
                    showMessage(intent.getStringExtra("Error"));
                    showProcessing(false);
                }
                else {

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void setForm() {

        if (mNewUser) {
            mEmailView.setVisibility(View.GONE);
            mEmailView.setText("");
            mPasswordValView.setVisibility(View.GONE);
            mPasswordValView.setText("");
            mSubmitView.setText(getResources().getString(R.string.login_login));
            mNewUserLoginView.setText(getResources().getString(R.string.login_new_user));
        } else {
            mEmailView.setVisibility(View.VISIBLE);
            mPasswordValView.setVisibility(View.VISIBLE);
            mSubmitView.setText(getResources().getString(R.string.login_create_account));
            mNewUserLoginView.setText(getResources().getString(R.string.login_member));
        }
        mNewUser = !mNewUser;
    }

    private boolean validateInput() {

        int nameLength = mUsernameView.getText().toString().length();
        String password = mPasswordView.getText().toString();

        if((UserProfile.MIN_USERNAME_LENGTH > nameLength)
                || UserProfile.MAX_USERNAME_LENGTH < nameLength ) {
            mUsernameView.setError("Username length must be between "
                    + UserProfile.MIN_USERNAME_LENGTH + " and " + UserProfile.MAX_USERNAME_LENGTH);
            return false;
        }
        if((UserProfile.MIN_PASSWORD_LENGTH > password.length())
                || UserProfile.MAX_PASSWORD_LENGTH < password.length()) {
            mPasswordView.setError("Password length must be between "
                    + UserProfile.MIN_PASSWORD_LENGTH +" and "
                    + UserProfile.MAX_PASSWORD_LENGTH);
            return false;
        }
        if(mNewUser) {
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(
                    mEmailView.getText().toString()).matches()) {
                mEmailView.setError("Invalid email address");
                return false;
            }
            if(!password.equals(mPasswordValView.getText().toString())) {
                mPasswordValView.setError("Password mismatch. Please re-type password");
                mPasswordValView.setText("");
                return false;
            }
        }
        return true;
    }

    private void showProcessing(boolean show) {
        if(show) {
            mProcessing.setVisibility(View.VISIBLE);
        }
        else {
            mProcessing.setVisibility(View.GONE);
        }
    }

    private void showMessage(final String msg) {
        if (TextUtils.isEmpty(msg))
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}