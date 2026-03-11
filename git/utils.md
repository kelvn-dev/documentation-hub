# Docker command utils

Common git commands, tips and tricks

## Command

- Config
  ```
  git config -g push.autosetupremote true
  ```

  ```
  git config -g core.pager cat
  ```

  ```
  git log -5
  ```

## Merge vs rebase vs cherry-pick

Suppose you create a new branch from the main branch then you add two commits, meanwhile another developer pushes new commits to main
```
A --- B --- C --- F --- G   (main)
           \
            D --- E   (feature)
```

If stand at feature branch and do git rebase main, git will remove commits D and E, Move the branch to G and replay commits D and E on top

```
A --- B --- C --- F --- G --- D --- E   (feature)
```

If do git merge main, git create a new commit with all changes from commit F and G

```
A --- B --- C --- D --- E --- H (F + g)   (feature)
```

So git merge create 1 new commit contain all the changes from all new commits, while rebase make the history linear by combining all commits together

Git cherry-pick applies a specific commit from one branch onto another branch. If apply 1 commit by `git cherry-pick 6dea77a`, git create 1 new commit with new hash. If cherry-pick a range of commits by `git cherry-pick 6dea77a^..6dea77c`, git create multiple commits

Note that git will exclude start commit if not specify `^`