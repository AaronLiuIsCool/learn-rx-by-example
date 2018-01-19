package com.samples.rx.$3advance.srv;

import com.samples.rx.$1basics.domain.Account;
import com.samples.rx.$3advance.domain.SearchModel;
import com.samples.rx.$3advance.domain.SearchResult;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class $1SearchService {


    public static void main(String[] args) {

        Thread thread1 = new Thread() {
            public void run() {
                client1();
            }
        };
        Thread thread2 = new Thread() {
            public void run() {
                client2();
            }
        };
        thread1.start();
        thread2.start();
    }

    private static void client1() {
        PublishSubject<SearchModel> suggestionSubject = PublishSubject.create();

        $1SearchService $1SearchService = new $1SearchService();

        $1SearchService.hookPublishSubject(suggestionSubject);

        SearchModel searchModel = new SearchModel();

        searchModel.setText("Al");
        $1SearchService.requestSuggestion(suggestionSubject, searchModel);
        searchModel.setText("Al");
        $1SearchService.requestSuggestion(suggestionSubject, searchModel);
        searchModel.setText("Al");
        $1SearchService.requestSuggestion(suggestionSubject, searchModel);
        searchModel.setText("Ale");
        $1SearchService.requestSuggestion(suggestionSubject, searchModel);
        searchModel.setText("Alex");
        $1SearchService.requestSuggestion(suggestionSubject, searchModel);
        searchModel.setText("Alexa");
        $1SearchService.requestSuggestion(suggestionSubject, searchModel);

        $1SearchService.completeSearch(suggestionSubject, searchModel);
    }

    private static void client2() {
        PublishSubject<SearchModel> suggestionSubject = PublishSubject.create();

        $1SearchService $1SearchService = new $1SearchService();

        $1SearchService.hookPublishSubject(suggestionSubject);

        SearchModel searchModel = new SearchModel();

        searchModel.setText("S");
        $1SearchService.requestSuggestion(suggestionSubject, searchModel);
        searchModel.setText("Sc");
        $1SearchService.requestSuggestion(suggestionSubject, searchModel);
        searchModel.setText("Sco");
        $1SearchService.requestSuggestion(suggestionSubject, searchModel);
        searchModel.setText("Scot");
        $1SearchService.requestSuggestion(suggestionSubject, searchModel);

        $1SearchService.completeSearch(suggestionSubject, searchModel);
    }

    private void completeSearch(PublishSubject<SearchModel> suggestionSubject, SearchModel searchModel) {

        System.out.println("triggering search complete");

        connectSearchEngineAndSearch(searchModel)
                .subscribe(new Observer<SearchModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SearchModel searchModel) {
                        suggestionSubject.onNext(searchModel);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        suggestionSubject.onComplete();
                    }
                });


    }

    private void hookPublishSubject(PublishSubject<SearchModel> suggestionSubject) {

        suggestionSubject
                .switchMap(this::connectSearchEngineAndSearchSuggestion)
                .subscribe(new Observer<SearchModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SearchModel searchModel) {
                        System.out.println(searchModel);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Complete triggered");
                    }
                });
    }

    private void requestSuggestion(PublishSubject<SearchModel> suggestionSubject, SearchModel searchModel) {

        suggestionSubject.onNext(searchModel);
    }

    private String[] accountIds = {"Alex", "Alec", "Scott", "Scoffed", "Alexander", "Dave", "David"};
    private List<Account> accounts = new ArrayList<>();

    public $1SearchService() {

        Arrays.stream(accountIds).map(accountId -> new Account(accountId, 100, 0))
                .peek(account -> {
                    accounts.add(account);
                })
                .collect(Collectors.toList());

    }

    private Observable<SearchModel> connectSearchEngineAndSearch(SearchModel searchModel) {

        return Observable.create(e -> {
            searchModel.setResult(
                    accounts.stream()
                            .filter(account -> account.getName().contains(searchModel.getText()))
                            .map(account -> new SearchResult(account))
                            .collect(Collectors.toList()));
            e.onNext(searchModel);
            e.onComplete();
        });

    }

    private Observable<SearchModel> connectSearchEngineAndSearchSuggestion(SearchModel searchModel) {

        return Observable.create(e -> {
            searchModel.setSuggestions(
                    accounts.stream()
                            .filter(account -> account.getName().contains(searchModel.getText()))
                            .map(account -> account.getName())
                            .collect(Collectors.toList()));
            e.onNext(searchModel);
            e.onComplete();
        });

    }


}
