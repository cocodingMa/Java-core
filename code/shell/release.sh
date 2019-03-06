#! /bin/sh
# chkconfig: - 85 1
# 注意要在linux下编写，否则易出错
echo "hello!!! this is begining!!"
HOST=$1
BRANCH=$2

update_java()
{
while read a b c d
do
if [[ $b = $1 ]]
then
# do someting
fi
done < /data/scripts/list.txt
}

update_php() {
while read a b c d
do
if [[ $b = $1 ]];then
# do someting
fi  
done < /data/scripts/list.txt
}

update_h5() {
while read a b c d
do
if [[ $b = $1 ]];then
# do someting
fi
done < /data/scripts/list.txt
}

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
		update_java $1 $2
		echo "java $b"
		fi
		;;

		php)
		if [ $b = $1 ]
		then
		update_php $1 $2
		echo "php $b"
		fi
		;;

		h5)
		if [ $b = $1 ]
		then
		update_h5 $1 $2
		echo "h5, $b"
		fi
		;;
	esac
	done < list.txt
	exit 0
fi
