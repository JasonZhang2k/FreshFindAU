package comp5216.sydney.edu.au.freshfindau.page;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import comp5216.sydney.edu.au.freshfindau.R;
import comp5216.sydney.edu.au.freshfindau.utils.NavigationUtils;

public class EncyclopediaActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayAdapter<String> adapter;
    ListView listView;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encyclopedia);
        NavigationUtils.setupBottomNavigation(this, R.id.bottom_navigation_btn2);
        Toolbar toolbar = findViewById(R.id.encyclopedia_back);
        Button btnFruit = findViewById(R.id.btn_fruit);
        Button btnVegetable = findViewById(R.id.btn_vegetable);
        Button btnNut = findViewById(R.id.btn_nut);
        Button btnRank = findViewById(R.id.btn_rank);
        Button btnAdd = findViewById(R.id.btn_add);
        listView = findViewById(R.id.listView);
        ArrayList<String> cookbookTitles = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cookbookTitles);
        listView.setAdapter(adapter);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // restart MainActivity
                Intent intent = new Intent(EncyclopediaActivity.this, EncyclopediaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // Set onClickListeners
        btnFruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCategoryDetail("fruit");
            }
        });

        btnVegetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCategoryDetail("vegetable");
            }
        });

        btnNut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCategoryDetail("nut");
            }
        });

        btnRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EncyclopediaActivity.this, NutrientActivity.class);
                startActivity(intent);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EncyclopediaActivity.this, UploadRecipeActivity.class);
                startActivity(intent);
            }
        });

        // Initialize Spinner and its Adapter
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.cookbook_types, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        // Spinner item select listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = (String) parent.getItemAtPosition(position);
                loadDataFromFirestore(selectedState);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDishName = adapter.getItem(position);
            Intent intent = new Intent(EncyclopediaActivity.this, CookbookActivity.class);
            intent.putExtra("selectedDish", selectedDishName);
            startActivity(intent);
        });

        // Load initial data
        loadDataFromFirestore("public");
    }

    private void navigateToCategoryDetail(String category) {
        Intent intent = new Intent(EncyclopediaActivity.this, CategoryDetailActivity.class);
        intent.putExtra("category", category);  // passing the category to the CategoryDetailActivity
        startActivity(intent);
    }

    private void loadDataFromFirestore(String state) {
        // Clear the previous data
        adapter.clear();

        // Log statement for debugging
        Log.d("EncyclopediaActivity", "Loading data for state: " + state);

        // Query Firestore based on the state
        db.collection("cookbook")  // Corrected the collection name
                .whereEqualTo("state", state.toLowerCase())  // Ensure lowercase for query
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Log statement for successful query
                        Log.d("EncyclopediaActivity", "Query successful");

                        // Check if any documents were retrieved
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            // Log statement for document count
                            Log.d("EncyclopediaActivity", "Number of documents retrieved: " + task.getResult().size());

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String title = document.getString("dish_name");
                                adapter.add(title);
                            }
                            // Notify the adapter that the data set has changed
                            adapter.notifyDataSetChanged();
                        } else {
                            // Log warning if no documents found
                            Log.w("EncyclopediaActivity", "No documents found for state: " + state);
                        }
                    } else {
                        // Log statement for failed query
                        Log.e("EncyclopediaActivity", "Query failed", task.getException());
                    }
                });
    }
}