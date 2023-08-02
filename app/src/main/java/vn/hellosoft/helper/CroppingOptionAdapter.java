package vn.hellosoft.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import vn.hellosoft.propertyhero.R;

/**
 * Created by CTO-HELLOSOFT on 4/21/2016.
 */
public class CroppingOptionAdapter extends ArrayAdapter<CroppingOption> {

    private ArrayList<CroppingOption> arrOptions;
    private LayoutInflater inflater;

    public CroppingOptionAdapter(Context context, ArrayList<CroppingOption> arrOptions) {
        super(context, R.layout.layout_cropping_image, arrOptions);
        this.arrOptions = arrOptions;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.layout_cropping_image, null);

        CroppingOption item = arrOptions.get(position);

        if (item != null) {
            ((ImageView) convertView.findViewById(R.id.imageView)).setImageDrawable(item.icon);
            ((TextView) convertView.findViewById(R.id.textView)).setText(item.title);

            return convertView;
        }

        return null;
    }
}
