package com.example.gestionnairenotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
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
    private String sortMode = "DATE_DESC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note note = adapter.getNotes().get(position);
                repository.deleteNote(note);
                Toast.makeText(MainActivity.this, "Note supprimée", Toast.LENGTH_SHORT).show();
                loadNotes();
            }
        }).attachToRecyclerView(recyclerView);
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

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_theme) {
                int currentMode = AppCompatDelegate.getDefaultNightMode();
                if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                return true;
            } else if (item.getItemId() == R.id.sort_date_desc) {
                sortMode = "DATE_DESC";
                loadNotes();
                return true;
            } else if (item.getItemId() == R.id.sort_title_asc) {
                sortMode = "TITLE_ASC";
                loadNotes();
                return true;
            }
            return false;
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

        if (sortMode.equals("TITLE_ASC")) {
            notes.sort((n1, n2) -> n1.getTitre().compareToIgnoreCase(n2.getTitre()));
        } else {
            notes.sort((n1, n2) -> Integer.compare(n2.getId(), n1.getId()));
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
