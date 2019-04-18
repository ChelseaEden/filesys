package com.example.g.filesys;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder>{

    private OnItemLongClickListener mlongClickListener;
    private OnItemClickListener mClickListener;
    private static Context mContext;
    private static List<Fileit> mfileList;
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private OnItemLongClickListener mlongListener;
        private OnItemClickListener mListener;
        ImageView fileimg;
        TextView filename;

        public ViewHolder(View view, OnItemClickListener listener,OnItemLongClickListener longlistener){
            super(view);

            mListener = (OnItemClickListener) listener;
            mlongListener = (OnItemLongClickListener) longlistener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            fileimg = (ImageView)view.findViewById(R.id.file_img);
            filename = (TextView)view.findViewById(R.id.file_name);


           /* view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(v,getPosition());
                    //Toast.makeText(mContext, "当前点击 "+ mfileList.get(getLayoutPosition()).getName(), Toast.LENGTH_SHORT).show();
                }
            }); */
        }

        @Override
        public boolean onLongClick(View v) {
            mlongListener.onItemLongClick(v,getPosition());
            return true;
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(v,getPosition());
        }

    }
    public FileAdapter(Context mContext, List<Fileit> fileitList){
        this.mContext = mContext;
        mfileList = fileitList;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int postion);
    }
    public interface OnItemLongClickListener {
        public void onItemLongClick(View view, int postion);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mlongClickListener = listener;
    }

    @Override
    public FileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);

         ViewHolder holder = new ViewHolder(view,mClickListener,mlongClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(FileAdapter.ViewHolder holder, int position) {
        Fileit fileit = mfileList.get(position);
        if (fileit.getImageId() == 1){
            holder.fileimg.setImageResource(R.drawable.pic_file);
            holder.filename.setText(fileit.getName());
        }else if (fileit.getImageId() == 0||fileit.getImageId() == 2||fileit.getImageId() == 3){
            holder.fileimg.setImageResource(R.drawable.pic_folder);
            holder.filename.setText(fileit.getName());
        }
    }


    @Override
    public int getItemCount() {
        return mfileList.size();
    }
    @Override
    public int getItemViewType(int position){
        return 1;
    }
}
