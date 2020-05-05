package com.example.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // SYSTEM-made class
    RecyclerView mRecyclerView;
    List<FoodData> myFoodList;
    // Buffer object
    //FoodData mFoodData;
    // SYSTEM-made class
    ProgressDialog progressDialog;
    // SELF-made class
    MyAdapter myAdapter;
    // SYSTEM-made class
    EditText txt_Search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // SYSTEM-made class
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        txt_Search = (EditText) findViewById(R.id.txt_searchtext);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Items...");

        // !!
        myFoodList = new ArrayList<FoodData>();

//mFoodData=new FoodData("Празнични телешки пържоли","Можем да посрещаме всеки празник с телешки пържолки, приготвени по този начин ","90 мин./6 порции",
        //R.drawable.qstie1);
        //myFoodList.add(mFoodData);
        //mFoodData=new FoodData("Домашен козунак с джинджифил","Златни ръце и много желание - това са тайните съставки на най-вкусния козунак","120 мин./2 порции",
        //R.drawable.qstie2);
        //myFoodList.add(mFoodData);
        //mFoodData=new FoodData("Зелена салата с риба тон и маслини","Тази салата покрива всички критерии за съвършенство - хем е свежа, хем вкусна, хем много полезна!","20 мин./2 порции",
        //R.drawable.qstie3);
        //myFoodList.add(mFoodData);
        //mFoodData=new FoodData("Агнешки бут със зелен лук и гъби","И като ни каже някой кулинарна класика, веднага се сещаме за агнешко печено със зелен лук","200 мин./8 порции",
        //R.drawable.qstie4);
        //myFoodList.add(mFoodData);

        myAdapter = new MyAdapter(MainActivity.this, myFoodList);
        mRecyclerView.setAdapter(myAdapter);


        // SYSTEM-made class
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Recipe");

        progressDialog.show();

        // SYSTEM-made class
        ValueEventListener eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                myFoodList.clear();

                // Buffer object
                FoodData foodData;

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {

                    foodData = itemSnapshot.getValue(FoodData.class);

                    // !?
                    foodData.setKey(itemSnapshot.getKey());
                    // !?
                    myFoodList.add(foodData);


                }

                myAdapter.notifyDataSetChanged();
                progressDialog.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });


        txt_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                filter(s.toString());

            }
        });


    }

    private void filter(String text)
    {

        ArrayList<FoodData> filterList = new ArrayList<>();

        for (FoodData item : myFoodList) {
            // !!
            if (item.getItemName().toLowerCase().contains(text.toLowerCase())) {

                filterList.add(item);

           }
        }

        myAdapter.filteredList(filterList);
    }



    public void btn_uploadActivity(View view) {
        startActivity(new Intent(this,Upload_Recipe.class));
    }
}
