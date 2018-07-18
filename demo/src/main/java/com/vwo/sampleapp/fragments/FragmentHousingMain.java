package com.vwo.sampleapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vwo.sampleapp.R;
import com.vwo.sampleapp.interfaces.ChangeFragment;
import com.vwo.sampleapp.interfaces.NavigationToggleListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

/**
 * Created by aman on 08/08/17.
 */

public class FragmentHousingMain extends Fragment implements ChangeFragment {
    private static final String LOG_TAG = FragmentHousingMain.class.getSimpleName();

    private NavigationToggleListener listener;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            VARIATION_LOGIN_TYPE_NORMAL})
    @interface LoginType {
    }

    public static final int VARIATION_LOGIN_TYPE_NORMAL = 2;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof NavigationToggleListener) {
            listener = (NavigationToggleListener) context;
        } else {
            Log.e(LOG_TAG, "Interface NavigationToggleListener not implemented in Activity");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_campaign, container, false);

        AppCompatImageView navigation = view.findViewById(R.id.campaign_navigation);
        AppCompatImageView refresh = view.findViewById(R.id.refresh_campaign);
        AppCompatTextView toolbarTitle = view.findViewById(R.id.toolbar_title);

        navigation.setOnClickListener(view1 -> {
            if (listener != null) {
                listener.onToggle();
            }
        });

        refresh.setOnClickListener(view12 -> loadDefaultFragments());

        toolbarTitle.setText(R.string.title_on_boarding);
        loadDefaultFragments();
        return view;
    }

    private void loadDefaultFragments() {
        loadFragment(null, VARIATION_LOGIN_TYPE_NORMAL, null);
    }

    /**
     * <b> This function is used to load a particular {@link android.app.Fragment} from the
     * controlling {@link Activity} or {@link android.app.Fragment} </b>
     *
     * @param bundle     is the data to be passed to {@link android.app.Fragment}
     * @param fragmentId is the id that identifies, which {@link android.app.Fragment} is to be loaded
     * @param tag        is the tag that is attached to {@link android.app.Fragment} which is to be loaded
     */
    @Override
    public void loadFragment(@Nullable Bundle bundle, int fragmentId, @Nullable String tag) {
        getChildFragmentManager().beginTransaction().replace(R.id.onboarding_variation_container,
                FragmentHousing.getInstance(fragmentId)).commit();
    }
}
