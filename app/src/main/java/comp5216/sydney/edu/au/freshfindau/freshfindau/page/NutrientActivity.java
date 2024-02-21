package comp5216.sydney.edu.au.freshfindau.page;

import static androidx.constraintlayout.widget.Constraints.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import comp5216.sydney.edu.au.freshfindau.R;
import comp5216.sydney.edu.au.freshfindau.utils.NavigationUtils;

public class NutrientActivity extends AppCompatActivity {

    private ListView nutrient_lv;
    private ArrayList<String> nutrient;
    private ArrayAdapter<String> nutrient_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrient);
        //Set Listview
        nutrient_lv = findViewById(R.id.nutrient_lv);
        nutrient = new ArrayList<String>();
        nutrient_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,nutrient);
        nutrient_lv.setAdapter(nutrient_adapter);

        Map<String, String> fieldMapping = new HashMap<>();

        nutrient.add("Energy with dietary fibre (kJ)");
        fieldMapping.put("Energy with dietary fibre (kJ)",
                "energyWithDietaryFibreEquatedKJ");
        nutrient.add("Protein (g)");
        fieldMapping.put("Protein (g)", "proteinG");
        nutrient.add("Dietary fibre (g)");
        fieldMapping.put("Dietary fibre (g)", "dietaryFibreG");
        nutrient.add("Fat (g)");
        fieldMapping.put("Fat (g)", "fatG");
        nutrient.add("Sugars (g)");
        fieldMapping.put("Sugars (g)", "sugarsG");
        nutrient.add("Available carbohydrate, without sugar alcohols (g)");
        fieldMapping.put("Available carbohydrate, without sugar alcohols (g)",
                "availableCarbohydrateWithoutSugarAlcoholsG");
        nutrient.add("Vitamin A retinol equivalents (ug)");
        fieldMapping.put("Vitamin A retinol equivalents (ug)", "vitaminARetinolEquivalentsUg");
        nutrient.add("Thiamin (B1) (mg)");
        fieldMapping.put("Thiamin (B1) (mg)", "thiaminB1Mg");
        nutrient.add("Riboflavin (B2) (mg)");
        fieldMapping.put("Riboflavin (B2) (mg)", "riboflavinB2Mg");
        nutrient.add("Niacin (B3) (mg)");
        fieldMapping.put("Niacin (B3) (mg)", "niacinB3Mg");
        nutrient.add("Pyridoxine (B6) (mg)");
        fieldMapping.put("Pyridoxine (B6) (mg)", "pyridoxineB6Mg");
        nutrient.add("Folates (ug)");
        fieldMapping.put("Folates (ug)", "folatesUg");
        nutrient.add("Vitamin C (mg)");
        fieldMapping.put("Vitamin C (mg)", "vitaminCMg");
        nutrient.add("Vitamin E (mg)");
        fieldMapping.put("Vitamin E (mg)", "vitaminEMg");

        nutrient_adapter.notifyDataSetChanged();

        Toolbar mytoolbar = (Toolbar) findViewById(R.id.nutrient_back);
        setSupportActionBar(mytoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mytoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NutrientActivity.this, EncyclopediaActivity.class);
                startActivity(intent);
            }
        });

        nutrient_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedField = nutrient.get(position);
                String actualField = fieldMapping.get(selectedField);
                Intent intent = new Intent(NutrientActivity.this, RankingActivity.class);
                intent.putExtra("actualField", actualField);
                intent.putExtra("selectedField", selectedField);
                startActivity(intent);
            }
        });
    }
}