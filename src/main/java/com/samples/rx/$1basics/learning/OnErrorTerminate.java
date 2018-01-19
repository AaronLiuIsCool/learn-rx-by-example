package com.samples.rx.$1basics.learning;

import com.samples.rx.$1basics.domain.Account;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

import java.util.Arrays;
import java.util.List;

public class OnErrorTerminate {

    // this creates mock accounts
    private final List<Account> accounts = Arrays.asList(
            new Account("Checking", 100f, 3),
            new Account("Saving", 100000f, 5),
            new Account("Investment", 1000f, 4),
            new Account(), // imagine call to get this account failed
            new Account("Business Checking", 10000f, 4)
    );

    private final BehaviorSubject<String> valleyOfDeath = BehaviorSubject.create();

    public static void main(String[] args) {
        (new OnErrorTerminate()).runOnErrorTerminateExample();
    }

    public OnErrorTerminate() {
        valleyOfDeath.subscribe(error -> {
            System.out.println("Somewhere in the system some poor reactive chain terminated because: " + error);
            System.out.println("Reporting the issue...");
        });
    }

    private void runOnErrorTerminateExample() {
        loadAccounts()
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

                    }
                });

    }

    private Observable<? extends Account> loadAccounts() {

        return Observable.fromIterable(accounts)
                .flatMap(account -> {
                    if (account.getName() == null) { // imagine call to get this account failed
                        return Observable.error(() -> {
                            String error = "Connection lost. Account not loaded";
                            valleyOfDeath.onNext(error);
                            return new RuntimeException(error);
                        });
                    }
                    return Observable.just(account);
                });
    }


}
