package com.example.gestionnairenotes.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gestionnairenotes.model.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<Note> getAllNotes();

    @Query("SELECT * FROM notes WHERE favori = 1 ORDER BY id DESC")
    List<Note> getFavoris();

    @Query("SELECT * FROM notes WHERE titre LIKE :recherche ORDER BY id DESC")
    List<Note> rechercherParTitre(String recherche);
}