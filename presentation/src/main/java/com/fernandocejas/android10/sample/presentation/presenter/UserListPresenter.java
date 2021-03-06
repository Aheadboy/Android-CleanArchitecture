/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fernandocejas.android10.sample.presentation.presenter;

import android.support.annotation.NonNull;
import com.fernandocejas.android10.sample.domain.User;
import com.fernandocejas.android10.sample.domain.exception.DefaultErrorBundle;
import com.fernandocejas.android10.sample.domain.exception.ErrorBundle;
import com.fernandocejas.android10.sample.domain.interactor.DefaultObserver;
import com.fernandocejas.android10.sample.domain.interactor.GetUserList;
import com.fernandocejas.android10.sample.presentation.exception.ErrorMessageFactory;
import com.fernandocejas.android10.sample.presentation.internal.di.PerActivity;
import com.fernandocejas.android10.sample.presentation.mapper.UserModelDataMapper;
import com.fernandocejas.android10.sample.presentation.model.UserModel;
import com.fernandocejas.android10.sample.presentation.view.UserListView;

import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

/**
 * {@link Presenter} that ***controls*** communication between views and models of the presentation
 * layer.
 *
 * 负责了，获取数据；
 * 负责了，获取到数据后通知界面现实；
 * 负责了，界面到显示逻辑；（首先，显示加载对话框；其次，显示数据；最后，隐藏加载对话框。等等......）
 * --ljj-comment
 */
@PerActivity
public class UserListPresenter implements Presenter {

    private UserListView viewListView;

    private final GetUserList getUserListUseCase;
    private final UserModelDataMapper userModelDataMapper;

    @Inject
    public UserListPresenter(GetUserList getUserListUserCase, UserModelDataMapper userModelDataMapper) {
        this.getUserListUseCase = getUserListUserCase;
        this.userModelDataMapper = userModelDataMapper;
    }


    /**
     * 将此 present   与相应的view 关联
     * 获取到view到引用
     * @param view
     */
    public void setView(@NonNull UserListView view) {
        this.viewListView = view;
    }

    //region present-mvp
    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void destroy() {
        this.getUserListUseCase.dispose();
        this.viewListView = null;
    }
    //endregion

    /**
     * Initializes the presenter by start retrieving the user list.
     */
    public void initialize() {
        this.loadUserList();
    }

    /**
     * Loads all users.
     */
    private void loadUserList() {
        this.hideViewRetry();
        this.showViewLoading();
        this.getUserList();
    }


    //region 与view-mvp交互的函数封装
    public void onUserClicked(UserModel userModel) {
        this.viewListView.viewUser(userModel);
    }

    private void showViewLoading() {
        this.viewListView.showLoading();
    }

    private void hideViewLoading() {
        this.viewListView.hideLoading();
    }

    private void showViewRetry() {
        this.viewListView.showRetry();
    }

    private void hideViewRetry() {
        this.viewListView.hideRetry();
    }

    private void showErrorMessage(ErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.viewListView.context(), errorBundle.getException());
        this.viewListView.showError(errorMessage);
    }

    private void showUsersCollectionInView(Collection<User> usersCollection) {
        final Collection<UserModel> userModelsCollection = this.userModelDataMapper.transform(usersCollection);
        this.viewListView.renderUserList(userModelsCollection);
    }
    //endregion


    private void getUserList() {
        this.getUserListUseCase.execute(new UserListObserver(), null);
    }

    /**
     * 这个observer会在UI线程执行。
     */
    private final class UserListObserver extends DefaultObserver<List<User>> {

        @Override
        public void onComplete() {
            UserListPresenter.this.hideViewLoading();
        }

        @Override
        public void onError(Throwable e) {
            UserListPresenter.this.hideViewLoading();
            UserListPresenter.this.showErrorMessage(new DefaultErrorBundle((Exception) e));
            UserListPresenter.this.showViewRetry();
        }

        @Override
        public void onNext(List<User> users) {
            UserListPresenter.this.showUsersCollectionInView(users);
        }
    }
}
