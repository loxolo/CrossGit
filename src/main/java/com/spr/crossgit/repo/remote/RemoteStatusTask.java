package com.spr.crossgit.repo.remote;

import com.spr.crossgit.JGitHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.concurrent.Task;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

public class RemoteStatusTask extends Task<RemoteStatus> {

    private final Repository repo;

    RemoteStatusTask(Repository repo) {
        this.repo = repo;
    }

    @Override
    protected RemoteStatus call() throws Exception {
        return getRemoteStatus();
    }

    private RemoteStatus getRemoteStatus() throws IOException {
        final List<Ref> diffs = new ArrayList<>();
        for (Ref remote : getRemoteRefs(repo)) {
            final Ref local = repo.exactRef(remote.getName());
            if (local != null) {
                boolean isRemoteAhead = !JGitHelper.getHash(local)
                        .equals(JGitHelper.getHash(remote));
                if (isRemoteAhead) {
                    diffs.add(remote);
                }
            }
        }
        RemoteStatus status = new RemoteStatus();
        status.setAheadRefs(diffs.stream()
                .map(ref -> ref.getName().replaceAll("refs/heads/", ""))
                .sorted()
                .collect(Collectors.joining(", "))
        );
        return status;
    }

    private Collection<Ref> getRemoteRefs(Repository repo) {
        try (Git git = new Git(repo)) {
            return git.lsRemote()
                    .setHeads(true)
                    .call();
        } catch (GitAPIException ex) {
            Logger.getLogger(RemoteRepoPane.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
