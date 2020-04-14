package com.xq.pgyer

import groovy.json.JsonSlurper
import org.gradle.api.Project


class PgyerUtils {

    /**
     * 上传apk到蒲公英
     */
    static def uploadPgyer(PgyerExtension pgyerExtension, Project project) {
        File apk = findApk(pgyerExtension)
        Object resp = uploadApk(pgyerExtension, apk)
        openBrowse(resp, project)
    }

    private static def uploadApk(PgyerExtension pgyerExtension, File apk) {
        println "*************** start upload file ***************"

        def twoHyphens = "--"
        def boundary = "*********"
        def end = "\r\n"

        //模拟表单上传 multipart/form-data
        def conn = new URL(pgyerExtension["uploadPath"]).openConnection()
        conn.setRequestMethod('POST')
        conn.setRequestProperty("Connection", "Keep-Alive")
        conn.setRequestProperty("Charset", "UTF-8")
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary)
        conn.setDoInput(true)
        conn.setDoOutput(true)

        //添加参数：_api_key
        def sb = new StringBuilder()
        sb.append(twoHyphens).append(boundary).append(end)
        sb.append("Content-Disposition: form-data; name=_api_key")
        sb.append(end).append(end)
        sb.append(pgyerExtension["_api_key"]).append(end)

        //添加参数：uKey
        sb.append(twoHyphens).append(boundary).append(end)
        sb.append("Content-Disposition: form-data; name=uKey")
        sb.append(end).append(end)
        sb.append(pgyerExtension["uKey"]).append(end)

        //添加参数：buildInstallType
        sb.append(twoHyphens).append(boundary).append(end)
        sb.append("Content-Disposition: form-data; name=buildInstallType")
        sb.append(end).append(end)
        sb.append(pgyerExtension["buildInstallType"]).append(end)

        //添加参数：buildPassword
        sb.append(twoHyphens).append(boundary).append(end)
        sb.append("Content-Disposition: form-data; name=buildPassword")
        sb.append(end).append(end)
        sb.append(pgyerExtension["buildPassword"]).append(end)

        //添加参数：buildUpdateDescription
        sb.append(twoHyphens).append(boundary).append(end)
        sb.append("Content-Disposition: form-data; name=buildUpdateDescription")
        sb.append(end).append(end)
        sb.append(pgyerExtension["buildUpdateDescription"]).append(end)

        //添加参数file: 需要上传的apk文件
        sb.append(twoHyphens).append(boundary).append(end)
        sb.append("Content-Disposition: form-data; name=file;filename=").append(apk.getName())
        sb.append(end).append(end)

        def dos = new DataOutputStream(conn.getOutputStream())
        //解决乱码
        dos.write(sb.toString().getBytes("UTF-8"))
        dos.flush()
        sb.delete(0, sb.length())

        def fis = new FileInputStream(apk)
        byte[] bf = new byte[8192]
        int len
        while ((len = fis.read(bf)) != -1) {
            dos.write(bf, 0, len)
        }
        sb.append(end)
        sb.append(twoHyphens).append(boundary).append(end)
        //解决乱码
        dos.write(sb.toString().getBytes("UTF-8"))

        dos.flush()
        fis.close()
        dos.close()
        conn.connect()

        def text = conn.getContent().text
        def resp = new JsonSlurper().parseText(text)

        println resp
        println "*************** upload finish ***************"
        resp
    }

    /**
     * 查找apk文件
     * @param pgyerExtension
     * @return
     */
    static def findApk(PgyerExtension pgyerExtension) {
        //查找上传的apk文件
        def apkDir = new File(pgyerExtension.apkPath)
        if (!apkDir.exists()) {
            throw new RuntimeException("apk output path not exists!")
        }

        def apk = null
        for (int i = apkDir.listFiles().length - 1; i >= 0; i--) {
            File file = apkDir.listFiles()[i]
            if (file.name.endsWith(".apk")) {
                apk = file
                break
            }
        }

        if (apk == null) {
            throw new RuntimeException("apk file not exists!")
        }
        return apk
    }

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
}