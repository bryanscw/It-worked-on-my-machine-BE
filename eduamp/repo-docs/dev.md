# Development Notes

This document contains the development notes.

# 1. MySQL server

As we use Docker for all shipping and developing, please ensure that MySQL is running before you build and run the code.

You can do this with the following command:

```shell script
docker run --name eduamp-mysql -e MYSQL_ROOT_PASSWORD=12345 -e MYSQL_USER=user -e MYSQL_PASSWORD=my5ql -p 3306:3306 -d mysql:latest
```
