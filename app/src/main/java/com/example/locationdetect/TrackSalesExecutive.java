package com.example.locationdetect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrackSalesExecutive extends AppCompatActivity {
    private ListView userListView;
    private ArrayAdapter<String> listAdapter;
    private List<String> userList;

    private TextView selectedUserNameTextView;

    private SearchView searchView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_sales_executive);

        searchView = findViewById(R.id.searchView);
        ListView listView = findViewById(R.id.list);
        userListView = findViewById(R.id.list);
        userList = new ArrayList<>();
        listAdapter = new CustomAdapter(this, userList);
        userListView.setAdapter(listAdapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the list when the user types
                listAdapter.getFilter().filter(newText);
                return false;
            }
        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = userList.get(position);
                String selectedUid = extractUidFromSelectedItem(selectedItem);

                Intent intent = new Intent(TrackSalesExecutive.this, MapsActivity.class);
                intent.putExtra("selectedUid", selectedUid);
                startActivity(intent);
            }
        });

        fetchUserList();
    }


    private void fetchUserList() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    String userName = snapshot.child("username").getValue(String.class);
                    if (uid != null && userName != null) {
                        userList.add(userName + " (UID: " + uid + ")");
                    }
                }

                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TrackSalesExecutive.this, "Error fetching user list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String extractUidFromSelectedItem(String selectedItem) {
        int start = selectedItem.lastIndexOf("(") + 5;
        int end = selectedItem.lastIndexOf(")");

        if (start >= 0 && end >= 0 && end > start) {
            return selectedItem.substring(start, end).trim();
        } else {
            return "";
        }
    }
}