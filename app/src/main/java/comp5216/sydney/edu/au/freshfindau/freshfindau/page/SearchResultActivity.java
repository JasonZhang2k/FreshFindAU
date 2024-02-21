package comp5216.sydney.edu.au.freshfindau.page;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import comp5216.sydney.edu.au.freshfindau.R;
import comp5216.sydney.edu.au.freshfindau.utils.NavigationUtils;

public class SearchResultActivity extends AppCompatActivity {

    private static final String TAG = "SearchResultActivity";

    private ArrayList<String> results;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        NavigationUtils.setupBottomNavigation(this, R.id.bottom_navigation_btn1);
        Toolbar toolbar = findViewById(R.id.search_back);
        ListView listView = findViewById(R.id.listView);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // restart MainActivity
                Intent intent = new Intent(SearchResultActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        results = getIntent().getStringArrayListExtra("results");
        query = getIntent().getStringExtra("query");

        // Check if data has been received successfully
        if (results != null) {
            Log.d(TAG, "Received results: " + results.toString()); // Log the results
        } else {
            Log.e(TAG, "No results received.");
        }

        if (query != null) {
            Log.d(TAG, "Received query: " + query); // Log the query
        } else {
            Log.e(TAG, "No query received.");
        }

        TextView title = findViewById(R.id.tv_search_result);

        // Check if query is not null to avoid null pointer exception
        if (query != null) {
            title.setText("Search for: " + query);
        } else {
            title.setText("No query provided");
        }

        if (results != null && !results.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, results);
            listView.setAdapter(adapter);
        } else {
            Log.e(TAG, "Results are either null or empty. Nothing to display in the ListView.");
        }

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String clickedFoodName = results.get(position);

            queryAndLaunchDetailActivity(clickedFoodName);
        });
    }

    private void queryAndLaunchDetailActivity(String foodName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String[] collections = {"fruit", "vegetable", "nut"};
        for (String collection : collections) {
            db.collection(collection)
                    .whereEqualTo("Food Name", foodName)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                            Intent intent = new Intent(SearchResultActivity.this, FoodDetailActivity.class);

                            intent.putExtra("Food Name", document.getString("Food Name"));
                            intent.putExtra("Classification Name", document.getString("classficationName"));
                            intent.putExtra("Classification", document.getString("classification"));
                            intent.putExtra("Food Description", document.getString("Food Description"));
                            intent.putExtra("Sampling Details", document.getString("Sampling Details"));
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(SearchResultActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}