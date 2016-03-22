package org.gdg.frisbee.android.app;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

import org.gdg.frisbee.android.common.GdgActivity;

public final class GoogleApiClientFactory {
    private GoogleApiClientFactory() {
    }

    public static GoogleApiClient create(GdgActivity context) {
        return createClient(context, false);
    }

    public static GoogleApiClient createWithGames(GdgActivity context) {
        return createClient(context, true);
    }

    public static GoogleApiClient createClient(GdgActivity context, boolean withGames) {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Plus.SCOPE_PLUS_LOGIN)
            .requestScopes(Plus.SCOPE_PLUS_PROFILE)
            .build();

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context)
            .enableAutoManage(context, context)
            .addConnectionCallbacks(context)
            .addApi(AppIndex.API)
            .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
            .addApi(Plus.API);

        if (withGames) {
            Games.GamesOptions gamesOptions = Games.GamesOptions.builder()
                .setRequireGooglePlus(true)
                .setShowConnectingPopup(false).build();

            builder.addApi(Games.API, gamesOptions)
                .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER);
        }

        return builder.build();
    }

}
