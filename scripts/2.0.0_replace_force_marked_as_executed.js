var DB = db.executedScripts;

DB.updateMany({executionStatus: 'FORCE_MARKED_AS_EXECUTED'}, {$set: {executionStatus: 'OK', action: 'MARK_AS_EXECUTED'}});