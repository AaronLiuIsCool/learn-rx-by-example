package com.samples.rx.$3advance.domain;

import java.util.ArrayList;
import java.util.List;

public class SearchModel extends DataModel {

    private String text = "al";
    private int page;
    private boolean suggestion;

    private List<String> suggestions;
    private List<SearchResult> result = new ArrayList<>();

    public SearchModel() {
    }

    public SearchModel(String text) {
        this.text = text;
    }

    public SearchModel(String text, int page) {
        this.text = text;
        this.page = page;
    }

    public boolean isSuggestion() {
        return suggestion;
    }

    public void setSuggestion(boolean suggestion) {
        this.suggestion = suggestion;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public List<SearchResult> getResult() {
        return result;
    }

    public void setResult(List<SearchResult> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "SearchModel{" +
                "text='" + text + '\'' +
                ", page=" + page +
                ", suggestions=" + suggestions +
                ", result=" + result +
                '}';
    }
}
