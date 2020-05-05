package com.example.cookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailActivity extends AppCompatActivity {

    TextView foodDescription,RecipeName,RecipeTime;
    ImageView foodImage;
    String key=" ";
    String imageUrl=" ";
    //private Object TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        RecipeName=(TextView) findViewById(R.id.txtRecipeName) ;
        RecipeTime=(TextView) findViewById(R.id.txtTime);

        foodDescription= (TextView)findViewById(R.id.txtDescription);
        foodImage=(ImageView)findViewById(R.id.ivImage1);


        Bundle mBundle=getIntent().getExtras();
        if(mBundle!=null){
            foodDescription.setText(mBundle.getString("Description"));
            key = mBundle.getString("keyValue");
            imageUrl=mBundle.getString("Image");
            RecipeName.setText(mBundle.getString("RecipeName"));
            RecipeTime.setText(mBundle.getString("time"));
            //foodImage.setImageResource(mBundle.getInt("Image"));

            Glide.with(this)
                    .load(mBundle.getString("Image"))
                    .into(foodImage);
        }
    }

    public void btnDeleteRecipe(View view) {

        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Recipe");
        FirebaseStorage storage=FirebaseStorage.getInstance();

        StorageReference storageReference=storage.getReferenceFromUrl(imageUrl);

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                reference.child(key).removeValue();
                Toast.makeText(DetailActivity.this, "Recipe Deleted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();



            }
        });

    }

    public void btnUpdateRecipe(View view) {

        startActivity(new Intent(getApplicationContext(),UpdateRecipeActivity.class)
        .putExtra("recipeNameKey",RecipeName.getText().toString())
        .putExtra("descriptionKey",foodDescription.getText().toString())
                .putExtra("timeKey",RecipeTime.getText().toString())
                        .putExtra("oldimageUrl",imageUrl)
                         .putExtra("key",key)


        );




    }
}
