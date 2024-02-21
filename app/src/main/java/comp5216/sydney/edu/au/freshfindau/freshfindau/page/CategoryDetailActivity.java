package comp5216.sydney.edu.au.freshfindau.page;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import comp5216.sydney.edu.au.freshfindau.R;
import comp5216.sydney.edu.au.freshfindau.utils.NavigationUtils;

public class CategoryDetailActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private List<HashMap<String, String>> foodsInfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);
        NavigationUtils.setupBottomNavigation(this, R.id.bottom_navigation_btn2);
        Toolbar toolbar = findViewById(R.id.category_back);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // restart MainActivity
                Intent intent = new Intent(CategoryDetailActivity.this, EncyclopediaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        String category = intent.getStringExtra("category");

        TextView categoryTitleTextView = findViewById(R.id.tv_category_title);
        categoryTitleTextView.setText(category.substring(0, 1).toUpperCase() + category.substring(1));

        loadCategoryData(category);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.d("CategoryDetailActivity", "ListView item clicked: " + position);
            if(position < foodsInfo.size()) {
                // get click information
                HashMap<String, String> selectedFoodInfo = foodsInfo.get(position);

                // create Intent to jump to FoodDetailActivity
                Intent detailIntent = new Intent(CategoryDetailActivity.this, FoodDetailActivity.class);

                // put food information in Intent
                detailIntent.putExtra("Food Name", selectedFoodInfo.get("Food Name"));
                detailIntent.putExtra("Classification Name", selectedFoodInfo.get("Classification Name"));
                detailIntent.putExtra("Classification", selectedFoodInfo.get("Classification"));
                detailIntent.putExtra("Food Description", selectedFoodInfo.get("Food Description"));
                detailIntent.putExtra("Sampling Details", selectedFoodInfo.get("Sampling Details"));

                Log.d("CategoryDetailActivity", "Starting FoodDetailActivity with intent: " + detailIntent.toString());
                startActivity(detailIntent);
            } else {
                Log.w("CategoryDetailActivity", "Invalid position: " + position);
            }
        });
    }

    private void loadCategoryData(String category) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection(category);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        collectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("CategoryDetailActivity", "Listen failed.", e);
                    return;
                }

                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                adapter.clear();

                for (DocumentSnapshot doc : documents) {
                    Log.d("CategoryDetailActivity", "Document: " + doc.getData());
                    HashMap<String, String> foodInfo = new HashMap<>();
                    foodInfo.put("Food Name", doc.getString("Food Name"));
                    foodInfo.put("Classification Name", doc.getString("classficationName"));
                    foodInfo.put("Classification", doc.getString("classification"));
                    foodInfo.put("Food Description", doc.getString("Food Description"));
                    foodInfo.put("Sampling Details", doc.getString("Sampling Details"));
                    foodsInfo.add(foodInfo);
                    adapter.add(foodInfo.get("Food Name"));
                }
            }
        });
    }
}