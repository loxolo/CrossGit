package com.spr.crossgit.branches;

import com.spr.crossgit.IBranchListener;
import com.spr.crossgit.screen.MainScreen;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

public class BranchesPane {

    private final VBox pane = new VBox();
    private final SortButtonBar sortBar;
    private final BranchesList branchesList = new BranchesList();
    private BranchesTask task;
    private ExecutorService executor;
    private final MainScreen app;
    private Repository repo;
    private BranchesInfo branchesInfo;

    public BranchesPane(MainScreen app) {
        this.app = app;
        sortBar = new SortButtonBar(this);
        sortBar.setPrefHeight(28.0);
        VBox.setVgrow(branchesList, Priority.ALWAYS);
        pane.getChildren().addAll(branchesList, sortBar.node());
    }

    public Node node() {
        return pane;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
        if (task != null && task.isRunning()) {
            task.cancel();
        }
        branchesList.setItems(FXCollections.emptyObservableList());
        task = new BranchesTask(repo);
        task.setOnSucceeded((WorkerStateEvent event) -> {
            branchesInfo = task.getValue();
            final ObservableList<Ref> branches = 
                    branchesInfo.getRefsList(SortOrder.getValue());
            branchesList.setItems(repo, branches);
            app.setBranches(branchesInfo);
        });
        if (executor != null && !executor.isTerminated()) {
            executor.shutdownNow();
        }
        executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
        executor.shutdown();
    }

    void setSortOrder(SortOrder sortOrder) {
        SortOrder.setValue(sortOrder);
        branchesList.setItems(repo, branchesInfo.getRefsList(sortOrder));
        branchesList.scrollTo(0);
        branchesList.requestFocus();
    }

    public void addListener(IBranchListener listener) {
        branchesList.addListener(listener);
    }
}
