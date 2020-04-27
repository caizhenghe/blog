#!/usr/bin/env python
# coding=utf-8

# User Guide:
# chmod 777 bmmgit.py
# ./bmmgit.py co <branch>  将所有仓库切换到<branch>分支
# ./bmmgit.py del <branch> 删除所有仓库的<branch>分支
# ./bmmgit.py push         将所有仓库的当前分支push到对应的远程分支
# ./bmmgit.py pr           更新所有仓库的当前分支

import os
import sys

repos = ["./", "../mediautils", "../opengldecoder", "../sponge"]

def git_checkout(path):
    os.chdir(path)
    os.system("git branch > ./branch")
    file = open("./branch")
    target_branch = str(sys.argv[2])
    source_branch = ""
    target_branch_exist = False
    for line in file:
        branchArgs = line.split(" ")
        if branchArgs[0] is '*':
            source_branch = branch = branchArgs[1].split('\n')[0]
        else:
            branch = branchArgs[2].split('\n')[0]

        if branch == target_branch:
            target_branch_exist = True

    if source_branch == target_branch:
        print "Target branch equals source branch, no need to checkout!"
        os.remove("./branch")
        return

    if target_branch_exist:
        os.system("git checkout " + target_branch)
    else:
        os.system("git checkout -b " + target_branch)
    os.remove("./branch")

def git_push(path):
    os.chdir(path)
    os.system("git push origin HEAD -f")

def git_delete(path):
    os.chdir(path)
    target_branch = str(sys.argv[2])
    os.system("git branch -D " + target_branch)

def git_pr(path):
    os.chdir(path)
    os.system("git pull --rebase")

if __name__ == '__main__':
    if len(sys.argv) < 2 \
            or sys.argv[1] == "co" and len(sys.argv) < 3\
            or sys.argv[1] == "del" and len(sys.argv) < 3:
        print "Argument illegal: " + str(sys.argv)
        sys.exit()

    for repo_path in repos:
        if sys.argv[1] == "co":
            git_checkout(repo_path)
        elif sys.argv[1] == "push":
            git_push(repo_path)
        elif sys.argv[1] == "del":
            git_delete(repo_path)
        elif sys.argv[1] == "pr":
            git_pr(repo_path)
        else:
            print "Unknown command: " + sys.argv[1]





