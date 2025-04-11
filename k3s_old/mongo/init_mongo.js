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
db.createCollection("Users");
db.createCollection("Posts");
db.createCollection("user-timeline");
db.createCollection("HomeTimelineEntries");