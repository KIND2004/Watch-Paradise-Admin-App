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
import lk.xrontech.watchparadiseadmin.linstener.OnItemClickListener;
import lk.xrontech.watchparadiseadmin.model.Product;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private ArrayList<Product> products;
    private Context context;
    private FirebaseStorage firebaseStorage;
    private OnItemClickListener onItemClickListener;

    public ProductListAdapter(ArrayList<Product> products, Context context,OnItemClickListener onItemClickListener) {
        this.products = products;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.firebaseStorage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.load_product_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.txtTitle.setText(product.getTitle());
        holder.txtPrice.setText("LKR. " + product.getPrice());
        holder.txtQuantity.setText("Qty: " + product.getQuantity());
        holder.txtDescription.setText(product.getDescription());
        holder.txtBrand.setText(product.getBrand().getName());
        if (product.getStatus() == true) {
            holder.txtStatus.setText("Active");
        } else {
            holder.txtStatus.setText("Deactive");
        }

        firebaseStorage.getReference("product-images/" + product.getImagePath())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso
                                .get()
                                .load(uri)
                                .resize(200, 200)
                                .centerCrop()
                                .into(holder.productImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtPrice, txtQuantity, txtDescription, txtBrand, txtStatus;
        ImageView productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.title);
            txtPrice = itemView.findViewById(R.id.price);
            txtQuantity = itemView.findViewById(R.id.quantity);
            txtDescription = itemView.findViewById(R.id.description);
            txtBrand = itemView.findViewById(R.id.brand);
            txtStatus = itemView.findViewById(R.id.status);

            productImage = itemView.findViewById(R.id.productImage);
        }
    }
}
