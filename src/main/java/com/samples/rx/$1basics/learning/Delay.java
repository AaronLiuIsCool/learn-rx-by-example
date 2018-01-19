package com.samples.rx.$1basics.learning;

import com.samples.rx.$1basics.domain.Account;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Delay {

    // this creates mock account ids
    private final List<String> accounts = Arrays.asList("Checking", "Saving", "Investment", "error", "BusinessChecking");

    private final BehaviorSubject<String> recoveredError = BehaviorSubject.create();

    public static void main(String[] args) {
        (new Delay()).runOnErrorTerminateExample();
    }

    private Delay() {
        recoveredError.subscribe(error -> {
            System.out.println("        Somewhere in the system some poor reactive chain skipped result but continued due to: " + error);
            System.out.println("        Reporting the issue...");
        });
    }

    private void runOnErrorTerminateExample() {
        loadAccounts()
                .onErrorResumeNext(Observable.empty())
                .subscribe(new Observer<Account>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("Subscribed");
                    }

                    @Override
                    public void onNext(Account account) {
                        System.out.println("Account with name: [" + account.getName() + "] is loaded successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("Loading all accounts failed.");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("All accounts that could be loaded are loaded successfully...");
                    }
                });

    }

    private Observable<? extends Account> loadAccounts() {

        PublishSubject<Account> ensuredAccountLoadingStream = PublishSubject.create();

        return Observable.fromIterable(accounts)
                .flatMap(accountName ->
                        getAccount(accountName)
                                .timeout(1000, TimeUnit.MILLISECONDS)
                                .toObservable()
                                .map(account -> account.get())
                )
                ;
    }

    private List<Account> update(Account... accounts) {
        //System.out.println(accounts);
        return new ArrayList<>();
    }

    private Maybe<Optional<Account>> getAccount(String account) {

        return Maybe.defer(() -> Maybe.just(account)
                .flatMap(accountToBeLoaded -> {
                    System.out.println("    -> loading " + accountToBeLoaded);
                    if (accountToBeLoaded.equals("error")) {
                        Thread.sleep(10000);
                        return Maybe.just(Optional.empty());
                    }

                    return Maybe.just(Optional.of(new Account(account, 100, 5)));
                }))
                ;
    }


    private Observable<Account> getNullAccountAlternativeBackend() {

        return Observable.defer(() -> Observable.create((ObservableOnSubscribe<Account>) e -> {
            System.out.println("  >> SWITCH: loading account from alternative system");
            e.onNext(new Account("No-name account", 1000, 5));
            e.onComplete();
        }));

    }

}
