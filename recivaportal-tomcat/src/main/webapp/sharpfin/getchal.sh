SREAD=/mnt/config/sread
LOG=/tmp/log/getchal.log

dosread()
{
chal=$1
echo Challenge:>>$LOG
echo $chal >>$LOG
$SREAD -c $chal >>$LOG
}

killall sernum
/usr/bin/sernum --daemon
mkdir -p /tmp/log
echo SREAD Challenges>$LOG
while read line; do dosread $line; done << EOF
3030303030303030
3030303030303031
3030303030303032
3030303030303033
3030303030303034
3030303030303035
3030303030303036
3030303030303037
3030303030303038
3030303030303039
303030303030303A
303030303030303B
303030303030303C
303030303030303D
303030303030303E
303030303030303F
3030303030303130
3030303030303131
3030303030303132
3030303030303133
3030303030303134
EOF

echo Challenges written to $LOG