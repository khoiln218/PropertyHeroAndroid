package vn.hellosoft.hellorent.activities;

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

import java.util.ArrayList;
import java.util.List;

import vn.hellosoft.app.Config;
import vn.hellosoft.hellorent.R;
import vn.hellosoft.hellorent.adapters.GalleryAdapter;
import vn.hellosoft.hellorent.callbacks.OnRecyclerTouchListener;
import vn.hellosoft.hellorent.callbacks.RecyclerTouchListner;
import vn.hellosoft.helper.Utils;

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
