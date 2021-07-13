package madt.capstone_codingcomrades_yum.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.createprofile.AboutMeActivity;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityLoginWithPhoneNumberBinding;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;

public class LoginWithPhoneNumberActivity extends BaseActivity {

    private ActivityLoginWithPhoneNumberBinding binding;
    private FirebaseAuth auth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_with_phone_number);

        auth = FirebaseAuth.getInstance();

        binding.btnGetCode.setOnClickListener(v -> {
            if (binding.txtPhnEntry.getText().toString().isEmpty()) {
                ySnackbar(this, getString(R.string.err_enter_phone_number));
            } else {
                startPhoneNumberVerification("+91"+binding.txtPhnEntry.getText().toString());
            }
        });

        binding.btnVerifyCode.setOnClickListener(v -> {
            yLog("verification code", mVerificationId + "//");
            yLog("otp code", binding.txtCodeEntry.getText().toString() + "//");

            if (binding.txtCodeEntry.getText().toString().isEmpty()) {
                ySnackbar(this, getString(R.string.err_enter_code));
            } else {
                verifyPhoneNumberWithCode(mVerificationId, binding.txtCodeEntry.getText().toString());
            }
        });


        auth.useAppLanguage();
        setAuthCallback();


    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        if (!verificationId.isEmpty()) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneAuthCredential(credential);
        }
        // [END verify_with_code]
    }

    private void setAuthCallback() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                yLog("onVerificationCompleted:", credential.toString());
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                yLog("onVerificationFailed:", e.getLocalizedMessage());
                ySnackbar(LoginWithPhoneNumberActivity.this, e.getLocalizedMessage());
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }


                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                yLog("onCodeSent:", verificationId);
                yToast(LoginWithPhoneNumberActivity.this, getString(R.string.verification_code_sent));

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String VerificationId) {
                super.onCodeAutoRetrievalTimeOut(VerificationId);
                mVerificationId = VerificationId;
            }
        };
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("signInWithCredential", ":success");

                            FirebaseUser user = task.getResult().getUser();
                            yLog("user", user.toString() + "//");

                           AppSharedPreferences.getInstance().setString(SharedConstants.USER_UID, user.getUid());
                         //  AppSharedPreferences.getInstance().setString(SharedConstants.USER_TOKEN, user.getIdToken(true).getResult().getToken());

                            Intent i = new Intent(LoginWithPhoneNumberActivity.this,
                                    AboutMeActivity.class);
                            startActivity(i);
                            finish();
                            yToast(LoginWithPhoneNumberActivity.this, getString(R.string.logged_in_successfully));
                        } else {
                            ySnackbar(LoginWithPhoneNumberActivity.this, task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    @Override
    protected void setTopBar() {
        YumTopBar.setToolbar(
                binding.topBar,
                R.drawable.ic_back_arrow,
                "",
                true,
                false,
                new YumTopBar.OnToolbarClickListener() {
                    @Override
                    public void onLeftIconClick() {
                        finish();
                    }
                });
    }
}