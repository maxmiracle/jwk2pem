# jwk2pem converter for integration micronaut-acme

https://micronaut-projects.github.io/micronaut-acme/latest/guide
and 
certBot for LetsEncrypt
https://certbot.eff.org/

Problem: certbot generated file formats are different from micronaut-acme.

Java snippet can be used to convert certbot files to micronaut-acme format.

###### usage:

gradlew run --args="/pathToJwkAccountKey/private_key.json /pathToOutAccountKey/out.pem /pathToDomainPrivKey/privkey.pem /domainKeyOutPath/domainout.pem"