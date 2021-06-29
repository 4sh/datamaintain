// Remove phone on users because not need after the deployment of v2

db.users.updateMany(
    {},
    {$unset: {phone: ""}}
);