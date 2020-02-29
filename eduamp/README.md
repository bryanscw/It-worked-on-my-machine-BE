# EduAmp

## 1. Insert some title here 1

## 2. Insert some title here 2

## 3. Warning(s)

EduaAmp uses *JDK11*. A few warnings can be safely ignored.

```text
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by org.springframework.cglib.core.ReflectUtils (file:/Users/su/.m2/repository/org/springframework/spring-core/5.2.4.RELEASE/spring-core-5.2.4.RELEASE.jar) to method java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain)
WARNING: Please consider reporting this to the maintainers of org.springframework.cglib.core.ReflectUtils
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
```

The warning shown above is caused by the use of internal JDK API in CGLIB, which will appear on *JDK9+*. 

You can read more about the [related issue here](https://github.com/spring-projects/spring-framework/issues/22674).