mvn install
if [ "$INIT_DB" = true ]; then
    migrate --path=./migrations bootstrap
fi
migrate --path=./migrations up
mvn compile