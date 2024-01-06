package lk.xrontech.watchparadiseadmin.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.xrontech.watchparadiseadmin.R;
import lk.xrontech.watchparadiseadmin.activity.AuthActivity;
import lk.xrontech.watchparadiseadmin.activity.MainActivity;
import lk.xrontech.watchparadiseadmin.model.Brand;
import lk.xrontech.watchparadiseadmin.model.Product;
import lk.xrontech.watchparadiseadmin.model.User;

public class HomeFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private List<Product> products;
    private List<User> users;
    private List<Brand> brands;
    private TextView totalProducts, totalUsers, totalBrands;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();

        products = new ArrayList<>();
        users = new ArrayList<>();
        brands = new ArrayList<>();

        totalProducts = view.findViewById(R.id.totalProducts);
        totalUsers = view.findViewById(R.id.totalUsers);
        totalBrands = view.findViewById(R.id.totalBrands);

        firebaseFirestore.collection("products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        products = value.toObjects(Product.class);
                        totalProducts.setText("Total Products " + products.size());
                    }
                });

        firebaseFirestore.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        users = value.toObjects(User.class);
                        totalUsers.setText("Total Users " + users.size());
                    }
                });

        firebaseFirestore.collection("brands")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        brands = value.toObjects(Brand.class);
                        totalBrands.setText("Total Brands " + brands.size());
                    }
                });
    }
}