package free.qr.code.scanner.generator.Activities;

import static android.content.Context.VIBRATOR_SERVICE;
import static com.unity3d.services.core.properties.ClientProperties.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import com.consoliads.mediation.ConsoliAds;
import com.consoliads.mediation.bannerads.CAMediatedBannerView;
import com.consoliads.mediation.constants.NativePlaceholderName;
import free.qr.code.scanner.generator.R;

public class SettingsActivity extends Fragment {

    @SuppressLint("StaticFieldLeak")
    private static SwitchPreferenceCompat beep,vibrate;
    public static String PACKAGE_NAME;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.settings_activity, container, false);
        PACKAGE_NAME = getActivity().getPackageName();
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) CAMediatedBannerView mediatedBannerView = view.findViewById(R.id.consoli_banner_view);
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowBanner(NativePlaceholderName.Activity1, getActivity(), mediatedBannerView);
            ConsoliAds.Instance().LoadInterstitial();
        }
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        return view;
    }

    public void backSetting(View view) {
        if (!getPurchaseSharedPreference()) {
            ConsoliAds.Instance().ShowInterstitial(NativePlaceholderName.Activity1, getActivity());
        }

    }

    public static  class SettingsFragment extends PreferenceFragmentCompat {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            beep= getPreferenceManager().findPreference("beep");
            vibrate= getPreferenceManager().findPreference("vibrate");
            Preference moreApps = getPreferenceManager().findPreference("more_apps");
            Preference share = getPreferenceManager().findPreference("share");

            assert share != null;
            share.setOnPreferenceClickListener(preference -> {
                Intent share1 = new Intent(Intent.ACTION_SEND);
                share1.setType("text/plain");
                share1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share1.putExtra(Intent.EXTRA_SUBJECT, "QR Scanner Link");
                share1.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/developer?id=developer+name"+ PACKAGE_NAME);
                startActivity(share1);
                return true;
            });
            assert moreApps != null;
            moreApps.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(
                        "https://play.google.com/store/apps/developer?id=developer+name"));
                intent.setPackage(PACKAGE_NAME);
                startActivity(intent);
                return true;
            });

            if (beep != null) {
                beep.setOnPreferenceChangeListener((preference, newValue) -> {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
                    SharedPreferences.Editor edit = prefs.edit();
                    if (newValue.toString().equals("true")) {
                        edit.putBoolean("beepsound", Boolean.TRUE);
                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 300);
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                    }
                    if (newValue.toString().equals("false")) {
                        edit.putBoolean("beepsound", Boolean.FALSE);
                    }
                    edit.apply();
                    return true;
                });
            }
            if (vibrate != null){
                vibrate.setOnPreferenceChangeListener((preference, newValue) -> {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
                    SharedPreferences.Editor edit = prefs.edit();
                    if (newValue.toString().equals("true")) {
                        edit.putBoolean("vibrate", Boolean.TRUE);
                        Vibrator v = (Vibrator) requireActivity().getSystemService(VIBRATOR_SERVICE);
                        v.vibrate(300);
                    }
                    if (newValue.toString().equals("false")) {
                        edit.putBoolean("vibrate", Boolean.FALSE);
                    }
                    edit.apply();
                    return true;
                });
            }
        }
    }
    public boolean getPurchaseSharedPreference(){
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }
}