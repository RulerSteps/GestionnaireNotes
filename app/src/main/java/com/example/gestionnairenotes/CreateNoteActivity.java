package com.example.gestionnairenotes;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestionnairenotes.model.Note;
import com.example.gestionnairenotes.repository.NoteRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText editTitre;
    private EditText editContenu;
    private Button btnCreer;
    private View scrollNote;

    private String couleurNote;
    private NoteRepository repository;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        editTitre   = findViewById(R.id.editTitre);
        editContenu = findViewById(R.id.editContenu);
        btnCreer    = findViewById(R.id.btnCreer);
        scrollNote  = findViewById(R.id.scrollNote);

        repository = new NoteRepository(this);

        couleurNote = getIntent().getStringExtra("note_color");
        if (couleurNote == null) {
            couleurNote = "#219653";
        }

        appliquerCouleurFond(couleurNote);

        btnCreer.setOnClickListener(v -> enregistrerNote());
    }

    private void appliquerCouleurFond(String couleurHex) {
        GradientDrawable fond = new GradientDrawable();
        fond.setShape(GradientDrawable.RECTANGLE);
        fond.setCornerRadius(16 * getResources().getDisplayMetrics().density);
        fond.setColor(Color.parseColor(couleurHex));
        scrollNote.setBackground(fond);
    }

    private void enregistrerNote() {
        String titre   = editTitre.getText().toString().trim();
        String contenu = editContenu.getText().toString().trim();

        if (TextUtils.isEmpty(titre)) {
            editTitre.setError("Le titre est obligatoire");
            editTitre.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(contenu)) {
            editContenu.setError("Le contenu est obligatoire");
            editContenu.requestFocus();
            return;
        }

        String date = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH).format(new Date());
        date = date.substring(0, 1).toUpperCase() + date.substring(1);

        Note note = new Note(titre, contenu, couleurNote, false, date);

        executor.execute(() -> {
            repository.insertNote(note);
            runOnUiThread(() -> {
                Toast.makeText(this, "Note créée", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
