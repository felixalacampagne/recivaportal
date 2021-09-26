#!/bin/ash
# File location: /opt/webserver/cgi-bin/Presets_edit.cgi
# 21-Sep-2021 WM201 requires the "station id" of the presets to be different otherwise all presets
#             load the last preset even if the preset name displayed is correct.
ACTION="`/opt/webserver/cgi-bin/getarg action`"
URL="`/opt/webserver/cgi-bin/getarg url`"
PLACE="`/opt/webserver/cgi-bin/getarg place`"
NAME="`/opt/webserver/cgi-bin/getarg name`"

#
# Show Form
#

echo -e "Content-type text/html\r\n\r"
echo "<html><head><title>Store Preset</title>"
echo "<link rel='STYLESHEET' href='/sharpfin.css' type='text/css' />"
echo "</head>"
echo "<body>"


if [ ! "$ACTION" = "" ]; then
   LINE1="<preset type='PresetType:Internet' id='$PLACE'><station id='350$PLACE'>"
   LINE2="<data><stream><url>$URL</url>"
   LINE3="<title>$NAME</title></stream></data></station></preset>"
   echo $LINE1 > /root/config/preset$PLACE.xml
   echo $LINE2 >> /root/config/preset$PLACE.xml
   echo $LINE3 >> /root/config/preset$PLACE.xml
   echo "<h1>Storing Preset...</h1>"
   echo "<h2>Preset $PLACE</h2>"
   echo "<pre>"
   echo "</pre>"
   echo "<p><i>... done ... </i></p>"

else
  URL=`grep url /root/config/preset$PLACE.xml | cut -b 0-1000 | awk -F"</url>" '{print $1}' | awk -F"<url>" '{print $2}'`
  echo "<h1>Edit Presets</h1>"
  echo "<p>This interface allows you to edit local presets on your radio. "
  echo "Remember that these presets are only localy availbale! </p>"
  echo ""
  echo "<p>Note: Dollar Sign in URL/Name does NOT work!</br>"
  echo "Formats supported for URL: wma, mp3, ... (try it!)</p>"
  echo ""
  echo "<form method=\"get\" action=\"/cgi-bin/Presets_edit.cgi\">"
  echo "<table style=\"width: 80%;\" border=\"0\">"
  echo "<tr>"
  echo "<td width=\"160\">Title for Preset:</td><td><input name=\"name\" type=\"text\" value=\"$NAME\" size=\"40\" /><br/></td></tr>"
  echo "<tr><td width=\"160\">URL:</td><td><input name=\"url\" type=\"text\" value=\"$URL\" size=\"40\" /><br/></td></tr>"
  echo "<tr><td width=\"160\">Preset Store No.:</td><td><input name=\"place\" type=\"text\" value=\"$PLACE\" size=\"5\" maxlength=\"2\" /><br/></td></tr>"
  echo "</table>"
  echo " <input type=\"hidden\" name=\"action\" value=\"program\" />"
  echo " <input type=\"submit\" value=\"Store\" />"
  echo "</form>"
fi

echo "</body></html>"
