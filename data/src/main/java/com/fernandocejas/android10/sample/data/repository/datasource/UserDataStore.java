/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fernandocejas.android10.sample.data.repository.datasource;

import com.fernandocejas.android10.sample.data.entity.UserEntity;
import io.reactivex.Observable;
import java.util.List;

/**
 * 一个代表 数据获取源 的接口。
 */
public interface UserDataStore {
  /**
   * 获取Observable，它能散发 UserEntity List
   */
  Observable<List<UserEntity>> userEntityList();

  /**

   *
   *  获取Observable 它能通过UserId散发 UserEntity
   */
  Observable<UserEntity> userEntityDetails(final int userId);
}
