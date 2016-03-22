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

package org.gdg.frisbee.android.onboarding;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import org.gdg.frisbee.android.R;
import org.gdg.frisbee.android.common.BaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FirstStartStep2Fragment extends BaseFragment {

    Step2Listener listener = Step2Listener.EMPTY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome_step2, container, false);
        ButterKnife.bind(this, v);

        SignInButton signInButton = (SignInButton) v.findViewById(R.id.sign_in_button);
        signInButton.setScopes(new Scope[] {Plus.SCOPE_PLUS_LOGIN, Plus.SCOPE_PLUS_PROFILE});
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Step2Listener) {
            listener = (Step2Listener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        listener = Step2Listener.EMPTY;
    }

    @OnClick(R.id.sign_in_button)
    public void onSignInButtonClick() {
        listener.onSignInButtonClick();
    }

    @OnClick(R.id.skipSignin)
    public void onSkippedSignInClick() {
        listener.onSkippedSignInClick();
    }

    public interface Step2Listener {
        void onSignInButtonClick();

        void onSkippedSignInClick();

        Step2Listener EMPTY = new Step2Listener() {
            @Override
            public void onSignInButtonClick() {
            }

            @Override
            public void onSkippedSignInClick() {
            }
        };
    }
}
