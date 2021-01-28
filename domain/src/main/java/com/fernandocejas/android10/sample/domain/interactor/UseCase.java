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
package com.fernandocejas.android10.sample.domain.interactor;

import com.fernandocejas.android10.sample.domain.executor.PostExecutionThread;
import com.fernandocejas.android10.sample.domain.executor.ThreadExecutor;
import com.fernandocejas.arrow.checks.Preconditions;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Abstract class for a Use Case (Interactor--交互器 in terms of Clean Architecture).
 * This interface represents a execution unit for different use cases (this means
 * ****any use case in the application should implement this contract******--合同).
 * <p>
 * By convention--公约 each UseCase implementation
 * will
 * return the result using a {@link DisposableObserver}
 * that will
 * execute its job in a background thread
 * and will
 * post the result in the UI thread.
 */
public abstract class UseCase<T, Params> {

    private final ThreadExecutor threadExecutor;
    private final PostExecutionThread postExecutionThread;
    private final CompositeDisposable disposables;

    UseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
        this.disposables = new CompositeDisposable();
    }

    /**
     * Builds an {@link Observable} which will
     * be used when executing the current {@link UseCase}.
     */
    abstract Observable<T> buildUseCaseObservable(Params params);

    /**
     * Executes the current use case.
     *
     * @param observer {@link DisposableObserver} which will be
     *                 listening to the observable
     *                 build by
     *                 {@link #buildUseCaseObservable(Params)} ()} method.
     *                 commentByLjj
     *                 观察者，观察  {@link #buildUseCaseObservable(Params)} ()}生成的 {@link DisposableObserver}
     *                 --------------------------------------------------------------------------------
     * @param params   Parameters (Optional) used to build/execute this use case.
     *                 commentByLjj
     *                 用来执行这个案例的参数
     */
    public void execute(DisposableObserver<T> observer, Params params) {
        Preconditions.checkNotNull(observer);
        final Observable<T> observable = this.buildUseCaseObservable(params)
                .subscribeOn(Schedulers.from(threadExecutor))//subscribeOn指定观察目标的线程。
                .observeOn(postExecutionThread.getScheduler());//指定观察者的线程

        addDisposable(observable.subscribeWith(observer));
    }

    /**
     * Dispose from current {@link CompositeDisposable}.
     * commentByLjj
     * 解除订阅
     */
    public void dispose() {
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }

    /**
     * Dispose from current {@link CompositeDisposable}.
     * commentByLjj
     * 订阅
     */
    private void addDisposable(Disposable disposable) {
        Preconditions.checkNotNull(disposable);
        Preconditions.checkNotNull(disposables);
        disposables.add(disposable);
    }
}
