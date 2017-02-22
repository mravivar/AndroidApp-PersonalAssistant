package mcgroup10.com.batroid;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by abhishekzambre on 20/11/16.
 */


public class CustomAdapter extends BaseAdapter {
    Activity context;
    boolean[] itemChecked;
    Model[] modelItems = null;
    ArrayList<String> Arr = new ArrayList<String>();

    public CustomAdapter(Activity context, Model[] resource) {
        super();
        this.context = context;
        this.modelItems = resource;
        itemChecked = new boolean[modelItems.length];
    }

    public int getCount() {
        //return packageList.size();
        return modelItems.length;
    }

    public Object getItem(int position) {
        //return modelItems.get(position);
        return modelItems[position].getName();
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row, null);
            holder = new ViewHolder();

            holder.location = (TextView) convertView.findViewById(R.id.textView1);
            holder.ck1 = (CheckBox) convertView.findViewById(R.id.checkBox1);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.location.setText(modelItems[position].getName());
        holder.ck1.setChecked(false);

        if (itemChecked[position])
            holder.ck1.setChecked(true);
        else
            holder.ck1.setChecked(false);

        holder.ck1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.ck1.isChecked()) {
                    itemChecked[position] = true;
                    Arr.add(holder.location.getText().toString());
                } else {
                    itemChecked[position] = false;
                    Arr.remove(holder.location.getText().toString());
                }
            }
        });

        return convertView;
    }

    public ArrayList<String> getChecked() {
        return Arr;

    }

    private class ViewHolder {
        TextView location;
        CheckBox ck1;
    }
}