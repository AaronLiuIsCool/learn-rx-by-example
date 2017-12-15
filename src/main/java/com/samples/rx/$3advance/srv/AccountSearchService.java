package com.samples.rx.$3advance.srv;

import com.samples.rx.$1basics.domain.Account;
import com.samples.rx.$3advance.domain.SearchModel;
import com.samples.rx.$3advance.domain.SearchResult;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@SuppressWarnings("Duplicates")
@Service
public class AccountSearchService {

    private String[] accountIds = {"Alex", "Alec", "Scott", "Scorpions", "Santa", "Alexander", "Dave", "David"};
    private List<Account> accounts = new ArrayList<>();


    public Observable<SearchModel> search(SearchModel searchModel) {
        return connectSearchEngineAndSearch(searchModel);
    }

    public Observable<SearchModel> searchSuggestion(SearchModel searchModel) {
        return connectSearchEngineAndSearchSuggestion(searchModel);
    }



    private AccountSearchService() {

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
                            .map(account -> new SearchResult(account, null))
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
