package comp5216.sydney.edu.au.freshfindau.page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp5216.sydney.edu.au.freshfindau.R;
import comp5216.sydney.edu.au.freshfindau.utils.NavigationUtils;

public class MarketActivity extends AppCompatActivity {

    private ListView listView;
    private List<String> storeNames = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Map<String, String> storeLocationMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        NavigationUtils.setupBottomNavigation(this, R.id.bottom_navigation_btn3);
        Toolbar toolbar = findViewById(R.id.market_back);

        listView = findViewById(R.id.listViewId);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storeNames);
        listView.setAdapter(adapter);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // restart MainActivity
                Intent intent = new Intent(MarketActivity.this, MarketActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        fetchStoreNamesFromFirestore();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedStoreName = adapter.getItem(position);
                String location = storeLocationMap.get(selectedStoreName);
                navigateToLocation(selectedStoreName, location);
            }
        });
    }

    private void fetchStoreNamesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("store").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    storeNames.clear();
                    storeLocationMap.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String storeName = document.getString("store_name");
                        String location = document.getString("location");
                        storeNames.add(storeName);
                        storeLocationMap.put(storeName, location);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.w("Firestore", "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void navigateToLocation(String storeName, String location) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location) + "(" + Uri.encode(storeName) + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Google Maps not installed", Toast.LENGTH_SHORT).show();
        }
    }
}