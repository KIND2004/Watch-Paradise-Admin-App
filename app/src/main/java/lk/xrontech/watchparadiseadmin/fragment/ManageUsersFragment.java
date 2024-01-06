package lk.xrontech.watchparadiseadmin.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import lk.xrontech.watchparadiseadmin.R;
import lk.xrontech.watchparadiseadmin.adapter.ProductListAdapter;
import lk.xrontech.watchparadiseadmin.adapter.UserListAdapter;
import lk.xrontech.watchparadiseadmin.model.Product;
import lk.xrontech.watchparadiseadmin.model.User;

public class ManageUsersFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private ArrayList<User> users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();

        users = new ArrayList<>();

        RecyclerView productList = view.findViewById(R.id.userList);

        UserListAdapter userListAdapter = new UserListAdapter(users, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        productList.setLayoutManager(linearLayoutManager);
        productList.setAdapter(userListAdapter);

        firebaseFirestore.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        users.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            User user = snapshot.toObject(User.class);
                            users.add(user);
                        }
                        userListAdapter.notifyDataSetChanged();
                    }
                });
    }
}