package comp5216.sydney.edu.au.freshfindau.page;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import comp5216.sydney.edu.au.freshfindau.R;
import comp5216.sydney.edu.au.freshfindau.utils.NavigationUtils;

public class CookbookActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cookbook);

        NavigationUtils.setupBottomNavigation(this, R.id.bottom_navigation_btn2);
        Toolbar toolbar = findViewById(R.id.cookbook_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Get the selected dish name from the Intent
        String selectedDish = getIntent().getStringExtra("selectedDish");
        if (selectedDish != null && !selectedDish.isEmpty()) {
            loadDataFromFirestore(selectedDish);
        } else {
            // Handle error: dish name is null or empty
        }
    }

    private void loadDataFromFirestore(String dishName) {
        db.collection("cookbook")
                .whereEqualTo("dish_name", dishName)
                .limit(1)  // Limit to 1 document since dish_name should be unique
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Check if any documents were retrieved
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            displayData(document);
                        } else {
                            // Handle error: No documents found
                        }
                    } else {
                        // Handle error: Query failed
                        Log.e("CookbookActivity", "Query failed", task.getException());
                    }
                });
    }

    private void displayData(DocumentSnapshot document) {
        // Assigning the data from the document to the UI elements
        ((TextView) findViewById(R.id.tvDishName)).setText(document.getString("dish_name"));
        ((TextView) findViewById(R.id.tvAuthor)).setText("by " + document.getString("author"));
        ((TextView) findViewById(R.id.tvMainIngredients)).setText(document.getString("main_ingredients"));
        ((TextView) findViewById(R.id.tvRecipe)).setText(document.getString("recipe"));
    }
}
