var charmander = db.starters.findOne({"name": "Charmander"});
db.starters.update(
    {_id: charmander._id},
    {$set: {specialAttack: charmander.special, specialDefense: charmander.special}},
    {$unset: {special: undefined}}
);