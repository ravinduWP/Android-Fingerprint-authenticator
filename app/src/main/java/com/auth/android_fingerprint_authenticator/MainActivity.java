package com.auth.android_fingerprint_authenticator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    Button btn_fp;
    Button btn_fppin;
    TextView text_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_fp = (Button) findViewById(R.id.fp);
        btn_fppin = (Button) findViewById(R.id.fppin);
        checkBioMetricsSupported();
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this,"Authentication error: "+errString,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this,"Authentication succeeded",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this,"Authentication Failed",Toast.LENGTH_SHORT).show();

            }
        });

        btn_fp.setOnClickListener(v -> {
            BiometricPrompt.PromptInfo.Builder promptinfo = dialogMetric();
            promptinfo.setNegativeButtonText("Cancel");
            biometricPrompt.authenticate(promptinfo.build());
        });
        btn_fppin.setOnClickListener(v -> {
            BiometricPrompt.PromptInfo.Builder promptinfo = dialogMetric();
            promptinfo.setDeviceCredentialAllowed(true);
            biometricPrompt.authenticate(promptinfo.build());
        });
    }

    BiometricPrompt.PromptInfo.Builder dialogMetric(){
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Login using your biometric credentials");
    }

    private void checkBioMetricsSupported() {
        String info = "";
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK
                | BiometricManager.Authenticators.BIOMETRIC_STRONG)){
            case BiometricManager.BIOMETRIC_SUCCESS:
                info = "Device can Authenticate using BioMetrics";
                enableButton(true);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                info = "no biometrcis features available on this device";
                enableButton(false);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                info = "Biometrics features are currently unavailable";
                enableButton(false);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                info = "First Register atleast one fingerprint";
                enableButton(false,true);
                break;
            default:
                info = "Unkown Error";
                break;
        }
        text_info = findViewById(R.id.tx_info);
        text_info.setText(info);
    }

    void enableButton(Boolean enable){
        btn_fp.setEnabled(enable);
        btn_fppin.setEnabled(true);
    }
    void enableButton(Boolean enable,Boolean enroll){
        enableButton(enable);
        if (!enroll) return;
        Intent enrollintent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
        enrollintent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_STRONG |
                BiometricManager.Authenticators.BIOMETRIC_WEAK);
        startActivity(enrollintent);


    }
}