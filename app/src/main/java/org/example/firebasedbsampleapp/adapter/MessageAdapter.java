package org.example.firebasedbsampleapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.example.firebasedbsampleapp.R;
import org.example.firebasedbsampleapp.entity.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    interface LayoutId {
        final int MY = 0;
        final int ANOTHOR = 1;
    }

    private LayoutInflater mInflater;
    private List<Message> mItems;
    private Context mContext;
    private String mUid;


    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;

        public ViewHolder(View view) {
            super(view);
            messageText = (TextView) view.findViewById(R.id.text);
        }
    }


    /**
     * コンストラクタ
     *
     * @param context
     * @param items
     */
    public MessageAdapter(Context context, List<Message> items, String myUid) {
        this.mItems = items;
        this.mUid = myUid;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return TextUtils.equals(mUid, mItems.get(position).getuId()) ? LayoutId.MY : LayoutId.ANOTHOR;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == LayoutId.MY) {
            View viewMy = mInflater.inflate(R.layout.list_item_message_my, parent, false);
            return new ViewHolder(viewMy);
        } else {
            View view = mInflater.inflate(R.layout.list_item_message, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.messageText.setText(mItems.get(position).getContent());
    }

    @Override
    public long getItemId(int arg0) {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addItem(Message message) {
        mItems.add(message);
        notifyItemInserted(mItems.size() - 1);
    }

    public void clear() {
        notifyItemRangeRemoved(0, mItems.size());
        mItems.clear();
    }

}
