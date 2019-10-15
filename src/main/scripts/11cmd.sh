#!/bin/bash
for arg in "$@"; do
  echo 'Execute command:' "$arg"
  eval "$arg"
done