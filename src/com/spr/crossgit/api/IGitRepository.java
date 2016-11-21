package com.spr.crossgit.api;

import com.spr.crossgit.GitCommit;
import com.spr.crossgit.branches.BranchesInfo;
import com.spr.crossgit.changeset.ChangeSetFile;
import javafx.collections.ObservableList;
import org.eclipse.jgit.lib.Ref;

public interface IGitRepository {
    public String getBranch();
    public boolean hasUnCommittedChanges();
    public String getRemoteUrl();
    public Iterable<Ref> getRemoteRefs();
    public Ref getRef(String name);
    public ObservableList<ChangeSetFile> getChangesetFiles(GitCommit gitCommit);
    public BranchesInfo getBranches();
    public ObservableList<Ref> getTags();
    public ObservableList<GitCommit> getAllCommits(BranchesInfo branches);
    public ObservableList<String> getChangesetFiles();
    public String getUnifiedDiff(String filePath);
}