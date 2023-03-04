package com.virlabs.demo_flx_application.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;
import com.virlabs.demo_flx_application.Provider.PrefManager;
import com.virlabs.demo_flx_application.R;
import com.virlabs.demo_flx_application.api.apiClient;
import com.virlabs.demo_flx_application.api.apiRest;
import com.virlabs.demo_flx_application.entity.ApiResponse;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private SignInButton sign_in_button_google;

    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog register_progress;
    private TextView text_view_skip_login;
    private RelativeLayout relative_layout_google_login;
    private RelativeLayout relative_layout_phone_login;
    String VerificationCode = "";
    private EditText editText;
    private CountryCodePicker countryCodePicker;
    private RelativeLayout relative_layout_confirm_phone_number;
    private EditText edit_text_otp_1;
    private EditText edit_text_otp_2;
    private EditText edit_text_otp_3;
    private EditText edit_text_otp_4;
    private EditText edit_text_otp_5;
    private EditText edit_text_otp_6;
    private EditText otp_edit_text_login_activity;
    private RelativeLayout relative_layout_confirm_top_login_activity;
    private EditText edit_text_phone_number_login_acitivty;
    private LinearLayout linear_layout_buttons_login_activity;
    private LinearLayout linear_layout_otp_confirm_login_activity;
    private LinearLayout linear_layout_phone_input_login_activity;
    private RelativeLayout relative_layout_confirm_full_name;
    private LinearLayout linear_layout_name_input_login_activity;
    private RelativeLayout relative_layout_email_login_signin;

    private EditText edit_text_name_login_acitivty;
    private String phoneNum ="";
    private PrefManager prf;
    private TextInputEditText text_input_editor_text_activity_login_email;
    private TextInputEditText text_input_editor_text_activity_login_password;
    private TextInputLayout text_input_layout_activity_login_password;
    private TextInputLayout text_input_layout_activity_login_email;
    private RelativeLayout relative_layout_email_login_back;
    private String backto = null;
    private RelativeLayout relative_layout_email_login;
    private LinearLayout linear_layout_email_login_activity;
    private RelativeLayout relative_layout_email_login_to_signup;
    private LinearLayout linear_layout_register_login_activity;
    private TextInputLayout text_input_layout_activity_login_register_email;
    private TextInputLayout text_input_layout_activity_login_register_name;
    private TextInputLayout text_input_layout_activity_login_register_password_confirm;
    private TextInputLayout text_input_layout_activity_login_register_password;
    private TextInputEditText text_input_editor_text_activity_login_register_password_confirm;
    private TextInputEditText text_input_editor_text_activity_login_register_email;
    private TextInputEditText text_input_editor_text_activity_login_register_name;
    private TextInputEditText text_input_editor_text_activity_login_register_password;
    private RelativeLayout relative_layout_email_login_register;
    private TextInputLayout text_input_layout_activity_login_reset_email;
    private LinearLayout linear_layout_reset_login_activity;
    private TextInputEditText text_input_editor_text_activity_login_reset_email;
    private RelativeLayout relative_layout_email_login_reset_now;
    private TextView text_view_activity_login_reset_password;
    private LinearLayout linear_layout_token_login_activity;
    private TextInputLayout text_input_layout_activity_login_token_code;
    private TextInputEditText text_input_editor_text_activity_login_token_email;
    private RelativeLayout relative_layout_email_login_token_now;
    private TextView text_view_activity_login_reset_has_code;
    private String user_id ="";
    private String user_token ="";
    private String token ="";
    private RelativeLayout relative_layout_email_login_reset_password;
    private TextInputLayout text_input_layout_activity_login_reset_password_confirm;
    private TextInputLayout text_input_layout_activity_login_reset_password;
    private TextInputEditText text_input_editor_text_activity_login_reset_password_confirm;
    private TextInputEditText text_input_editor_text_activity_login_reset_password;
    private LinearLayout linear_layout_password_login_activity;
    private CheckBox check_box_login_activity_privacy;
    private TextView text_view_login_activity_privacy;
    private FirebaseAuth mAuth;
    private String verificationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        prf= new PrefManager(getApplicationContext());

        if (prf.getString("LOGGED").toString().equals("TRUE")){
            Intent intent= new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    LoginActivity.this.token = task.getResult();

                });
        initView();
        initAction();
        GoogleSignIn();
        mAuth = FirebaseAuth.getInstance();

    }


    public void initView(){
        this.edit_text_name_login_acitivty   =      (EditText)  findViewById(R.id.edit_text_name_login_acitivty);
        this.edit_text_phone_number_login_acitivty   =      (EditText)  findViewById(R.id.edit_text_phone_number_login_acitivty);
        this.otp_edit_text_login_activity   =      (EditText)  findViewById(R.id.otp_edit_text_login_activity);
        this.relative_layout_confirm_top_login_activity   =      (RelativeLayout)  findViewById(R.id.relative_layout_confirm_top_login_activity);
        this.relative_layout_google_login   =      (RelativeLayout)  findViewById(R.id.relative_layout_google_login);
        this.sign_in_button_google   =      (SignInButton)  findViewById(R.id.sign_in_button_google);
        this.relative_layout_phone_login =      (RelativeLayout)   findViewById(R.id.relative_layout_phone_login);
        this.relative_layout_confirm_phone_number =      (RelativeLayout)   findViewById(R.id.relative_layout_confirm_phone_number);
        this.linear_layout_buttons_login_activity =      (LinearLayout)   findViewById(R.id.linear_layout_buttons_login_activity);
        this.linear_layout_otp_confirm_login_activity =      (LinearLayout)   findViewById(R.id.linear_layout_otp_confirm_login_activity);
        this.linear_layout_phone_input_login_activity =      (LinearLayout)   findViewById(R.id.linear_layout_phone_input_login_activity);
        this.linear_layout_name_input_login_activity =      (LinearLayout)   findViewById(R.id.linear_layout_name_input_login_activity);
        this.relative_layout_confirm_full_name =      (RelativeLayout)   findViewById(R.id.relative_layout_confirm_full_name);
        this.relative_layout_email_login_signin =      (RelativeLayout)   findViewById(R.id.relative_layout_email_login_signin);

        this.countryCodePicker =      (CountryCodePicker)   findViewById(R.id.CountryCodePicker);

        this.text_input_editor_text_activity_login_email =      (TextInputEditText)   findViewById(R.id.text_input_editor_text_activity_login_email);
        this.text_input_editor_text_activity_login_password =      (TextInputEditText)   findViewById(R.id.text_input_editor_text_activity_login_password);
        this.text_input_editor_text_activity_login_register_email =      (TextInputEditText)   findViewById(R.id.text_input_editor_text_activity_login_register_email);
        this.text_input_editor_text_activity_login_register_name =      (TextInputEditText)   findViewById(R.id.text_input_editor_text_activity_login_register_name);
        this.text_input_editor_text_activity_login_register_password =      (TextInputEditText)   findViewById(R.id.text_input_editor_text_activity_login_register_password);
        this.text_input_editor_text_activity_login_register_password_confirm =      (TextInputEditText)   findViewById(R.id.text_input_editor_text_activity_login_register_password_confirm);
        this.text_input_editor_text_activity_login_reset_email =      (TextInputEditText)   findViewById(R.id.text_input_editor_text_activity_login_reset_email);
        this.text_input_editor_text_activity_login_token_email =      (TextInputEditText)   findViewById(R.id.text_input_editor_text_activity_login_token_email);
        this.text_input_editor_text_activity_login_reset_password_confirm =      (TextInputEditText)   findViewById(R.id.text_input_editor_text_activity_login_reset_password_confirm);
        this.text_input_editor_text_activity_login_reset_password =      (TextInputEditText)   findViewById(R.id.text_input_editor_text_activity_login_reset_password);

        this.text_input_layout_activity_login_email =      (TextInputLayout)   findViewById(R.id.text_input_layout_activity_login_email);
        this.text_input_layout_activity_login_password =      (TextInputLayout)   findViewById(R.id.text_input_layout_activity_login_password);
        this.text_input_layout_activity_login_register_password =      (TextInputLayout)   findViewById(R.id.text_input_layout_activity_login_register_password);
        this.text_input_layout_activity_login_register_password_confirm =      (TextInputLayout)   findViewById(R.id.text_input_layout_activity_login_register_password_confirm);
        this.text_input_layout_activity_login_register_name =      (TextInputLayout)   findViewById(R.id.text_input_layout_activity_login_register_name);
        this.text_input_layout_activity_login_register_email =      (TextInputLayout)   findViewById(R.id.text_input_layout_activity_login_register_email);
        this.text_input_layout_activity_login_reset_email = (TextInputLayout) findViewById(R.id.text_input_layout_activity_login_reset_email);
        this.text_input_layout_activity_login_token_code = (TextInputLayout) findViewById(R.id.text_input_layout_activity_login_token_code);
        this.text_input_layout_activity_login_reset_password = (TextInputLayout) findViewById(R.id.text_input_layout_activity_login_reset_password);
        this.text_input_layout_activity_login_reset_password_confirm = (TextInputLayout) findViewById(R.id.text_input_layout_activity_login_reset_password_confirm);



        this.relative_layout_email_login_back =  (RelativeLayout)  findViewById(R.id.relative_layout_email_login_back);

        this.relative_layout_email_login = (RelativeLayout) findViewById(R.id.relative_layout_email_login);
        this.linear_layout_email_login_activity = (LinearLayout) findViewById(R.id.linear_layout_email_login_activity);
        this.relative_layout_email_login_to_signup = (RelativeLayout) findViewById(R.id.relative_layout_email_login_to_signup);
        this.linear_layout_register_login_activity = (LinearLayout) findViewById(R.id.linear_layout_register_login_activity);
        this.relative_layout_email_login_register = (RelativeLayout) findViewById(R.id.relative_layout_email_login_register);
        this.relative_layout_email_login_register = (RelativeLayout) findViewById(R.id.relative_layout_email_login_register);
        this.linear_layout_reset_login_activity = (LinearLayout) findViewById(R.id.linear_layout_reset_login_activity);
        this.relative_layout_email_login_reset_now = (RelativeLayout) findViewById(R.id.relative_layout_email_login_reset_now);
        this.text_view_activity_login_reset_password = (TextView) findViewById(R.id.text_view_activity_login_reset_password);
        this.linear_layout_token_login_activity = (LinearLayout) findViewById(R.id.linear_layout_token_login_activity);
        this.relative_layout_email_login_token_now = (RelativeLayout) findViewById(R.id.relative_layout_email_login_token_now);
        this.text_view_activity_login_reset_has_code = (TextView) findViewById(R.id.text_view_activity_login_reset_has_code);
        this.relative_layout_email_login_reset_password = (RelativeLayout) findViewById(R.id.relative_layout_email_login_reset_password);

        this.text_view_login_activity_privacy = (TextView) findViewById(R.id.text_view_login_activity_privacy);
        this.check_box_login_activity_privacy = (CheckBox) findViewById(R.id.check_box_login_activity_privacy);

    }
    private boolean validatEmail() {
        if (text_input_editor_text_activity_login_email.getText().toString().trim().isEmpty() || text_input_editor_text_activity_login_email.getText().length()  < 5 ) {
            text_input_layout_activity_login_email.setError(getString(R.string.error_short_value));
            requestFocus(text_input_editor_text_activity_login_email);
            return false;
        } else {
            text_input_layout_activity_login_email.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatEmailSignUp() {
        if (text_input_editor_text_activity_login_register_email.getText().toString().trim().isEmpty() || text_input_editor_text_activity_login_register_email.getText().length()  < 5 ) {
            text_input_layout_activity_login_register_email.setError(getString(R.string.error_short_value));
            requestFocus(text_input_editor_text_activity_login_register_email);
            return false;
        } else {
            text_input_layout_activity_login_register_email.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatNameSignUp() {
        if (text_input_editor_text_activity_login_register_name.getText().toString().trim().isEmpty() || text_input_editor_text_activity_login_register_name.getText().length()  < 6 ) {
            text_input_layout_activity_login_register_name.setError(getString(R.string.error_short_value));
            requestFocus(text_input_editor_text_activity_login_register_name);
            return false;
        } else {
            text_input_layout_activity_login_email.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatSignInPassword() {
        if (text_input_editor_text_activity_login_register_password.getText().toString().isEmpty() || text_input_editor_text_activity_login_register_password.getText().length()  < 6 ) {
            text_input_layout_activity_login_register_password.setError(getString(R.string.error_short_value));
            requestFocus(text_input_editor_text_activity_login_register_password);
            return false;
        } else {
            text_input_layout_activity_login_register_password.setErrorEnabled(false);
        }
        return true;
    }
    private boolean confirmignInPassword() {
        if (!text_input_editor_text_activity_login_register_password_confirm.getText().toString().equals(text_input_editor_text_activity_login_register_password.getText().toString())) {
            text_input_layout_activity_login_register_password_confirm.setError(getString(R.string.password_confirm_message));
            requestFocus(text_input_editor_text_activity_login_register_password_confirm);
            return false;
        } else {
            text_input_layout_activity_login_register_password_confirm.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatPassword() {
        if (text_input_editor_text_activity_login_password.getText().toString().trim().isEmpty() || text_input_editor_text_activity_login_password.getText().length()  < 6 ) {
            text_input_layout_activity_login_password.setError(getString(R.string.error_short_value));
            requestFocus(text_input_editor_text_activity_login_password);
            return false;
        } else {
            text_input_layout_activity_login_email.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatAdressEmail() {
        if (!isEmailValid(text_input_editor_text_activity_login_email.getText().toString().trim())) {
            text_input_layout_activity_login_email.setError(getString(R.string.error_mail_valide));
            requestFocus(text_input_editor_text_activity_login_email);
            return false;
        } else {
            text_input_layout_activity_login_email.setErrorEnabled(false);
        }
        return true;
    }
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    public void initAction(){
        this.text_view_login_activity_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,PolicyActivity.class));
            }
        });
        this.check_box_login_activity_privacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    check_box_login_activity_privacy.setError(null);
                    relative_layout_email_login.setAlpha(1);
                    relative_layout_phone_login.setAlpha(1);
                    relative_layout_google_login.setAlpha(1);
                }else{
                    relative_layout_email_login.setAlpha((float) 0.7);
                    relative_layout_phone_login.setAlpha((float) 0.7);
                    relative_layout_google_login.setAlpha((float) 0.7);
                }
            }
        });
        this.relative_layout_email_login_reset_password.setOnClickListener(v -> {
            submitFormPasswrod();
        });
        this.relative_layout_email_login_token_now.setOnClickListener(v->{
            submitToken();
        });
        this.text_view_activity_login_reset_has_code.setOnClickListener(v -> {
            linear_layout_token_login_activity.setVisibility(View.VISIBLE);
            linear_layout_reset_login_activity.setVisibility(View.GONE);
            backto ="reset";
        });
        this.relative_layout_email_login_reset_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
        this.text_view_activity_login_reset_password.setOnClickListener(v -> {
            this.linear_layout_email_login_activity.setVisibility(View.GONE);
            this.linear_layout_reset_login_activity.setVisibility(View.VISIBLE);
            backto = "email";
        });
        this.relative_layout_email_login_register.setOnClickListener(v -> {
            if (!validatEmailSignUp())
                return;
            if(!validatNameSignUp())
                return;
            if(!validatSignInPassword())
                return;
            if(!confirmignInPassword())
                return;

            signUp(text_input_editor_text_activity_login_register_email.getText().toString(),text_input_editor_text_activity_login_register_password.getText().toString(),text_input_editor_text_activity_login_register_name.getText().toString(),"email","https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg");
        });
        relative_layout_email_login_signin.setOnClickListener(v -> {
            if (!validatEmail())
                return ;
            if (!validatAdressEmail())
                return ;
            if (!validatPassword())
                return ;

            signIn(text_input_editor_text_activity_login_email.getText().toString(),text_input_editor_text_activity_login_password.getText().toString());
        });
        this.relative_layout_email_login_back.setOnClickListener(v->{
            switch (backto){
                case "home":
                    linear_layout_buttons_login_activity.setVisibility(View.VISIBLE);
                    linear_layout_email_login_activity.setVisibility(View.GONE);
                    relative_layout_email_login_back.setVisibility(View.GONE);
                    linear_layout_phone_input_login_activity.setVisibility(View.GONE);
                    linear_layout_reset_login_activity.setVisibility(View.GONE);
                    break;
                case "email":
                    linear_layout_register_login_activity.setVisibility(View.GONE);
                    linear_layout_email_login_activity.setVisibility(View.VISIBLE);
                    linear_layout_reset_login_activity.setVisibility(View.GONE);

                    backto = "home";
                    break;
                case "phone":
                    linear_layout_otp_confirm_login_activity.setVisibility(View.GONE);
                    linear_layout_phone_input_login_activity.setVisibility(View.VISIBLE);
                    backto = "home";
                    break;
                case "reset":
                    linear_layout_token_login_activity.setVisibility(View.GONE);
                    linear_layout_reset_login_activity.setVisibility(View.VISIBLE);
                    backto ="home";
                    break;
                case "token":
                    linear_layout_token_login_activity.setVisibility(View.VISIBLE);
                    linear_layout_password_login_activity.setVisibility(View.GONE);
                    backto ="reset";
                    break;

                default:

            }
        });


        this.relative_layout_email_login_to_signup.setOnClickListener(v -> {
            backto = "email";
            linear_layout_register_login_activity.setVisibility(View.VISIBLE);
            linear_layout_email_login_activity.setVisibility(View.GONE);
        });
        this.relative_layout_email_login.setOnClickListener(v -> {
            if (!check_box_login_activity_privacy.isChecked()){
                check_box_login_activity_privacy.setError(getResources().getString(R.string.accept_privacy_policy_error));
                return;
            }

            backto = "home";
            linear_layout_buttons_login_activity.setVisibility(View.GONE);
            linear_layout_email_login_activity.setVisibility(View.VISIBLE);
            relative_layout_email_login_back.setVisibility(View.VISIBLE);
        });
        relative_layout_confirm_full_name.setOnClickListener(v->{

            String token_user =  prf.getString("TOKEN_USER");
            String id_user =  prf.getString("ID_USER");
            if (edit_text_name_login_acitivty.getText().toString().length()<3) {
                Toasty.error(getApplicationContext(), "This name very shot ", Toast.LENGTH_LONG).show();
                return;
            }
            updateToken(Integer.parseInt(id_user),token_user,token,edit_text_name_login_acitivty.getText().toString());


        });
        relative_layout_confirm_top_login_activity.setOnClickListener(v->{
            if (otp_edit_text_login_activity.getText().toString().trim().equals(VerificationCode.toString().trim())){
                String photo = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg" ;
                signUp(phoneNum,phoneNum,"null".toString(),"phone",photo);
            }else{
                Toasty.error(this, "The verification code you have been entered incorrect !", Toast.LENGTH_SHORT).show();
            }
        });

        this.relative_layout_phone_login.setOnClickListener(v -> {
            if (!check_box_login_activity_privacy.isChecked()){
                check_box_login_activity_privacy.setError(getResources().getString(R.string.accept_privacy_policy_error));
                return;
            }
            linear_layout_buttons_login_activity.setVisibility(View.GONE);
            linear_layout_phone_input_login_activity.setVisibility(View.VISIBLE);
            relative_layout_email_login_back.setVisibility(View.VISIBLE);
            backto = "home";
        });
        relative_layout_confirm_phone_number.setOnClickListener(v ->{
            phoneNum = "+"+countryCodePicker.getSelectedCountryCode().toString()+edit_text_phone_number_login_acitivty.getText().toString();

            new AlertDialog.Builder(this)
                    .setTitle("We will be verifying the phone number:"  )
                    .setMessage(" \n"+phoneNum+" \n\n Is this OK,or would you like to edit the number ?")
                    .setPositiveButton("Confrim",
                            (dialog, which) -> {
                                //Do Something Here
                                loginWithPhone();

                            })
                    .setNegativeButton("Edit",
                            (dialog, which) -> {
                                dialog.dismiss();
                            }).show();
        });
        relative_layout_google_login.setOnClickListener(view -> {
            if (!check_box_login_activity_privacy.isChecked()){
                check_box_login_activity_privacy.setError(getResources().getString(R.string.accept_privacy_policy_error));
                return;
            }
            signIn();
        });
        this.sign_in_button_google.setOnClickListener(view -> {
            if (!check_box_login_activity_privacy.isChecked()){
                check_box_login_activity_privacy.setError(getResources().getString(R.string.accept_privacy_policy_error));
                return;
            }
            signIn();
        });

    }

    private void loginWithPhone() {
        linear_layout_phone_input_login_activity.setVisibility(View.GONE);
        linear_layout_otp_confirm_login_activity.setVisibility(View.VISIBLE);
        backto = "phone";
        sendVerificationCode(phoneNum);
    }
    private void sendVerificationCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    // callback method is called on Phone auth provider.
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            final String code = phoneAuthCredential.getSmsCode();
            // checking if the code
            // is null or not.
            if (code != null) {
                // if the code is not null then
                // we are setting that code to
                // our OTP edittext field.
                otp_edit_text_login_activity.setText(code);
                VerificationCode =  code;

                // after setting this code
                // to OTP edittext field we
                // are calling our verifycode method.
                verifyCode(code);
            }
        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    // below method is use to verify code from Firebase.
    private void verifyCode(String code) {
        // below line is used for getting getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }
    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String photo = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg" ;
                            signUp(phoneNum,phoneNum,"null".toString(),"phone",photo);
                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            getResultGoogle(result);
        }
    }
    public void GoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

    }
    private void getResultGoogle(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();
            String photo = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg" ;
            if (acct.getPhotoUrl()!=null){
                photo =  acct.getPhotoUrl().toString();
            }

            signUp(acct.getId(),acct.getId(), acct.getDisplayName(),"google",photo);
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }
    public void signIn(String username,String password){
        register_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.login(username,password);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null){
                    if (response.body().getCode()==200){

                        String id_user="0";
                        String name_user="x";
                        String username_user="x";
                        String salt_user="0";
                        String token_user="0";
                        String type_user="x";
                        String image_user="x";
                        String enabled="x";
                        String user_subscribed="FALSE";
                        for (int i=0;i<response.body().getValues().size();i++){
                            if (response.body().getValues().get(i).getName().equals("salt")){
                                salt_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("token")){
                                token_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("id")){
                                id_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("name")){
                                name_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("type")){
                                type_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("username")){
                                username_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("url")){
                                image_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("enabled")){
                                enabled=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("subscribed")){
                                user_subscribed=response.body().getValues().get(i).getValue();
                            }
                        }if (enabled.equals("true")){
                            PrefManager prf= new PrefManager(getApplicationContext());
                            prf.setString("ID_USER",id_user);
                            prf.setString("SALT_USER",salt_user);
                            prf.setString("TOKEN_USER",token_user);
                            prf.setString("NAME_USER",name_user);
                            prf.setString("TYPE_USER",type_user);
                            prf.setString("USERN_USER",username_user);
                            prf.setString("IMAGE_USER",image_user);
                            prf.setString("NEW_SUBSCRIBE_ENABLED",user_subscribed);

                            prf.setString("LOGGED","TRUE");
                            if (name_user.equals("null")){
                                linear_layout_otp_confirm_login_activity.setVisibility(View.GONE);
                                linear_layout_name_input_login_activity.setVisibility(View.VISIBLE);
                            }else{
                                updateToken(Integer.parseInt(id_user),token_user,token,name_user);
                            }
                        }else{
                            Toasty.error(getApplicationContext(),getResources().getString(R.string.account_disabled), Toast.LENGTH_SHORT, true).show();
                        }
                    }
                    if (response.body().getCode()==500){
                        Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                    }
                }else{
                    Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                }
                register_progress.dismiss();
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                register_progress.dismiss();
            }
        });
    }
    public void signUp(String username,String password,String name,String type,String image){
        register_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.register(name,username,password,type,image);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null){
                    if (response.body().getCode()==200){

                        String id_user="0";
                        String name_user="x";
                        String username_user="x";
                        String salt_user="0";
                        String token_user="0";
                        String type_user="x";
                        String image_user="x";
                        String enabled="x";
                        String user_subscribed="FALSE";

                        for (int i=0;i<response.body().getValues().size();i++){
                            if (response.body().getValues().get(i).getName().equals("salt")){
                                salt_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("token")){
                                token_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("id")){
                                id_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("name")){
                                name_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("type")){
                                type_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("username")){
                                username_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("url")){
                                image_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("enabled")){
                                enabled=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("subscribed")){
                                user_subscribed=response.body().getValues().get(i).getValue();
                            }
                        }if (enabled.equals("true")){
                            PrefManager prf= new PrefManager(getApplicationContext());
                            prf.setString("ID_USER",id_user);
                            prf.setString("SALT_USER",salt_user);
                            prf.setString("TOKEN_USER",token_user);
                            prf.setString("NAME_USER",name_user);
                            prf.setString("TYPE_USER",type_user);
                            prf.setString("USERN_USER",username_user);
                            prf.setString("IMAGE_USER",image_user);
                            prf.setString("LOGGED","TRUE");
                            prf.setString("NEW_SUBSCRIBE_ENABLED",user_subscribed);

                            if (name_user.equals("null")){
                                linear_layout_otp_confirm_login_activity.setVisibility(View.GONE);
                                linear_layout_name_input_login_activity.setVisibility(View.VISIBLE);
                            }else{
                                updateToken(Integer.parseInt(id_user),token_user,token,name_user);

                            }
                        }else{
                            Toasty.error(getApplicationContext(),getResources().getString(R.string.account_disabled), Toast.LENGTH_SHORT, true).show();
                        }
                    }
                    if (response.body().getCode()==500){
                        Toasty.error(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
                        text_input_layout_activity_login_register_email.setError(response.body().getMessage());
                        requestFocus(text_input_editor_text_activity_login_register_email);
                    }
                }else{
                    Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                }
                register_progress.dismiss();
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                register_progress.dismiss();
            }
        });
    }
    public  void set(Activity activity, String s){
        Toasty.error(activity,s,Toast.LENGTH_LONG).show();
        if (!prf.getString("APP_LOGIN_REQUIRED").toString().equals("TRUE")) {
            activity.finish();
        }
    }
    public void updateToken(Integer id,String key,String token,String name){
        register_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.editToken(id,key,token,name);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()){
                    prf.setString("NAME_USER",name );

                    Toasty.success(getApplicationContext(),response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
                    register_progress.dismiss();
                    if (prf.getString("APP_LOGIN_REQUIRED").toString().equals("TRUE")) {
                        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                register_progress.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed(){

        super.onBackPressed();
        overridePendingTransition(R.anim.slide_down_reverse, R.anim.slide_up_reverse);
        return;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                super.onBackPressed();
                overridePendingTransition(R.anim.slide_down_reverse, R.anim.slide_up_reverse);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private boolean validatAdressEmailForReset() {
        if (!isEmailValid(text_input_editor_text_activity_login_reset_email.getText().toString().trim())) {
            text_input_layout_activity_login_reset_email.setError(getString(R.string.error_mail_valide));
            requestFocus(text_input_editor_text_activity_login_reset_email);
            return false;
        } else {
            text_input_layout_activity_login_reset_email.setErrorEnabled(false);
        }
        return true;
    }
    private void submitForm() {
        if (!validatAdressEmailForReset()) {
            return;
        }

        register_progress= ProgressDialog.show(this,null,getString(R.string.operation_progress));



        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.sendEmail(text_input_editor_text_activity_login_reset_email.getText().toString());
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null){
                    int code = response.body().getCode();
                    String message=response.body().getMessage();
                    if (code==200) {
                        Toasty.success(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        linear_layout_token_login_activity.setVisibility(View.VISIBLE);
                        linear_layout_reset_login_activity.setVisibility(View.GONE);
                        backto ="reset";
                    }else{
                        Toasty.error(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.error_server),Toast.LENGTH_SHORT).show();
                }
                register_progress.dismiss();

            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                register_progress.dismiss();

            }
        });
    }

    private void submitToken() {
        if (!validateKey()) {
            return;
        }

        register_progress= ProgressDialog.show(this,null,getString(R.string.operation_progress));


        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.request(this.text_input_editor_text_activity_login_token_email.getText().toString());
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null){
                    int code = response.body().getCode();
                    String message=response.body().getMessage();
                    if (code==200) {
                        String id_user="0";
                        String token_user="0";
                        for (int i=0;i<response.body().getValues().size();i++){

                            if (response.body().getValues().get(i).getName().equals("token")){
                                token_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("id")){
                                id_user=response.body().getValues().get(i).getValue();
                            }

                        }
                        user_id = id_user;
                        user_token = token_user;
                        if (user_id != "" && user_token !=""){
                            linear_layout_password_login_activity.setVisibility(View.VISIBLE);
                            linear_layout_token_login_activity.setVisibility(View.GONE);
                            backto = "token";
                        }

                    }else{
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.error_server),Toast.LENGTH_SHORT).show();
                }
                register_progress.dismiss();

            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                register_progress.dismiss();

            }
        });
    }
    private boolean validatePassword(EditText et,TextInputLayout tIL) {
        if (et.getText().toString().trim().isEmpty() || et.getText().length()  < 6 ) {
            tIL.setError(getString(R.string.error_short_value));
            requestFocus(et);
            return false;
        } else {
            tIL.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatePasswordConfrom() {
        if (!text_input_editor_text_activity_login_reset_password.getText().toString().equals(text_input_editor_text_activity_login_reset_password_confirm.getText().toString())) {
            text_input_layout_activity_login_reset_password_confirm.setError(getString(R.string.password_confirm_message));
            requestFocus(text_input_editor_text_activity_login_reset_password);
            return false;
        } else {
            text_input_layout_activity_login_reset_password_confirm.setErrorEnabled(false);
        }
        return true;
    }
    private void submitFormPasswrod() {

        if (!validatePassword(text_input_editor_text_activity_login_reset_password,text_input_layout_activity_login_reset_password)) {
            return;
        }
        if (!validatePasswordConfrom()) {
            return;
        }
        register_progress= ProgressDialog.show(this,null,getString(R.string.operation_progress));



        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.reset(user_id,user_token,text_view_activity_login_reset_password.getText().toString());
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null){
                    int code = response.body().getCode();
                    String message=response.body().getMessage();
                    if (code==200){
                        String id_user="0";
                        String name_user="x";
                        String username_user="x";
                        String salt_user="0";
                        String token_user="0";
                        String type_user="x";
                        String image_user="x";
                        String enabled="x";
                        for (int i=0;i<response.body().getValues().size();i++){
                            if (response.body().getValues().get(i).getName().equals("salt")){
                                salt_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("token")){
                                token_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("id")){
                                id_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("name")){
                                name_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("type")){
                                type_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("username")){
                                username_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("url")){
                                image_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("enabled")){
                                enabled=response.body().getValues().get(i).getValue();
                            }
                        }if (enabled.equals("true")){
                            PrefManager prf= new PrefManager(getApplicationContext());
                            prf.setString("ID_USER",id_user);
                            prf.setString("SALT_USER",salt_user);
                            prf.setString("TOKEN_USER",token_user);
                            prf.setString("NAME_USER",name_user);
                            prf.setString("TYPE_USER",type_user);
                            prf.setString("USERN_USER",username_user);
                            prf.setString("IMAGE_USER",image_user);
                            prf.setString("LOGGED","TRUE");
                            if (name_user.equals("null")){
                                linear_layout_otp_confirm_login_activity.setVisibility(View.GONE);
                                linear_layout_name_input_login_activity.setVisibility(View.VISIBLE);
                            }else{
                                updateToken(Integer.parseInt(id_user),token_user,token,name_user);

                            }
                        }else{
                            Toasty.error(getApplicationContext(),getResources().getString(R.string.account_disabled), Toast.LENGTH_SHORT, true).show();
                        }
                        register_progress.dismiss();
                    } else if (code == 500) {
                        register_progress.dismiss();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.error_server),Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                register_progress.dismiss();
            }
        });
    }
    private boolean validateKey() {
        if (text_input_editor_text_activity_login_token_email.getText().toString().trim().isEmpty() || text_input_editor_text_activity_login_token_email.getText().length()  < 6 ) {
            text_input_layout_activity_login_token_code.setError(getString(R.string.error_short_value));
            requestFocus(text_input_editor_text_activity_login_token_email);
            return false;
        } else {
            text_input_layout_activity_login_token_code.setErrorEnabled(false);
        }
        return true;
    }
}

