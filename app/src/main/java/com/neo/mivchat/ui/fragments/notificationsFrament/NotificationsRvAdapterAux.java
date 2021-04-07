package com.neo.mivchat.ui.fragments.notificationsFrament;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.neo.mivchat.R;
import com.neo.mivchat.dataSource.database.User;
import com.neo.mivchat.utilities.IMainActivity;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsRvAdapterAux extends ListAdapter<User, NotificationsRvAdapterAux.NotificationsRvViewHolder> {

    private IMainActivity mListener;
    private Context mContext;

    private static DiffUtil.ItemCallback<User> sItemCallback = new DiffUtil.ItemCallback<User>() {
        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getUser_id().equals(newItem.getUser_id());
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    protected NotificationsRvAdapterAux(Context context) {
        super(sItemCallback);
        mContext = context;
        mListener = (IMainActivity) mContext;
    }

    @NonNull
    @Override
    public NotificationsRvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notifications, parent, false);
        return new NotificationsRvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsRvViewHolder holder, int position) {
        User user = getItem(position);
        if(user != null){
            holder.setData(user);
            holder.setListeners();
        }
    }

    class NotificationsRvViewHolder extends RecyclerView.ViewHolder{
        private TextView mUserName;
        private Button mAcceptBtn, mDeclineBtn;
        private CircleImageView mUserImage;
        private User mUser;

        public NotificationsRvViewHolder(@NonNull View itemView) {
            super(itemView);
            mUserName = itemView.findViewById(R.id.user_name_notification);
            mAcceptBtn = itemView.findViewById(R.id.btn_accept);
            mDeclineBtn = itemView.findViewById(R.id.btn_decline);
            mUserImage = itemView.findViewById(R.id.user_image_notification);
        }

        public void setData(User user){
            mUser = user;
            mUserName.setText(user.getName());
            if(!user.getProfile_image().equals("")){
                Picasso picasso = Picasso.get();
                picasso.setLoggingEnabled(true);
                picasso.load(user.getProfile_image())
                        .placeholder(R.drawable.profile_image)
                        .error(R.drawable.ic_image_error)
                        .into(mUserImage);
            }
        }

        public void setListeners(){
            mAcceptBtn.setOnClickListener(v -> {
                mListener.acceptRequest(mUser.getUser_id());
            });
            mDeclineBtn.setOnClickListener(v -> {
                mListener.declineRequest(mUser.getUser_id());
            });


        }
    }
}
