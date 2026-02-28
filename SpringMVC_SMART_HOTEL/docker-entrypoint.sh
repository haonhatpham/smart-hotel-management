#!/bin/sh
set -e
# Railway/Render cung cấp biến PORT
if [ -n "$PORT" ]; then
  sed -i "s/8080/$PORT/g" /usr/local/tomcat/conf/server.xml
fi
# Cấu hình từ biến môi trường
CATALINA_OPTS="$CATALINA_OPTS"
[ -n "$FRONTEND_BASE_URL" ] && CATALINA_OPTS="$CATALINA_OPTS -Dfrontend.baseUrl=$FRONTEND_BASE_URL"
[ -n "$BACKEND_BASE_URL" ] && CATALINA_OPTS="$CATALINA_OPTS -Dvnpay.returnUrl=$BACKEND_BASE_URL/SpringMVC_SMART_HOTEL/api/callback/vnpay -Dmomo.redirectUrl=$BACKEND_BASE_URL/SpringMVC_SMART_HOTEL/api/callback/momo/guest -Dmomo.ipnUrl=$BACKEND_BASE_URL/SpringMVC_SMART_HOTEL/api/callback/momo"
[ -n "$GEMINI_API_KEY" ] && CATALINA_OPTS="$CATALINA_OPTS -Dgemini.api.key=$GEMINI_API_KEY"
[ -n "$MOMO_PARTNER_CODE" ] && CATALINA_OPTS="$CATALINA_OPTS -Dmomo.partnerCode=$MOMO_PARTNER_CODE"
[ -n "$MOMO_ACCESS_KEY" ] && CATALINA_OPTS="$CATALINA_OPTS -Dmomo.accessKey=$MOMO_ACCESS_KEY"
[ -n "$MOMO_SECRET_KEY" ] && CATALINA_OPTS="$CATALINA_OPTS -Dmomo.secretKey=$MOMO_SECRET_KEY"
[ -n "$VNPAY_TMN_CODE" ] && CATALINA_OPTS="$CATALINA_OPTS -Dvnpay.tmnCode=$VNPAY_TMN_CODE"
[ -n "$VNPAY_HASH_SECRET" ] && CATALINA_OPTS="$CATALINA_OPTS -Dvnpay.hashSecret=$VNPAY_HASH_SECRET"
# Railway MySQL: dùng MYSQLHOST,... nếu có; không thì dùng HIBERNATE_CONNECTION_*
if [ -n "$MYSQLHOST" ]; then
  _db="${MYSQL_DATABASE_OVERRIDE:-$MYSQLDATABASE}"
  _url="jdbc:mysql://${MYSQLHOST}:${MYSQLPORT:-3306}/${_db}?allowPublicKeyRetrieval=true&useSSL=true&requireSSL=true&serverTimezone=UTC"
  CATALINA_OPTS="$CATALINA_OPTS -Dhibernate.connection.url=$_url -Dhibernate.connection.username=$MYSQLUSER -Dhibernate.connection.password=$MYSQLPASSWORD"
else
  [ -n "$HIBERNATE_CONNECTION_URL" ] && CATALINA_OPTS="$CATALINA_OPTS -Dhibernate.connection.url=$HIBERNATE_CONNECTION_URL"
  [ -n "$HIBERNATE_CONNECTION_USERNAME" ] && CATALINA_OPTS="$CATALINA_OPTS -Dhibernate.connection.username=$HIBERNATE_CONNECTION_USERNAME"
  [ -n "$HIBERNATE_CONNECTION_PASSWORD" ] && CATALINA_OPTS="$CATALINA_OPTS -Dhibernate.connection.password=$HIBERNATE_CONNECTION_PASSWORD"
fi
export CATALINA_OPTS
exec catalina.sh run
