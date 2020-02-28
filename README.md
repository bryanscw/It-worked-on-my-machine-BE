# Worked-on-my-machine
[![Build Status](https://travis-ci.com/bryanscw/travis-ci-tutorial-java.svg?branch=master)](https://travis-ci.com/bryanscw/travis-ci-tutorial-java)
[![Code Coverage](https://codecov.io/github/bryanscw/travis-ci-tutorial-java/coverage.svg)](https://codecov.io/gh/bryanscw/travis-ci-tutorial-java)

## 3. Contributing 
### 3.1 Style Guide - Java

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
