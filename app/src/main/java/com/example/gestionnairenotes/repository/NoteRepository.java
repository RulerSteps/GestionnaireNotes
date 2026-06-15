package com.example.gestionnairenotes.repository;

import android.content.Context;

import com.example.gestionnairenotes.database.NoteDao;
import com.example.gestionnairenotes.database.NoteDatabase;
import com.example.gestionnairenotes.model.Note;

import java.util.List;

public class NoteRepository {

    private final NoteDao noteDao;

    public NoteRepository(Context context) {
        NoteDatabase db = NoteDatabase.getInstance(context);
        this.noteDao = db.noteDao();
    }

    public void insertNote(Note note) {
        noteDao.insert(note);
    }

    public void updateNote(Note note) {
        noteDao.update(note);
    }

    public void deleteNote(Note note) {
        noteDao.delete(note);
    }

    public List<Note> getAllNotes() {
        return noteDao.getAllNotes();
    }

    public List<Note> getFavoris() {
        return noteDao.getFavoris();
    }

    public List<Note> rechercherParTitre(String texte) {
        return noteDao.rechercherParTitre("%" + texte + "%");
    }
}