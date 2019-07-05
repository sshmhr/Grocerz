package com.example.hp.grocerz;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

class CartItemAdapter  extends ResourceCursorAdapter {
    public CartItemAdapter(Context context, int layout, Cursor cursor, int flags) {
        super(context, layout, cursor, flags);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView prodName = view.findViewById(R.id.cart_prod_names);
        TextView prodQty = view.findViewById(R.id.cart_prod_qty);
        TextView prodKey = view.findViewById(R.id.hiddentv);
        TextView prodPrice = view.findViewById(R.id.cart_prod_price);
        TextView prodTotal = view.findViewById(R.id.total);
        prodName.setText(cursor.getString(2));
        prodPrice.setText(cursor.getString(5));
        prodQty.setText(cursor.getString(3));
        prodKey.setText(cursor.getString(1));
        prodTotal.setText(cursor.getString(4));
        FloatingActionButton add = view.findViewById(R.id.cart_add_fab);
        final Context a = context ;

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = mAuth.getCurrentUser();
        final SQLiteDatabase db  = a.openOrCreateDatabase("testProducts2",Context.MODE_PRIVATE,null);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView prod =  ((ConstraintLayout) v.getParent()).findViewById(R.id.hiddentv);
                TextView prodTotal =((ConstraintLayout) v.getParent()).findViewById(R.id.total);
                TextView prodCount = ((ConstraintLayout) v.getParent()).findViewById(R.id.cart_prod_qty);
                String productId = prod.getText().toString();
                String total = prodTotal.getText().toString();
                String count = prodCount.getText().toString();
                if(Integer.parseInt(count) + 1 > Integer.parseInt(total))
                    Toast.makeText(a, "cannot add more Store Limit Reached", Toast.LENGTH_SHORT).show();
                else {
                    db.execSQL("update cart" + mUser.getUid() + " set count = count + 1 where prodId='" + productId + "';");
                    Cursor c = db.rawQuery("Select rowid _id,* ,1 as z from cart" + mUser.getUid() + ";", null);
                    swapCursor(c);
                }
            }
        });

        FloatingActionButton sub = view.findViewById(R.id.cart_subtract_fab);
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView prod = ((ConstraintLayout) v.getParent()).findViewById(R.id.hiddentv);
                TextView prodCount = ((ConstraintLayout) v.getParent()).findViewById(R.id.cart_prod_qty);
                String productId = prod.getText().toString();
                String count = prodCount.getText().toString();
                if(Integer.parseInt(count) - 1 <= 0) {
                    db.execSQL("delete from cart" + mUser.getUid() + " where prodId='" + productId + "';");
                    Toast.makeText(a,((TextView)((ConstraintLayout) v.getParent()).findViewById(R.id.cart_prod_names)).getText().toString() +  " is Deleted from the cart", Toast.LENGTH_SHORT).show();
                }
                else
                    db.execSQL("update cart" + mUser.getUid() + " set count = count - 1 where prodId='" + productId + "';");
                Cursor c = db.rawQuery("Select rowid _id,* from cart" + mUser.getUid() + ";", null);
                swapCursor(c);
            }
        });

        FloatingActionButton del = view.findViewById(R.id.cart_delete_fab);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView prod =((ConstraintLayout) v.getParent()).findViewById(R.id.hiddentv);
                String productId = prod.getText().toString();
                db.execSQL("delete from cart"+mUser.getUid()+" where prodId='" + productId + "';");
                Toast.makeText(a,((TextView)((ConstraintLayout) v.getParent()).findViewById(R.id.cart_prod_names)).getText().toString() +  " is Deleted from the cart", Toast.LENGTH_SHORT).show();
                Cursor c = db.rawQuery("Select rowid _id,* from cart"+mUser.getUid()+";",null) ;
                swapCursor(c);
            }
        });

    }
}
