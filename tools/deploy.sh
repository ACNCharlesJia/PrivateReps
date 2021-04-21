#!/usr/bin/env bash
#
# Deploy the content of _site to 'origin/<BRANCH_NAME>'

set -eu

BRANCH_NAME="SNX"
_no_branch=false

init() {
  if [[ -z ${GITHUB_ACTION+x} ]]; then
    echo "ERROR: This script is not allowed to run outside of GitHub Action."
    exit -1
  fi

  if [[ -z $(git branch -av | grep "$BRANCH_NAME") ]]; then
    _no_branch=true
    git branch "$BRANCH_NAME"
  fi
}

execute() {
	java -version
	java -jar ./tools/SnxTools-jar-with-dependencies.jar
}

deploy() {
  git config --global user.name "GitHub Actions"
  git config --global user.email "github-actions[bot]@users.noreply.github.com"

  git update-ref -d HEAD
  git add -A
  git commit -m "[Automation] SNX update No.${GITHUB_RUN_NUMBER}"

  git push -u origin "$BRANCH_NAME"
}

main() {
  init
  execute
  deploy
}

main
