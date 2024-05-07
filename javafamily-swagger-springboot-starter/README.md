# javafamily-swagger-springboot-starter
> Swagger Support for springBoot starter

[![MavenPublish](https://github.com/JavaFamilyClub/javafamily-core/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/JavaFamilyClub/javafamily-core/actions/workflows/maven-publish.yml)

## 1. 引入依赖

* Maven Central Release

```xml
<dependency>
   <groupId>club.javafamily</groupId>
   <artifactId>javafamily-swagger-springboot-starter</artifactId>
   <version>2.3.2-beta.6</version>
</dependency>
```

* Maven Central Snapshot 仓库

``` xml
   <repositories>
      <repository>
         <id>maven-central</id>
         <url>https://oss.sonatype.org/content/repositories/snapshots</url>
         <releases>
            <enabled>false</enabled>
            <updatePolicy>never</updatePolicy>
         </releases>
         <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
         </snapshots>
      </repository>
   </repositories>

      <dependencies>
         <dependency>
            <groupId>club.javafamily</groupId>
            <artifactId>javafamily-resttemplate-starter</artifactId>
            <version>2.3.2-SNAPSHOT</version>
         </dependency>
      </dependencies>
```

