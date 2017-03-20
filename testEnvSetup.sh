#!/usr/bin/env bash

dd if=/dev/zero of=dummy_file1.txt count=1024 bs=1024
dd if=/dev/zero of=dummy_file2.txt count=2048 bs=1024
rm -rf PeerDownloads/*
mv dummy_file1.txt PeerDownloads/
mv dummy_file2.txt PeerDownloads/