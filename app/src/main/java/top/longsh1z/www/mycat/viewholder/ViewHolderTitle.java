package top.longsh1z.www.mycat.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import top.longsh1z.www.mycat.R;

public class ViewHolderTitle extends RecyclerView.ViewHolder {

    public TextView tv_date;

    public ViewHolderTitle(@NonNull View itemView) {
        super(itemView);
        tv_date = itemView.findViewById(R.id.tv_date);
    }
}
