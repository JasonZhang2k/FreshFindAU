package comp5216.sydney.edu.au.freshfindau.page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import comp5216.sydney.edu.au.freshfindau.R;

public class RankingActivity extends AppCompatActivity {

    private ListView food_lv;
    private ArrayList<String> food;
    private ArrayAdapter<String> food_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        String actualField = getIntent().getStringExtra("actualField");

        if(actualField == null) {
            Log.e("RankingActivity", "actualField is null!");
            // Handle error: finish activity, show error message, etc.
            return;
        }
        String selectedField = getIntent().getStringExtra("selectedField");

        food_lv = findViewById(R.id.food_lv);
        food = new ArrayList<>();
        food_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, food);
        food_lv.setAdapter(food_adapter);
        food.add(selectedField+" & "+"Food Name");

        FirebaseFirestore foodInfo = FirebaseFirestore.getInstance();
        foodInfo.collection("foodInfo").orderBy(actualField, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("RankingActivity", "Query successful.");
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d("RankingActivity", "Processing document: " + document.getId());
                            // 获取并显示数据
                            Double fieldValue = document.getDouble(actualField);
                            String foodValue = document.getString("foodName");

                            Log.d("RankingActivity", "Retrieved values: " + fieldValue + ", " + foodValue);

                            if (fieldValue != null && foodValue != null) {
                                food.add(fieldValue + "     " + foodValue);
                            } else {
                                Log.d("RankingActivity", "Null data found!");
                            }
                        }
                        food_adapter.notifyDataSetChanged();
                    } else {
                        Log.w("RankingActivity", "Error getting documents: ", task.getException());
                    }
                });

        Toolbar mytoolbar = (Toolbar) findViewById(R.id.ranking_back);
        setSupportActionBar(mytoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mytoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RankingActivity.this, NutrientActivity.class);
                startActivity(intent);
            }
        });
    }
}