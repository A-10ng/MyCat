package top.longsh1z.www.mycat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import top.longsh1z.www.mycat.R;
import top.longsh1z.www.mycat.bean.CheckRecordBean;
import top.longsh1z.www.mycat.viewholder.ViewHolderContent;
import top.longsh1z.www.mycat.viewholder.ViewHolderTitle;

public class CheckRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> recordList;
    private static int ITEM_TITLE = 1;
    private static int ITEM_CONTENT = 2;

    public CheckRecordAdapter(List<Object> recordList){
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == ITEM_TITLE){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_titile_layout,viewGroup,false);
            viewHolder = new ViewHolderTitle(view);
        }else if(viewType == ITEM_CONTENT){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_content_layout,viewGroup,false);
            viewHolder = new ViewHolderContent(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ViewHolderTitle){
            String title = (String) recordList.get(position);
            ((ViewHolderTitle)viewHolder).tv_date.setText(title);
        }else if (viewHolder instanceof ViewHolderContent){
            CheckRecordBean.ContentBean contentBeans = (CheckRecordBean.ContentBean) recordList.get(position);

            ((ViewHolderContent)viewHolder).tv_record.setText(contentBeans.getRecordContent());
            ((ViewHolderContent)viewHolder).tv_recordTime.setText(contentBeans.getRecordTime());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (recordList.get(position) instanceof String){
            return ITEM_TITLE;
        }else if (recordList.get(position) instanceof CheckRecordBean.ContentBean){
            return ITEM_CONTENT;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return (recordList == null ? 0 : recordList.size() );
    }

    public  void addData(List<Object> newList){
        int position = recordList.size();
        recordList.addAll(position, newList);
        notifyItemInserted(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
