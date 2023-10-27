# Tips

## 强制更新依赖

```bash
mvn clean install -U  -DskipTests
```

## `pom.xml` 配置阿里maven仓库镜像

```xml
<!--https://developer.aliyun.com/mvn/guide-->
<repository>
    <id>central</id>
    <url>https://maven.aliyun.com/repository/central</url>
    <releases>
        <enabled>true</enabled>
    </releases>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
```