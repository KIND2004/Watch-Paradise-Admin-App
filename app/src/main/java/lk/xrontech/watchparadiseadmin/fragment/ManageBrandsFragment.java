package lk.xrontech.watchparadiseadmin.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import lk.xrontech.watchparadiseadmin.R;
import lk.xrontech.watchparadiseadmin.adapter.BrandListAdapter;
import lk.xrontech.watchparadiseadmin.model.Brand;

public class ManageBrandsFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Brand> brands;
    TextView txtAddBrand;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_brands, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();

        brands = new ArrayList<>();

        RecyclerView brandList = view.findViewById(R.id.brandList);

        txtAddBrand = view.findViewById(R.id.txtAddBrand);

        BrandListAdapter brandListAdapter = new BrandListAdapter(brands, getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        brandList.setLayoutManager(gridLayoutManager);
        brandList.setAdapter(brandListAdapter);

        firebaseFirestore.collection("brands")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        brands.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Brand brand = snapshot.toObject(Brand.class);
                            brands.add(brand);
                        }
                        brandListAdapter.notifyDataSetChanged();
                    }
                });

        view.findViewById(R.id.btnAddNewBrand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.addBrandDesign).setVisibility(View.VISIBLE);
            }
        });

        view.findViewById(R.id.btnAddBrand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String brandName = String.valueOf(txtAddBrand.getText());
                Brand brand = new Brand(brandName);
                firebaseFirestore.collection("brands")
                        .add(brand)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        txtAddBrand.setText(null);
                                        view.findViewById(R.id.addBrandDesign).setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "New Brand Added Successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        txtAddBrand.setText(null);
                                        view.findViewById(R.id.addBrandDesign).setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "New Brand Added Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            }
        });

    }
}