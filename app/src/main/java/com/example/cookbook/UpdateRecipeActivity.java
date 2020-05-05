package com.example.cookbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public abstract class UpdateRecipeActivity extends AppCompatActivity {

    ImageView recipeImage;
    Uri uri;
    EditText txt_name,txt_description,txt_time;
    String imageUrl;
    String key,oldImageUrl;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String recipeName,recipeDescription,recipeTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recipe);
        final ProgressDialog progressDialog=new ProgressDialog(this);

        recipeImage=(ImageView)findViewById(R.id.iv_foodImage);
        txt_name=(EditText)findViewById(R.id.txt_recipe_name);
        txt_description=(EditText)findViewById(R.id.text_description);
        txt_time=(EditText)findViewById(R.id.text_time);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){

            Glide.with(UpdateRecipeActivity.this)
                    .load(bundle.getString("oldimageUrl"))
                    .into(recipeImage);
            txt_name.setText(bundle.getString("recipeNameKey"));
            txt_description.setText(bundle.getString("descriptionKey"));
            txt_time.setText(bundle.getString("timeKey"));
            key=bundle.getString("key");
            oldImageUrl=bundle.getString("oldimageUrl");
        }



        databaseReference= FirebaseDatabase.getInstance().getReference("Recipe").child(key);


        Button button=findViewById(R.id.Updt_Buttn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txt_name.getText().equals(null))
                    recipeName=txt_name.getText().toString().trim();
                if(!txt_description.getText().equals(null))
                    recipeDescription=txt_description.getText().toString().trim();
                if(!txt_time.getText().equals(null))
                    recipeTime=txt_time.getText().toString();

                progressDialog.setMessage("Recipe Uploading...");
                progressDialog.show();
               storageReference=FirebaseStorage.getInstance().getReference().child("RecipeImage").child(uri.getLastPathSegment());

                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isComplete());
                        Uri urlImage=uriTask.getResult();
                        //assert urlImage != null;
                        imageUrl=urlImage.toString();
                        uUploadRecipe();
                        progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });

            }
        });


    }

    public void uBtnSelectImage(View view) {
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        startActivityForResult(photoPicker,1);
    }
/*
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            uri = data.getData();
            recipeImage.setImageURI(uri);


        }

        else Toast.makeText(this,"You haven't picked image",Toast.LENGTH_SHORT).show();

    }


    public void uBtnUpdateRecipe(View view) {

        if(!txt_name.getText().equals(null))
        recipeName=txt_name.getText().toString().trim();
        if(!txt_description.getText().equals(null))
        recipeDescription=txt_description.getText().toString().trim();
        if(!txt_time.getText().equals(null))
        recipeTime=txt_time.getText().toString();


        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Recipe Uploading...");
        progressDialog.show();
        storageReference=FirebaseStorage.getInstance()
                .getReference().child("RecipeImage").child(uri.getLastPathSegment());

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isComplete());
                Uri urlImage=uriTask.getResult();
                //assert urlImage != null;
                imageUrl=urlImage.toString();
                uUploadRecipe();
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });


    }
*/

    public void uUploadRecipe(){

        FoodData foodData = new FoodData(
                //txt_name.getText().toString(),
                //txt_description.getText().toString(),
                //txt_time.getText().toString(),
                //imageUrl
                recipeName,
                recipeDescription,
                recipeTime,
                imageUrl
        );


        databaseReference.setValue(foodData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                StorageReference storageReferenceNew = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
                storageReferenceNew.delete();
                Toast.makeText(UpdateRecipeActivity.this, "Data Updated", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
