package comp5216.sydney.edu.au.freshfindau.page;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import comp5216.sydney.edu.au.freshfindau.R;
import comp5216.sydney.edu.au.freshfindau.utils.NavigationUtils;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private FirebaseFirestore db;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();

        ArrayList<String> seasonalFoods = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, seasonalFoods);
        ListView listView = findViewById(R.id.recommend);
        listView.setAdapter(adapter);

        loadSeasonalFoods();

        NavigationUtils.setupBottomNavigation(this, R.id.bottom_navigation_btn1);
        Toolbar toolbar = findViewById(R.id.home_back);
        toolbar.setNavigationOnClickListener(v -> recreate());

        Button searchButton = findViewById(R.id.search_button);
        SearchView searchView = findViewById(R.id.search_view);

        searchButton.setOnClickListener(v -> {
            String query = searchView.getQuery().toString();
            // Search function
            searchAndDisplayResults(query);
        });
    }

    private void loadSeasonalFoods() {
        //Log.d(TAG, "Attempting to load seasonal foods");
        String currentSeason = getCurrentSeason();
        ArrayList<String> allSeasonalFoods = new ArrayList<>();

        String[] categories = new String[]{"fruit", "vegetable", "nut"};
        for(String category : categories) {
            db.collection(category)
                    .whereEqualTo(currentSeason, true)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        Log.d(TAG, "Successfully loaded seasonal foods from Firestore for category: " + category);
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String foodName = document.getString("Food Name");
                            if(foodName == null) {
                                Log.w(TAG, "Food name is null for document ID: " + document.getId());
                                continue;
                            }
                            //Log.d(TAG, "Adding food to list: " + foodName);
                            allSeasonalFoods.add(foodName);
                        }
                        adapter.clear();
                        adapter.addAll(allSeasonalFoods);
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading seasonal foods from Firestore for category: " + category, e);
                    });
        }
    }
    
    private String getCurrentSeason() {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        if (month >= 3 && month <= 5) {
            return "spring";
        } else if (month >= 6 && month <= 8) {
            return "summer";
        } else if (month >= 9 && month <= 11) {
            return "autumn";
        } else {
            return "winter";
        }
    }

    private void searchAndDisplayResults(String query) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<String> foodNames = new ArrayList<>();

        String[] collections = {"fruit", "vegetable", "nut"};

        for (String collection : collections) {
            db.collection(collection)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String foodName = documentSnapshot.getString("Food Name");
                            if (foodName != null) {
                                foodNames.add(foodName);
                            }
                        }

                        ArrayList<String> results = new ArrayList<>();
                        for (String name : foodNames) {
                            if (name.toLowerCase().contains(query.toLowerCase())) {  // Ignore case
                                results.add(name);
                            }
                        }

                        if (!results.isEmpty()) {
                            Intent intent = new Intent(HomeActivity.this, SearchResultActivity.class);
                            intent.putStringArrayListExtra("results", results);
                            intent.putExtra("query", query);
                            startActivity(intent);
                        } else {
                            Toast.makeText(HomeActivity.this, "No results found for: " + query, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // handle the error here, for example notify user with a Toast
                        Toast.makeText(HomeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void displaySearchResults(ArrayList<String> results, String query) {
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putStringArrayListExtra("results", results);
        intent.putExtra("query", query);
        startActivity(intent);
    }
}
