package com.example.hp.grocerz;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.Transaction;

import java.util.ArrayList;

public  class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private final Context mContext;
    private ArrayList<Product> mInventory=null;
    private ArrayList<Transactions> mTransactions=null;
    private int flag=0;
    private LayoutInflater mlayoutInflater;
    private ArrayList<String> idList ;

    public InventoryAdapter(Context mContext, ArrayList<Product> mInventory, ArrayList<String> id) {
        this.mContext = mContext;
        mlayoutInflater = LayoutInflater.from(mContext);
        this.mInventory = mInventory;
        idList = id ;
    }

    public InventoryAdapter(Context mContext, ArrayList<Transactions> mTransactions, ArrayList<String> id, int flag) {
        this.mContext = mContext;
        mlayoutInflater = LayoutInflater.from(mContext);
        this.mTransactions = mTransactions;
        idList = id ;
        this.flag = flag ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mlayoutInflater.inflate(R.layout.inventory_item,viewGroup,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if(flag==0) {
            Product inventoryItem = mInventory.get(i);
            viewHolder.itemName.setText(inventoryItem.name);
            viewHolder.itemPrice.setText("" + inventoryItem.price);
            viewHolder.itemQty.setText("" + inventoryItem.quantity);
            viewHolder.itemId.setText("" + idList.get(i));
        }else{
            Transactions transactionItem = mTransactions.get(i);
            viewHolder.itemName.setText("TId : " + idList.get(i));
            viewHolder.itemIdLabel.setText("UserId: ");
            viewHolder.itemQtyLabel.setText("Details: ");
            viewHolder.itemPriceLabel.setText("Date: ");
            viewHolder.itemPrice.setText("" + transactionItem.date);
            viewHolder.itemQty.setText("" + transactionItem.details);
            viewHolder.itemId.setText("" + transactionItem.userId);
        }
    }

    @Override
    public int getItemCount() {
        return((flag==0)?mInventory.size():mTransactions.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView itemName;
        private final TextView itemPrice;
        private final TextView itemPriceLabel;
        private final TextView itemId;
        private final TextView itemIdLabel;
        private final TextView itemQty;
        private final TextView itemQtyLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.cart_prod_names);
            itemPrice = itemView.findViewById(R.id.inventory_product_price);
            itemPriceLabel = itemView.findViewById(R.id.invN4);
            itemId = itemView.findViewById(R.id.inventory_product_id);
            itemIdLabel = itemView.findViewById(R.id.invN);
            itemQty = itemView.findViewById(R.id.inventory_product_qty);
            itemQtyLabel = itemView.findViewById(R.id.invN3);
            if(flag!=1) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, GenerateProductQR.class);
                        i.putExtra("productId", itemId.getText().toString());
                        mContext.startActivity(i);
                    }
                });
            }
        }
    }
}
