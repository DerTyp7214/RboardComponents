package de.dertyp7214.rboard

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RboardTheme(
    val name: String,
    val metadata: ThemeMetadata,
    val preview: Bitmap?,
    val css: List<CssFile>,
    val images: List<ImageFile>,
) : Parcelable

@Parcelize
data class CssFile(
    val name: String,
    val content: String
): Parcelable

@Parcelize
data class ImageFile(
    val name: String,
    val content: Bitmap
): Parcelable