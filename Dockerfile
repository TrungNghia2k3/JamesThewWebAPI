FROM tomcat:9.0-jdk17

# Xoá các ứng dụng mặc định trong Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy war vào Tomcat và đổi tên thành ROOT.war để truy cập qua /
COPY target/JamesThewWebAPI.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
