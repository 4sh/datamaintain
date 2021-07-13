docker run --rm --volume $PATH_TO_DATAMAINTAIN_PROJECT/samples/workflows/case-downtime:/case-downtime --network=host docker.pkg.github.com/4sh/datamaintain/datamaintain:2.0-mongo-4.4 \
--config-file-path /case-downtime/datamaintain.properties \
update-db \
--tag AFTER=/scripts/*_AFTER.js \
--whitelisted-tags AFTER \