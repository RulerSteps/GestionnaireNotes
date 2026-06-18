package com.example.gestionnairenotes;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionnairenotes.model.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes = new ArrayList<>();
    private final OnNoteClickListener listener;

    public List<Note> getNotes() {
        return notes;
    }

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
        void onNoteDoubleClick(Note note);
        void onNoteLongClick(Note note);
    }

    public NoteAdapter(OnNoteClickListener listener) {
        this.listener = listener;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note, listener);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNoteTitle;
        private final TextView tvNoteDate;
        private final ImageView ivFavorite;
        private final ConstraintLayout cardLayout;

        private boolean isClickPending = false;
        private final Handler handler = new Handler(Looper.getMainLooper());

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNoteTitle = itemView.findViewById(R.id.tvNoteTitle);
            tvNoteDate = itemView.findViewById(R.id.tvNoteDate);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            cardLayout = itemView.findViewById(R.id.cardLayout);
        }

        public void bind(Note note, OnNoteClickListener listener) {
            tvNoteTitle.setText(note.getTitre());
            tvNoteDate.setText(note.getDate());
            
            try {
                cardLayout.setBackgroundColor(Color.parseColor(note.getCouleur()));
            } catch (Exception e) {
                cardLayout.setBackgroundColor(Color.LTGRAY); // Default if parsing fails
            }

            if (note.isFavori()) {
                ivFavorite.setImageResource(R.drawable.ic_star);
            } else {
                ivFavorite.setImageResource(R.drawable.ic_star_outline);
            }

            itemView.setOnClickListener(v -> {
                if (isClickPending) {
                    // Double click
                    isClickPending = false;
                    handler.removeCallbacksAndMessages(null);
                    listener.onNoteDoubleClick(note);
                } else {
                    // Single click
                    isClickPending = true;
                    handler.postDelayed(() -> {
                        if (isClickPending) {
                            isClickPending = false;
                            listener.onNoteClick(note);
                        }
                    }, 300); // 300ms window for double click
                }
            });

            itemView.setOnLongClickListener(v -> {
                listener.onNoteLongClick(note);
                return true;
            });

        }
    }
}
