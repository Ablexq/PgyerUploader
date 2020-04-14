


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
# 









































