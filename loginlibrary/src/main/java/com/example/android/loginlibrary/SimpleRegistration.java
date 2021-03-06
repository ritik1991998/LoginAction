package com.example.android.loginlibrary;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Pattern;

/**
 * Created by ritik on 10-03-2018.
 */

public class SimpleRegistration {

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private OnRegistrationResult mOnRegistrationResult;

    public SimpleRegistration() {
    }

    public void setOnRegistrationResult(OnRegistrationResult eventListener) {
        mOnRegistrationResult = eventListener;
    }

    public void attemptRegistration(@NonNull Activity var1, String email, String passwordInput, String passwordRecheck, final String userName, final Uri uploadedDpLink) {
        email = email.trim();
        if (checkCredentials(email, passwordInput, passwordRecheck)) {

            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, passwordInput)
                    .addOnCompleteListener(var1, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user = mAuth.getCurrentUser();
                                if (mOnRegistrationResult != null) {
                                    mOnRegistrationResult.resultSuccessful(user);
                                }

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userName)
                                        .setPhotoUri(uploadedDpLink)
                                        .build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.i("76", "User profile successfully updated.");
                                                    if (mOnRegistrationResult != null) {
                                                        mOnRegistrationResult.resultName(user);
                                                        mOnRegistrationResult.resultDp(uploadedDpLink);
                                                    }
                                                } else {
                                                    Log.i("82", task.getException() + "");
                                                    if (mOnRegistrationResult != null) {
                                                        mOnRegistrationResult.profileUpdateError(task.getException());
                                                    }
                                                }
                                            }
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                try {
                                    Log.i("94", task.getException().toString());
                                    throw task.getException();
                                } catch (com.google.firebase.auth.FirebaseAuthUserCollisionException e) {
                                    if (mOnRegistrationResult != null) {
                                        mOnRegistrationResult.sameEmailError(task.getException());
                                    }
                                } catch (com.google.firebase.FirebaseNetworkException e) {
                                    if (mOnRegistrationResult != null) {
                                        mOnRegistrationResult.networkError(task.getException());
                                    }
                                } catch (Exception e) {
                                    Log.i("105", e.toString());
                                    if (mOnRegistrationResult != null) {
                                        mOnRegistrationResult.resultError(task.getException());
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private boolean checkCredentials(String email, String password1, String password2) {
        if (!isEmailValid(email)) {
            return false;
        }
        if (passwordCheck(password1, 1)) {
            return false;
        }
        if (passwordCheck(password2, 2)) {
            return false;
        }
        if (!password1.equals(password2)) {
            Log.i("124", "unequal");
            if (mOnRegistrationResult != null) {
                mOnRegistrationResult.wrongCredentials("password1 and password2", "mismatch");
            }
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            if (mOnRegistrationResult != null)
                mOnRegistrationResult.wrongCredentials("email", "empty");
            return false;

        }
        if (!pat.matcher(email).matches()) {
            if (mOnRegistrationResult != null) {
                mOnRegistrationResult.wrongCredentials("email", "invalid");
            }
            return false;
        } else return true;
    }

    private boolean passwordCheck(String password, int passwordNumber) {
        if (TextUtils.isEmpty(password)) {
            if (mOnRegistrationResult != null) {
                mOnRegistrationResult.wrongCredentials("password" + passwordNumber, "empty");
            }
            return true;
        } else if (password.length() < 7) {
            if (mOnRegistrationResult != null) {
                mOnRegistrationResult.wrongCredentials("password" + passwordNumber, "short");
            }
            return true;
        }
        return false;
    }

    public interface OnRegistrationResult {
        void resultSuccessful(FirebaseUser registeredUser);

        void sameEmailError(Exception errorResult);

        void networkError(Exception errorResult);

        void resultError(Exception errorResult);

        void profileUpdateError(Exception errorResult);

        void resultName(FirebaseUser registeredUser);

        void resultDp(Uri uploadUriLink);

        void wrongCredentials(String doubtfulCredential, String errorMessage);
    }
}
