package com.baset.anki.pro.generator.presentation.util

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment

fun View.extractString(@StringRes id: Int): CharSequence {
    if (id == -1) {
        return ""
    }
    return context.resources.getString(id)
}

fun Context.extractColor(@ColorRes resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}

fun View.extractColor(@ColorRes resId: Int): Int {
    return context.extractColor(resId)
}

fun Fragment.extractColor(@ColorRes resId: Int): Int {
    return requireContext().extractColor(resId)
}

fun Context.extractDrawable(@DrawableRes resId: Int): Drawable? {
    return AppCompatResources.getDrawable(this, resId)
}

fun View.extractDrawable(@DrawableRes resId: Int): Drawable? {
    return context.extractDrawable(resId)
}

fun Fragment.extractDrawable(@DrawableRes resId: Int): Drawable? {
    return requireContext().extractDrawable(resId)
}

fun Context.getFontCompat(@FontRes res: Int): Typeface? {
    return ResourcesCompat.getFont(this, res)
}

fun View.getFontCompat(@FontRes res: Int): Typeface? {
    return context.getFontCompat(res)
}

fun Fragment.getFontCompat(@FontRes res: Int): Typeface? {
    return requireContext().getFontCompat(res)
}

fun Context.extractDimen(@DimenRes id: Int): Float {
    if (id == -1) {
        return 0f
    }
    return resources.getDimension(id)
}

fun View.extractDimen(@DimenRes id: Int): Float {
    if (id == -1) {
        return 0f
    }
    return context.extractDimen(id)
}

fun Fragment.extractDimen(@DimenRes id: Int): Float {
    if (id == -1) {
        return 0f
    }
    return requireContext().extractDimen(id)
}

fun Context.extractDimenPixelSize(@DimenRes id: Int): Int {
    if (id == -1) {
        return 0
    }
    return resources.getDimensionPixelSize(id)
}

fun View.extractDimenPixelSize(@DimenRes id: Int): Int {
    if (id == -1) {
        return 0
    }
    return context.extractDimenPixelSize(id)
}

fun Fragment.extractDimenPixelSize(@DimenRes id: Int): Int {
    if (id == -1) {
        return 0
    }
    return requireContext().extractDimenPixelSize(id)
}
