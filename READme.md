## Rate limiting using Bucket4j, redis, Postgres on Springboot

# Example provides limiting a specific uri path using a username supplied in the request body.

We query the tps from the db using the username and path, the use username as the key, 
and tps the limiting capacity. the username, path and tps are store in redis for faster retrieval.

## Achievement: 

Limit specific api based on identifiable request (username) and a custom tps for every user.
