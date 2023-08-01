package vn.hellosoft.hellorent.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import vn.hellosoft.app.AppController;
import vn.hellosoft.app.Config;
import vn.hellosoft.hellorent.R;
import vn.hellosoft.hellorent.activities.ProductDetailsActivity;
import vn.hellosoft.hellorent.adapters.ProductCollectionAdapter;
import vn.hellosoft.hellorent.callbacks.OnRecyclerTouchListener;
import vn.hellosoft.hellorent.callbacks.RecyclerTouchListner;
import vn.hellosoft.hellorent.model.Product;
import vn.hellosoft.ui.DividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductViewFragment extends Fragment {


    private RecyclerView recyclerViewItems;
    private RelativeLayout resultLayout;
    private List<Product> productList;
    private ProductCollectionAdapter adapter;

    public ProductViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_product_view, container, false);

        recyclerViewItems = (RecyclerView) root.findViewById(R.id.recyclerViewItems);
        resultLayout = (RelativeLayout) root.findViewById(R.id.resultLayout);
        resultLayout.setVisibility(View.GONE);

        productList = new ArrayList<>();
        adapter = new ProductCollectionAdapter(productList);

        recyclerViewItems.setItemAnimator(new DefaultItemAnimator());
        recyclerViewItems.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewItems.setAdapter(adapter);
        recyclerViewItems.addOnItemTouchListener(new RecyclerTouchListner(getActivity(), recyclerViewItems, new OnRecyclerTouchListener() {
            @Override
            public void onClick(View view, int position) {
                showSelectConfirm(productList.get(position).getId());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        productList = AppController.getInstance().getWritableDb().getProductList();
        adapter.setProductList(productList);

        if (productList.size() == 0)
            resultLayout.setVisibility(View.VISIBLE);
        else
            resultLayout.setVisibility(View.GONE);

    }

    private void showSelectConfirm(final long id) {
        String[] items = getResources().getStringArray(R.array.selected_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                        intent.putExtra(Config.DATA_EXTRA, id);
                        startActivity(intent);
                        break;
                    case 1:
                        AppController.getInstance().getWritableDb().deleteProductByID(id);
                        onStart();
                        break;
                    default:
                        break;
                }

                dialog.dismiss();
            }
        });

        builder.show();
    }
}
