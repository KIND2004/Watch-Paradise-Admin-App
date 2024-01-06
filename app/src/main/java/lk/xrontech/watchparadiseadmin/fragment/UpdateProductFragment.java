package lk.xrontech.watchparadiseadmin.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

import lk.xrontech.watchparadiseadmin.R;
import lk.xrontech.watchparadiseadmin.model.Admin;
import lk.xrontech.watchparadiseadmin.model.Brand;
import lk.xrontech.watchparadiseadmin.model.Product;

public class UpdateProductFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private EditText txtUpdateProductTitle, txtUpdateProductPrice, txtUpdateProductQuantity, txtUpdateProductDescription;
    private TextView txtUpdateProductBrand;
    private ImageButton imageProduct;
    private Uri productImageUri;
    private Product product;
    private String documentId;
    private String productImageId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            documentId = bundle.getString("documentId");

            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseStorage = FirebaseStorage.getInstance();

            txtUpdateProductTitle = view.findViewById(R.id.txtUpdateProductTitle);
            txtUpdateProductPrice = view.findViewById(R.id.txtUpdateProductPrice);
            txtUpdateProductQuantity = view.findViewById(R.id.txtUpdateProductQuantity);
            txtUpdateProductDescription = view.findViewById(R.id.txtUpdateProductDescription);

            txtUpdateProductBrand = view.findViewById(R.id.txtUpdateProductBrand);

            imageProduct = view.findViewById(R.id.imageProduct);

            imageProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    intentActivityResultLauncher.launch(Intent.createChooser(intent, "Select Product Image"));
                }
            });

            firebaseFirestore.collection("products")
                    .document(documentId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                product = snapshot.toObject(Product.class);
                                if (product != null) {
                                    txtUpdateProductTitle.setText(product.getTitle());
                                    txtUpdateProductBrand.setText(product.getBrand().getName());
                                    txtUpdateProductPrice.setText(String.valueOf(product.getPrice()));
                                    txtUpdateProductQuantity.setText(String.valueOf(product.getQuantity()));
                                    txtUpdateProductDescription.setText(product.getDescription());

                                    firebaseStorage.getReference("product-images/" + product.getImagePath())
                                            .getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    if (uri != null) {
                                                        Picasso.get()
                                                                .load(uri)
                                                                .fit()
                                                                .into(imageProduct);
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            }
                        }
                    });

            view.findViewById(R.id.btnUpdateProduct).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String productTitle = txtUpdateProductTitle.getText().toString();
                    String productBrand = txtUpdateProductBrand.getText().toString();
                    Double productPrice = Double.parseDouble(String.valueOf(txtUpdateProductPrice.getText()));
                    Integer productQuantity = Integer.parseInt(String.valueOf(txtUpdateProductQuantity.getText()));
                    String productDescription = txtUpdateProductDescription.getText().toString();

                    if (productTitle.equals("")) {
                        Toast.makeText(getContext(), "Please Enter Product Title", Toast.LENGTH_SHORT).show();
                    } else if (productBrand.equals("")) {
                        Toast.makeText(getContext(), "Please Enter Product Brand", Toast.LENGTH_SHORT).show();
                    } else if (String.valueOf(productPrice).equals("")) {
                        Toast.makeText(getContext(), "Please Enter Product Price", Toast.LENGTH_SHORT).show();
                    } else if (String.valueOf(productQuantity).equals("")) {
                        Toast.makeText(getContext(), "Please Enter Product Quantity", Toast.LENGTH_SHORT).show();
                    } else if (productDescription.equals("")) {
                        Toast.makeText(getContext(), "Please Enter Product Description", Toast.LENGTH_SHORT).show();
                    } else {

                        product.setTitle(productTitle);
                        product.setBrand(new Brand(productBrand));

                        if (productImageId != null) {
                            product.setImagePath(productImageId);
                        }

                        product.setPrice(productPrice);
                        product.setQuantity(productQuantity);
                        product.setDescription(productDescription);

                        if (documentId != null) {

                            firebaseFirestore.collection("products")
                                    .document(documentId)
                                    .set(product)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            if (productImageUri != null) {
                                                StorageReference reference = firebaseStorage.getReference("product-images")
                                                        .child(productImageId);
                                                reference.putFile(productImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        Toast.makeText(getContext(), "Product Updated Success", Toast.LENGTH_SHORT).show();

                                                        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                                                        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
                                                        fragmentTransaction.replace(R.id.container, new ManageProductsFragment());
                                                        fragmentTransaction.commit();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            } else {
                                                FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                                                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
                                                fragmentTransaction.replace(R.id.container, new ManageProductsFragment());
                                                fragmentTransaction.commit();
                                            }

                                            Toast.makeText(getContext(), "Product Details Updated Success", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(getActivity(), "Something Went Wrong!", Toast.LENGTH_LONG).show();
                        }

                    }

                }
            });

        }
    }

    ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                productImageUri = result.getData().getData();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), productImageUri);
                    try {
                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                        imageProduct.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Failed to Select Image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), productImageUri);
                        imageProduct.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Failed to Select Image", Toast.LENGTH_SHORT).show();
                    }
                }

                Picasso.get()
                        .load(productImageUri)
                        .fit()
                        .into(imageProduct);

                productImageId = UUID.randomUUID().toString();
            }
        }
    });
}