package com.example.gestionnairenotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionnairenotes.model.Note;
import com.example.gestionnairenotes.repository.NoteRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {

    private NoteRepository repository;
    private NoteAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private MaterialToolbar toolbar;
    private SearchView searchView;
    private MaterialButton btnFavoris;

    private boolean isFilterFavorites = false;
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new NoteRepository(this);

        initViews();
        setupRecyclerView();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.searchView);
        btnFavoris = findViewById(R.id.btnFavoris);
        recyclerView = findViewById(R.id.recyclerView);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        
        new ColorPaletteHelper(this);
    }

    private void setupRecyclerView() {
        adapter = new NoteAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        btnFavoris.setOnClickListener(v -> {
            isFilterFavorites = !isFilterFavorites;
            if (isFilterFavorites) {
                btnFavoris.setIconResource(R.drawable.ic_star);
            } else {
                btnFavoris.setIconResource(R.drawable.ic_star_outline);
            }
            loadNotes();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                loadNotes();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                loadNotes();
                return true;
            }
        });
    }

    private void loadNotes() {
        List<Note> notes;
        
        if (isFilterFavorites) {
            notes = repository.getFavoris();
            // Filter further by search if needed (in a real app, query would combine them, 
            // but we can filter the list in memory since it's a TP)
            if (!currentQuery.isEmpty()) {
                notes.removeIf(n -> !n.getTitre().toLowerCase().contains(currentQuery.toLowerCase()));
            }
        } else {
            if (currentQuery.isEmpty()) {
                notes = repository.getAllNotes();
            } else {
                notes = repository.rechercherParTitre(currentQuery);
            }
        }

        adapter.setNotes(notes);
        toolbar.setSubtitle(notes.size() + " note(s)");

        if (notes.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNoteClick(Note note) {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra("note_id", note.getId());
        startActivity(intent);
    }

    @Override
    public void onNoteDoubleClick(Note note) {
        // Toggle favorite
        note.setFavori(!note.isFavori());
        repository.updateNote(note);
        loadNotes();
    }

    @Override
    public void onNoteLongClick(Note note) {
        // Bonus: Delete note
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la note")
                .setMessage("Voulez-vous vraiment supprimer cette note ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    repository.deleteNote(note);
                    loadNotes();
                    Toast.makeText(this, "Note supprimée", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Non", null)
                .show();
    }
}
