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


public class $2SearchService {

    PublishSubject<SearchModel> searchSubject = PublishSubject.create();


    public static void main(String[] args) {

        Thread thread1 = new Thread(() -> {
            try {
                SearchModel searchModel = client1();
                System.out.println(searchModel);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                SearchModel searchModel = client2();
                System.out.println(searchModel);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread thread3 = new Thread(() -> {
            try {
                SearchModel searchModel = client3();
                System.out.println(searchModel);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();
    }

    private static SearchModel client1() throws InterruptedException {

        $2SearchService searchService = new $2SearchService();

        searchService.hookPublishSubject();

        SearchModel searchModel = new SearchModel();

        searchModel.setText("Al");
        searchService.requestSuggestion(searchModel);
        Thread.sleep(300);
        searchModel.setText("Al");
        searchService.requestSuggestion(searchModel);
        Thread.sleep(300);
        searchModel.setText("Al");
        searchService.requestSuggestion(searchModel);
        Thread.sleep(300);
        searchModel.setText("Ale");
        searchService.requestSuggestion(searchModel);
        Thread.sleep(300);
        searchModel.setText("Alex");
        searchService.requestSuggestion(searchModel);
        Thread.sleep(500);
        searchModel.setText("Alexa");
        searchService.requestSuggestion(searchModel);

        searchService.completeSearch(searchModel);

        return searchModel;
    }

    private static SearchModel client2() throws InterruptedException {
        PublishSubject<SearchModel> suggestionSubject = PublishSubject.create();

        $2SearchService searchService = new $2SearchService();

        searchService.hookPublishSubject();

        SearchModel searchModel = new SearchModel();

        searchModel.setText("S");
        searchService.requestSuggestion(searchModel);
        Thread.sleep(300);

        searchModel.setText("Sc");
        searchService.requestSuggestion(searchModel);
        Thread.sleep(300);

        searchModel.setText("Sco");
        searchService.requestSuggestion(searchModel);

        Thread.sleep(500);
        searchService.completeSearch(searchModel);

        return searchModel;
    }

    private static SearchModel client3() throws InterruptedException {
        PublishSubject<SearchModel> suggestionSubject = PublishSubject.create();

        $2SearchService searchService = new $2SearchService();

        searchService.hookPublishSubject();

        SearchModel searchModel = new SearchModel();

        searchModel.setText("c");
        searchService.requestSuggestion(searchModel);
        Thread.sleep(100);

        searchModel.setText("co");
        searchService.requestSuggestion(searchModel);
        Thread.sleep(100);

        searchModel.setText("cot");
        searchService.requestSuggestion(searchModel);

        Thread.sleep(200);
        searchService.completeSearch(searchModel);

        return searchModel;
    }

    private void completeSearch(SearchModel searchModel) {

        System.out.println("triggering search complete");

        connectSearchEngineAndSearch(searchModel)
                .subscribe(new Observer<SearchModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SearchModel searchModel) {
                        searchSubject.onNext(searchModel);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        searchSubject.onComplete();
                    }
                });


    }

    private void hookPublishSubject() {

        searchSubject
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

    private void requestSuggestion( SearchModel searchModel) {

        searchSubject.onNext(searchModel);
    }

    private String[] accountIds = {"Alex", "Alec", "Scott", "Scorpions", "Santa", "Alexander", "Dave", "David"};
    private List<Account> accounts = new ArrayList<>();

    public $2SearchService() {

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
