# Worked-on-my-machine
[![Build Status](https://travis-ci.com/bryanscw/It-worked-on-my-machine-BE.svg?token=q7C1PLBif2CYVGier3sp&branch=master)](https://travis-ci.com/bryanscw/It-worked-on-my-machine-BE)

## 1. Introduction

This is the repository used for the development of the backend of CZ3003 Project, titled eduamp. 
For dependencies/deployment/testing steps, refer to the [README.md](eduamp/README.md) in eduamp

## 2. Contributing 
### 2.1 Style Guide - Java

We conform to the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). Maven can helpfully take care of that for you before you commit:

```text
$ mvn spotless:apply
```

Formatting will be checked automatically during the `verify` phase. This can be skipped temporarily:

```text
$ mvn spotless:check  # Check is automatic upon `mvn verify`
$ mvn verify -Dspotless.check.skip
```

If you're using IntelliJ, you can import [these code style settings](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml) if you'd like to use the IDE's reformat function as you develop.
