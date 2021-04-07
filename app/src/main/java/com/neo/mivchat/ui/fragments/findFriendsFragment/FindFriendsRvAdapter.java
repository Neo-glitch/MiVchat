package com.neo.mivchat.ui.fragments.findFriendsFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.neo.mivchat.utilities.IMainActivity;
import com.neo.mivchat.R;
import com.neo.mivchat.dataSource.database.User;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * newer version of the RvAdapter to be used in App
 */
public class FindFriendsRvAdapter extends PagedListAdapter<User, FindFriendsRvAdapter.FindFriendsRvViewHolder> {

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

    protected FindFriendsRvAdapter(Context context) {
        super(sItemCallback);
        mContext = context;
        mListener = (IMainActivity) mContext;
    }

    @NonNull
    @Override
    public FindFriendsRvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_find_friends, parent, false);
        return new FindFriendsRvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FindFriendsRvViewHolder holder, int position) {
        User user = getItem(position);
        if (user != null) {
            holder.setData(user);
            holder.setListener();
        }
    }

    class FindFriendsRvViewHolder extends RecyclerView.ViewHolder {
        private TextView mUserName, mUserBio;
        private CircleImageView mUserImage;
        private View view;
        private User mUser;

        public FindFriendsRvViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            mUserName = itemView.findViewById(R.id.user_name_findfriends);
            mUserBio = itemView.findViewById(R.id.user_bio_findfriends);
            mUserImage = itemView.findViewById(R.id.user_image_findfriends);
        }


        public void setData(User user) {
            mUser = user;
            mUserName.setText(user.getName());
            mUserBio.setText(user.getBio());
            if (!user.getProfile_image().equals("")) {
                Picasso picasso = Picasso.get();
                picasso.setLoggingEnabled(true);
                picasso.load(user.getProfile_image())
                        .placeholder(R.drawable.profile_image)
                        .error(R.drawable.ic_image_error)
                        .into(mUserImage);
            }
        }

        public void setListener() {
            view.setOnClickListener(v -> {
                mListener.inflateProfileActivity(
                        mUser.getUser_id(),
                        mUser.getProfile_image(),
                        mUser.getName(),
                        mUser.getBio()
                );
            });
        }
    }
}
