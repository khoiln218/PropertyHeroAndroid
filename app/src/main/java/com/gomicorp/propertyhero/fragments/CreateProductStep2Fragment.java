package com.gomicorp.propertyhero.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gomicorp.app.Config;
import com.gomicorp.helper.L;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.activities.CreateProductActivity;
import com.gomicorp.propertyhero.activities.GalleryActivity;
import com.gomicorp.propertyhero.adapters.SelectedImageAdapter;
import com.gomicorp.propertyhero.callbacks.OnRecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateProductStep2Fragment extends Fragment implements View.OnClickListener {


    private RecyclerView recyclerImageProduct;
    private List<String> imageList;
    private SelectedImageAdapter imageAdapter;

    private TextView tvNumImage, tvHintImage;
    private Button btnNextStep3;

    public CreateProductStep2Fragment() {
        // Required empty public constructor
    }

    public static CreateProductStep2Fragment instance(List<String> imageList) {
        CreateProductStep2Fragment step2Fragment = new CreateProductStep2Fragment();
        Bundle args = new Bundle();
        args.putStringArrayList(Config.IMAGE_LIST, (ArrayList<String>) imageList);
        step2Fragment.setArguments(args);

        return step2Fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_create_product_step_2, container, false);

        recyclerImageProduct = (RecyclerView) root.findViewById(R.id.recyclerImageProduct);
        setupRecycler();

        tvNumImage = (TextView) root.findViewById(R.id.tvNumImage);
        tvHintImage = (TextView) root.findViewById(R.id.tvHintImage);
        btnNextStep3 = (Button) root.findViewById(R.id.btnNextStep3);

        tvNumImage.setText(Config.PIC_TEXT.replace("...", "0"));

        root.findViewById(R.id.btnCaptureImage).setOnClickListener(this);
        root.findViewById(R.id.btnChooseGallery).setOnClickListener(this);
        root.findViewById(R.id.btnBackStep1).setOnClickListener(this);
        btnNextStep3.setOnClickListener(this);

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageList = new ArrayList<>();
        if (getArguments() != null)
            imageList = getArguments().getStringArrayList(Config.IMAGE_LIST);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void setupRecycler() {
        imageAdapter = new SelectedImageAdapter(imageList, new OnRecyclerItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                imageList.remove(position);
                imageAdapter.setImageList(imageList);
                updateUI();
            }
        });

        recyclerImageProduct.setAdapter(imageAdapter);
        recyclerImageProduct.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerImageProduct.setHasFixedSize(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Config.REQUEST_CAMERA:
                imageList.add(Utils.getBitmapPath(getActivity(), (Bitmap) data.getExtras().get("data")));
                imageAdapter.setImageList(imageList);
                break;
            case Config.REQUEST_GALLERY:
                if (resultCode == Config.SUCCESS_RESULT) {
                    imageList.clear();
                    imageList.addAll(data.getStringArrayListExtra(Config.RESULT_DATA));
                    imageAdapter.setImageList(imageList);
                }
                break;
            default:
                break;
        }

        updateUI();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChooseGallery:
                if (Utils.isSDPresent()) {
                    Intent gallery = new Intent(getActivity(), GalleryActivity.class);
                    gallery.putStringArrayListExtra(Config.DATA_EXTRA, (ArrayList<String>) imageList);
                    ActivityCompat.startActivityForResult(requireActivity(), gallery, Config.REQUEST_GALLERY, null);
                } else
                    L.showToast(getString(R.string.err_msg_sd_card));
                break;
            case R.id.btnCaptureImage:
                if (Utils.isSDPresent()) {
                    Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    ActivityCompat.startActivityForResult(requireActivity(), takePhoto, Config.REQUEST_CAMERA, null);
                } else
                    L.showToast(getString(R.string.err_msg_sd_card));
                break;
            case R.id.btnBackStep1:
                ((CreateProductActivity) getActivity()).backStep();
                break;
            case R.id.btnNextStep3:
                ((CreateProductActivity) getActivity()).imageList = imageList;
                ((CreateProductActivity) getActivity()).nextStep();
                break;
        }
    }

    private void updateUI() {
        if (imageList.size() > 0)
            tvHintImage.setVisibility(View.GONE);
        else
            tvHintImage.setVisibility(View.VISIBLE);

        tvNumImage.setText(Config.PIC_TEXT.replace("...", imageList.size() + ""));

        if (imageList.size() >= Config.MIN_PIC && imageList.size() <= Config.MAX_PIC)
            btnNextStep3.setEnabled(true);
        else
            btnNextStep3.setEnabled(false);
    }
}
