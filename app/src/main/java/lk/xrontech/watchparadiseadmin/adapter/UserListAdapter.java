package lk.xrontech.watchparadiseadmin.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.xrontech.watchparadiseadmin.R;
import lk.xrontech.watchparadiseadmin.model.Product;
import lk.xrontech.watchparadiseadmin.model.User;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private ArrayList<User> users;
    private Context context;
    private FirebaseStorage firebaseStorage;

    public UserListAdapter(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
        this.firebaseStorage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.load_user_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.fullName.setText(user.getFullName());
        holder.email.setText(user.getEmail());
        holder.addressNo.setText(user.getAddressNo());
        holder.streetName.setText(user.getStreetName());
        holder.city.setText(user.getCity());
        holder.zipCode.setText(user.getZipCode());
        if (user.getStatus() == true) {
            holder.userStatus.setText("Active");
        } else {
            holder.userStatus.setText("Deactive");
        }

        firebaseStorage.getReference("user-profile-images/" + user.getProfileUri())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso
                                .get()
                                .load(uri)
                                .resize(100, 100)
                                .centerCrop()
                                .into(holder.userImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, email, addressNo, streetName, city, zipCode, userStatus;
        ImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.fullName);
            email = itemView.findViewById(R.id.email);
            addressNo = itemView.findViewById(R.id.addressNo);
            streetName = itemView.findViewById(R.id.streetName);
            city = itemView.findViewById(R.id.city);
            zipCode = itemView.findViewById(R.id.zipCode);
            userStatus = itemView.findViewById(R.id.userStatus);

            userImage = itemView.findViewById(R.id.userImage);
        }
    }
}