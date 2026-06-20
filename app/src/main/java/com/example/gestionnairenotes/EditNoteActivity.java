package com.example.gestionnairenotes;

import android.content.Intent;
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

public class EditNoteActivity extends AppCompatActivity {

    private EditText etTitre;
    private EditText etContenu;
    private FloatingActionButton fabSave;
    private MaterialToolbar toolbar;
    private NoteRepository repository;

    private Note currentNote;
    private String selectedColor = "#EEEEEE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_note);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new NoteRepository(this);

        initViews();
        setupToolbar();

        int noteId = getIntent().getIntExtra("note_id", -1);
        if (noteId != -1) {
            loadNoteData(noteId);
        } else {
            Toast.makeText(this, "Erreur de chargement de la note", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // CORRECTIF : si l'ID était valide mais qu'aucune note correspondante n'a été
        // trouvée en base (ex: note supprimée entre-temps), on arrête ici plutôt que
        // de laisser l'utilisateur "sauvegarder" une note qui n'existe pas.
        if (currentNote == null) {
            Toast.makeText(this, "Cette note est introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupColorPickers();

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

        // Sans cet appel, le menu (action_share) n'apparaît jamais dans la toolbar
        toolbar.inflateMenu(R.menu.menu_edit);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_share) {
                shareNote();
                return true;
            }
            return false;
        });
    }

    private void shareNote() {
        if (currentNote == null) return;

        String shareText = currentNote.getTitre() + "\n\n" + currentNote.getContenu();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Partager la note via");
        startActivity(shareIntent);
    }

    private void loadNoteData(int id) {
        for (Note note : repository.getAllNotes()) {
            if (note.getId() == id) {
                currentNote = note;
                break;
            }
        }

        if (currentNote != null) {
            etTitre.setText(currentNote.getTitre());
            etContenu.setText(currentNote.getContenu());
            selectedColor = currentNote.getCouleur();
            applyColor();
        }
        // Si currentNote reste null ici, c'est géré juste après l'appel
        // dans onCreate() (voir CORRECTIF plus haut).
    }

    private void setupColorPickers() {
        findViewById(R.id.btnColorVert).setOnClickListener(v -> changeColor("#A5D6A7"));
        findViewById(R.id.btnColorRouge).setOnClickListener(v -> changeColor("#EF9A9A"));
        findViewById(R.id.btnColorBleu).setOnClickListener(v -> changeColor("#90CAF9"));
        findViewById(R.id.btnColorJaune).setOnClickListener(v -> changeColor("#FFF59D"));
        findViewById(R.id.btnColorOrange).setOnClickListener(v -> changeColor("#FFCC80"));
        findViewById(R.id.btnColorGris).setOnClickListener(v -> changeColor("#EEEEEE"));
    }

    private void changeColor(String color) {
        selectedColor = color;
        applyColor();
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

        currentNote.setTitre(titre);
        currentNote.setContenu(contenu);
        currentNote.setCouleur(selectedColor);
        repository.updateNote(currentNote);
        Toast.makeText(this, "Note modifiée", Toast.LENGTH_SHORT).show();

        finish();
    }
}
