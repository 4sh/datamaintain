var DB = db.executedScripts;

DB.updateMany({executionStatus: {$in:['OK', 'KO']}}, {$set: {action: 'RUN'}});
DB.updateMany({executionStatus: 'FORCE_MARKED_AS_EXECUTED'}, {$set: {action: 'MARK_AS_EXECUTED'}});