package com.fretron.fleet.Timeline;

import android.content.Context;

import android.graphics.Bitmap;

import android.graphics.Canvas;

import android.graphics.PorterDuff;

import android.graphics.drawable.Drawable;

import android.os.Build;

import android.support.graphics.drawable.VectorDrawableCompat;

import android.support.v4.content.ContextCompat;



class VectorDrawableUtils {



    private static Drawable getDrawable(Context context, int drawableResId) {

        Drawable drawable;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            drawable = context.getResources().getDrawable(drawableResId, context.getTheme());

        }
        else {
            drawable = VectorDrawableCompat.create(context.getResources(), drawableResId, context.getTheme());

        }
        return drawable;
    }

    static Drawable getDrawable(Context context, int drawableResId, int colorFilter) {

        Drawable drawable = getDrawable(context, drawableResId);

        drawable.setColorFilter(ContextCompat.getColor(context, colorFilter), PorterDuff.Mode.SRC_IN);

        return drawable;

    }

}
