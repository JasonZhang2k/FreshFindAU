package comp5216.sydney.edu.au.freshfindau.page;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import comp5216.sydney.edu.au.freshfindau.R;
import comp5216.sydney.edu.au.freshfindau.utils.NavigationUtils;

public class FoodDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        Intent intent = getIntent();
        String foodName = intent.getStringExtra("Food Name");
        String classificationName = intent.getStringExtra("Classification Name");
        String classification = intent.getStringExtra("Classification");
        String foodDescription = intent.getStringExtra("Food Description");
        String samplingDetails = intent.getStringExtra("Sampling Details");

        Log.d("FoodDetailActivity", "Received Food Name: " + foodName);
        Log.d("FoodDetailActivity", "Received Classification Name: " + classificationName);

        NavigationUtils.setupBottomNavigation(this, R.id.bottom_navigation_btn2);
        Toolbar toolbar = findViewById(R.id.food_detail_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // restart MainActivity
                Intent intent = new Intent(FoodDetailActivity.this, EncyclopediaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // Get references to the TextViews
        TextView foodNameTextView = findViewById(R.id.food_name_textView);
        TextView classificationNameTextView = findViewById(R.id.classification_name_textView);
        TextView classificationTextView = findViewById(R.id.classification_textView);
        TextView foodDescriptionTextView = findViewById(R.id.food_description_textView);
        TextView samplingDetailsTextView = findViewById(R.id.sampling_details_textView);

        Log.d("FoodDetailActivity", "foodNameTextView: " + (foodNameTextView == null ? "null" : "not null"));

        // Set the text on the TextViews
        foodNameTextView.setText("Food Name: " + foodName);
        classificationNameTextView.setText("Classification Name: " + classificationName);
        classificationTextView.setText("Classification: " + classification);
        foodDescriptionTextView.setText("Food Description: " + foodDescription);
        samplingDetailsTextView.setText("Sampling Details: " + samplingDetails);
    }
}