package com.gomicorp.propertyhero.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gomicorp.app.Config;
import com.gomicorp.helper.Utils;
import com.gomicorp.propertyhero.R;
import com.gomicorp.propertyhero.adapters.GalleryAdapter;
import com.gomicorp.propertyhero.callbacks.OnRecyclerTouchListener;
import com.gomicorp.propertyhero.callbacks.RecyclerTouchListner;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerGalerry;
    private List<String> imageList;
    private List<String> selectedList;
    private GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        recyclerGalerry = (RecyclerView) findViewById(R.id.recyclerGallery);

        imageList = Utils.getImagePaths(this);
        selectedList = getIntent().getStringArrayListExtra(Config.DATA_EXTRA);

        adapter = new GalleryAdapter(imageList, selectedList);

        recyclerGalerry.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerGalerry.setHasFixedSize(true);
        recyclerGalerry.setAdapter(adapter);

        recyclerGalerry.addOnItemTouchListener(new RecyclerTouchListner(this, recyclerGalerry, new OnRecyclerTouchListener() {
            @Override
            public void onClick(View view, int position) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.chkImageGallery);
                boolean checked = !checkBox.isChecked();
                checkBox.setChecked(checked);
                if (checked)
                    selectedList.add(imageList.get(position));
                else
                    selectedList.remove(imageList.get(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_choose, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_choose:
                Intent intent = getIntent();
                intent.putStringArrayListExtra(Config.RESULT_DATA, (ArrayList<String>) selectedList);
                setResult(Config.SUCCESS_RESULT, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
