package madt.capstone_codingcomrades_yum.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.createprofile.AboutMeActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityLoginWithPhoneNumberBinding;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;

public class LoginWithPhoneNumberActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private ActivityLoginWithPhoneNumberBinding binding;
    private FirebaseAuth auth;
    private String mVerificationId = "";
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String[] countryCodes = {"+91", "+1"};
    private String selectedCountryCode = "";
    public static boolean isEdit = false;
    public static String phoneNumber = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_with_phone_number);

        auth = FirebaseAuth.getInstance();
        setTopBar();
        setCountryCodeSpinner();

        if (isEdit) {
            yLog("phone number :", phoneNumber);
            if(!phoneNumber.isEmpty()) {
                String[] phoneNumberArray = phoneNumber.split(" ");
                binding.txtPhnEntry.setText(phoneNumberArray[1]);
                binding.spnCountryCode.setSelection(Arrays.asList(countryCodes).indexOf(phoneNumberArray[0]));
            }
        }

        binding.btnGetCode.setOnClickListener(v -> {
            if (binding.txtPhnEntry.getText().toString().isEmpty()) {
                ySnackbar(this, getString(R.string.err_enter_phone_number));
            } else {
                startPhoneNumberVerification(selectedCountryCode + " " + binding.txtPhnEntry.getText().toString());
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

    private void setCountryCodeSpinner() {

        binding.spnCountryCode.setOnItemSelectedListener(this);
        ArrayAdapter<String> ad
                = new ArrayAdapter<String>(
                LoginWithPhoneNumberActivity.this,
                android.R.layout.simple_spinner_item,
                countryCodes);
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        binding.spnCountryCode.setAdapter(ad);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]

        if (verificationId != null) {
            if (!verificationId.isEmpty()) {
                CommonUtils.showProgress(LoginWithPhoneNumberActivity.this);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                if (isEdit)
                    // Edit Phone Number
                    updatePhoneNumber(credential);

                else
                    // Create new User with phone Number
                    signInWithPhoneAuthCredential(credential);
            }
        }
        // [END verify_with_code]
    }

    private void updatePhoneNumber(PhoneAuthCredential credential) {

        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updatePhoneNumber(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updatePhoneInDB();

            }

            private void updatePhoneInDB() {

                Map<String, Object> updateNumber = new HashMap<>();
                updateNumber.put(FSConstants.USER.PHONE_NUMBER, selectedCountryCode + " " + binding.txtPhnEntry.getText().toString());

                FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), updateNumber).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        yToast(LoginWithPhoneNumberActivity.this, getString(R.string.phone_updated_successfully));
                        CommonUtils.hideProgress();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                        CommonUtils.hideProgress();
                        ySnackbar(LoginWithPhoneNumberActivity.this, getString(R.string.error_saving_user));
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                ySnackbar(LoginWithPhoneNumberActivity.this, e.getLocalizedMessage());
                CommonUtils.hideProgress();
            }
        });
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
                if (!isEdit)
                    signInWithPhoneAuthCredential(credential);
                else
                    updatePhoneNumber(credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                yLog("onVerificationFailed:", e.getLocalizedMessage());
                ySnackbar(LoginWithPhoneNumberActivity.this, e.getLocalizedMessage());
                CommonUtils.hideProgress();
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
                CommonUtils.hideProgress();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String VerificationId) {
                super.onCodeAutoRetrievalTimeOut(VerificationId);
                mVerificationId = VerificationId;
                CommonUtils.hideProgress();
            }
        };
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        yLog("phone number :", phoneNumber);
        // [START start_phone_auth]
        CommonUtils.showProgress(LoginWithPhoneNumberActivity.this);
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
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        AppSharedPreferences.getInstance().setString(SharedConstants.FS_AUTH_ID, user.getUid());
                        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, user.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {

                                Intent i = new Intent(LoginWithPhoneNumberActivity.this,
                                        AboutMeActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                AboutMeActivity.phoneNumber = selectedCountryCode + " " + binding.txtPhnEntry.getText().toString();

                                startActivity(i);
                                finish();
                                yToast(LoginWithPhoneNumberActivity.this, getString(R.string.logged_in_successfully));
                                CommonUtils.hideProgress();

                            }
                        }).addOnFailureListener(e -> {
                            ySnackbar(LoginWithPhoneNumberActivity.this, task.getException().getLocalizedMessage());
                            CommonUtils.hideProgress();
                        });


                    } else {
                        ySnackbar(LoginWithPhoneNumberActivity.this, task.getException().getLocalizedMessage());
                        CommonUtils.hideProgress();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCountryCode = countryCodes[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}