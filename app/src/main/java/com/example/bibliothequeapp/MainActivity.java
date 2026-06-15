package com.example.bibliothequeapp;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLivres;

    private LivreAdapter livreAdapter;

    private ArrayList<Livre> listeLivres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        recyclerViewLivres =
                findViewById(R.id.recyclerViewLivres);

        listeLivres = new ArrayList<>();

        listeLivres.add(new Livre(
                1,
                "Le Petit Prince",
                "Antoine de Saint-Exupéry",
                "9780156013987",
                true));

        listeLivres.add(new Livre(
                2,
                "L'Étranger",
                "Albert Camus",
                "9782070360024",
                false));

        listeLivres.add(new Livre(
                3,
                "Les Misérables",
                "Victor Hugo",
                "9782253096344",
                true));

        recyclerViewLivres.setLayoutManager(
                new LinearLayoutManager(this));

        livreAdapter = new LivreAdapter(listeLivres);

        recyclerViewLivres.setAdapter(livreAdapter);
    }
}