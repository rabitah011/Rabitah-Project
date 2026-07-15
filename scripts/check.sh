#!/usr/bin/env sh
set -eu
if command -v xvfb-run >/dev/null 2>&1; then
  xvfb-run -a mvn clean test
else
  mvn clean test
fi
