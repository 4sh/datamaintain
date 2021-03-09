#!/usr/bin/env bash
set -eEuox pipefail

# Lock
is_locked=0
lock_id=datamaintain
lock_collection=datamaintainLock

mongo_eval() {
  mongo "$MONGO_URI" --quiet --eval "$1"
}

echo "Start datamaintain"
# loop while not locked
while [ "$is_locked" -eq 0 ]
  do
    echo "Remove expired lock"
    mongo_eval "db.getCollection(\"$lock_collection\").remove({_id: \"$lock_id\", expire : {\$lt : ISODate()}})"

    echo "Trying to acquire lock"
    # Insert a lock that will expire in $LOCK_EXPIRE_IN_MINUTES minutes.
    try_lock_response=$(mongo_eval "db.getCollection(\"$lock_collection\").insert({_id: \"$lock_id\", expire : new Date(ISODate().getTime() + 1000 * 60 * $LOCK_EXPIRE_IN_MINUTES)})")

    if [[ $try_lock_response == *'"nInserted" : 1'* ]]
    then
      # "nInserted" : 1 = insert successfully
      is_locked=1;
      echo "Acquired lock successfully"
    elif [[ $try_lock_response == *'duplicate key error collection'* ]]
    then
      # Mongo error : a document with the same id already exist. So another Datamaintain has the lock
      echo "Another Datamaintain has the lock, retry in $SLEEP_BEFORE_RETRY_MILLI"
      sleep "$SLEEP_BEFORE_RETRY_MILLI";
      continue
    else
      echo "Do not know what do to with mongo message. Message : $try_lock_response"
      exit 1
    fi;
  done

# Execute datamaintain
{
  "/code/datamaintain/bin/cli" \
    --db-type mongo \
    --mongo-uri "$MONGO_URI" \
    update-db \
      --path /workspace/scripts \
      --identifier-regex "(.*)" \
      --mongo-print-output
} || {
  echo "Error while executing Datamaintain !"
  echo "Remove datamaintain lock"
  mongo_eval "db.getCollection(\"$lock_collection\").remove({_id: \"$lock_id\"})"
  exit 1
}

echo
echo "Remove datamaintain lock"
mongo_eval "db.getCollection(\"$lock_collection\").remove({_id: \"$lock_id\"})"
