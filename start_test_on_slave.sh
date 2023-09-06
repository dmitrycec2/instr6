#!/usr/bin/env bash
#Script created to launch Jmeter tests directly from the current terminal without accessing the jmeter master pod.
#It requires that you supply the path to the jmx file
#After execution, test script jmx file may be deleted from the pod itself but not locally.

working_dir="`pwd`"

#Get namesapce variable
tenant=`awk '{print $NF}' "$working_dir/tenant_export"`

jmx="$1"
profile="$3"
[ -n "$jmx" ] || read -p 'Enter path to the jmx file ' jmx

if [ ! -f "$jmx" ];
then
    echo "Test script file was not found in PATH"
    echo "Kindly check and input the correct file path"
    exit
fi


IFS="," read -a slavearray <<< $2
echo "My array: ${slavearray[@]}"
slavenum=${#slavearray[@]}
echo "slavesnum  "$slavenum
slavedigit="${#slavenum}"
echo "slavedigit  "$slavedigit
printf "Number of slaves is %s\n" "${slavesnum}"


test_name=$(basename ${jmx%.jmx})
data_path=${jmx%.jmx}
echo "data_path ${data_path}"


# Split and upload csv files
printf "data_path is %s\n" "${data_path}"
for csvfilefull in "${data_path}"/*.csv	
do
		csvfile="${csvfilefull##*/}"
		printf "Processing %s\n" "$csvfile"
		split --suffix-length="${slavedigit}" --additional-suffix=.csv -d --number="l/${slavenum}" "${data_path}/${csvfile}" "$data_path"/
		cp "${data_path}/${i}.csv" "${slavearray[j]}":/opt/$jmeter/bin/${product_name}/${data_path}/${csvfile}
done
	