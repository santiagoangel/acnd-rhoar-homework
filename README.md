# acnd-rhoar-homework
Advanced Cloud-Native Development with Red Hat OpenShift Application Runtimes homework

## run local
mvn thorntail:run

## test services
rest service: http://localhost:8080/hello

health: http://localhost:8080/health

fault tolerance:

    Retry  http://localhost:8080/retry
    Fallback  http://localhost:8080/fallback
    Circuit Breaker  http://localhost:8080/circuit

Metrics http://localhost:8080/metrics


## run in OpenShift
#First login and create a project, then:

mvn clean fabric8:deploy -Popenshift


