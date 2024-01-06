package lk.xrontech.watchparadiseadmin.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lk.xrontech.watchparadiseadmin.activity.MainActivity;
import lk.xrontech.watchparadiseadmin.R;
import lk.xrontech.watchparadiseadmin.model.Brand;
import lk.xrontech.watchparadiseadmin.model.Product;

public class AddProductFragment extends Fragment {

    public static final String TAG = MainActivity.class.getName();
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private ImageButton imageAddProduct;
    private TextView txtProductTitle, txtProductPrice, txtProductQuantity, txtProductDescription;
    private Uri imagePath;
    private Spinner spinner;
    private ArrayAdapter<String> arrayAdapter;
    private List<Brand> brands;
    private String brandName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        imageAddProduct = view.findViewById(R.id.imageAddProduct);
        txtProductTitle = view.findViewById(R.id.txtProductTitle);
        txtProductPrice = view.findViewById(R.id.txtProductPrice);
        txtProductQuantity = view.findViewById(R.id.txtProductQuantity);
        txtProductDescription = view.findViewById(R.id.txtProductDescription);

        spinner = view.findViewById(R.id.spinnerProductBrand);
        brands = new ArrayList<>();

        imageAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                intentActivityResultLauncher.launch(Intent.createChooser(intent, "Select Product Image"));
            }
        });

        firebaseFirestore.collection("brands")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Brand brand = document.toObject(Brand.class);
                                brands.add(brand);
                            }

                            List<String> brandNames = new ArrayList<>();
                            for (Brand item : brands) {
                                brandNames.add(item.getName());
                            }

                            arrayAdapter = new ArrayAdapter<>(getContext(),
                                    android.R.layout.simple_spinner_item, brandNames);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(arrayAdapter);

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                    Brand selectedItem = brands.get(position);
                                    brandName = selectedItem.getName();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                }
                            });
                        }
                    }
                });

        view.findViewById(R.id.btnAddProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtProductTitle.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please Enter Product Title", Toast.LENGTH_SHORT).show();
                } else if (String.valueOf(txtProductPrice.getText()).equals("")) {
                    Toast.makeText(getContext(), "Please Enter Product Price", Toast.LENGTH_SHORT).show();
                } else if (String.valueOf(txtProductPrice.getText()).equals("0")) {
                    Toast.makeText(getContext(), "Invalid Product Price", Toast.LENGTH_SHORT).show();
                } else if (String.valueOf(txtProductQuantity.getText()).equals("")) {
                    Toast.makeText(getContext(), "Please Enter Product Quantity", Toast.LENGTH_SHORT).show();
                } else if (String.valueOf(txtProductQuantity.getText()).equals("0")) {
                    Toast.makeText(getContext(), "Invalid Product Quantity", Toast.LENGTH_SHORT).show();
                } else if (txtProductDescription.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please Enter Product Description", Toast.LENGTH_SHORT).show();
                } else {

                    view.findViewById(R.id.btnAddProduct).setVisibility(View.INVISIBLE);

                    String productTitle = txtProductTitle.getText().toString();
                    Double productPrice = Double.parseDouble(txtProductPrice.getText().toString());
                    Integer productQuantity = Integer.parseInt(txtProductQuantity.getText().toString());
                    String productDescription = txtProductDescription.getText().toString();

                    String imageUri = UUID.randomUUID().toString();

                    Product product = new Product();
                    product.setImagePath(imageUri);
                    product.setTitle(productTitle);
                    product.setPrice(productPrice);
                    product.setQuantity(productQuantity);
                    product.setDescription(productDescription);
                    product.setStatus(true);
                    product.setBrand(new Brand(brandName));

                    firebaseFirestore.collection("products")
                            .add(product)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    if (imagePath != null) {
                                        StorageReference reference = firebaseStorage.getReference("product-images")
                                                .child(imageUri);
                                        reference.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Picasso
                                                        .get()
                                                        .load(R.drawable.image)
                                                        .fit()
                                                        .into(imageAddProduct);
                                                txtProductTitle.setText(null);
                                                txtProductPrice.setText(null);
                                                txtProductQuantity.setText(null);
                                                txtProductDescription.setText(null);
                                                view.findViewById(R.id.btnAddProduct).setVisibility(View.VISIBLE);
                                                Toast.makeText(getContext(), "Product Added Successfully!", Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Failed");
                                }
                            });
                    
                }

            }
        });
    }

    ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                imagePath = result.getData().getData();

                Log.i(TAG, imagePath.getPath());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), imagePath);
                    try {
                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                        imageAddProduct.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                } else {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imagePath);
                        imageAddProduct.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }

                Picasso.get()
                        .load(imagePath)
                        .fit()
                        .into(imageAddProduct);
            }
        }
    });
}