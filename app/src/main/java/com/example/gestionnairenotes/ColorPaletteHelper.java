package com.example.gestionnairenotes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ColorPaletteHelper {

    private final AppCompatActivity activity;
    private final View layoutColorPalette;
    private final FloatingActionButton fabMain;

    private boolean isPaletteOpen = false;

    public ColorPaletteHelper(AppCompatActivity activity) {
        this.activity = activity;

        layoutColorPalette = activity.findViewById(R.id.layoutColorPalette);
        fabMain = activity.findViewById(R.id.fabMain);

        setupListeners();
    }

    private void setupListeners() {
        fabMain.setOnClickListener(v -> togglePalette());

        activity.findViewById(R.id.fabColorVert).setOnClickListener(v -> launchCreateNote("#A5D6A7"));
        activity.findViewById(R.id.fabColorRouge).setOnClickListener(v -> launchCreateNote("#EF9A9A"));
        activity.findViewById(R.id.fabColorBleu).setOnClickListener(v -> launchCreateNote("#90CAF9"));
        activity.findViewById(R.id.fabColorJaune).setOnClickListener(v -> launchCreateNote("#FFF59D"));
        activity.findViewById(R.id.fabColorOrange).setOnClickListener(v -> launchCreateNote("#FFCC80"));
        activity.findViewById(R.id.fabColorGris).setOnClickListener(v -> launchCreateNote("#EEEEEE"));
    }

    private void togglePalette() {
        if (!isPaletteOpen) {
            // Open animation
            layoutColorPalette.setVisibility(View.VISIBLE);
            layoutColorPalette.setAlpha(0f);
            layoutColorPalette.setTranslationY(100f);
            layoutColorPalette.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setListener(null);
            fabMain.animate().rotation(45f).setDuration(300);
            isPaletteOpen = true;
        } else {
            // Close animation
            layoutColorPalette.animate()
                    .alpha(0f)
                    .translationY(100f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            layoutColorPalette.setVisibility(View.GONE);
                        }
                    });
            fabMain.animate().rotation(0f).setDuration(300);
            isPaletteOpen = false;
        }
    }

    private void launchCreateNote(String colorHex) {
        togglePalette(); // close it
        Intent intent = new Intent(activity, CreateNoteActivity.class);
        intent.putExtra("note_color", colorHex);
        activity.startActivity(intent);
    }
}
