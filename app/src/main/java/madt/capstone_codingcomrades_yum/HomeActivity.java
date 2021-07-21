package madt.capstone_codingcomrades_yum;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

import madt.capstone_codingcomrades_yum.chat.ChatFragment;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.HomescreenBinding;
import madt.capstone_codingcomrades_yum.matcheslisting.MatchesFragment;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class HomeActivity extends BaseActivity {
    private HomescreenBinding binding;
    String screenTitle;

    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.homescreen);

        FragmentManager manager = getSupportFragmentManager();
        contextOfApplication = getApplicationContext();

      /*  manager.beginTransaction().add(R.id.navHostFragment, new MatchesFragment()).commit();



        screenTitle = getString(R.string.title_matches);
        setTopBar();*/
        binding.bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_matches:
                        switchToFragment(manager, new MatchesFragment());
                        screenTitle = getString(R.string.title_matches);
                        break;
                    case R.id.navigation_chat:
                        switchToFragment(manager, new ChatFragment());
                        screenTitle = getString(R.string.title_chat);
                        break;
                    case R.id.navigation_profile:
                        switchToFragment(manager, new ProfileFragment());
                        screenTitle = getString(R.string.title_profile);
                        break;


                }
                setTopBar();
                return false;
            }
        });


    }

    public void switchToFragment(FragmentManager manager, Fragment fragment) {

        manager.beginTransaction().replace(R.id.navHostFragment, fragment).commit();
    }

    @Override
    protected void setTopBar() {
        YumTopBar.setToolbar(
                binding.topBar,
                0,
                screenTitle,
                false,
                true,
               null);
    }
}
