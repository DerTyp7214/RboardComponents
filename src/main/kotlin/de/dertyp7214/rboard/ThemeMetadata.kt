package de.dertyp7214.rboard

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThemeMetadata(
    @SerializedName("format_version")
    val formatVersion: Int = 3,
    val id: String = "gboard_theme_creator_${Math.random()}",
    val name: String,
    @SerializedName("prefer_key_border")
    val preferKeyBorder: Boolean = true,
    @SerializedName("lock_key_border")
    val lockKeyBorder: Boolean = false,
    @SerializedName("is_light_theme")
    val isLightTheme: Boolean,
    @SerializedName("style_sheets")
    val styleSheets: List<String> = listOf(
        "style_sheet_variables.css",
        "style_sheet_md2.css"
    ),
    val flavors: List<Flavor> = listOf(
        Flavor()
    )
) : Parcelable {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}

@Parcelize
data class Flavor(
    val type: String = "BORDER",
    @SerializedName("style_sheets")
    val styleSheets: List<String> = listOf(
        "style_sheet_md2_border.css"
    )
) : Parcelable