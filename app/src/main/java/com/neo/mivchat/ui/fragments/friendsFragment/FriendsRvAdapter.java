package com.neo.mivchat.ui.fragments.friendsFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.neo.mivchat.utilities.IMainActivity;
import com.neo.mivchat.R;
import com.neo.mivchat.dataSource.database.User;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsRvAdapter extends PagedListAdapter<User, FriendsRvAdapter.FriendsRvViewHolder> {

    private IMainActivity mListener;
    private Context mContext;

    private static DiffUtil.ItemCallback<User> sItemCallback = new DiffUtil.ItemCallback<User>() {
        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getUser_id() == newItem.getUser_id();
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    protected FriendsRvAdapter(Context context) {
        super(sItemCallback);
        mContext = context;
        mListener = (IMainActivity)mContext;
    }

    @NonNull
    @Override
    public FriendsRvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends, parent, false);
        return new FriendsRvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsRvViewHolder holder, int position) {
        User user = getItem(position);
        if(user != null){
            holder.setData(user);
            holder.setListener();
        }
    }

    class FriendsRvViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView mUserImage;
        private TextView mUserName;
        private ImageView mVideoCallBtn;
        private User mUser;

        public FriendsRvViewHolder(@NonNull View itemView) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.user_image_home);
            mUserName = itemView.findViewById(R.id.user_name_home);
            mVideoCallBtn = itemView.findViewById(R.id.video_call_home);
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

        public void setListener(){
            mVideoCallBtn.setOnClickListener(v -> {
                mListener.startCallActivity(mUser.getUser_id());
            });
        }
    }
}
