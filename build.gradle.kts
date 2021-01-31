plugins {
    kotlin("multiplatform") version "1.4.0" apply false
    id("org.hidetake.ssh") version "2.9.0"
}

allprojects {
    version = "0.1.1"

    repositories {
        mavenCentral()
        jcenter()
    }
}

tasks.register<Copy>("stage") {
    dependsOn("server:build")

    destinationDir = File("build/dist")

    from(tarTree("server/build/distributions/server-0.1.1.tar"))
}

val webServer = remotes.create("webServer") {
    host = "45.77.141.24"
    user = "root"
    identity = File("${System.getProperty("user.home")}/.ssh/id_rsa")
}

task<Exec>("copyDist") {
    commandLine = listOf("scp", "-r", "build/dist/", "root@45.77.141.24:/home/")
}

task("deploy") {
    dependsOn("copyDist")

    doLast {
        ssh.run(delegateClosureOf<org.hidetake.groovy.ssh.core.RunHandler> {
            session(webServer, delegateClosureOf<org.hidetake.groovy.ssh.session.SessionHandler> {
                execute("killall java || true")
                // val distDir = File(rootDir, "build/dist/")
                // put(
                //     hashMapOf(
                //         "from" to distDir.absolutePath,
                //         "into" to "/home",
                //         "fileTransfer" to "scp"
                //     )
                // )
                execute("nohup bash /home/dist/server-0.1.1/bin/server --host $webServer.host &")
            }
        )}
    )}
}