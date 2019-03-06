#! /bin/sh
# chkconfig: - 85 1
echo "hello!!! this is begining!!"
HOST=$1
BRANCH=$2
if [ $# -ne 2 ]
then
	echo "error: wrong parameters"
	exit 0
else
	while read a b c d
	do
	case "$d" in
	
		java)
		if [ $b = $1 ]
		then
		echo "java $b"
		fi
		;;

		php)
		if [ $b = $1 ]
		then
		echo "php $b"
		fi
		;;

		h5)
		if [ $b = $1 ]
		then
		echo "h5, $b"
		fi
		;;
	esac
	done < list.txt
	exit 0
fi
