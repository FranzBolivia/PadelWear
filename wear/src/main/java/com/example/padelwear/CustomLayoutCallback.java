package com.example.padelwear;

import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.view.View;

public class CustomLayoutCallback extends
        WearableLinearLayoutManager.LayoutCallback {
    private static final float MAX_ICON_PROGRESS = 0.65f; //Escalado máximo
    private float progressToCenter;
    @Override
    public void onLayoutFinished(View child, RecyclerView parent) {
// Calcula % de avance del hijo desde arriba
        float centerOffset = ((float) child.getHeight() / 2.0f) /
                (float) parent.getHeight();
        float yRelativeToCenterOffset = (child.getY() / parent.getHeight())
                + centerOffset;
// Normaliza desde el centro
        progressToCenter = Math.abs(0.5f - yRelativeToCenterOffset);
// Ajusta al máximo de escala
        progressToCenter = Math.min(progressToCenter, MAX_ICON_PROGRESS);
        child.setScaleX(1 - progressToCenter);
        child.setScaleY(1 - progressToCenter);
// Ajusta X
        float seno = (float) Math.sin(yRelativeToCenterOffset * Math.PI);
        child.setX(+(1 - seno) * 100);
    }
}