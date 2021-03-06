/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 *
 * @author Fernando Cejas (the android10 coder)
 */
package com.fernandocejas.android10.sample.presentation.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.fernandocejas.android10.sample.presentation.R;
import com.fernandocejas.android10.sample.presentation.internal.di.components.UserComponent;
import com.fernandocejas.android10.sample.presentation.model.UserModel;
import com.fernandocejas.android10.sample.presentation.presenter.UserListPresenter;
import com.fernandocejas.android10.sample.presentation.view.UserListView;
import com.fernandocejas.android10.sample.presentation.view.adapter.UsersAdapter;
import com.fernandocejas.android10.sample.presentation.view.adapter.UsersLayoutManager;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.Collection;

/**
 * Fragment that shows a list of Users.
 */
public class UserListFragment extends BaseFragment implements UserListView {

    public interface UserListListener {
        void onUserClicked(final UserModel userModel);
    }

    @Inject
    UserListPresenter userListPresenter;
    @Inject
    UsersAdapter usersAdapter;

    @Bind(R.id.rv_users)
    RecyclerView rv_users;
    @Bind(R.id.rl_progress)
    RelativeLayout rl_progress;
    @Bind(R.id.rl_retry)
    RelativeLayout rl_retry;
    @Bind(R.id.bt_retry)
    Button bt_retry;

    private final String TAG = "UserListFragment";

    private UserListListener userListListener;

    public UserListFragment() {
        setRetainInstance(true);
    }


    /**
     * fragment 与宿主Activity发生关联时，回调。
     * 保证了宿主Activity有去实现{@link UserListListener}
     * 这个{@link UserListListener}主要是为了该fragment发生事件时，通知activity，activity通常会通知其他fragment
     * 该写法时官网demo的写法。
     * ljj-comment
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof UserListListener) {
            this.userListListener = (UserListListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(UserComponent.class).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_user_list, container, false);
        ButterKnife.bind(this, fragmentView);
        setupRecyclerView();
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.userListPresenter.setView(this);
        if (savedInstanceState == null) {
            this.loadUserList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.userListPresenter.resume();
        Log.i(TAG, MessageFormat.format("onResume-adapter  :{0}", usersAdapter));

    }

    @Override
    public void onPause() {
        super.onPause();
        this.userListPresenter.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rv_users.setAdapter(null);
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.userListPresenter.destroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.userListListener = null;
    }
    //endregion

    //region View-mvp
    @Override
    public void showLoading() {
        this.rl_progress.setVisibility(View.VISIBLE);
        this.getActivity().setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void hideLoading() {
        this.rl_progress.setVisibility(View.GONE);
        this.getActivity().setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void showRetry() {
        this.rl_retry.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRetry() {
        this.rl_retry.setVisibility(View.GONE);
    }

    @Override
    public void renderUserList(Collection<UserModel> userModelCollection) {
        if (userModelCollection != null) {
            this.usersAdapter.setUsersCollection(userModelCollection);
        }
    }

    @Override
    public void viewUser(UserModel userModel) {
        if (this.userListListener != null) {
            this.userListListener.onUserClicked(userModel);
        }
    }

    @Override
    public void showError(String message) {
        this.showToastMessage(message);
    }

    @Override
    public Context context() {
        return this.getActivity().getApplicationContext();
    }
    //endregion

    //region recycleview 初始化
    private void setupRecyclerView() {
        this.usersAdapter.setOnItemClickListener(onItemClickListener);
//        this.rv_users.setLayoutManager(new StaggeredGridLayoutManager(12,HORIZONTAL));
        this.rv_users.setLayoutManager(new UsersLayoutManager(context()));
        this.rv_users.setAdapter(usersAdapter);
    }
    //endregion

    //region 与present层交互
    private void loadUserList() {
        this.userListPresenter.initialize();
    }
    //endregion

    @OnClick(R.id.bt_retry)
    void onButtonRetryClick() {
        UserListFragment.this.loadUserList();
    }

    //region listview item点击事件响应实现
    private UsersAdapter.OnItemClickListener onItemClickListener =
            new UsersAdapter.OnItemClickListener() {
                @Override
                public void onUserItemClicked(UserModel userModel) {
                    if (UserListFragment.this.userListPresenter != null && userModel != null) {
                        UserListFragment.this.userListPresenter.onUserClicked(userModel);
                    }
                }
            };
    //endregion
}
