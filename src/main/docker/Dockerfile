# 指定jdk版本
FROM java:8
#VOLUME 指定了临时文件目录为/tmp。其效果是在主机 /var/lib/docker 目录下创建了一个临时文件，并链接到容器的/tmp。改步骤是可选的，
#如果涉及到文件系统的应用就很有必要了。/tmp目录用来持久化到 Docker 数据文件夹，因为 Spring Boot 使用的内嵌 Tomcat 容器默认使用/tmp作为工作目录
VOLUME /tmp
# 将jar包添加到容器中并更名为easyweb.jar
ADD easyweb.jar /easyweb.jar
# 运行jar包
RUN sh -c 'touch /easyweb.jar'
ENV JAVA_OPTS=""
#ENTRYPOINT 执行项目 easyweb.jar。为了缩短 Tomcat 启动时间，添加一个系统属性指向 “/dev/./urandom” 作为 Entropy Source
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Xmx1024m","-Xms1024m","/easyweb.jar"]