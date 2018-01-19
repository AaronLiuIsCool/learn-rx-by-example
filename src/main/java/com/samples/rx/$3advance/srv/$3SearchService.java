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


@SuppressWarnings("Duplicates")
public class $3SearchService {

    PublishSubject<SearchModel> searchSubject = PublishSubject.create();


    public static void main(String[] args) throws InterruptedException {

        client1();

    }

    private static void client1() throws InterruptedException {

        $3SearchService searchService = new $3SearchService();

        searchService.listen(searchService.observeSearch());

        searchService.requestSuggestion(new SearchModel("Al"));
        Thread.sleep(300);
        searchService.requestSuggestion(new SearchModel("Al"));
        Thread.sleep(300);
        searchService.requestSuggestion(new SearchModel("Al"));
        Thread.sleep(300);
        searchService.requestSuggestion(new SearchModel("Ale"));
        Thread.sleep(300);
        searchService.requestSuggestion(new SearchModel("Alex"));
        Thread.sleep(500);


        SearchModel finalSearchModel = new SearchModel("Alexa");
        searchService.requestSuggestion(finalSearchModel);
        searchService.completeSearch(finalSearchModel);

    }



    private void completeSearch(SearchModel finalSearchModel) {

        connectSearchEngineAndSearch(finalSearchModel)
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

    private void listen(Observer<SearchModel> observer) {

        searchSubject
                .distinctUntilChanged((searchModel1, searchModel2) -> searchModel1.getText().equals(searchModel2.getText())) // ignore repeated searches
                .switchMap(this::connectSearchEngineAndSearchSuggestion)
                .subscribe(observer);

    }

    private Observer<SearchModel> observeSearch() {
        return new Observer<SearchModel>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(SearchModel searchModel) {
                System.out.println("=> "+searchModel);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                System.out.println("Complete triggered");
            }
        };
    }

    private void requestSuggestion(SearchModel searchModel) {
        searchSubject.onNext(searchModel);
    }



    private String[] accountIds = {"Alex", "Alec", "Scott", "Scorpions", "Santa", "Alexander", "Dave", "David"};
    private List<Account> accounts = new ArrayList<>();

    public $3SearchService() {

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
