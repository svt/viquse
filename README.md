    ██╗   ██╗██╗ ██████╗ ██╗   ██╗███████╗███████╗
    ██║   ██║██║██╔═══██╗██║   ██║██╔════╝██╔════╝
    ██║   ██║██║██║   ██║██║   ██║███████╗█████╗  
    ╚██╗ ██╔╝██║██║▄▄ ██║██║   ██║╚════██║██╔══╝  
     ╚████╔╝ ██║╚██████╔╝╚██████╔╝███████║███████╗
      ╚═══╝  ╚═╝ ╚══▀▀═╝  ╚═════╝ ╚══════╝╚══════╝
                 VIDEO QUALITY SERVER
                                              

Viquse is a service for calculating objective video quality metrics. It currently supports
 [VMAF](https://github.com/Netflix/vmaf), [PSNR](https://en.wikipedia.org/wiki/Peak_signal-to-noise_ratio),
  and [SSIM](https://en.wikipedia.org/wiki/Structural_similarity).

## Dependencies
* Java 11 or later
* FFmpeg with libvmaf enabled

## Building
`./gradlew build`

An executable jar will be built and written to `build/libs/viquse.jar`.

## Running
`./gradlew bootRun`
or, if allready built,
`java -jar build/libs/viquse.jar`

A job is started by posting a job to the server:
```
curl http://localhost:8080/viquseJobs --data '{
  "referenceFile": "YOUR_REFERENCE_FILE",
  "transcodedFile": "YOUR_TRANSCODED_FILE"
}'
```
The server will return the created job in json-format
```
{
  "referenceFile" : "YOUR_REFERENCE_FILE",
  "transcodedFile" : "YOUR_TRANSCODED_FILE",
  "createdDate" : "2020-08-12T09:13:13.949342",
  "lastModifiedDate" : "2020-08-12T09:13:13.949367",
  "status" : "NEW",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/viquseJobs/9510d25f-c9c7-4587-acbb-add5a498d66f"
    },
    "viquseJob" : {
      "href" : "http://localhost:8080/viquseJobs/9510d25f-c9c7-4587-acbb-add5a498d66f"
    },
    "resultSummary" : {
      "href" : "http://localhost:8080/viquseJobs/9510d25f-c9c7-4587-acbb-add5a498d66f/resultSummary"
    }
  }
}
```

Processing the job may take a while, currently viquse only processes one job at a time. Processing may be slower than
realtime, depending on your hardware, so for a one hour video dont be surprised if you have to wait two hours for
viquse to finish.

The `_links.self.href` contains the URI for your job. You can use it to check the status of the job. Once the job is
finished, you can see the result in the location given by `_links.resultSummary.href`.

## Configuration
Viquse is a spring boot app and as such can be configured in different ways, 
see https://docs.spring.io/spring-boot/docs/1.2.2.RELEASE/reference/html/boot-features-external-config.html. The 
easiest way is to create a application.yml/application.properties in the directory where viquse is started (note
: this might not work if run with `gradle bootRun`) or to use
environment variables.

### Database
Per default, viquse will use a H2 inmemory database, which means your data will be lost when the service exits.
Viquse also has support for mariadb, to use mariadb instead configure viquse like below.

    # application.yml
    spring:
      jpa:
        open-in-view: false
        properties:
          dialect: org.hibernate.dialect.MariaDB53Dialect
          hibernate:
            dialect: org.hibernate.dialect.MariaDB53Dialect
      datasource:
        driver-class-name: org.mariadb.jdbc.Driver
        url: 'jdbc:mariadb://YOUR_DB_SERVER/YOUR_DB_NAME?useFractionalSeconds=true&autoReconnect=true'
        username: 'YOUR_DB_USERNAME'
        password: 'YOUR_DB_PASSWORD'
      flyway:
        url: 'jdbc:mariadb://YOUR_DB_SERVER/YOUR_DB_NAME?useFractionalSeconds=true&autoReconnect=true'
        username: 'YOUR_DB_USERNAME'
        password: 'YOUR_DB_PASSWORD'
        
### Spring cloud config
Viquse can use
 [spring cloud config client](https://cloud.spring.io/spring-cloud-config/multi/multi__spring_cloud_config_client.html) 
 to fetch configuration from a remote source. To enable it, you can put a `bootstrap.yml` in the working directory of
  viquse.
  
    # bootstrap.yml example
    spring:
      application:
        name: viquse
      cloud:
        config:
          enabled: true
          uri: https://YOUR_CONFIG_SERVER
          fail-fast: true
          
# License
Copyright 2020 Sveriges Television AB

This software is released under the [European Union Public Licence (EUPL v1.2)](LICENSE).
          
# Credits and references
* https://github.com/Netflix/vmaf
* https://medium.com/netflix-techblog/toward-a-practical-perceptual-video-quality-metric-653f208b9652
* https://netflixtechblog.com/vmaf-the-journey-continues-44b51ee9ed12
* https://ffmpeg.org/
