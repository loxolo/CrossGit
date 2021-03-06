package com.spr.crossgit.screen;

import com.spr.crossgit.GitCommit;
import com.spr.crossgit.IScreen;
import com.spr.crossgit.MainApp;
import com.spr.crossgit.ScreenController;
import com.spr.crossgit.branches.BranchesPane;
import com.spr.crossgit.branches.BranchesInfo;
import com.spr.crossgit.changeset.ChangeSetPane;
import com.spr.crossgit.commits.CommitsPane;
import com.spr.crossgit.repo.local.LocalRepoPane;
import com.spr.crossgit.repo.remote.RemoteRepoPane;
import com.spr.crossgit.tags.TagsPane;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.eclipse.jgit.lib.Repository;

public class MainScreen implements IScreen {

    private final BorderPane root = new BorderPane();

    private Repository repo;
    private final CommitsPane commitsPane;
    private final BranchesPane branchesPane;
    private final ChangeSetPane changesetPane;
    private final LocalRepoPane localRepoPane;
    private final RemoteRepoPane remoteRepoPane;
    private final TagsPane tagsPane;
    private final Tab branchesTab = new Tab("Branches: ...");
    private final Tab tagsTab = new Tab("Tags: ...");
    private final TabPane sidebar = new TabPane();
    private final SplitPane splitPane = new SplitPane();

    public MainScreen() {
        
        commitsPane = new CommitsPane(this);
        branchesPane = new BranchesPane(this);
        changesetPane = new ChangeSetPane();
        localRepoPane = new LocalRepoPane(this);
        remoteRepoPane = new RemoteRepoPane();
        tagsPane = new TagsPane(this);
        
        branchesPane.addListener(commitsPane);
                
        sidebar.setVisible(false);
        splitPane.setVisible(false);

        // repo bar.
        HBox.setHgrow(localRepoPane, Priority.NEVER);
        HBox.setHgrow(remoteRepoPane, Priority.ALWAYS);
        HBox toolbar = new HBox(localRepoPane, remoteRepoPane);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // tabpane
        VBox.setVgrow(branchesPane.node(), Priority.ALWAYS);
        VBox.setVgrow(tagsPane, Priority.ALWAYS);
        branchesTab.setClosable(false);
        final VBox branchesBox = new VBox(branchesPane.node());
        branchesTab.setContent(branchesBox);
        sidebar.getTabs().add(branchesTab);
        tagsTab.setClosable(false);
        final VBox tagsBox = new VBox(tagsPane);
        tagsTab.setContent(tagsBox);
        sidebar.getTabs().add(tagsTab);

        // main content SplitPane
        final StackPane sp1 = new StackPane(commitsPane);
        sp1.setStyle("-fx-background-color: transparent;");
        final StackPane sp2 = new StackPane(changesetPane);
        sp2.setStyle("-fx-background-color: transparent;");
        splitPane.setStyle("-fx-background-color: transparent;");
        splitPane.getItems().addAll(sp1, sp2);
        splitPane.setDividerPositions(0.7f);
        splitPane.setOrientation(Orientation.VERTICAL);

        // Layout        
        root.setTop(toolbar);
        root.setLeft(sidebar);
        root.setCenter(splitPane);

        ScreenController.getStage().iconifiedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Logger.getLogger(MainApp.class.getName())
                        .log(Level.INFO, "ClientApp.iconifiedProperty.changed()");
                setRepoStatus();
            }
        });

        ScreenController.getStage().focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue == true) {
                    Logger.getLogger(MainApp.class.getName())
                            .log(Level.INFO, "ClientApp.focusedProperty.changed()");
                    setRepoStatus();
                }
            }
        });
    }

    private void setRepoStatus() {
        if (repo != null) {
            try {
                localRepoPane.setRepo();
                remoteRepoPane.setRepo(repo);
            } catch (IOException ex) {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            
            }
        }
    }

    public void onSelectCommit(GitCommit newValue) {
        if (newValue != null) {
            changesetPane.setCommit(repo, newValue);
        }
    }

    public void setTagsTotal(int size) {
        tagsTab.setText("Tags: " + size);
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
        branchesTab.setText("Branches: ...");
        branchesPane.setRepo(repo);
        tagsTab.setText("Tags: ...");
        tagsPane.setRepo(repo);
        setRepoStatus();
        sidebar.setVisible(true);
        splitPane.setVisible(true);
    }

    public void setBranches(BranchesInfo info) {
        branchesTab.setText("Branches: " + info.getBranchesTotal());
        commitsPane.setRepo(repo, info);
    }

    @Override
    public Parent getRoot() {
        return root;
    }

}
