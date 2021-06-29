// Type PREMIUM is renamed VIP. It a change we want to do during downtime.

db.users.updateMany(
    {type: "PREMIUM"},
    {$set: {type: "VIP"}}
);