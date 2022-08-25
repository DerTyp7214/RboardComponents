package de.dertyp7214.rboardcomponents.core

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

fun View.setMargin(
    leftMargin: Int? = null, topMargin: Int? = null,
    rightMargin: Int? = null, bottomMargin: Int? = null,
    all: Int? = null
) {
    if (layoutParams is ConstraintLayout.LayoutParams) {
        val params = layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(
            leftMargin ?: all ?: params.leftMargin,
            topMargin ?: all ?: params.topMargin,
            rightMargin ?: all ?: params.rightMargin,
            bottomMargin ?: all ?: params.bottomMargin
        )
        layoutParams = params
    } else if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(
            leftMargin ?: all ?: params.leftMargin,
            topMargin ?: all ?: params.topMargin,
            rightMargin ?: all ?: params.rightMargin,
            bottomMargin ?: all ?: params.bottomMargin
        )
        layoutParams = params
    }
}

fun View.setWidth(width: Int) {
    if (layoutParams != null) layoutParams.width = width
    else layoutParams = ViewGroup.LayoutParams(width, height)
}

fun View.setHeight(height: Int) {
    if (layoutParams != null) layoutParams.height = height
    else layoutParams = ViewGroup.LayoutParams(width, height)
}