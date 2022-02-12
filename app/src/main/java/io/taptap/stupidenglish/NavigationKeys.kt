package io.taptap.stupidenglish

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

object NavigationKeys {

    object Arg {
        const val WORDS_ID = "words_id"
        const val SENTENCE_WORDS_ID = "sentence_words_id"
    }

    object Route {
        const val SE_MAIN = "stupid_english_main"

        const val SE_ADD_WORD = "stupid_english_add_word"
        const val SE_ADD_SENTENCE = "stupid_english_add_sentences"
        const val SE_REMEMBER = "stupid_english_remember"
    }

    enum class BottomNavigationScreen(
        val route: String,
        val title: String,
        @DrawableRes val icon: Int
    ) {
        SE_WORDS(
            route = "stupid_english_words",
            title = "Home",
            icon = android.R.drawable.btn_minus
        ),
        SE_SENTENCES(
            route = "stupid_english_sentences",
            title = "Search",
            icon = android.R.drawable.btn_star
        )
    }
}