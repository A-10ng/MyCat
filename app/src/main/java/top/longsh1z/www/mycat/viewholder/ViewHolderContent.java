package top.longsh1z.www.mycat.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import top.longsh1z.www.mycat.R;

public class ViewHolderContent extends RecyclerView.ViewHolder {

    public TextView tv_record;
    public TextView tv_recordTime;

    public ViewHolderContent(@NonNull View itemView) {
        super(itemView);
        tv_record = itemView.findViewById(R.id.tv_record);
        tv_recordTime = itemView.findViewById(R.id.tv_recordTime);
    }
}
