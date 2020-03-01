# Development Notes

This document contains development notes that contributors might find helpful.

# 1. MySQL server

As we use Docker for all shipping and developing, please ensure that MySQL is running before you build and run the code.

You can do this with the following command:

```shell script
docker run --name eduamp-mysql -e MYSQL_ROOT_PASSWORD=12345 -e MYSQL_USER=user -e MYSQL_PASSWORD=my5ql -p 3306:3306 -d mysql:latest
```

# 2. Naming Tests
Test names follow the following format: `should_expectedBehavior_stateUnderTest`.

A combination of **snake_case** and **camelCase** is used.  

## 2.1 Examples

1. should_allow_ifValidCredentials
2. should_reject_ifInvalidCredentials
3. should_failToWithdrawMoney_forInvalidAccount
4. should_throwException_ifAgeLessThan18
5. should_failToAdmit_ifMandatoryFieldsAreMissing
