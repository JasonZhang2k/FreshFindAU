package comp5216.sydney.edu.au.freshfindau.page;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import comp5216.sydney.edu.au.freshfindau.R;
import comp5216.sydney.edu.au.freshfindau.utils.NavigationUtils;

public class UploadRecipeActivity extends AppCompatActivity {

    EditText etDishName, etMainIngredients, etRecipe;
    Button btnUploadRecipe;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_recipe);

        // Existing code...
        NavigationUtils.setupBottomNavigation(this, R.id.bottom_navigation_btn2);
        Toolbar toolbar = findViewById(R.id.recipe_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadRecipeActivity.this, EncyclopediaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // Initialize Firebase and UI components
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etDishName = findViewById(R.id.etDishName);
        etMainIngredients = findViewById(R.id.etMainIngredients);
        etRecipe = findViewById(R.id.etRecipe);
        btnUploadRecipe = findViewById(R.id.btnUploadRecipe);

        // Set the button click listener
        btnUploadRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadRecipeToFirestore();
            }
        });
    }

    private void uploadRecipeToFirestore() {
        String dishName = etDishName.getText().toString();
        String mainIngredients = etMainIngredients.getText().toString();
        String recipe = etRecipe.getText().toString();

        if (dishName.isEmpty() || mainIngredients.isEmpty() || recipe.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get username from Firestore and then upload the recipe
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");  // Assume the field is named "username"
                        uploadRecipeWithUsername(dishName, mainIngredients, recipe, username, userId);
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to get user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void uploadRecipeWithUsername(String dishName, String mainIngredients, String recipe, String username, String userId) {
        Map<String, Object> newRecipe = new HashMap<>();
        newRecipe.put("dish_name", dishName);
        newRecipe.put("main_ingredients", mainIngredients);
        newRecipe.put("recipe", recipe);
        newRecipe.put("author", username);
        newRecipe.put("user_id", userId);
        newRecipe.put("state", "private");

        db.collection("cookbook").add(newRecipe)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Recipe Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    // Clear the text fields
                    etDishName.setText("");
                    etMainIngredients.setText("");
                    etRecipe.setText("");
                    // Navigate to EncyclopediaActivity
                    navigateToEncyclopediaActivity();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void navigateToEncyclopediaActivity() {
        Intent intent = new Intent(UploadRecipeActivity.this, EncyclopediaActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
