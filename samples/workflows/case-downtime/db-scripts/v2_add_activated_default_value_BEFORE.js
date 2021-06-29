// Add a new property on users. It is a change we can do before deploying v2.

db.users.updateMany(
    {},
    {$set: {activated: true}}
);