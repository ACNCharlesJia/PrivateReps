#!/usr/bin/env bash
#
# Deploy the content of _site to 'origin/<BRANCH_NAME>'

set -eu

BRANCH_NAME="SNX"
_no_branch=false
_backup_dir="$(mktemp -d)"

init() {
  if [[ -z ${GITHUB_ACTION+x} ]]; then
    echo "ERROR: This script is not allowed to run outside of GitHub Action."
    exit -1
  fi

  git push -d origin "$BRANCH_NAME"

  if [[ -z $(git branch -av | grep "$BRANCH_NAME") ]]; then
    _no_branch=true
    git checkout -b "$BRANCH_NAME"
  else
    git checkout "$BRANCH_NAME"
  fi
}

backup() {
  mv *.snx "$_backup_dir"
  mv .git "$_backup_dir"
}

flush() {
  rm -rf ./*
  rm -rf .[^.] .??*

  shopt -s dotglob nullglob
  mv "$_backup_dir"/* .
}

deploy() {
  git config --global user.name "GitHub Actions"
  git config --global user.email "github-actions[bot]@users.noreply.github.com"

  git update-ref -d HEAD
  git add -A
  git commit -m "[Automation] SNX update No.${GITHUB_RUN_NUMBER}"

  if $_no_branch; then
    git push -u origin "$BRANCH_NAME"
  else
    git push -f
  fi
}

main() {
  init
  backup
  flush
  deploy
}

main
