


# 使用


project的gradle文件中：

```groovy

buildscript {

    repositories {
        maven {
            //1/2、本地Maven仓库地址
            url uri('repo')
        }
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.1'
        //2/2、导入库 ：  maven.gradle中配置
        classpath 'com.xq.plugin:pgyer:1.0.0'
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}


```

app的gradle文件中：

```groovy
apply plugin: 'com.android.application'
//1/2、引入 ： resources.META-INF.gradle-plugins 中 properties名称
apply plugin: 'com.xq.pgyer'

//2/2、
pgyerExtension {
    buildUpdateDescription '上传了'
}

```

# 注意

- pgyer module的 groovy 文件夹中文件后缀为 groovy

- pgyer module的 resources.META-INF.gradle-plugins 文件夹中文件后缀为 properties


- 打开浏览器根据系统：

task中可访问project，但util中没有，需要传进来

```groovy

  /**
     * 打开浏览器
     * @param resp
     * @return
     */
    static def openBrowse(resp, Project project) {
        if (resp.code != 0) {
            throw new RuntimeException(resp.message)
        }

        def url = "https://www.pgyer.com/" + resp.data.buildShortcutUrl
        println "下载地址：===============" + url

        def osName = System.getProperty("os.name").toLowerCase()
        if (osName != null && osName.contains("win")) {
            new ByteArrayOutputStream().withStream { os ->
                project.exec {
                    commandLine "powershell", "start", url
                }
            }
        } else {
            new ByteArrayOutputStream().withStream { os ->
                project.exec {
                    executable = "open"
                    args = [url]
                    standardOutput = os
                }
            }
        }
    }
```
- 解决乱码

```groovy

//解决乱码
dos.write(sb.toString().getBytes("UTF-8"))
```

- 执行顺序

```groovy

  PgyerTask() {
        mustRunAfter(['clean', 'assemble'])
        dependsOn(['clean', 'assemble'])
        project.tasks.getByName("assemble") {
            mustRunAfter("clean")
            dependsOn("clean")
        }

        group = "publish"
    }
```



# 远程plugin的生成

[jitpack官网（可查看所有库及版本）](https://jitpack.io/)

[jitpack官方集成文档](https://jitpack.io/docs/ANDROID/)

gradle版本需要在4.6之上

1) 根目录下 build.gradle:

```
buildscript { 
  dependencies {
    classpath 'com.github.dcendents:android-maven-gradle-plugin:2.1' // Add this line
```

2) library/build.gradle add:

```
 apply plugin: 'com.github.dcendents.android-maven'  

 group='com.github.Ablexq'
```

3) Create a GitHub release or add a git tag.

# 使用plugin： 

因为我们是plugin 不是Java library，所以引用有所不同。

- 根目录的build.gradle : 

这里和本地仓库的模式一致。

```kotlin

buildscript {

    repositories {
//        maven {
//            //1/2、本地Maven仓库地址
//            url uri('repo')
//        }
        google()
        jcenter()
        
        //一/二 、 远程仓库
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.1'
        classpath 'com.github.dcendents:android-maven-gradle-plugin:2.1' // Add this line
//        //2/2、导入本地库
//        classpath 'com.xq.plugin:pgyer:1.0.0'
        
        //二/二 、导入远程库
        classpath 'com.github.Ablexq:PgyerUploader:1.0.2'
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

```
- app.gradle:

这里和本地仓库引入一样的方式

```kotlin
//1/2、引入
apply plugin: 'com.xq.pgyer'

//2/2、
pgyerExtension {
    buildUpdateDescription '上传了'
}
```









































