package kgk.beacon.view.general.adapter;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import kgk.beacon.R;
import kgk.beacon.model.product.Product;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder>
        implements RecyclerView.OnItemTouchListener {

    public interface OnItemClickListener {

        void onClick(Product product);
    }

    ////

    private OnItemClickListener listener;
    private GestureDetector gestureDetector;

    private List<Product> products;

    ////

    public ProductListAdapter(Context context, List<Product> products,
                              OnItemClickListener listener) {
        this.listener = listener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        this.products = products;
    }

    ////

    @Override
    public ProductListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Product product = products.get(position);
        viewHolder.productTitle.setText(product.getTitle());
        viewHolder.productImage.setImageDrawable(product.getImage());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    ////

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent motionEvent) {
        View itemView = rv.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

        if (itemView != null) {
            CardView cardView = (CardView) itemView.findViewById(R.id.product_item_view_card);

            if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                cardView.setCardElevation(16.0f);
            } else if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_UP) {
                cardView.setCardElevation(2.0f);
            }

            if (itemView != null && listener != null && gestureDetector.onTouchEvent(motionEvent)) {
                listener.onClick(products.get(rv.getChildAdapterPosition(itemView)));
            }
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }


    ////

    public static class ViewHolder extends  RecyclerView.ViewHolder {

        public TextView productTitle;
        public ImageView productImage;

        ////

        public ViewHolder(View itemView) {
            super(itemView);
            productTitle = (TextView) itemView.findViewById(R.id.productTitle);
            productImage = (ImageView) itemView.findViewById(R.id.productImage);
        }
    }
}
