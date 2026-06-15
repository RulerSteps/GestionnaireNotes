package com.example.gestionnairenotes;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gestionnairenotes.model.Note;
import com.example.gestionnairenotes.repository.NoteRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText etTitre;
    private EditText etContenu;
    private FloatingActionButton fabSave;
    private MaterialToolbar toolbar;
    private NoteRepository repository;

    private String selectedColor = "#FFFFFF"; // Default white

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_note);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new NoteRepository(this);

        // Receive color from Intent
        if (getIntent().hasExtra("note_color")) {
            selectedColor = getIntent().getStringExtra("note_color");
        }

        initViews();
        setupToolbar();
        applyColor();

        fabSave.setOnClickListener(v -> saveNote());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etTitre = findViewById(R.id.etTitre);
        etContenu = findViewById(R.id.etContenu);
        fabSave = findViewById(R.id.fabSave);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void applyColor() {
        try {
            findViewById(R.id.mainLayout).setBackgroundColor(Color.parseColor(selectedColor));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveNote() {
        String titre = etTitre.getText().toString().trim();
        String contenu = etContenu.getText().toString().trim();

        if (titre.isEmpty() || contenu.isEmpty()) {
            Toast.makeText(this, "Le titre et le contenu ne peuvent pas être vides", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());

        Note note = new Note(titre, contenu, selectedColor, false, currentDate);
        repository.insertNote(note);

        Toast.makeText(this, "Note créée", Toast.LENGTH_SHORT).show();
        finish();
    }
}
