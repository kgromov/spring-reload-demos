# Spring config with consul
Apart from server-side discovery consul provides functionality of key/value storage that can be used as alternative to    
`spring-cloud-config` project. In order to use this feature - add the following dependency:
```xml
        <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-consul-config</artifactId>
		</dependency>
```

There are similar way to combine property sources as for `spring-cloud-config` project:   
- common for application (aka `application.properties`) - `config/application/`
- specific per application - (`${my-app}.propeties`) - `config/${my-app}/`;
- specific for active profile - `application-${my-profile}.properties`:
`config/${my-app},${my-profile}/` or `config/application,${my-profile}/`    
where 2nd option use profile separator `,`

* the most specific case - where config context (virtual folder) is defined alongside with server address
`spring.config.import=consul:http://localhost:8500/contextone;/context/two`

* explicit host:port configuration, `spring.config.import` has higher priority.   
To specify optional connection (not to fail on application startup when consul is not available) via `optional:consul`
```properties
spring.cloud.consul.host=localhost
spring.cloud.consul.port=8500
```

## Customizations
It's possible to customize defaults by own needs:

* Override default context (default is `application`)
`spring.cloud.consul.config.default-context=${my-context}`
* root folder of key/value storage; default is `config`
`spring.cloud.consul.config.prefixes=my-app1,my-app2`
* profile separator can also be customized (default is `,`):
`spring.cloud.consul.configprofileSeparator='::'`

## Use text blob as K/V
It's possible to save entire configuration (`yml/properties`) as value. First activate this feature in project:
`spring.cloud.consul.config.format=YAML|PROPERTIES` (default value is `KEY_VALUE`)
And then save under specific key called `data` that should be crated directly after any combination (described above):
```properties
config/testApp,dev/data
config/testApp/data
config/application,dev/data
config/application/data
```
Default key value can be changed via property `spring.cloud.consul.config.data-key=${my-key}`

## The Consul Config Watch
The Consul Config Watch takes advantage of the ability of consul to watch a key prefix.     
The Config Watch makes a blocking Consul HTTP API call to determine if any relevant configuration data has changed for the current application.    
If there is new configuration data a Refresh Event is published. This is equivalent to calling the /refresh actuator endpoint.
To change the frequency of when the Config Watch is called change spring.cloud.consul.config.watch.delay.     
The default value is 1000, which is in milliseconds.     
The delay is the amount of time after the end of the previous invocation and the start of the next.
To disable the Config Watch set `spring.cloud.consul.config.watch.enabled=false`.

## Fail fast
It may be convenient in certain circumstances (like local development or certain test scenarios) to not fail if consul isn’t available for configuration.    
Setting spring.cloud.consul.config.fail-fast=false will cause the configuration module to log a warning rather than throw an exception.   
This will allow the application to continue startup normally.

## git2consul with Config
git2consul is a Consul community project that loads files from a git repository to individual keys into Consul.   
**NOTE:** works ONLY on unix OS (at least does not work on Windows - but there is workaround - `wsl` 🙂).
It's something similar to `spring-cloud-config` with git server type:   
```yaml
  spring:
     cloud:
        config:
          server:
            git:
              uri: https://github.com/${organization}/${repository}.git
              clone-on-start: true
              default-label: master
```
In order to install:
```
 npm install -g git2consul
```

Then create configuration that points to git repository (or multiple repositories):
``git2consul --config-file ./consul/git2consul.json``

By default the names of the keys are names of the files. YAML and Properties files are supported with file extensions of .yml and .properties respectively.    
Set the `spring.cloud.consul.config.format` property to `FILES`.   

Given the following keys in /config, the development profile and an application name of foo:
```
    .gitignore
    application.yml
    bar.properties
    foo-development.properties
    foo-production.yml
    foo.properties
    master.ref
```

the following property sources would be created:
```
    config/foo-development.properties
    config/foo.properties
    config/application.yml
```

The value of each key needs to be a properly formatted YAML or Properties file.
