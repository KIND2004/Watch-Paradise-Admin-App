package lk.xrontech.watchparadiseadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import lk.xrontech.watchparadiseadmin.R;
import lk.xrontech.watchparadiseadmin.model.Brand;

public class BrandListAdapter extends RecyclerView.Adapter<BrandListAdapter.ViewHolder> {

    private ArrayList<Brand> brands;
    private Context context;

    public BrandListAdapter(ArrayList<Brand> brands, Context context) {
        this.brands = brands;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.load_brand_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Brand brand = brands.get(position);
        holder.txtBrandName.setText(brand.getName());
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtBrandName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBrandName = itemView.findViewById(R.id.txtBrandName);
        }
    }
}