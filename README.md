# VIQUSE - Video Quality Server

Viquse is a service for calculating objective quality metrics, ie vmaf. It uses ffmpeg/libvmaf for this.

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

Per default, viquse will use a H2 inmemory database, which means your data will be lost when the service exits.
For persistent use, a mysql database can be used instead.
