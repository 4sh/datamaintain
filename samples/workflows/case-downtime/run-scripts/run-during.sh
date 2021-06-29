docker run --rm --volume $PATH_TO_DATAMAINTAIN_PROJECT/samples/workflows/case-downtime/db-scripts:/scripts --network=host docker.pkg.github.com/4sh/datamaintain/datamaintain:1.2.0-mongo-4.4 \
--db-type mongo \
--db-uri mongodb://localhost:27017/case-downtime \
update-db \
--path /scripts \
--identifier-regex "(v\d*).*" \
--tag DURING=/scripts/*_DURING.js \
--whitelisted-tags DURING \
--verbose