package io.taptap.authorisation

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import java.lang.ref.WeakReference

class Authenticator(
    context: Context
) {
    private val contextReference: WeakReference<Context> = WeakReference(context)
    private lateinit var launcher: ManagedActivityResultLauncher<Intent, FirebaseAuthUIAuthenticationResult>

    private val intent: Intent by lazy {
        val providers = arrayListOf(
//            AuthUI.IdpConfig.EmailBuilder().build(),
//            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
//            AuthUI.IdpConfig.FacebookBuilder().build(),
//            AuthUI.IdpConfig.TwitterBuilder().build()
        )
        // Create and launch sign-in intent
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
    }

    @Composable
    fun login(
        onResult: (FirebaseAuthUIAuthenticationResult?) -> Unit
    ): Authenticator {
        launcher = rememberLauncherForActivityResult(FirebaseAuthUIActivityResultContract()) {
            onResult(it)
        }
        return this
    }

    fun logout(
        onComplete: (isSuccess: Boolean) -> Unit
    ) {
        contextReference.get()?.let {
            AuthUI.getInstance()
                .signOut(it)
                .addOnCompleteListener {
                    onComplete(true)
                }
        } ?: onComplete(false)
    }

    fun launch() {
        launcher.launch(intent)
    }
}

@Composable
fun rememberAuthenticator(context: Context): Authenticator = remember {
    Authenticator(context)
}