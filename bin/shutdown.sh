#!/bin/bash

script_path=$(readlink -f $0)
bin_dir=$(dirname $script_path)
root_dir=$(dirname $bin_dir)

cd $root_dir

pid_file="${root_dir}/var/client.pid"

kill `cat $pid_file`

