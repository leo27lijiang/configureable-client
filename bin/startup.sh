#!/bin/sh

# Base path
script_path=$(readlink -f $0)
bin_dir=$(dirname $script_path)
root_dir=$(dirname $bin_dir)

cd $root_dir

conf_dir="./conf"
lib_dir="./lib"
log_dir="./logs"
var_dir="./var"

if [ ! -d "$log_dir" ]; then
    $(mkdir "$log_dir")
fi

if [ ! -d "$var_dir" ]; then
    $(mkdir "$var_dir")
fi

cp=".:${conf_dir}"
for f in ${lib_dir}/*.jar ; do
  cp="${cp}:${f}"
done

pid_file="${var_dir}/client.pid"
output_file="${log_dir}/client.out"

java_opts="-Xms1g -Xmx2g -XX:MaxPermSize=64m"

main_class="com.lefu.databus.client.ConfigurableClient"

cmdline="java -cp ${cp} ${java_opts} ${main_class}"

echo $cmdline
$cmdline 2>&1 > ${output_file} &
echo $! > ${pid_file}
