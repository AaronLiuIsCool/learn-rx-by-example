package com.samples.rx.$1basics.learning;

import com.samples.rx.$1basics.domain.Account;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

import java.util.Arrays;
import java.util.List;

public class $8BasicsFilterEmpty {

    public static void main(String[] args) {

//        filter();
//        empty();
//        error();
        errorAndErrorHandling();

    }


    private static void filter() {


        List<String> alphabets = Arrays.asList("a", "b", "c", "d", "e");

        /**
         * The following code shows how to filter. We are excluding "c" from being emitted
         * The following code should result in
         *
         * subscribed
         * (a)
         * (b)
         * (d)
         * (e)
         * completed
         *
         * Please read: http://reactivex.io/documentation/operators/filter.html
         */
        Observable.fromIterable(alphabets)
                .filter(alphabet -> !alphabet.equals("c"))
                .flatMap(cleanedFilter -> addParentheses(cleanedFilter))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("subscribed");
                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println(s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        System.out.println("completed");
                    }
                });

        System.out.println();


    }


    private static void empty() {

        /**
         * empty
         * -----
         * create an Observable that emits no items but terminates normally
         *
         * This is used for scenarios that we don't want to emit some observables. For example
         * we want to mask certain results. Masking can also be achieved by filter but if you
         * want to write you own complex business filter the items that should not be emitted
         * should come back as empty
         */

        List<Account> accounts = Arrays.asList(
                new Account("Checking", 100f, 3),
                new Account("Saving", 100000f, 5),
                new Account("Investment", 1000f, 4),
                new Account("Business Checking", 10000f, 4)
        );


        Observable.fromIterable(accounts)
                .flatMap($8BasicsFilterEmpty::extractApproachable)
                .subscribe(new Observer<Account>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("We can now approach account holder for further investment opportunity to transfer funds from the following account");
                    }

                    @Override
                    public void onNext(Account account) {
                        System.out.println(account);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        System.out.println();


    }

    private static void error() {
        /**
         * error
         * -----
         * The error operator takes as a parameter the Throwable with which you want the Observable to terminate.
         *
         * Sometimes and Observable is a result of connectivity to another system and may return and error. In such scenarios
         * we need to make sure the chain is either broken or errors are logged then chain is broker.
         */

        List<Account> accounts = Arrays.asList(
                new Account("Checking", 100f, 3),
                new Account("Saving", 100000f, 5),
                new Account("Investment", 1000f, 4),
                new Account(), // imagine call to get this account failed
                new Account("Business Checking", 10000f, 4)
        );

        Observable.fromIterable(accounts)
                .flatMap($8BasicsFilterEmpty::loadAccount) // this has an error in it
                .subscribe(new Observer<Account>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("Loading accounts");
                        System.out.println("------------------");
                    }

                    @Override
                    public void onNext(Account account) {
                        System.out.println(account);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("Terminating the operation due to:");
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Accounts loaded successfully");
                    }
                });


    }


    private static void errorAndErrorHandling() {
        /**
         * The following example handles the error but allows further observables to be emitted
         */

        List<Account> accounts = Arrays.asList(
                new Account("Checking", 100f, 3),
                new Account("Saving", 100000f, 5),
                new Account("Investment", 1000f, 4),
                new Account(), // imagine call to get this account failed
                new Account("Business Checking", 10000f, 4)
        );

        Observable.fromIterable(accounts)
                .flatMap($8BasicsFilterEmpty::loadAccount) // ignore the error and continue
                .onErrorResumeNext(whenExceptionIsThenLogAndIgnore(RuntimeException.class))
                .subscribe(new Observer<Account>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("Loading accounts");
                        System.out.println("------------------");
                    }

                    @Override
                    public void onNext(Account account) {
                        System.out.println(account);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("Terminating the operation due to:");
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Accounts loaded successfully");
                    }
                });


    }

    private static Observable<? extends Account> loadAccount(Account account) {
        if (account.getName() == null) { // imagine call to get this account failed
            return Observable.error(new RuntimeException("Connection lost. Account not loaded"));
        }
        return Observable.just(account);
    }


    private static Observable<Account> extractApproachable(Account account) {
        // assume this condition call a service over the wire
        if (account.getScore() < 3) {
            return Observable.empty();
        }

        // assume this condition calls a service over the wire
        if (account.getAvailableAmount() < 5000) {
            return Observable.empty();
        }

        // account is approachable
        return Observable.just(account);
    }


    private static Observable<? extends String> addParentheses(String individualAlphabet) {
        return Observable.just("(" + individualAlphabet + ")");
    }


    public static <T> Function<Throwable, Observable<T>> whenExceptionIsThenLogAndIgnore(Class what) {
        return t -> what.isInstance(t) ? fireErrorLog(t) : breakTheChain(t);
    }

    public  static <T> Observable<T> breakTheChain(Throwable t) {
        System.out.println("\t\tBreaking the chain");
        return Observable.error(t);
    }

    public static  <T> Observable<T> fireErrorLog(Throwable t) {
        System.out.println("\t\tMaking sure chain is resilient");
        return log(t.getMessage());
    }

    public static  <T> Observable<T> log(String message) {
        System.out.println("\t\tLog Error: " + message);
        return Observable.empty();
    }



}
