package lk.xrontech.watchparadiseadmin.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import lk.xrontech.watchparadiseadmin.R;
import lk.xrontech.watchparadiseadmin.adapter.ProductListAdapter;
import lk.xrontech.watchparadiseadmin.linstener.OnItemClickListener;
import lk.xrontech.watchparadiseadmin.model.Product;

public class ManageProductsFragment extends Fragment implements OnItemClickListener {
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Product> products;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();

        products = new ArrayList<>();

        RecyclerView productList = view.findViewById(R.id.productList);

        ProductListAdapter productListAdapter = new ProductListAdapter(products, getContext(), this::onItemClick);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        productList.setLayoutManager(linearLayoutManager);
        productList.setAdapter(productListAdapter);

        firebaseFirestore.collection("products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        products.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            String documentId = snapshot.getId();
                            Product product = snapshot.toObject(Product.class);
                            product.setDocumentId(documentId);
                            products.add(product);
                        }
                        productListAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        UpdateProductFragment updateProductFragment = new UpdateProductFragment();
        Bundle bundle = new Bundle();
        bundle.putString("documentId", products.get(position).getDocumentId());
        updateProductFragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, updateProductFragment);
        transaction.commit();
    }
}