package com.example.hp.grocerz;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public  class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<Product> mInventory;
    private LayoutInflater mlayoutInflater;
    private ArrayList<String> idList ;

    public InventoryAdapter(Context mContext, ArrayList<Product> mInventory , ArrayList<String> id) {
        this.mContext = mContext;
        mlayoutInflater = LayoutInflater.from(mContext);
        this.mInventory = mInventory;
        idList = id ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mlayoutInflater.inflate(R.layout.inventory_item,viewGroup,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Product inventoryItem = mInventory.get(i);
        viewHolder.itemName.setText(inventoryItem.name);
        viewHolder.itemPrice.setText("" + inventoryItem.price);
        viewHolder.itemQty.setText("" + inventoryItem.quantity);
        viewHolder.itemId.setText("" + idList.get(i));
    }

    @Override
    public int getItemCount() {
        return mInventory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView itemName;
        private final TextView itemPrice;
        private final TextView itemId;
        private final TextView itemQty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.inventory_product_name);
            itemPrice = itemView.findViewById(R.id.inventory_product_price);
            itemId = itemView.findViewById(R.id.inventory_product_id);
            itemQty = itemView.findViewById(R.id.inventory_product_qty);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext , GenerateProductQR.class);
                    i.putExtra("productId",itemId.getText().toString());
                    mContext.startActivity(i);
                }
            });
        }
    }
}
