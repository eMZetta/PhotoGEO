package ch.uifz725.photogeo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by eMZetta March 2019.
 */

public class PictureListAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<Picture> pictureList;

    public PictureListAdapter(Context context, int layout, ArrayList<Picture> picturesList) {
        this.context = context;
        this.layout = layout;
        this.pictureList = picturesList;
    }

    @Override
    public int getCount() {
        return pictureList.size();
    }

    @Override
    public Object getItem(int position) {
        return pictureList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView pictureView;
        TextView txtName, tvLocation;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtName = (TextView) row.findViewById(R.id.txtName);
            holder.tvLocation = (TextView) row.findViewById(R.id.txtLocation);
            holder.pictureView = (ImageView) row.findViewById(R.id.imgCapture);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Picture picture = pictureList.get(position);

        holder.txtName.setText(picture.getName());
        holder.tvLocation.setText(picture.getLocation());

        byte[] image = picture.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        holder.pictureView.setImageBitmap(bitmap);

        return row;
    }
}
