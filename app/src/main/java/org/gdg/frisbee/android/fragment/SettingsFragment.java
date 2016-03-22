/*
 * Copyright 2013-2015 The GDG Frisbee Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gdg.frisbee.android.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.GoogleAnalytics;

import org.gdg.frisbee.android.Const;
import org.gdg.frisbee.android.R;
import org.gdg.frisbee.android.api.model.Chapter;
import org.gdg.frisbee.android.api.model.Directory;
import org.gdg.frisbee.android.api.model.HomeGdgRequest;
import org.gdg.frisbee.android.app.App;
import org.gdg.frisbee.android.appwidget.UpcomingEventWidgetProvider;
import org.gdg.frisbee.android.cache.ModelCache;
import org.gdg.frisbee.android.utils.PrefUtils;
import org.gdg.frisbee.android.view.LocationListPreference;

import java.io.IOException;

public class SettingsFragment extends PreferenceFragment {

    private Preference.OnPreferenceChangeListener mOnHomeGdgPreferenceChange =
        new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                final String homeGdg = (String) o;

                if (PrefUtils.getUserServerAuthCode(getActivity()) != null) {
                    setHomeGdg(homeGdg);
                }
                // Update widgets to show newest chosen GdgHome events
                App.getInstance().startService(new Intent(App.getInstance(),
                    UpcomingEventWidgetProvider.UpdateService.class));

                return true;
            }
        };

    private Preference.OnPreferenceChangeListener mOnAnalyticsPreferenceChange =
        new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean analytics = (Boolean) newValue;
                GoogleAnalytics.getInstance(getActivity()).setAppOptOut(!analytics);
                return true;
            }
        };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(PrefUtils.PREF_NAME);
        addPreferencesFromResource(R.xml.settings);
        initPreferences();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_prefs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initPreferences() {
        final LocationListPreference prefHomeGdgList =
            (LocationListPreference) findPreference(PrefUtils.SETTINGS_HOME_GDG);
        if (prefHomeGdgList != null) {
            prefHomeGdgList.setEnabled(false);

            App.getInstance().getModelCache().getAsync(Const.CACHE_KEY_CHAPTER_LIST_HUB, false,
                new ModelCache.CacheListener() {
                    @Override
                    public void onGet(Object item) {
                        Directory directory = (Directory) item;

                        String[] entries = new String[directory.getGroups().size()];
                        String[] entryValues = new String[directory.getGroups().size()];

                        int i = 0;
                        for (Chapter chapter : directory.getGroups()) {
                            entries[i] = chapter.getName();
                            entryValues[i] = chapter.getGplusId();
                            i++;
                        }
                        prefHomeGdgList.setEntries(entries);
                        prefHomeGdgList.setEntryValues(entryValues);
                        prefHomeGdgList.setEnabled(true);
                    }

                    @Override
                    public void onNotFound(String key) {

                    }
                });

            prefHomeGdgList.setOnPreferenceChangeListener(mOnHomeGdgPreferenceChange);
        }

        CheckBoxPreference prefAnalytics = (CheckBoxPreference) findPreference(PrefUtils.SETTINGS_ANALYTICS);
        if (prefAnalytics != null) {
            prefAnalytics.setOnPreferenceChangeListener(mOnAnalyticsPreferenceChange);
        }
    }

    private void setHomeGdg(final String homeGdg) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    String token = PrefUtils.getUserServerAuthCode(getActivity());
                    App.getInstance().getGdgXHub().setHomeGdg("Bearer " + token,
                        new HomeGdgRequest(homeGdg))
                        .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }
}
