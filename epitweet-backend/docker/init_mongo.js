db.createUser(
    {
        user: "admin",
        pwd: "admin",
        roles:[
            {
                role: "readWrite",
                db:   "Epitweet"
            }
        ]
    }
);
db.createCollection("Purchases");
db.createCollection("Products");