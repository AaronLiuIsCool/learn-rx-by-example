package com.samples.rx.$2intermediate.learning;


import com.samples.rx.$1basics.domain.Account;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

import java.net.HttpRetryException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class $1ExceptionsEmptyErrors {

    public static void main(String[] args) {

        strategy1Variation1();
        System.out.println("------------------------------");
//        strategy1Variation2();
//        System.out.println("------------------------------");
//        strategy2Variation1();
//        System.out.println("------------------------------");
//        strategy2Variation2();

    }


    public static void strategy1Variation1() {

        // Strategy 1:
        // --------------------------------------------------
        // error happens
        // retries for three times
        // if error happened on the forth time it emits empty

        AtomicInteger retries = new AtomicInteger(0);
        getAccountIds()
                .flatMap(accountId ->
                        getAccount(accountId)
                                // tries three times, it is nicer that the retry to be inside the getAccount method
                                .retry(e -> retries.incrementAndGet() < 3)

                )
                .onErrorResumeNext(Observable.empty()) // if after three ties it fails it emits nothing
                .subscribe(subscribe());

    }


    public static void strategy1Variation2() {

        // Strategy 1: cleaner more readable code
        // --------------------------------------------------
        // error happens
        // retries for three times
        // if error happened on the forth time it emits empty

        AtomicInteger retries = new AtomicInteger(0);
        getAccountIds()
                .flatMap($1ExceptionsEmptyErrors::getAccount)
                // tries three times, it is nicer that the retry to be inside the getAccount method
                .retry(e -> retries.incrementAndGet() < 3)
                .onErrorResumeNext(Observable.empty()) // if after three ties it fails it emits nothing
                .subscribe(subscribe());

    }


    public static void strategy2Variation1() {

        // Strategy 2:
        // --------------------------------------------------
        // error happens
        // it switches to another complimentary method to retry

        getAccountIds()
                .flatMap($1ExceptionsEmptyErrors::getAccount$Resilient1)
                .subscribe(subscribe());

    }

    public static void strategy2Variation2() {

        // Strategy 2: handling error
        // --------------------------------------------------
        // error happens
        // based on error it decides to log or break the chain
        // it switches to another complimentary method to retry

        getAccountIds()
                .flatMap($1ExceptionsEmptyErrors::getAccount$Resilient2)
                .subscribe(subscribe());

    }


    public static Observable<Integer> getAccountIds() {
        return Observable.range(1, 10);
    }

    public static Observable<Account> getAccount$Resilient1(Integer accountId) {
        return Observable.create((ObservableOnSubscribe<Account>) e -> {

            if (accountId == 6) { // assume this failed and we need to rely on old system
                e.onComplete();
            }
            Account account = new Account("NEW: Account " + accountId, accountId * 100, 5);
            e.onNext(account);
            e.onComplete();

        }).switchIfEmpty(getAccountFromOldSystem(accountId));
    }

    public static Observable<Account> getAccount$Resilient2(Integer accountId) {

        AtomicInteger retries = new AtomicInteger(0);

        return Observable.create((ObservableOnSubscribe<Account>) e -> {

            if (accountId == 6) { // assume this failed and we need to rely on old system
                e.onComplete();
            }
            if (accountId == 7) {
                System.out.println("\tSecurity error happened. Retry");
                e.onError(new IllegalAccessError("Security failed"));
            }
            if (accountId == 8) {
                System.out.println("\tNetwork error happened. Retry");
                e.onError(new HttpRetryException("Network error", 1));
            }
            Account account = new Account("NEW: Account " + accountId, accountId * 100, 5);
            e.onNext(account);
            e.onComplete();

        })
                .retry(e -> retries.incrementAndGet() < 3)
                // if expected exception happens, log them, then return and empty
                .onErrorResumeNext(whenExceptionIsThenLogAndIgnore(HttpRetryException.class, IllegalAccessError.class))
                // when an empty is returned, try to get the result from the old system
                .switchIfEmpty(getAccountFromOldSystem(accountId));
    }

    public static Observable<Account> getAccount(Integer accountId) {

        return Observable.create(e -> {

            if (accountId == 6) { // assume this failed and we need to rely on old system
                e.onComplete();
            }
            Account account = new Account("NEW: Account " + accountId, accountId * 100, 5);
            e.onNext(account);

            System.out.println("Error happened");

            e.onError(new RuntimeException("Exception happened"));

            e.onComplete();

        });
    }


    public static Observable<Account> getAccountFromOldSystem(Integer accountId) {
        return Observable.create(e -> {

            Account account = new Account("OLD: Account " + accountId, accountId * 100, 5);
            e.onNext(account);
            e.onComplete();
        });
    }


    public static Observer<Account> subscribe() {
        return new Observer<Account>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("Subscribed");
            }

            @Override
            public void onNext(Account account) {
                System.out.println(account.getName());
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("Completed");
            }
        };
    }


    public static <T> Function<Throwable, Observable<T>> whenExceptionIsThenLogAndIgnore(Class... what) {
        return t -> Arrays.stream(what).anyMatch(e -> e.isInstance(t)) ? fireErrorLog(t) : breakTheChain(t);
    }

    public static <T> Observable<T> breakTheChain(Throwable t) {
        System.out.println("\t\tBreaking the chain : " + t);
        return Observable.error(t);
    }

    public static <T> Observable<T> fireErrorLog(Throwable t) {
        System.out.println("\t\tFiring error log event");
        // you can do bunch of stuff here when an error happens
        return logError(t);
    }

    public static <T> Observable<T> logError(Throwable throwable) {
        // imagine this is connecting to an external system
        System.out.println("\t\tError: " + throwable.getMessage());
        return Observable.empty();
    }


}
