package kgk.beacon.view;

import android.support.v4.app.Fragment;


/**
 * Activity class, that represents screen for authentication and entering to app functions
 */
public class LoginActivity extends SingleFragmentActivity {

    //// Activity methods

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }
}
