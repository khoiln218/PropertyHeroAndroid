package vn.hellosoft.hellorent.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import vn.hellosoft.hellorent.R;

/**
 * Created by CTO-HELLOSOFT on 5/5/2016.
 */
public class ViewHolderProgress extends RecyclerView.ViewHolder {

    ProgressBar progressBar;

    public ViewHolderProgress(View itemView) {
        super(itemView);

        progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
    }
}
