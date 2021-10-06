#!/bin/ash

# File location: /opt/webserver/cgi-bin/admin_Info_File_Explorer.cgi
# 21-Sep-2021 Use just the file name in the header. Empty line must come after ALL the headers.
#             HTTP header names
#             must and with a colon or they are ignored. Now clicking a
#             file results in the file being downloaded instead of displayed
#             and the original filename is used.

# Get HTTP ACTION
FILEPATH="`/opt/webserver/cgi-bin/getarg path`"
FILENAME="`basename $FILEPATH`"

#Default is root directory
if [ "x$FILEPATH" = "x" ]; then
  FILEPATH="/"
fi

# Work out what we are looking at (can't cd into a file)
cd $FILEPATH
if [ "$FILEPATH" = "`pwd`" ]; then
  type="dir" 
  DIR="`pwd`" 
else
  type="file" 
fi

# Handle File download
if [ "$type" = "file" ]; then
  len="`ls -l $FILEPATH | cut -c30-42 | sed -e 's/ //g'`"
#  echo -e "Content-Type binary/octet-stream\r\n\r"
  echo "Content-Type: binary/octet-stream"
  echo "Content-Length: $length"
  echo "Content-Disposition: attachment; filename=$FILENAME; size=$len"
  echo ""
  cat "$FILEPATH"
  exit
fi

# Handle directory ACTION
if [ "$type" = "dir" ]; then

  ENTRIES="`ls`"

  echo -e "Content-Type: text/html\r\n\r"
  echo "<html><head><title>File Explorer</title>"
  echo "<link rel='STYLESHEET' href='/sharpfin.css' type='text/css' />"
  echo "</head><body><h1>$DIR</h1>"

  echo "<table border=0 cellspacing=0 cellpadding=4>"

  if [ "$DIR" = "/" ]; then
    DIR=""
  else
    PARENT="`(cd .. ; pwd)`"
    iinfo="<a href=\"/cgi-bin/admin_Info_File_Explorer.cgi?path=$PARENT\"><span style=\"font-family: monospace\">..</span></a>"
    echo "<tr><td valign=top>$iinfo</td><td></td></tr>"
  fi

  for i in $ENTRIES; do
    iinfo="`/bin/ls -lad $i | cut -c1-55`"
    ilink="<a href=\"/cgi-bin/admin_Info_File_Explorer.cgi?path=$DIR/$i\"><span style=\"font-family: monospace\">$i</span></a>"
    echo "<tr><td valign=top>$ilink</td>"
    echo "<td valign=top><span style=\"font-family: monospace\">$iinfo</span></td></tr>"
  done
  
  echo "</table>"
  echo "</body></html>"
fi
